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
package org.b3log.symphony.model;

/**
 * 会员等级定义模型常量。
 */
public final class MembershipLevel {
    private MembershipLevel() {}

    public static final String MEMBERSHIP_LEVEL = "membership_level";

    public static final String LV_NAME = "lvName";
    public static final String LV_CODE = "lvCode";
    public static final String PRICE = "price";
    public static final String DURATION_TYPE = "durationType";
    public static final String DURATION_VALUE = "durationValue";
    public static final String BENEFITS = "benefits";
    public static final String CREATED_AT = "createdAt";
    public static final String UPDATED_AT = "updatedAt";
}