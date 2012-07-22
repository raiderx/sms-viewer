package org.karpukhin.smsviewer.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Pavel Karpukhin
 */
public class DateUtils {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy, HH:mm");

    public static String formatDate(Date date) {
        return sdf.format(date);
    }
}
