package com.github.supermoonie;

import javafx.application.Application;

import java.awt.*;
import java.lang.reflect.Method;
import java.net.URL;

/**
 * @author supermoonie
 * @since 2020/8/22
 */
public class AppLauncher {

    public static void main(String[] args) {
        try {
            Class<?> util = Class.forName("com.apple.eawt.Application");
            Method getApplication = util.getMethod("getApplication");
            Object application = getApplication.invoke(util);
            Class<?>[] params = new Class[1];
            params[0] = Image.class;
            Method setDockIconImage = util.getMethod("setDockIconImage", params);
            URL url = App.class.getClassLoader().getResource("lightning-mac.png");
            Image image = Toolkit.getDefaultToolkit().getImage(url);
            setDockIconImage.invoke(application, image);
        } catch (Exception ignore) {
            // Won't work on Windows or Linux.
        }
        System.setProperty("apple.awt.UIElement", "true");
        java.awt.Toolkit.getDefaultToolkit();
        Application.launch(App.class);
    }
}
