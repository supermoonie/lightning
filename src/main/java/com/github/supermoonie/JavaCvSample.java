package com.github.supermoonie;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;

/**
 * @author supermoonie
 * @since 2020/8/28
 */
public class JavaCvSample {

    public static void main(String[] args) throws Exception {
        FrameGrabber grabber = FrameGrabber.createDefault(0);
        grabber.start();
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        Mat grabbedImage = converter.convert(grabber.grab());
        CanvasFrame frame = new CanvasFrame("Some Title", CanvasFrame.getDefaultGamma() / grabber.getGamma());
//        frame.showImage(grabbedImage);
    }
}
