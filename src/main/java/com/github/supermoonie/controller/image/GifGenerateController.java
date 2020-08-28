package com.github.supermoonie.controller.image;

import com.github.supermoonie.App;
import com.github.supermoonie.component.ImageViewWrapper;
import com.github.supermoonie.constant.StageKey;
import com.github.supermoonie.io.GifSequenceWriter;
import com.github.supermoonie.util.AlertUtil;
import com.github.supermoonie.util.ClipboardUtil;
import com.github.supermoonie.view.image.GifGenerateView;
import com.sun.javafx.PlatformUtil;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;

import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author supermoonie
 * @date 2020-08-25
 */
public class GifGenerateController extends GifGenerateView {

    public GifGenerateController() {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Stage stage = App.STAGE_MAP.get(StageKey.GIF_GENERATE);
        splitPane.prefWidthProperty().bind(stage.widthProperty());
        splitPane.prefHeightProperty().bind(stage.heightProperty());
        HBox.setHgrow(addButton, Priority.ALWAYS);
        addButton.setMaxWidth(Double.MAX_VALUE);
        initImageListContentMenu();
        initGifContentMenu();
        if (PlatformUtil.isWindows()) {
            imageViewList.prefHeightProperty().bind(stage.heightProperty().subtract(64));
        } else {
            imageViewList.prefHeightProperty().bind(stage.heightProperty().subtract(50));
        }
        BorderPane.setAlignment(rightBox, Pos.CENTER);
    }

