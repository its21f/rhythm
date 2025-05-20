package org.b3log.symphony.processor;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Latkes;
import org.b3log.latke.http.Request;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.renderer.AbstractFreeMarkerRenderer;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.symphony.processor.middleware.CSRFMidware;
import org.b3log.symphony.processor.middleware.LoginCheckMidware;
import org.b3log.latke.http.Dispatcher;
import org.b3log.symphony.service.ActivityMgmtService;
import org.b3log.symphony.service.DataModelService;
import org.b3log.symphony.util.OpenIdUtil;
import org.b3log.symphony.util.Sessions;
import org.b3log.symphony.util.StatusCodes;
import org.b3log.symphony.util.Symphonys;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Singleton
public class OpenIdProcessor {
    private static final Logger LOGGER = LogManager.getLogger(ActivityMgmtService.class);

    private static final String OPENID_NS_KEY = "openid.ns";
    private static final String OPENID_NS = "http://specs.openid.net/auth/2.0";
    private static final String OPENID_MODE_KEY = "openid.mode";
    private static final String OPENID_MODE_CHECKID = "checkid_setup";
    private static final String OPENID_MODE_RES = "id_res";
    private static final String OPENID_MODE_CHECK_AUTH = "check_authentication";
    private static final String OPENID_IDENTITY_KEY = "openid.identity";
    private static final String OPENID_IDENTITY = "http://specs.openid.net/auth/2.0/identifier_select";
    private static final String OPENID_CLAIMED_ID_KEY = "openid.claimed_id";
    private static final String OPENID_CLAIMED_ID = "http://specs.openid.net/auth/2.0/identifier_select";
    private static final String OPENID_RETURN_TO_KEY = "openid.return_to";
    private static final String OPENID_REALM_KEY = "openid.realm";
    private static final String OPENID_OP_ENDPOINT_KEY = "openid.op_endpoint";
    private static final String OPENID_ASSOC_HANDLE_KEY = "openid.assoc_handle";
    private static final String OPENID_ASSOC_HANDLE = Symphonys.get("openid.assocHandle");
    private static final String OPENID_RESPONSE_NONCE_KEY = "openid.response_nonce";
    private static final String OPENID_SIGNED_KEY = "openid.signed";
    private static final String OPENID_SIGNED = "op_endpoint,claimed_id,identity,return_to,response_nonce,assoc_handle";
    private static final String OPENID_SIG_KEY = "openid.sig";


    private static final Map<String, String>  respNonceMap = new LinkedHashMap<>();


    @Inject
    private DataModelService dataModelService;

    public static void register() {
        final BeanManager beanManager = BeanManager.getInstance();
        final LoginCheckMidware loginCheck = beanManager.getReference(LoginCheckMidware.class);
        final CSRFMidware csrfMidware = beanManager.getReference(CSRFMidware.class);


        final OpenIdProcessor openIdProcessor = beanManager.getReference(OpenIdProcessor.class);
        Dispatcher.get("/openid/login", openIdProcessor::showLoginForm, loginCheck::handle,csrfMidware::fill);
        Dispatcher.post("/openid/confirm", openIdProcessor::confirm, loginCheck::handle,csrfMidware::fill);
        Dispatcher.post("/openid/verify", openIdProcessor::verify, csrfMidware::fill);
    }

    /**
     * 显示授权页
     * @param context
     */
    public void showLoginForm(final RequestContext context) {
        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context, null);
        context.setRenderer(renderer);

        String ns = context.param(OPENID_NS_KEY);
        String mode = context.param(OPENID_MODE_KEY);
        String return_to = context.param(OPENID_RETURN_TO_KEY);
        String realm = context.param(OPENID_REALM_KEY);
        String identity = context.param(OPENID_IDENTITY_KEY);
        String claimed_id = context.param(OPENID_CLAIMED_ID_KEY);

        if(ns==null||mode==null||return_to==null||realm==null||identity==null||claimed_id==null){
            context.sendError(500);
            return;
        }
        // 先验证参数是否正确，不正确就回首页
        if(!OPENID_NS.equals(ns)){
            context.sendError(500);
            return;
        }
        if(!OPENID_MODE_CHECKID.equals(mode)){
            context.sendError(500);
            return;
        }
        if(!OPENID_IDENTITY.equals(identity)){
            context.sendError(500);
            return;
        }
        if(!OPENID_CLAIMED_ID.equals(claimed_id)){
            context.sendError(500);
            return;
        }
        // 判断return url 是否是https
        if(!return_to.startsWith("https://")){
            context.sendError(500);
            return;
        }
        // 判断return_to 是否在 realm 下
        if(!return_to.startsWith(realm)){
            context.sendError(500);
            return;
        }

        // 取得目标站点名称
        String realmName = realm.substring(realm.lastIndexOf("/")+1);

