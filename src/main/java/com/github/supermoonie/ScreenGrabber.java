package com.github.supermoonie;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;

import java.util.Arrays;

/**
 * @author supermoonie
 * @since 2020/8/28
 */
public class ScreenGrabber {

    public static void main(String[] args) throws Exception {
        int x = 0, y = 0, w = 1024, h = 768; // specify the region of screen to grab
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber("1:none");

        grabber.setFormat("avfoundation");
        grabber.setImageWidth(w);
        grabber.setImageHeight(h);
        grabber.start();

        CanvasFrame frame = new CanvasFrame("Screen Capture");
        while (frame.isVisible()) {
            Frame grab = grabber.grab();
//            System.out.println(grab.imageWidth + ", " + grab.imageHeight);
            frame.showImage(grab);
        }
        frame.dispose();
        grabber.stop();
    }
}
