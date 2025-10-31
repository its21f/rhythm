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

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.PropertyFilter;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.SortDirection;
import org.b3log.latke.service.ServiceException;
import org.b3log.symphony.cache.MembershipCache;
import org.b3log.symphony.model.Membership;
import org.b3log.symphony.model.MembershipLevel;
import org.b3log.symphony.repository.MembershipLevelRepository;
import org.b3log.symphony.repository.MembershipRepository;
import org.json.JSONObject;

@Singleton
public class MembershipQueryService {
    private static final Logger LOGGER = LogManager.getLogger(MembershipQueryService.class);

    @Inject
    private MembershipRepository membershipRepository;

    @Inject
    private MembershipLevelRepository levelRepository;

    @Inject
    private MembershipCache membershipCache;

    public JSONObject getStatus(final String userId, final String lvCode) throws ServiceException {
        try {
            final long now = System.currentTimeMillis();
            final JSONObject cached = membershipCache.get(userId);
            if (null != cached) {
                final long expiresAt = cached.optLong(Membership.EXPIRES_AT, 0L);
                if (expiresAt != 0L && expiresAt <= now) {
                    membershipCache.remove(userId);
                    return new JSONObject().put(Membership.STATE, 0);
                }
                if (lvCode.equals(cached.optString(Membership.LV_CODE))) {
                    return cached;
                } else {
                    return new JSONObject().put(Membership.STATE, 0);
                }
            }

            final JSONObject membership = membershipRepository.getByUserIdAndLvCode(userId, lvCode);
            if (null == membership) {
                return new JSONObject().put(Membership.STATE, 0);
            }
            final int state = membership.optInt(Membership.STATE, 0);
            final long expiresAt = membership.optLong(Membership.EXPIRES_AT, 0L);
            if (state != 1 || (expiresAt != 0L && expiresAt <= now)) {
                return new JSONObject().put(Membership.STATE, 0);
            }
            membershipCache.put(membership);
            return membership;
        } catch (RepositoryException e) {
            LOGGER.error("Get membership status failed", e);
            throw new ServiceException(e);
        }
    }

    public JSONObject getStatusByUserId(final String userId) throws ServiceException {
        try {
            final long now = System.currentTimeMillis();
            final JSONObject cached = membershipCache.get(userId);
            if (null != cached) {
                final long expiresAt = cached.optLong(Membership.EXPIRES_AT, 0L);
                if (expiresAt != 0L && expiresAt <= now) {
                    membershipCache.remove(userId);
                    return new JSONObject().put(Membership.STATE, 0);
                }
                return cached;
            }

            final JSONObject membership = membershipRepository.getActiveByUserId(userId);
            if (null == membership) {
                return new JSONObject().put(Membership.STATE, 0);
            }
            final long expiresAt = membership.optLong(Membership.EXPIRES_AT, 0L);
            if (expiresAt != 0L && expiresAt <= now) {
                return new JSONObject().put(Membership.STATE, 0);
            }
            membershipCache.put(membership);
            return membership;
        } catch (RepositoryException e) {
            LOGGER.error("Get membership status by userId failed", e);
            throw new ServiceException(e);
        }
    }

    /**
     * 列出所有会员等级。
     */
    public List<JSONObject> listLevels() throws ServiceException {
        try {
            final Query query = new Query()
                    .addSort(MembershipLevel.PRICE, SortDirection.ASCENDING)
                    .addSort(Keys.OBJECT_ID, SortDirection.ASCENDING);
            final List<JSONObject> levels = levelRepository.getList(query);
            // 附加每个等级的已开会员人数（state=1）
            for (final JSONObject level : levels) {
                final String lvCode = level.optString(MembershipLevel.LV_CODE);
                if (null == lvCode || lvCode.isEmpty()) {
                    level.put("openedMemberCount", 0);
                    continue;
                }
                final Query countQuery = new Query().setFilter(
                        org.b3log.latke.repository.CompositeFilterOperator.and(
                                new PropertyFilter(Membership.LV_CODE, FilterOperator.EQUAL, lvCode),
                                new PropertyFilter(Membership.STATE, FilterOperator.EQUAL, 1)
                        )
                );
                long cnt = 0;
                try {
                    cnt = membershipRepository.count(countQuery);
                } catch (final RepositoryException ignore) {
                    cnt = 0;
                }
                level.put("openedMemberCount", cnt);
            }
            return levels;
        } catch (final RepositoryException e) {
            LOGGER.error("List membership levels failed", e);
            throw new ServiceException(e);
        }
    }
}