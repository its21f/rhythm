/*
 * Rhythm - A modern community (forum/BBS/SNS/blog) platform written in Java.
 * Modified version from Symphony, Thanks Symphony :)
 * Copyright (C) 2012-present, b3log.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.b3log.symphony.processor;

import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.http.Dispatcher;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.renderer.AbstractFreeMarkerRenderer;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.model.User;
import org.b3log.latke.service.ServiceException;
import org.b3log.symphony.model.*;
import org.b3log.symphony.processor.middleware.LoginCheckMidware;
import org.b3log.symphony.service.DataModelService;
import org.b3log.symphony.service.MembershipMgmtService;
import org.b3log.symphony.service.MembershipQueryService;
import org.b3log.symphony.util.Sessions;
import org.b3log.symphony.util.StatusCodes;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.Map;

/**
 * 会员模块处理器：Admin 管理与开放 API。
 */
@Singleton
public class MembershipProcessor {

    @Inject
    private MembershipMgmtService membershipMgmtService;

    @Inject
    private MembershipQueryService membershipQueryService;

    /**
     * Data model service.
     */
    @Inject
    private DataModelService dataModelService;

    public static void register() {
        final BeanManager beanManager = BeanManager.getInstance();
        final LoginCheckMidware loginCheck = beanManager.getReference(LoginCheckMidware.class);

        final MembershipProcessor processor = beanManager.getReference(MembershipProcessor.class);

        // Admin：会员等级管理
        Dispatcher.post("/admin/membership/level", processor::addLevel, loginCheck::handle);
        Dispatcher.put("/admin/membership/level/{oId}", processor::updateLevel, loginCheck::handle);
        Dispatcher.delete("/admin/membership/level/{oId}", processor::removeLevel, loginCheck::handle);
        Dispatcher.get("/api/membership/levels", processor::listLevels);
        // API：查询会员状态（按 userId 唯一） & 开通会员 & 更新用户配置
        Dispatcher.get("/api/membership/{userId}", processor::getUserMembershipStatus);
        Dispatcher.post("/api/membership/open", processor::openMembership, loginCheck::handle);
        Dispatcher.put("/api/membership/config", processor::updateUserConfig, loginCheck::handle);
        // Page: 会员页面
        Dispatcher.get("/vips", processor::showVipPage, loginCheck::handle);
        Dispatcher.get("/vips-admin",processor::showVipAdminPage,loginCheck::handle);
    }

    private boolean isAdmin(final JSONObject user) {
        return null != user && Role.ROLE_ID_C_ADMIN.equals(user.optString(User.USER_ROLE));
    }

    public void addLevel(final RequestContext context) {
        final JSONObject user = getUser(context);
        if (!isAdmin(user)) {
            context.renderJSON(StatusCodes.ERR).renderMsg("无权限");
            return;
        }
        final JSONObject req = context.requestJSON();
        try {
            final JSONObject level = new JSONObject();
            level.put(MembershipLevel.LV_NAME, req.optString(MembershipLevel.LV_NAME));
            level.put(MembershipLevel.LV_CODE, req.optString(MembershipLevel.LV_CODE));
            level.put(MembershipLevel.PRICE, req.optInt(MembershipLevel.PRICE));
            level.put(MembershipLevel.DURATION_TYPE, req.optString(MembershipLevel.DURATION_TYPE));
            level.put(MembershipLevel.DURATION_VALUE, req.optInt(MembershipLevel.DURATION_VALUE));
            level.put(MembershipLevel.BENEFITS, req.optString(MembershipLevel.BENEFITS));

            final String id = membershipMgmtService.addLevel(level);
            context.renderJSON(StatusCodes.SUCC).renderJSON(new JSONObject().put(Keys.OBJECT_ID, id));
        } catch (ServiceException e) {
            context.renderJSON(StatusCodes.ERR).renderMsg(e.getMessage());
        }
    }

    public void updateLevel(final RequestContext context) {
        final JSONObject user = getUser(context);
        if (!isAdmin(user)) {
            context.renderJSON(StatusCodes.ERR).renderMsg("无权限");
            return;
        }
        final String oId = context.pathVar("oId");
        final JSONObject req = context.requestJSON();
        try {
            membershipMgmtService.updateLevel(oId, req);
            context.renderJSON(StatusCodes.SUCC);
        } catch (ServiceException e) {
            context.renderJSON(StatusCodes.ERR).renderMsg(e.getMessage());
        }
    }

    public void removeLevel(final RequestContext context) {
        final JSONObject user = getUser(context);
        if (!isAdmin(user)) {
            context.renderJSON(StatusCodes.ERR).renderMsg("无权限");
            return;
        }
        final String oId = context.pathVar("oId");
        try {
            membershipMgmtService.removeLevel(oId);
            context.renderJSON(StatusCodes.SUCC);
        } catch (ServiceException e) {
            context.renderJSON(StatusCodes.ERR).renderMsg(e.getMessage());
        }
    }

