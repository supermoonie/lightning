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
import javafx.scene.control.ListCell;
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
            imageViewList.prefHeightProperty().bind(stage.heightProperty().subtract(20));
        }
    }

    @FXML
    public void generateButtonClicked() {
        ObservableList<ImageView> imageList = imageViewList.getItems();
        if (null == imageList || imageList.isEmpty()) {
            AlertUtil.warning("Please select image!");
            return;
        }
        try {
            File gif = File.createTempFile(UUID.randomUUID().toString() ,".gif");
//            gif.deleteOnExit();

            ImageOutputStream output = new FileImageOutputStream(gif);
            GifSequenceWriter gifWriter = new GifSequenceWriter(output, BufferedImage.TYPE_3BYTE_BGR, Integer.parseInt(intervalField.getText()), true);
            try {
                for (ImageView imageView : imageList) {
                    gifWriter.writeToSequence(SwingFXUtils.fromFXImage(imageView.getImage(), null));
                }
            } finally {
                gifWriter.close();
                output.close();
            }
            System.out.println(gif.getAbsolutePath());
            gifView.setFitWidth(120);
            gifView.setPreserveRatio(true);
            gifView.setSmooth(true);
            gifView.setCache(true);
            Platform.runLater(() -> {
                try {
                    gifView.setImage(new Image(new FileInputStream(gif.getAbsolutePath())));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            });
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
        ImageView imageView = new ImageView();
        imageView.setFitWidth(imageViewList.getWidth());
        imageView.fitWidthProperty().bind(imageViewList.widthProperty());
        imageView.setFitHeight(400);
        imageView.setImage(image);
        imageViewList.getItems().add(imageView);
    }

    private Image selectImage() {
        FileChooser.ExtensionFilter imageFilter
                = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(imageFilter);
        File file = fileChooser.showOpenDialog(App.primaryStage);
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
