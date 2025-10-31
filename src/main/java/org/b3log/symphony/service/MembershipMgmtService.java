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
package org.b3log.symphony.service;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.PropertyFilter;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.util.Ids;
import org.b3log.latke.util.Times;
import org.b3log.symphony.model.Membership;
import org.b3log.symphony.model.MembershipActivation;
import org.b3log.symphony.model.MembershipLevel;
import org.b3log.symphony.model.Pointtransfer;
import org.b3log.symphony.model.Coupon;
import org.b3log.symphony.repository.MembershipActivationRepository;
import org.b3log.symphony.repository.MembershipLevelRepository;
import org.b3log.symphony.repository.MembershipRepository;
import org.b3log.symphony.repository.CouponRepository;
import org.b3log.symphony.cache.MembershipCache;
import org.json.JSONObject;
import org.json.JSONArray;

@Singleton
public class MembershipMgmtService {
    private static final Logger LOGGER = LogManager.getLogger(MembershipMgmtService.class);

    @Inject
    private MembershipLevelRepository levelRepository;

    @Inject
    private MembershipRepository membershipRepository;

    @Inject
    private MembershipActivationRepository activationRepository;

    @Inject
    private PointtransferMgmtService pointtransferMgmtService;

    @Inject
    private CouponRepository couponRepository;

    @Inject
    private MembershipCache membershipCache;

    public String addLevel(final JSONObject level) throws ServiceException {
        try {
            final String lvCode = level.optString(MembershipLevel.LV_CODE);
            final String durationType = level.optString(MembershipLevel.DURATION_TYPE);
            if (StringUtils.isBlank(lvCode) || StringUtils.isBlank(durationType)) {
                throw new ServiceException("lvCode 与 durationType 不能为空");
            }
            if (null != levelRepository.getByCodeAndDurationType(lvCode, durationType)) {
                throw new ServiceException("已存在相同 lvCode + durationType 的等级配置");
            }
            final long now = System.currentTimeMillis();
            level.put(Keys.OBJECT_ID, Ids.genTimeMillisId());
            level.put(MembershipLevel.CREATED_AT, now);
            level.put(MembershipLevel.UPDATED_AT, now);
            levelRepository.add(level);
            return level.optString(Keys.OBJECT_ID);
        } catch (RepositoryException e) {
            LOGGER.error("Add level failed", e);
            throw new ServiceException(e);
        }
    }

    public void updateLevel(final String oId, final JSONObject levelPatch) throws ServiceException {
        try {
            final JSONObject old = levelRepository.getById(oId);
            if (null == old) {
                throw new ServiceException("等级不存在");
            }
            for (final String key : levelPatch.keySet()) {
                old.put(key, levelPatch.opt(key));
            }
            old.put(MembershipLevel.UPDATED_AT, System.currentTimeMillis());
            levelRepository.update(oId, old);
        } catch (RepositoryException e) {
            LOGGER.error("Update level failed", e);
            throw new ServiceException(e);
        }
    }

    public void removeLevel(final String oId) throws ServiceException {
        try {
            levelRepository.remove(oId);
        } catch (RepositoryException e) {
            LOGGER.error("Remove level failed", e);
            throw new ServiceException(e);
        }
    }

