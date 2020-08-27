package com.github.supermoonie.view.gif;

import com.github.supermoonie.component.ImageViewWrapper;
import com.github.supermoonie.component.NumberField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

/**
 * @author supermoonie
 * @date 2020-08-25
 */
public abstract class GifGenerateView implements Initializable {

    @FXML
    public SplitPane splitPane;

    @FXML
    public VBox leftBox;

    @FXML
    public BorderPane rightBox;

    @FXML
    public ListView<ImageView> imageViewList;

    @FXML
    public ListView<ImageViewWrapper> gifImageViewList;

    @FXML
    public Image addButtonImage;

    @FXML
    public Button addButton;

    @FXML
    public NumberField intervalField;

    @FXML
    public CheckBox isLoopBox;

    @FXML
    public Button generateButton;

    @FXML
    public Button saveAllButton;

    @FXML
    public Button saveButton;

    @FXML
    public Button copyButton;

}