        renderer.setTemplateName("verify/openid.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();
        dataModel.put("realmName",realmName);
        dataModel.put("openid_ns",ns);
        dataModel.put("openid_mode",mode);
        dataModel.put("openid_identity",identity);
        dataModel.put("openid_claimed_id",claimed_id);
        dataModel.put("openid_realm",realm);
        dataModel.put("openid_return_to",return_to);
        dataModelService.fillHeaderAndFooter(context, dataModel);

    }


    /**
     * 授权登录
     * @param context
     */
    public void confirm(final RequestContext context){

        Request request = context.getRequest();
        final String ns = request.getParameter(OPENID_NS_KEY);
        final String mode = request.getParameter(OPENID_MODE_KEY);
        final String return_to = request.getParameter(OPENID_RETURN_TO_KEY);
        final String realm = request.getParameter(OPENID_REALM_KEY);
        final String identity = request.getParameter(OPENID_IDENTITY_KEY);
        final String claimed_id = request.getParameter(OPENID_CLAIMED_ID_KEY);

        if(ns==null||mode==null||return_to==null||realm==null||identity==null||claimed_id==null){
            redirectWithError(context,return_to);
            return;
        }
        // 先验证参数是否正确，不正确就回首页
        if(!OPENID_NS.equals(ns)){
            redirectWithError(context,return_to);
            return;
        }
        if(!OPENID_MODE_CHECKID.equals(mode)){
            redirectWithError(context,return_to);
            return;
        }
        if(!OPENID_IDENTITY.equals(identity)){
            redirectWithError(context,return_to);
            return;
        }
        if(!OPENID_CLAIMED_ID.equals(claimed_id)){
            redirectWithError(context,return_to);
            return;
        }
        // 判断return url 是否是https
        if(!return_to.startsWith("https://")){
            redirectWithError(context,return_to);
            return;
        }
        // 判断return_to 是否在 realm 下
        if(!return_to.startsWith(realm)){
            redirectWithError(context,return_to);
            return;
        }
        Map<String, String> result = new LinkedHashMap<>();
        try {
            // 取得当前登录的用户
            JSONObject user = Sessions.getUser();
            String userId = user.optString("oId");
            result.put(OPENID_NS_KEY,OPENID_NS);
            result.put(OPENID_OP_ENDPOINT_KEY, Latkes.getServePath()+"/openid" );
            result.put(OPENID_MODE_KEY, OPENID_MODE_RES);
            result.put(OPENID_CLAIMED_ID_KEY,Latkes.getServePath() + "/openid/id/"+ userId);
            result.put(OPENID_IDENTITY_KEY,Latkes.getServePath() + "/openid/id/" +userId);
            result.put(OPENID_ASSOC_HANDLE_KEY, OPENID_ASSOC_HANDLE);
            String nonce = OpenIdUtil.generateNonce();
            result.put(OPENID_RESPONSE_NONCE_KEY, nonce);
            respNonceMap.put(nonce,userId);
            result.put(OPENID_RETURN_TO_KEY,return_to);
            result.put(OPENID_SIGNED_KEY,OPENID_SIGNED);
            result.put(OPENID_SIG_KEY, OpenIdUtil.sign(result));

            StringBuilder redirect = new StringBuilder(return_to);
            if (!return_to.contains("?")) {
                redirect.append("?");
            } else {
                redirect.append("&");
            }

            for (Map.Entry<String, String> entry : result.entrySet()) {
                redirect.append(URLEncoder.encode(entry.getKey(), "UTF-8"))
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), "UTF-8"))
                        .append("&");
            }
            redirect.deleteCharAt(redirect.length() - 1);
            context.sendRedirect(redirect.toString());
        }catch (Exception e){
            LOGGER.log(Level.ERROR,e.getMessage(),e);
            redirectWithError(context,return_to);
        }

    }

    private void redirectWithError(final RequestContext context,String returnTo){
        if(returnTo==null||returnTo.isEmpty()){
            context.sendRedirect(Latkes.getServePath());
            return;
        }
        StringBuilder redirect = new StringBuilder(returnTo);
        if (!returnTo.contains("?")) {
            redirect.append("?");
        } else {
            redirect.append("&");
        }
        try {
            redirect.append("error=").append(URLEncoder.encode("登录失败","UTF-8"));
        }catch (Exception e){
            LOGGER.log(Level.ERROR,e.getMessage(),e);
        }
        context.sendRedirect(redirect.toString());

    }

    public void verify(final RequestContext context){
        final JSONObject requestJSONObject = context.requestJSON();
        final String ns = requestJSONObject.optString(OPENID_NS_KEY);
        final String mode = requestJSONObject.optString(OPENID_MODE_KEY);
        final String op_endpoint = requestJSONObject.optString(OPENID_OP_ENDPOINT_KEY);
        final String return_to = requestJSONObject.optString(OPENID_RETURN_TO_KEY);
        final String identity = requestJSONObject.optString(OPENID_IDENTITY_KEY);
        final String claimed_id = requestJSONObject.optString(OPENID_CLAIMED_ID_KEY);
        final String response_nonce = requestJSONObject.optString(OPENID_RESPONSE_NONCE_KEY);
        final String assoc_handle = requestJSONObject.optString(OPENID_ASSOC_HANDLE_KEY);
        final String sig = requestJSONObject.optString(OPENID_SIG_KEY);
        String userId =null;

        if(response_nonce!=null){
            // 先验证时间
            try{
                Date nonceTime = OpenIdUtil.extractNonceTimestamp(response_nonce);
                long now = System.currentTimeMillis();
                long delta = Math.abs(now - nonceTime.getTime());
                if (delta > 5 * 60 * 1000) {
                    respNonceMap.remove(response_nonce);
//                    context.renderJSON(StatusCodes.ERR).renderMsg("验证失败1");
                    sendVerifyResult(context,false);
                    return;
                }
            }catch (Exception e){
                respNonceMap.remove(response_nonce);
//                context.renderJSON(StatusCodes.ERR).renderMsg("验证失败2");
                sendVerifyResult(context,false);
                return;
            }
        }

        if(!respNonceMap.containsKey(response_nonce)){
//            context.renderJSON(StatusCodes.ERR).renderMsg("验证失败3");
            sendVerifyResult(context,false);
            return;
        }

        userId = respNonceMap.get(response_nonce);
        respNonceMap.remove(response_nonce);

        if(ns==null||mode==null||op_endpoint==null||return_to==null||identity==null||claimed_id==null||response_nonce==null||assoc_handle==null){
//            context.renderJSON(StatusCodes.ERR).renderMsg("验证失败4");
            sendVerifyResult(context,false);
            return;
        }

        Map<String, String> signMap = new LinkedHashMap<>();
        signMap.put(OPENID_OP_ENDPOINT_KEY,op_endpoint);
        signMap.put(OPENID_CLAIMED_ID_KEY,claimed_id);
        signMap.put(OPENID_IDENTITY_KEY,identity);
        signMap.put(OPENID_RETURN_TO_KEY,return_to);
        signMap.put(OPENID_RESPONSE_NONCE_KEY,response_nonce);
        signMap.put(OPENID_ASSOC_HANDLE_KEY,assoc_handle);
        signMap.put(OPENID_SIGNED_KEY,OPENID_SIGNED);
        try {
            if(!sig.equals(OpenIdUtil.sign(signMap))){
                sendVerifyResult(context,false);
//                context.renderJSON(StatusCodes.ERR).renderMsg("验证失败5");
                return;
            }
        } catch (Exception e) {
            LOGGER.log(Level.ERROR,e.getMessage(),e);
//            context.renderJSON(StatusCodes.ERR).renderMsg("验证失败6");
            sendVerifyResult(context,false);
            return;
        }

        if(!OPENID_NS.equals(ns)){
//            context.renderJSON(StatusCodes.ERR).renderMsg("验证失败7");
            sendVerifyResult(context,false);
            return;
        }
        if(!OPENID_MODE_CHECK_AUTH.equals(mode)){
//            context.renderJSON(StatusCodes.ERR).renderMsg("验证失败8");
            sendVerifyResult(context,false);
            return;
        }
        if(!op_endpoint.equals(Latkes.getServePath()+"/openid")){
//            context.renderJSON(StatusCodes.ERR).renderMsg("验证失败9");
            sendVerifyResult(context,false);
            return;
        }
        if(!return_to.startsWith("https://")){
//            context.renderJSON(StatusCodes.ERR).renderMsg("验证失败10");
            sendVerifyResult(context,false);
            return;
        }

        if(!identity.equals(claimed_id)){
//            context.renderJSON(StatusCodes.ERR).renderMsg("验证失败11");
            sendVerifyResult(context,false);
            return;
        }
        if(userId==null){
//            context.renderJSON(StatusCodes.ERR).renderMsg("验证失败12");
            sendVerifyResult(context,false);
            return;
        }
        String requestUserId = identity.substring(identity.lastIndexOf("/")+1);
        if(!requestUserId.equals(userId)){
//            context.renderJSON(StatusCodes.ERR).renderMsg("验证失败13");
            sendVerifyResult(context,false);
            return;
        }

//        context.renderJSON(StatusCodes.SUCC).renderMsg("验证成功");
        sendVerifyResult(context,true);

    }

    private void sendVerifyResult(final RequestContext context,Boolean result){
      // 根据result纯文本返回
        if(result){
            context.sendString("ns:http://specs.openid.net/auth/2.0\nis_valid:true\n");
        }else{
            context.sendString("ns:http://specs.openid.net/auth/2.0\nis_valid:false\n");
        }

    }


}