    private void initImageListContentMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Delete");
        deleteItem.setOnAction(event -> {
            int selectedIndex = imageViewList.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                imageViewList.getItems().remove(selectedIndex);
            }
        });
        MenuItem replaceItem = new MenuItem("Replace");
        replaceItem.setOnAction(event -> {
            List<Image> images = selectImage();
            if (null == images || images.size() == 0) {
                return;
            }
            imageViewList.getSelectionModel().getSelectedItem().setImage(images.get(0));
            for (int i = 1; i < images.size(); i++) {
                imageViewList.getItems().add(new ImageView(images.get(i)));
            }
        });
        MenuItem clearItem = new MenuItem("Clear");
        clearItem.setOnAction(event -> imageViewList.getItems().clear());
        contextMenu.getItems().addAll(replaceItem, deleteItem, clearItem);
        imageViewList.setContextMenu(contextMenu);
    }

    private void initGifContentMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Delete");
        deleteItem.setOnAction(event -> {
            int selectedIndex = gifImageViewList.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                gifImageViewList.getItems().remove(selectedIndex);
            }
        });
        MenuItem copyItem = new MenuItem("Copy");
        copyItem.setOnAction(event -> {
            ImageViewWrapper selectedItem = gifImageViewList.getSelectionModel().getSelectedItem();
            if (null != selectedItem) {
                ClipboardUtil.copyFile(List.of(new File(selectedItem.getFilePath())));
                AlertUtil.info("Copy Success!");
            }
        });
        contextMenu.getItems().addAll(copyItem, deleteItem);
        gifImageViewList.setContextMenu(contextMenu);
    }

    @FXML
    public void onCopyButtonClicked() {
        ObservableList<ImageViewWrapper> items = gifImageViewList.getItems();
        if (null == items || items.size() == 0) {
            AlertUtil.warning("Please Generate One gif!");
            return;
        }
        ImageViewWrapper selectedItem = gifImageViewList.getSelectionModel().getSelectedItem();
        if (null == selectedItem) {
            AlertUtil.warning("Please Select One gif!");
            return;
        }
        ClipboardUtil.copyFile(List.of(new File(selectedItem.getFilePath())));
        AlertUtil.info("Copy Success!");
    }

    @FXML
    public void saveAllButtonClicked() {
        ObservableList<ImageViewWrapper> items = gifImageViewList.getItems();
        if (null == items || items.size() == 0) {
            return;
        }
        Stage stage = App.STAGE_MAP.get(StageKey.GIF_GENERATE);
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File dir = directoryChooser.showDialog(stage);
        if (null != dir && dir.exists()) {
            try {
                for (int i = 0; i < items.size(); i++) {
                    ImageViewWrapper imageView = items.get(i);
                    File gif = new File(imageView.getFilePath());
                    FileUtils.copyFile(gif, new File(dir.getAbsolutePath() + File.separator + (i + 1) + ".gif"));
                }
            } catch (IOException e) {
                AlertUtil.error(e);
            }
        }
    }

    @FXML
    public void saveButtonClicked() {
        ImageViewWrapper selectedItem = gifImageViewList.getSelectionModel().getSelectedItem();
        if (null == selectedItem) {
            AlertUtil.warning("Please Select One gif!");
            return;
        }
        Stage stage = App.STAGE_MAP.get(StageKey.GIF_GENERATE);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(System.currentTimeMillis() + ".gif");
        File file = fileChooser.showSaveDialog(stage);
        if (null != file) {
            try {
                File gif = new File(selectedItem.getFilePath());
                FileUtils.copyFile(gif, file);
            } catch (IOException e) {
                AlertUtil.error(e);
            }
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
            File gif = File.createTempFile(String.valueOf(System.currentTimeMillis()), ".gif", new File(App.TEMP_DIR));
            ImageOutputStream output = new FileImageOutputStream(gif);
            int interval = Integer.parseInt(intervalField.getText());
            GifSequenceWriter gifWriter = new GifSequenceWriter(output, BufferedImage.TYPE_3BYTE_BGR, interval, isLoopBox.isSelected());
            try {
                for (ImageView imageView : imageList) {
                    gifWriter.writeToSequence(SwingFXUtils.fromFXImage(imageView.getImage(), null));
                }
            } finally {
                gifWriter.close();
                output.close();
            }
            Image image = new Image(new FileInputStream(gif.getAbsolutePath()));
            ImageViewWrapper imageViewWrapper = new ImageViewWrapper(image, gif.getAbsolutePath());
            gifImageViewList.getItems().add(imageViewWrapper);
            gifImageViewList.scrollTo(gifImageViewList.getItems().size() - 1);
        } catch (IOException e) {
            AlertUtil.error(e);
        }
    }

    @FXML
    public void addButtonClicked() {
        List<Image> images = selectImage();
        if (null == images || images.size() == 0) {
            return;
        }
        Image first = images.get(0);
        double position = Math.min((first.getWidth() + 28) / splitPane.getWidth(), 0.5);
        splitPane.setDividerPositions(position);
        imageViewList.setMinWidth(position * splitPane.getWidth());
        for (Image image : images) {
            ImageView imageView = new ImageView();
            imageView.setFitWidth(image.getWidth());
            imageView.setFitHeight(image.getHeight());
            imageView.setImage(image);
            imageViewList.getItems().add(imageView);
        }
        imageViewList.scrollTo(imageViewList.getItems().size() - 1);
    }

    private List<Image> selectImage() {
        Stage stage = App.STAGE_MAP.get(StageKey.GIF_GENERATE);
        FileChooser.ExtensionFilter imageFilter
                = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(imageFilter);
        List<File> files = fileChooser.showOpenMultipleDialog(stage);
        if (null == files || files.size() == 0) {
            return null;
        }
        try {
            List<Image> imageList = new ArrayList<>();
            Image first = null;
            for (File file : files) {
                Image image = new Image(new FileInputStream(file));
                if (null == first) {
                    first = image;
                } else {
                    if (image.getWidth() != first.getWidth() || image.getHeight() != first.getHeight()) {
                        AlertUtil.warning("Please select the same size picture!");
                        return null;
                    }
                }
                if (imageViewList.getItems().size() > 0) {
                    Image firstImage = imageViewList.getItems().get(0).getImage();
                    if (image.getWidth() != firstImage.getWidth() || image.getHeight() != firstImage.getHeight()) {
                        AlertUtil.warning("Please select the same size picture!");
                        return null;
                    }
                }

                imageList.add(image);
            }
            return imageList;
        } catch (FileNotFoundException e) {
            return null;
        }
    }
}
