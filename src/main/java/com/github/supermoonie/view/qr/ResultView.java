package com.github.supermoonie.view.qr;

import com.github.supermoonie.model.QrResult;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * @author supermoonie
 * @since 2020/8/22
 */
public abstract class ResultView implements Initializable {

    @FXML
    public TableView<QrResult> qrTableView;

    @FXML
    public TableColumn<QrResult, String> resultColumn;

    @FXML
    public TableColumn<QrResult, String> scanDateColumn;

    @FXML
    public Button clearButton;
}
