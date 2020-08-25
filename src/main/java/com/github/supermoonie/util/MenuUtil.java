package com.github.supermoonie.util;


import com.sun.javafx.PlatformUtil;

import java.awt.*;

/**
 * @author supermoonie
 * @since 2020/8/23
 */
public class MenuUtil {

    private static final String ACTIVE_FLAG = "   ✔️";

    private MenuUtil() {

    }

    public static void activeItem(Menu menu, int index) {
        int size = menu.getItemCount();
        for (int i = 0; i < size; i++) {
            MenuItem item = menu.getItem(i);
            if (i == index) {
                if (PlatformUtil.isWindows()) {
                    item.setFont(new Font(null, Font.BOLD, 14));
                } else {
                    item.setLabel(item.getLabel().replace(ACTIVE_FLAG, "") + ACTIVE_FLAG);
                }
            } else {
                if (PlatformUtil.isWindows()) {
                    item.setFont(new Font(null, Font.PLAIN, 14));
                } else {
                    item.setLabel(item.getLabel().replace(ACTIVE_FLAG, ""));
                }
            }
        }
    }
}
