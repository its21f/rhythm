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

import org.b3log.latke.Keys;
import org.b3log.latke.http.Dispatcher;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.model.User;
import org.b3log.latke.service.ServiceException;
import org.b3log.symphony.model.Coupon;
import org.b3log.symphony.model.Role;
import org.b3log.symphony.service.CouponMgmtService;
import org.b3log.symphony.processor.middleware.LoginCheckMidware;
import org.b3log.symphony.util.StatusCodes;
import org.json.JSONObject;
import org.json.JSONArray;

@Singleton
public class CouponProcessor {

    @Inject
    private CouponMgmtService couponMgmtService;

    public static void register() {
        final BeanManager beanManager = BeanManager.getInstance();
        final CouponProcessor processor = beanManager.getReference(CouponProcessor.class);
        final LoginCheckMidware loginCheck = beanManager.getReference(LoginCheckMidware.class);

        // Admin 管理接口
        Dispatcher.get("/admin/coupons", processor::list, loginCheck::handle);
        Dispatcher.post("/admin/coupon", processor::add, loginCheck::handle);
        Dispatcher.delete("/admin/coupon/{oId}", processor::remove, loginCheck::handle);
        Dispatcher.put("/admin/coupon/{oId}", processor::updateTimes, loginCheck::handle);
    }

    private boolean isAdmin(final JSONObject user) {
        return null != user && Role.ROLE_ID_C_ADMIN.equals(user.optString(User.USER_ROLE));
    }

    public void list(final RequestContext context) {
        try {
            final JSONObject currentUser = (JSONObject) context.attr(User.USER);
            if (!isAdmin(currentUser)) {
                final JSONObject response = new JSONObject();
                response.put("code", StatusCodes.ERR);
                response.put("msg", "无权限");
                response.put("data", new JSONArray());
                context.renderJSON(response);
                return;
            }
            final java.util.List<JSONObject> list = couponMgmtService.list();
            final JSONObject response = new JSONObject();
            response.put("code", StatusCodes.SUCC);
            response.put("msg", "success");
            response.put("data", new JSONArray(list));
            context.renderJSON(response);
        } catch (ServiceException e) {
            final JSONObject response = new JSONObject();
            response.put("code", StatusCodes.ERR);
            response.put("msg", e.getMessage());
            response.put("data", new JSONArray());
            context.renderJSON(response);
        }
    }

    public void add(final RequestContext context) {
        try {
            final JSONObject req = context.requestJSON();
            final JSONObject currentUser = (JSONObject) context.attr(User.USER);
            if (!isAdmin(currentUser)) {
                final JSONObject response = new JSONObject();
                response.put("code", StatusCodes.ERR);
                response.put("msg", "无权限");
                response.put("data", new JSONObject());
                context.renderJSON(response);
                return;
            }
            final String creater = null == currentUser ? null : currentUser.optString(Keys.OBJECT_ID);
            final String couponCode = req.optString(Coupon.COUPON_CODE, null);
            final Integer couponType = req.has(Coupon.COUPON_TYPE) ? req.optInt(Coupon.COUPON_TYPE) : null;
            final Integer discount = req.has(Coupon.DISCOUNT) ? req.optInt(Coupon.DISCOUNT) : null;
            final Integer times = req.has(Coupon.TIMES) ? req.optInt(Coupon.TIMES) : null;

            final JSONObject created = couponMgmtService.add(creater, couponCode, couponType, discount, times);
            final JSONObject response = new JSONObject();
            response.put("code", StatusCodes.SUCC);
            response.put("msg", "success");
            response.put("data", new JSONObject().put("coupon", created));
            context.renderJSON(response);
        } catch (ServiceException e) {
            final JSONObject response = new JSONObject();
            response.put("code", StatusCodes.ERR);
            response.put("msg", e.getMessage());
            response.put("data", new JSONObject());
            context.renderJSON(response);
        }
    }

    public void remove(final RequestContext context) {
        try {
            final JSONObject currentUser = (JSONObject) context.attr(User.USER);
            if (!isAdmin(currentUser)) {
                final JSONObject response = new JSONObject();
                response.put("code", StatusCodes.ERR);
                response.put("msg", "无权限");
                response.put("data", new JSONObject());
                context.renderJSON(response);
                return;
            }
            final String oId = context.pathVar("oId");
            couponMgmtService.remove(oId);
            final JSONObject response = new JSONObject();
            response.put("code", StatusCodes.SUCC);
            response.put("msg", "success");
            response.put("data", new JSONObject().put("oId", oId));
            context.renderJSON(response);
        } catch (ServiceException e) {
            final JSONObject response = new JSONObject();
            response.put("code", StatusCodes.ERR);
            response.put("msg", e.getMessage());
            response.put("data", new JSONObject());
            context.renderJSON(response);
        }
    }

    public void updateTimes(final RequestContext context) {
        try {
            final JSONObject currentUser = (JSONObject) context.attr(User.USER);
            if (!isAdmin(currentUser)) {
                final JSONObject response = new JSONObject();
                response.put("code", StatusCodes.ERR);
                response.put("msg", "无权限");
                response.put("data", new JSONObject());
                context.renderJSON(response);
                return;
            }
            final String oId = context.pathVar("oId");
            final JSONObject req = context.requestJSON();
            final Integer times = req.has(Coupon.TIMES) ? req.optInt(Coupon.TIMES) : null;
            final JSONObject updated = couponMgmtService.updateTimes(oId, times);
            final JSONObject response = new JSONObject();
            response.put("code", StatusCodes.SUCC);
            response.put("msg", "success");
            response.put("data", new JSONObject().put("coupon", updated));
            context.renderJSON(response);
        } catch (ServiceException e) {
            final JSONObject response = new JSONObject();
            response.put("code", StatusCodes.ERR);
            response.put("msg", e.getMessage());
            response.put("data", new JSONObject());
            context.renderJSON(response);
        }
    }
}