package org.antitech.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 * Some common utility functions for UI development
 *
 * @author Adam Olsen
 * @created November 30, 2004
 * @version 1.0
 */
public class Standard {

    private static Standard instance;

    /**
     * Constructor is private. Only the static methods should be used.
     */
    private Standard() {
    }

    /**
     */
    public static URL getURL(String resource) {
        if (instance == null) {
            instance = new Standard();
        }
        URL resourceUrl = instance.getClass().getClassLoader().getResource(
                resource);
        if (resourceUrl == null) {
            return null;
        }

        return resourceUrl;
    }

    /**
     * Gets an Image from the resources (usually the jar or current directory
     * that the app is running from)
     *
     * @param icon
     *            the name of the image to get
     * @return the requested image, or <tt>null</tt> if it could not be found
     */
    public static Image getImage(String icon) {
        if (instance == null) {
            instance = new Standard();
        }

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        URL imageUrl = instance.getClass().getClassLoader().getResource(
                "images/" + icon);
        if (imageUrl == null) {
            return null;
        }

        return toolkit.createImage(imageUrl);
    }

    /**
     * Gets an Icon from the resources (usually the jar or current directory
     * that the app is running from)
     *
     * @param icon
     *            the name of the image to get
     * @return the requested icon, or <tt>null</tt> if it could not be found
     */
    public static ImageIcon getIcon(String icon) {
        if (instance == null) {
            instance = new Standard();
        }

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        URL iconUrl = instance.getClass().getClassLoader().getResource(icon);

        if (iconUrl == null) {
            Transaction.updateConsole("Could not find an image for " + icon);
            return null;
        }

        return new ImageIcon(iconUrl);
    }

}
