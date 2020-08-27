package com.github.supermoonie.view.gif;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;

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
    public ListView<ImageView> gifImageViewList;

    @FXML
    public Image addButtonImage;

    @FXML
    public Button addButton;

    @FXML
    public TextField intervalField;

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

    @FXML
    protected FileChooser fileChooser;

    protected File gif;

}