    /**
     * 开通会员：按照等级定义计算过期时间，写入会员记录与开通记录。
     */
    public JSONObject openMembership(final String userId, final String levelOId, final String configJson, final String couponCode)
            throws ServiceException {
        final Transaction transaction = membershipRepository.beginTransaction();
        try {
            if (StringUtils.isBlank(userId) || StringUtils.isBlank(levelOId)) {
                throw new ServiceException("参数不完整");
            }
            final JSONObject level = levelRepository.getById(levelOId);
            if (null == level) {
                throw new ServiceException("等级不存在");
            }
            final String lvCode = level.optString(MembershipLevel.LV_CODE);
            final String durationType = level.optString(MembershipLevel.DURATION_TYPE);
            final int durationValue = level.optInt(MembershipLevel.DURATION_VALUE);
            final int price = level.optInt(MembershipLevel.PRICE);
            if (durationValue <= 0) {
                throw new ServiceException("等级周期配置非法");
            }
            final long now = System.currentTimeMillis();
            final long expiresAt = calcExpires(now, durationType, durationValue);

            // 如果当前已有激活会员且未过期，则直接返回失败
            final JSONObject active = membershipRepository.getActiveByUserId(userId);
            if (null != active) {
                final long existsExpiresAt = active.optLong(Membership.EXPIRES_AT, 0L);
                if (existsExpiresAt == 0L || existsExpiresAt > now) {
                    throw new ServiceException("已经是会员了, 等待会员周期结束");
                }
            }

            JSONObject membership = membershipRepository.getByUserIdAndLvCode(userId, lvCode);
            if (null == membership) {
                membership = new JSONObject();
                membership.put(Keys.OBJECT_ID, Ids.genTimeMillisId());
                membership.put(Membership.USER_ID, userId);
                membership.put(Membership.LV_CODE, lvCode);
                membership.put(Membership.CREATED_AT, now);
            }
            // 优惠券校验, 如果有优惠券代码, 计算优惠价格.
            // 如果查出来没有结果或 times=0 就说明是假的/无效, 最后按 1.2 倍计算 (略施小惩)
            int finalPrice = price;
            if (StringUtils.isNotBlank(couponCode)) {
                try {
                    final JSONObject coupon = couponRepository.getByCode(couponCode);
                    if (null == coupon) {
                        // 券不存在：按 1.2 倍处罚
                        finalPrice = (int) Math.round(price * 1.2d);
                    } else {
                        final int times = coupon.optInt(Coupon.TIMES);
                        if (times == 0) {
                            // 券次数为 0（不可用）：不惩罚、按原价结算
                            finalPrice = price;
                        } else {
                            final int discount = coupon.optInt(Coupon.DISCOUNT, 100);
                            finalPrice = (int) Math.round(price * (discount / 100.0d));
                            // 消耗一次（仅当 times > 0），-1 表示不限次不消耗
                            if (times > 0) {
                                coupon.put(Coupon.TIMES, times - 1);
                                coupon.put(Coupon.UPDATED_AT, now);
                                couponRepository.update(coupon.optString(Keys.OBJECT_ID), coupon);
                            }
                        }
                    }
                } catch (final RepositoryException ignore) {
                    // 更新失败? 那就原价吧                    
                    finalPrice = price;
                }
            }
            // 扣积分（余额不足则失败），参与当前事务
            final String transferId = pointtransferMgmtService.transferInCurrentTransaction(
                    userId,
                    Pointtransfer.ID_C_SYS,
                    Pointtransfer.TRANSFER_TYPE_C_ACCOUNT2ACCOUNT,
                    finalPrice,
                    "",
                    now,
                    "开通会员[" + level.optString(MembershipLevel.LV_NAME) + "](" + lvCode + ")，" +
                            "原价：" + price + "，" +
                            "优惠价：" + finalPrice);
            if (null == transferId) {
                throw new ServiceException("当前积分不足, 少年需要继续努力");
            }

            membership.put(Membership.STATE, 1);
            membership.put(Membership.EXPIRES_AT, expiresAt);
            membership.put(Membership.CONFIG_JSON, configJson);
            membership.put(Membership.UPDATED_AT, now);

            if (null == membership.optString(Keys.OBJECT_ID)) {
                membership.put(Keys.OBJECT_ID, Ids.genTimeMillisId());
                membershipRepository.add(membership);
            } else {
                membershipRepository.update(membership.optString(Keys.OBJECT_ID), membership);
            }

            final JSONObject activation = new JSONObject();
            activation.put(Keys.OBJECT_ID, Ids.genTimeMillisId());
            activation.put(MembershipActivation.USER_ID, userId);
            activation.put(MembershipActivation.LV_CODE, lvCode);
            activation.put(MembershipActivation.PRICE, finalPrice);
            activation.put(MembershipActivation.DURATION_TYPE, durationType);
            activation.put(MembershipActivation.DURATION_VALUE, durationValue);
            activation.put(MembershipActivation.COUPON_CODE, couponCode);
            activation.put(MembershipActivation.CONFIG_JSON, configJson);
            activation.put(MembershipActivation.CREATED_AT, now);
            activation.put(MembershipActivation.UPDATED_AT, now);
            activationRepository.add(activation);

            transaction.commit();

            // Update cache with the latest active membership
            membershipCache.put(membership);

            final JSONObject ret = new JSONObject();
            ret.put("membership", membership);
            ret.put("activation", activation);
            return ret;
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.error("Open membership failed", e);
            throw new ServiceException(e);
        }
    }

    private long calcExpires(final long start, final String durationType, final int durationValue) {
        // 统一从“次日 0 点”起算
        final long nextDayStart = Times.getDayStartTime(start) + 24L * 60L * 60L * 1000L;
        final long days = durationValue;
        return nextDayStart + days * 24L * 60L * 60L * 1000L;
    }

