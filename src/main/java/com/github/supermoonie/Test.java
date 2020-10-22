package com.github.supermoonie;

import java.awt.*;

/**
 * @author supermoonie
 * @since 2020/8/23
 */
public class Test {

    public static void main(String[] args) {
        GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment(); //返回本地 GraphicsEnvironment 。
        String[] forName = e.getAvailableFontFamilyNames(); //返回包含在此所有字体系列名称的数组， GraphicsEnvironment本地化为默认的语言环境，如返回 Locale.getDefault() 。
        for (int i = 0; i < forName.length; i++) //逐行输出
            System.out.println(forName[i]);
    }
}
