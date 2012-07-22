package org.karpukhin.smsviewer.utils;

import java.awt.*;

/**
 * @author Pavel Karpukhin
 */
public class ViewUtils {

    /**
     * Sets window position in the center of screen
     */
    public static void setWindowPositionInCenterOfScreen(Window window) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension d = toolkit.getScreenSize();
            window.setLocation(
                    (int) (d.getWidth() - window.getSize().getWidth()) / 2,
                    (int) (d.getHeight() - window.getSize().getHeight()) / 2);
    }
}
