package com.github.supermoonie.controller.qr;

import com.github.supermoonie.App;
import com.github.supermoonie.constant.StageKey;
import com.github.supermoonie.service.QrService;
import com.github.supermoonie.util.AlertUtil;
import com.github.supermoonie.view.qr.GenerateView;
import com.google.zxing.WriterException;
import com.sun.javafx.PlatformUtil;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author supermoonie
 * @since 2020/8/23
 */
public class GenerateController extends GenerateView {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sizeCombobox.getItems().add(80);
        sizeCombobox.getItems().add(160);
        sizeCombobox.getItems().add(320);
        sizeCombobox.getItems().add(640);
        sizeCombobox.setValue(160);
        Stage stage = App.STAGE_MAP.get(StageKey.QR_GENERATE);
        splitPane.prefWidthProperty().bind(stage.widthProperty());
        splitPane.prefHeightProperty().bind(stage.heightProperty());
        if (PlatformUtil.isWindows()) {
            qrContentInput.prefHeightProperty().bind(stage.heightProperty().subtract(72));
            imageViewContainer.prefHeightProperty().bind(stage.heightProperty().subtract(75));
        } else {
            qrContentInput.prefHeightProperty().bind(stage.heightProperty().subtract(60));
            imageViewContainer.prefHeightProperty().bind(stage.heightProperty().subtract(62));
        }
    }

    @FXML
    public void onGenerateButtonClicked() {
        String text = qrContentInput.getText();
        if (null == text || "".equals(text.trim())) {
            AlertUtil.warning("Empty !");
            return;
        }
        Integer size = sizeCombobox.getValue();
        try {
            BufferedImage image = QrService.generate(text, size, size);
            Image img = SwingFXUtils.toFXImage(image, null);
            qrImageView.setImage(img);
        } catch (WriterException e) {
            AlertUtil.error(e);
        }
    }

    @FXML
    public void onSaveButtonClicked() {
        Image image = qrImageView.getImage();
        if (null != image) {
            BufferedImage img = SwingFXUtils.fromFXImage(image, null);
            FileChooser fileChooser = new FileChooser();
            Integer size = sizeCombobox.getValue();
            fileChooser.setInitialFileName(size + ".png");
            Stage stage = App.STAGE_MAP.get(StageKey.QR_GENERATE);
            File file = fileChooser.showSaveDialog(stage);
            if (null != file) {
                try {
                    ImageIO.write(img, "png", file);
                } catch (IOException e) {
                    AlertUtil.error(e);
                }
            }
        }
    }
}
