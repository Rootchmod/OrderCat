package com.myjo.ordercat.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;
import java.util.Locale;

/**
 * Created by lee5hx on 17/4/27.
 */
public class OcDateTimeUtils {

    public static final DateTimeFormatter OC_DATE_TIME;

    static {
        OC_DATE_TIME = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .append(DateTimeFormatter.ISO_LOCAL_DATE)
                .appendLiteral(" ")
                .append(DateTimeFormatter.ISO_LOCAL_TIME)
                .toFormatter(Locale.CHINESE);
    }

    /**
     * 字符串转LocalDateTime
     * @param dateTime
     * @return
     */
    public static LocalDateTime string2LocalDateTime(String dateTime){
        return LocalDateTime.parse(dateTime, OcDateTimeUtils.OC_DATE_TIME);
    }

    public static String localDateTime2String(LocalDateTime dateTime){
        return dateTime.format(OC_DATE_TIME);
    }



    public static Date localDateTime2Date(LocalDateTime localDateTime){
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        java.util.Date date = Date.from(instant);
        return date;
    }




}
