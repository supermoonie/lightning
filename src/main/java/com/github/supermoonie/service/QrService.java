package com.github.supermoonie.service;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import javafx.scene.robot.Robot;
import javafx.stage.Screen;

import java.awt.image.BufferedImage;
import java.util.Hashtable;
import java.util.Map;

/**
 * @author supermoonie
 * @since 2020/8/23
 */
public class QrService {

    public static Result scanScreen() throws NotFoundException {
        Robot robot = new Robot();
        WritableImage image = robot.getScreenCapture(null, Screen.getPrimary().getBounds());
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
        Binarizer binarizer = new HybridBinarizer(source);
        BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
        return new MultiFormatReader().decode(binaryBitmap);
    }

    public static BufferedImage generate(String contents, int width, int height) throws WriterException {
        Map<EncodeHintType, Object> hints = new Hashtable<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 0);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(contents, BarcodeFormat.QR_CODE, width, height, hints);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }
}
