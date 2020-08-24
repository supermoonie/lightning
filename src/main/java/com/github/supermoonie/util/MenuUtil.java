package com.github.supermoonie.util;


import java.awt.*;

import static javafx.scene.text.FontWeight.BOLD;

/**
 * @author supermoonie
 * @since 2020/8/23
 */
public class MenuUtil {

    private static final String ACTIVE_FLAG = "";

    private MenuUtil() {

    }

    public static void activeItem(Menu menu, String label) {
        int size = menu.getItemCount();
        for (int i = 0; i < size; i ++) {
            MenuItem item = menu.getItem(i);
            if (item.getLabel().equals(label)) {
                item.setFont(new Font(null, Font.BOLD, 14));
            } else {
                item.setFont(new Font(null, Font.PLAIN, 14));
            }
        }
    }

    public static void activeItem(Menu menu, int index) {
        int size = menu.getItemCount();
        for (int i = 0; i < size; i ++) {
            MenuItem item = menu.getItem(i);
            if (i == index) {
                item.setFont(new Font(null, Font.BOLD, 14));
            } else {
                item.setFont(new Font(null, Font.PLAIN, 14));
            }
        }
    }
}
