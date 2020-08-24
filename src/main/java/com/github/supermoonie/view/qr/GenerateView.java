package com.github.supermoonie.view.qr;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * @author supermoonie
 * @since 2020/8/23
 */
public abstract class GenerateView implements Initializable {

    @FXML
    public SplitPane splitPane;

    @FXML
    public VBox leftBox;

    @FXML
    public VBox rightBox;

    @FXML
    public HBox leftButtonBox;

    @FXML
    public HBox rightButtonBox;

    @FXML
    public TextArea qrContentInput;

    @FXML
    public HBox imageViewContainer;

    @FXML
    public ImageView qrImageView;

    @FXML
    public Button generateButton;

    @FXML
    public Button saveButton;

    @FXML
    public ComboBox<Integer> sizeCombobox;
}
