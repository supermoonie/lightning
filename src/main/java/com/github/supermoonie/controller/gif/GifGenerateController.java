package com.github.supermoonie.controller.gif;

import com.github.supermoonie.App;
import com.github.supermoonie.constant.StageKey;
import com.github.supermoonie.io.GifSequenceWriter;
import com.github.supermoonie.util.AlertUtil;
import com.github.supermoonie.view.gif.GifGenerateView;
import com.sun.javafx.PlatformUtil;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

/**
 * @author supermoonie
 * @date 2020-08-25
 */
public class GifGenerateController extends GifGenerateView {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Stage stage = App.STAGE_MAP.get(StageKey.GIF_GENERATE);
        splitPane.prefWidthProperty().bind(stage.widthProperty());
        splitPane.prefHeightProperty().bind(stage.heightProperty());
        HBox.setHgrow(addButton, Priority.ALWAYS);
        addButton.setMaxWidth(Double.MAX_VALUE);
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Delete");
        deleteItem.setOnAction(event -> imageViewList.getItems().remove(imageViewList.getSelectionModel().getSelectedIndex()));
        MenuItem replaceItem = new MenuItem("Replace");
        replaceItem.setOnAction(event -> {
            Image image = selectImage();
            if (null == image) {
                return;
            }
            imageViewList.getSelectionModel().getSelectedItem().setImage(image);
        });
        contextMenu.getItems().add(replaceItem);
        contextMenu.getItems().add(deleteItem);
        imageViewList.setContextMenu(contextMenu);
        if (PlatformUtil.isWindows()) {
            imageViewList.prefHeightProperty().bind(stage.heightProperty().subtract(64));
        } else {
            imageViewList.prefHeightProperty().bind(stage.heightProperty().subtract(50));
        }
        FileChooser.ExtensionFilter imageFilter
                = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png");
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(imageFilter);
    }

    @FXML
    public void saveButtonClicked() {

    }

    @FXML
    public void generateButtonClicked() {
        ObservableList<ImageView> imageList = imageViewList.getItems();
        if (null == imageList || imageList.isEmpty()) {
            AlertUtil.warning("Please select image!");
            return;
        }
        try {
            File gif = File.createTempFile(String.valueOf(System.currentTimeMillis()), ".gif", new File(App.TEMP_DIR));
            ImageOutputStream output = new FileImageOutputStream(gif);
            int interval = Integer.parseInt(intervalField.getText());
            GifSequenceWriter gifWriter = new GifSequenceWriter(output, BufferedImage.TYPE_3BYTE_BGR, interval, true);
            try {
                for (ImageView imageView : imageList) {
                    gifWriter.writeToSequence(SwingFXUtils.fromFXImage(imageView.getImage(), null));
                }
            } finally {
                gifWriter.close();
                output.close();
            }
            gifView.setImage(new Image(new FileInputStream(gif.getAbsolutePath())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void addButtonClicked() {
        Image image = selectImage();
        if (null == image) {
            return;
        }
        if (imageViewList.getItems().size() > 0) {
            Image firstImage = imageViewList.getItems().get(0).getImage();
            if (image.getWidth() != firstImage.getWidth() || image.getHeight() != firstImage.getHeight()) {
                AlertUtil.warning("Please select the same size picture!");
                return;
            }
        }
        ImageView imageView = new ImageView();
        imageView.fitWidthProperty().bind(imageViewList.widthProperty().subtract(30));
        imageView.setFitHeight(400);
        imageView.setImage(image);
        imageViewList.getItems().add(imageView);
    }

    private Image selectImage() {
        Stage stage = App.STAGE_MAP.get(StageKey.GIF_GENERATE);
        File file = fileChooser.showOpenDialog(stage);
        if (null == file) {
            return null;
        }
        try {
            return new Image(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            return null;
        }
    }
}