    /**
     * 列出所有会员等级（公开接口）。
     */
    public void listLevels(final RequestContext context) {
        try {
            final java.util.List<JSONObject> levels = membershipQueryService.listLevels();
            context.renderJSON(new JSONObject().put("data", new JSONArray(levels))).renderJSON(StatusCodes.SUCC);
        } catch (ServiceException e) {
            context.renderJSON(StatusCodes.ERR).renderMsg(e.getMessage());
        }
    }

    public void getMembershipStatus(final RequestContext context) {
        final String userId = context.pathVar("userId");
        final String lvCode = context.pathVar("lvCode");
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(lvCode)) {
            context.renderJSON(StatusCodes.ERR).renderMsg("参数错误");
            return;
        }
        try {
            final JSONObject status = membershipQueryService.getStatus(userId, lvCode);
            context.renderJSON(StatusCodes.SUCC).renderJSON(status);
        } catch (ServiceException e) {
            context.renderJSON(StatusCodes.ERR).renderMsg(e.getMessage());
        }
    }

    public void getUserMembershipStatus(final RequestContext context) {
        final String userId = context.pathVar("userId");
        if (StringUtils.isBlank(userId)) {
            context.renderJSON(StatusCodes.ERR).renderMsg("参数错误");
            return;
        }
        try {
            final org.json.JSONObject status = membershipQueryService.getStatusByUserId(userId);
            context.renderJSON(StatusCodes.SUCC).renderJSON(status);
        } catch (ServiceException e) {
            context.renderJSON(StatusCodes.ERR).renderMsg(e.getMessage());
        }
    }

    public void openMembership(final RequestContext context) {
        final JSONObject user = getUser(context);
        if (null == user) {
            context.renderJSON(StatusCodes.ERR).renderMsg("未登录");
            return;
        }
        final JSONObject req = context.requestJSON();
        final String levelOId = req.optString(Keys.OBJECT_ID);
        final String configJson = req.optString(Membership.CONFIG_JSON);
        // 优惠券
        final String couponCode = req.optString(Membership.COUPON_CODE);

        if (StringUtils.isBlank(levelOId)) {
            context.renderJSON(StatusCodes.ERR).renderMsg("参数错误");
            return;
        }

        try {
            final JSONObject result = membershipMgmtService.openMembership(user.optString(Keys.OBJECT_ID), levelOId, configJson, couponCode);
            context.renderJSON(StatusCodes.SUCC).renderJSON(result);
        } catch (ServiceException e) {
            context.renderJSON(StatusCodes.ERR).renderMsg(e.getMessage());
        }
    }

    /**
     * 更新当前用户的会员配置 configJson。
     */
    public void updateUserConfig(final RequestContext context) {
        final JSONObject user = getUser(context);
        if (null == user) {
            context.renderJSON(StatusCodes.ERR).renderMsg("未登录");
            return;
        }
        final JSONObject req = context.requestJSON();
        String configJson = req.optString(Membership.CONFIG_JSON);
        // 兼容直接传对象
        if (StringUtils.isBlank(configJson)) {
            final JSONObject cfgObj = req.optJSONObject("config");
            if (null != cfgObj) {
                configJson = cfgObj.toString();
            }
        }
        if (StringUtils.isBlank(configJson)) {
            context.renderJSON(StatusCodes.ERR).renderMsg("参数错误：configJson 不能为空");
            return;
        }
        try {
            final JSONObject updated = membershipMgmtService.updateUserConfig(user.optString(Keys.OBJECT_ID), configJson);
            context.renderJSON(StatusCodes.SUCC).renderJSON(new JSONObject().put("membership", updated));
        } catch (ServiceException e) {
            context.renderJSON(StatusCodes.ERR).renderMsg(e.getMessage());
        }
    }

    public void showVipPage(final RequestContext context) {
        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context, "vip/index.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();
        dataModelService.fillHeaderAndFooter(context, dataModel);
    }

    public void showVipAdminPage(final RequestContext context) {
        final JSONObject user = getUser(context);
        if (!isAdmin(user)) {
            context.sendError(404);
            return;
        }
        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context, "vip/admin.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();
        dataModelService.fillHeaderAndFooter(context, dataModel);
    }

    private JSONObject getUser(final RequestContext context) {
        JSONObject currentUser = Sessions.getUser();
        try {
            currentUser = ApiProcessor.getUserByKey(context.param("apiKey"));
        } catch (NullPointerException ignored) {}
        try {
            final JSONObject requestJSONObject = context.requestJSON();
            currentUser = ApiProcessor.getUserByKey(requestJSONObject.optString("apiKey"));
        } catch (NullPointerException ignored) {}
        return currentUser;
    }
}