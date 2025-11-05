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
package org.b3log.symphony.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

/**
 * Date utilities for common parse/format and basic calculations.
 *
 * This class is self-contained (no external dependencies) and uses
 * {@link java.text.SimpleDateFormat} in strict mode for parsing.
 */
public final class Dates {

    /** Default date pattern: yyyy-MM-dd. */
    public static final String PATTERN_DATE = "yyyy-MM-dd";
    /** Default datetime pattern: yyyy-MM-dd HH:mm:ss. */
    public static final String PATTERN_DATETIME = "yyyy-MM-dd HH:mm:ss";

    private Dates() {
    }

    private static SimpleDateFormat sdf(final String pattern) {
        final SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setLenient(false);
        return sdf;
    }

    /**
     * Checks if the given date string matches the pattern strictly.
     *
     * @param dateStr date string
     * @param pattern pattern, e.g. yyyy-MM-dd
     * @return true if valid, false otherwise
     */
    public static boolean isValid(final String dateStr, final String pattern) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return false;
        }
        try {
            sdf(pattern).parse(dateStr.trim());
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * Parses date string with the given pattern. Returns null if invalid.
     *
     * @param dateStr date string
     * @param pattern pattern
     * @return parsed {@link Date} or null
     */
    public static Date parseOrNull(final String dateStr, final String pattern) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            return sdf(pattern).parse(dateStr.trim());
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * Formats date with the given pattern. Returns null if date is null.
     *
     * @param date    date
     * @param pattern pattern
     * @return formatted string or null
     */
    public static String format(final Date date, final String pattern) {
        if (date == null) {
            return null;
        }
        return sdf(pattern).format(date);
    }

    /**
     * Converts date string to epoch millis. Returns -1 if invalid.
     *
     * @param dateStr date string
     * @param pattern pattern
     * @return epoch millis or -1 if invalid
     */
    public static long toMillis(final String dateStr, final String pattern) {
        final Date d = parseOrNull(dateStr, pattern);
        return d == null ? -1L : d.getTime();
    }

    /**
     * Returns today's date string with given pattern.
     *
     * @param pattern pattern
     * @return formatted today string
     */
    public static String today(final String pattern) {
        return format(new Date(), pattern);
    }

    /**
     * Adds days to the given date.
     *
     * @param date date
     * @param days days to add (negative for subtract)
     * @return new date
     */
    public static Date addDays(final Date date, final int days) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, days);
        return cal.getTime();
    }

    /**
     * Adds days to a date string and returns formatted result.
     * Returns null if input invalid.
     *
     * @param dateStr date string
     * @param pattern pattern
     * @param days    days to add
     * @return formatted string or null
     */
    public static String addDays(final String dateStr, final String pattern, final int days) {
        final Date d = parseOrNull(dateStr, pattern);
        if (d == null) {
            return null;
        }
        return format(addDays(d, days), pattern);
    }

    /**
     * Gets start of day for the given date.
     *
     * @param date date
     * @return start of day
     */
    public static Date startOfDay(final Date date) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * Gets end of day for the given date.
     *
     * @param date date
     * @return end of day
     */
    public static Date endOfDay(final Date date) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    /**
     * Determines whether the given date string (with pattern) is expired (before
     * now).
     * Invalid input returns false.
     *
     * @param dateStr date string
     * @param pattern pattern
     * @return true if before now
     */
    public static boolean isExpired(final String dateStr, final String pattern) {
        final Date d = parseOrNull(dateStr, pattern);
        return d != null && d.before(new Date());
    }

    /**
     * Calculates expire date string.
     *
     * @param optString optional string
     * @return expire date string
     */
    public static String calExpire(String optString) {
        if (StringUtils.isNotBlank(optString) && isValid(optString, PATTERN_DATE)) {
            return optString;
        }
        return "2099-12-31";
    }
}