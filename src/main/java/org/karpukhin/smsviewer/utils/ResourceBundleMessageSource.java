package org.karpukhin.smsviewer.utils;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Pavel Karpukhin
 */
public class ResourceBundleMessageSource implements MessageSource {

    private ResourceBundle resourceBundle;
    private ResourceBundle defaultResourceBundle;

    public ResourceBundleMessageSource(String baseName) {
        resourceBundle = ResourceBundle.getBundle(baseName);
        defaultResourceBundle = ResourceBundle.getBundle(baseName, Locale.ENGLISH);
    }

    @Override
    public String getMessage(String key) {
        if (resourceBundle.containsKey(key)) {
            return resourceBundle.getString(key);
        }
        if (defaultResourceBundle.containsKey(key)) {
            return defaultResourceBundle.getString(key);
        }
        return key;
    }
}
