package com.github.supermoonie.util;

import java.awt.image.BufferedImage;

/**
 * @author supermoonie
 * @since 2020/8/31
 */
public class ImageUtil {

    /**
     * A convenience method for getting ARGB pixels from an image. This tries to avoid the performance
     * penalty of BufferedImage.getRGB unmanaging the image.
     */
    public static int[] getRGB(BufferedImage image, int x, int y, int width, int height, int[] pixels) {
        int type = image.getType();
        if (type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB) {
            return (int[]) image.getRaster().getDataElements(x, y, width, height, pixels);
        }
        return image.getRGB(x, y, width, height, pixels, 0, width);
    }

    /**
     * A convenience method for setting ARGB pixels in an image. This tries to avoid the performance
     * penalty of BufferedImage.setRGB unmanaging the image.
     */
    public static void setRGB(BufferedImage image, int x, int y, int width, int height, int[] pixels) {
        int type = image.getType();
        if (type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB) {
            image.getRaster().setDataElements(x, y, width, height, pixels);
        } else {
            image.setRGB(x, y, width, height, pixels, 0, width);
        }
    }

    public static int saturateCast(int value) {
        return value > 255 ? 255 :
                (Math.max(value, 0));
    }
}
