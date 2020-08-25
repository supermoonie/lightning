package com.github.supermoonie.controller.qr;

import com.github.supermoonie.App;
import com.github.supermoonie.constant.StageKey;
import com.github.supermoonie.util.ClipboardUtil;
import com.github.supermoonie.view.qr.ResultView;
import com.sun.javafx.PlatformUtil;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author supermoonie
 * @since 2020/8/22
 */
public class ResultController extends ResultView {

    final KeyCodeCombination macKeyCodeCopy = new KeyCodeCombination(KeyCode.C, KeyCombination.META_DOWN);

    final KeyCodeCombination winKeyCodeCopy = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Stage stage = App.STAGE_MAP.get(StageKey.QR_RESULT);
        qrTableView.prefWidthProperty().bind(stage.widthProperty());
        if (PlatformUtil.isWindows()) {
            qrTableView.prefHeightProperty().bind(stage.heightProperty().subtract(70));
        } else {
            qrTableView.prefHeightProperty().bind(stage.heightProperty().subtract(60));
        }
    }

    @FXML
    public void onKeyPressed(KeyEvent event) {
        if (macKeyCodeCopy.match(event) || winKeyCodeCopy.match(event)) {
            ClipboardUtil.copySelectionToClipboard(qrTableView, List.of(0));
        }
    }

    public void onClearButtonClicked() {
        qrTableView.getItems().clear();
    }
}
