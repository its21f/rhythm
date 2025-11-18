package org.b3log.symphony.util;

import java.time.LocalDate;
import java.time.ZoneId;

public class DateUtils {
    public static long getYesterdayStartMillis() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        return yesterday.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static long getYesterdayEndMillis() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        return yesterday.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() - 1;
    }
}

