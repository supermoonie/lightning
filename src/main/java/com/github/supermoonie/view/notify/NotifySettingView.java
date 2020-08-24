package com.github.supermoonie.view.notify;

import com.github.supermoonie.component.NumberField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.util.concurrent.TimeUnit;

/**
 * @author supermoonie
 * @since 2020/8/23
 */
public abstract class NotifySettingView implements Initializable {

    @FXML
    public Text title;

    @FXML
    public TextField titleField;

    @FXML
    public TextField contentField;

    @FXML
    public NumberField periodField;

    @FXML
    public ComboBox<TimeUnit> timeUnitComboBox;

    @FXML
    public CheckBox isLoopBox;

    @FXML
    public Button okButton;

    @FXML
    public Button cancelButton;

}
