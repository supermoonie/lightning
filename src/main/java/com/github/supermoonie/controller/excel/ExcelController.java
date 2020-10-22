package com.github.supermoonie.controller.excel;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author supermoonie
 * @since 2020/9/2
 */
public class ExcelController implements Initializable {

    @FXML
    public TableView<Map<String, Object>> tableView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
