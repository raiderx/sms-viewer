package org.karpukhin.smsviewer.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Pavel Karpukhin
 */
public class DateUtils {

    public static final String DATE_FORMAT = "dd.MM.yyyy, HH:mm";

    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(date);
    }

    public static Date parseDate(String str) throws ParseException {
        return parseDate(str, DATE_FORMAT);
    }

    public static Date parseDate(String str, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.parse(str);
    }
}
