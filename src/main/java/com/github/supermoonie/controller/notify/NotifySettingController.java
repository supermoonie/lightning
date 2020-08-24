package com.github.supermoonie.controller.notify;

import com.github.supermoonie.App;
import com.github.supermoonie.constant.StageKey;
import com.github.supermoonie.model.NotifySetting;
import com.github.supermoonie.util.AlertUtil;
import com.github.supermoonie.view.notify.NotifySettingView;
import javafx.fxml.FXML;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

/**
 * @author supermoonie
 * @since 2020/8/23
 */
public class NotifySettingController extends NotifySettingView {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        timeUnitComboBox.getItems().add(TimeUnit.SECONDS);
        timeUnitComboBox.getItems().add(TimeUnit.MINUTES);
        timeUnitComboBox.getItems().add(TimeUnit.HOURS);
        NotifySetting notifySetting = NotifySetting.INSTANCE;
        titleField.setText(notifySetting.getTitle());
        contentField.setText(notifySetting.getContent());
        periodField.setText(String.valueOf(notifySetting.getPeriod()));
        timeUnitComboBox.setValue(notifySetting.getTimeUnit());
        isLoopBox.setSelected(notifySetting.isLoop());
    }

    @FXML
    public void onOkButtonClicked() {
        String title = titleField.getText();
        if (null == title || "".equals(title.trim())) {
            AlertUtil.warning("Title Is Empty !");
            return;
        }
        String content = contentField.getText();
        if (null == content || "".equals(content.trim())) {
            AlertUtil.warning("Content Is Empty !");
            return;
        }
        String period = periodField.getText();
        if (null == period || "".equals(period.trim())) {
            AlertUtil.warning("Period Is Empty !");
            return;
        }
        NotifySetting notifySetting = NotifySetting.INSTANCE;
        notifySetting.setTitle(title);
        notifySetting.setContent(content);
        notifySetting.setPeriod(Integer.parseInt(period));
        notifySetting.setTimeUnit(timeUnitComboBox.getValue());
        notifySetting.setLoop(isLoopBox.isSelected());
        Stage stage = App.STAGE_MAP.get(StageKey.NOTIFY_SETTING);
        stage.setUserData(true);
        stage.hide();
    }

    @FXML
    public void onCancelButtonClicked() {
        Stage stage = App.STAGE_MAP.get(StageKey.NOTIFY_SETTING);
        stage.setUserData(false);
        stage.hide();
    }
}