    /**
     * 更新用户会员配置（仅更新当前激活会员的 configJson）。
     * 需要校验：用户有激活会员、未过期；configJson 与当前等级 benefits 模板匹配（键集合为模板子集且类型一致）。
     */
    public JSONObject updateUserConfig(final String userId, final String configJson) throws ServiceException {
        if (StringUtils.isBlank(userId)) {
            throw new ServiceException("未登录");
        }
        if (StringUtils.isBlank(configJson)) {
            throw new ServiceException("参数错误：configJson 不能为空");
        }
        try {
            final long now = System.currentTimeMillis();
            final JSONObject membership = membershipRepository.getActiveByUserId(userId);
            if (null == membership) {
                throw new ServiceException("未开通会员或未激活");
            }
            final long expiresAt = membership.optLong(Membership.EXPIRES_AT, 0L);
            if (expiresAt != 0L && expiresAt <= now) {
                throw new ServiceException("会员已过期");
            }

            final String lvCode = membership.optString(Membership.LV_CODE);
            // 通过 lvCode 获取一个等级定义（不依赖 durationType）
            final Query query = new Query().setFilter(new PropertyFilter(MembershipLevel.LV_CODE, FilterOperator.EQUAL, lvCode))
                    .setPageCount(1).setPage(1, 1);
            final JSONObject level = levelRepository.getFirst(query);
            if (null == level) {
                throw new ServiceException("会员等级定义不存在");
            }

            final String benefitsTemplateStr = level.optString(MembershipLevel.BENEFITS);
            JSONObject benefitsTemplate;
            try {
                benefitsTemplate = StringUtils.isBlank(benefitsTemplateStr) ? new JSONObject() : new JSONObject(benefitsTemplateStr);
            } catch (final Exception e) {
                throw new ServiceException("等级配置模板非法");
            }

            JSONObject userConfig;
            try {
                userConfig = new JSONObject(configJson);
            } catch (final Exception e) {
                throw new ServiceException("configJson 非法 JSON");
            }

            // 1) 用户配置项必须在模板内（不允许额外键），且类型匹配（与模板）
            for (final String key : userConfig.keySet()) {
                if (!benefitsTemplate.has(key)) {
                    throw new ServiceException("不允许的配置项: " + key);
                }
                final Object tplVal = benefitsTemplate.opt(key);
                final Object usrVal = userConfig.opt(key);
                if (tplVal != null && usrVal != null) {
                    final Class<?> tCls = tplVal.getClass();
                    final Class<?> uCls = usrVal.getClass();
                    final boolean bothNumber = (tplVal instanceof Number) && (usrVal instanceof Number);
                    if (!bothNumber && !tCls.equals(uCls)) {
                        throw new ServiceException("配置项类型不匹配: " + key);
                    }
                }
            }

            // 2) 严格校验：模板中的所有键都必须在用户配置中出现，且值不能为空
            for (final String key : benefitsTemplate.keySet()) {
                if (!userConfig.has(key)) {
                    throw new ServiceException("缺少配置项: " + key);
                }
                final Object usrVal = userConfig.opt(key);
                if (isEmptyValue(usrVal)) {
                    throw new ServiceException("配置项不能为空: " + key);
                }
                final Object tplVal = benefitsTemplate.opt(key);
                if (tplVal != null && usrVal != null) {
                    final Class<?> tCls = tplVal.getClass();
                    final Class<?> uCls = usrVal.getClass();
                    final boolean bothNumber = (tplVal instanceof Number) && (usrVal instanceof Number);
                    if (!bothNumber && !tCls.equals(uCls)) {
                        throw new ServiceException("配置项类型不匹配: " + key);
                    }
                }
            }

            // 更新配置
            membership.put(Membership.CONFIG_JSON, userConfig.toString());
            membership.put(Membership.UPDATED_AT, now);
            membershipRepository.update(membership.optString(Keys.OBJECT_ID), membership);
            // Update cache after config change
            membershipCache.put(membership);
            return membership;
        } catch (final RepositoryException e) {
            LOGGER.error("Update membership config failed", e);
            throw new ServiceException(e);
        }
    }

    /**
     * 判断值是否为空：null、空字符串、空对象或空数组视为为空。
     */
    private boolean isEmptyValue(final Object v) {
        if (v == null) {
            return true;
        }
        if (v instanceof String) {
            return StringUtils.isBlank((String) v);
        }
        if (v instanceof JSONObject) {
            return ((JSONObject) v).length() == 0;
        }
        if (v instanceof JSONArray) {
            return ((JSONArray) v).length() == 0;
        }
        // 数字、布尔类型不作“空”校验
        return false;
    }
}