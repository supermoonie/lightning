package com.github.supermoonie.imageproc;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;

/**
 * @author supermoonie
 * @date 2018/9/29 14:31
 */
public abstract class AbstractBufferedImageOp implements BufferedImageOp {

    @Override
    public Rectangle2D getBounds2D(BufferedImage src) {
        return new Rectangle(0, 0, src.getWidth(), src.getHeight());
    }

    @Override
    public BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel dest) {
        if (dest == null) {
            dest = src.getColorModel();
        }
        return new BufferedImage(dest,
                dest.createCompatibleWritableRaster(src.getWidth(), src.getHeight()),
                dest.isAlphaPremultiplied(),
                null);
    }

    @Override
    public Point2D getPoint2D(Point2D srcPt, Point2D dstPt) {
        if (dstPt == null) {
            dstPt = new Point2D.Double();
        }
        dstPt.setLocation(srcPt.getX(), srcPt.getY());
        return dstPt;
    }

    @Override
    public RenderingHints getRenderingHints() {
        return null;
    }


}
