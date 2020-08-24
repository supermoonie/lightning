package com.github.supermoonie;

import com.github.supermoonie.constant.StageKey;
import com.github.supermoonie.controller.qr.ResultController;
import com.github.supermoonie.model.NotifySetting;
import com.github.supermoonie.model.QrResult;
import com.github.supermoonie.service.QrService;
import com.github.supermoonie.util.AlertUtil;
import com.github.supermoonie.util.MenuUtil;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Hello world!
 *
 * @author supermoonie
 */
public class App extends Application {

    public static Stage primaryStage;

    public static final Map<StageKey, Stage> STAGE_MAP = new ConcurrentHashMap<>();

    public static Scene scene;

    public static SystemTray tray;

    public static final Map<String, Future<?>> JOB_MAP = new ConcurrentHashMap<>();
    public static final ScheduledExecutorService NOTIFY_EXECUTOR = new ScheduledThreadPoolExecutor(2);

    public ResultController qrResultController;

    private Parent qrResultParent;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Platform.setImplicitExit(false);
        boolean addedAppToTray = addAppToTray();
        if (!addedAppToTray) {
            Platform.exit();
            return;
        }
        App.primaryStage = primaryStage;
        STAGE_MAP.put(StageKey.PRIMARY, primaryStage);
        App.scene = new Scene(new VBox());
        App.primaryStage.setScene(scene);
        setCommonIcon(App.primaryStage);
    }

    private void showQrGeneratePane() {
        Stage stage = STAGE_MAP.computeIfAbsent(StageKey.QR_GENERATE, stageKey -> {
            Stage s = new Stage();
            s.setOnHidden(windowEvent -> STAGE_MAP.remove(StageKey.QR_GENERATE));
            return s;
        });
        try {
            setCommonIcon(stage);
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/ui/qr/Generate.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.error(e);
            return;
        }
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setResizable(true);
        stage.show();
        stage.toFront();
    }

    private void showQrTableView() {
        Stage stage = STAGE_MAP.computeIfAbsent(StageKey.QR_RESULT, stageKey -> new Stage());
        if (null == qrResultParent) {
            setCommonIcon(stage);
            stage.setMinWidth(400);
            stage.setMinHeight(300);
            stage.setResizable(true);
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/ui/qr/Result.fxml"));
                qrResultParent = fxmlLoader.load();
                stage.setScene(new Scene(qrResultParent));
                qrResultController = fxmlLoader.getController();
            } catch (IOException e) {
                AlertUtil.error(e);
                return;
            }
        }
        stage.show();
        stage.toFront();
    }

    private Boolean showNotifySettingView() {
        Stage stage = STAGE_MAP.get(StageKey.NOTIFY_SETTING);
        if (null != stage) {
            stage.toFront();
            if (!stage.isShowing()) {
                stage.showAndWait();
                return (Boolean) stage.getUserData();
            }
            return null;
        }
        stage = new Stage();
        STAGE_MAP.put(StageKey.NOTIFY_SETTING, stage);
        setCommonIcon(stage);
        stage.setMinWidth(300);
        stage.setMinHeight(200);
        stage.setResizable(false);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/ui/notify/NotifySetting.fxml"));
            Parent root = fxmlLoader.load();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            AlertUtil.error(e);
            return null;
        }
        stage.toFront();
        stage.showAndWait();
        return (Boolean) stage.getUserData();
    }

    private boolean addAppToTray() {
        if (!SystemTray.isSupported()) {
            AlertUtil.warning("This System Not Support!");
            return false;
        }
        try {
            tray = SystemTray.getSystemTray();
            initTray(tray);
            return true;
        } catch (Exception e) {
            AlertUtil.error(new IllegalStateException("Add Tray Failed ! " + e.getMessage()));
            return false;
        }
    }

    private void initTray(SystemTray tray) throws IOException, AWTException {
        URL iconUrl = App.class.getClassLoader().getResource("lightning.png");
        assert null != iconUrl;
        BufferedImage icon = ImageIO.read(iconUrl);
        TrayIcon trayIcon = new TrayIcon(icon);
        trayIcon.setToolTip("Lightning");
        PopupMenu popupMenu = new PopupMenu();

        MenuItem quitItem = new MenuItem("Quit");
        quitItem.addActionListener(event -> {
            NOTIFY_EXECUTOR.shutdownNow();
            tray.remove(trayIcon);
            Platform.runLater(Platform::exit);
        });

        popupMenu.add(initQrMenu());
        popupMenu.addSeparator();
        popupMenu.add(initRestMenu());
        popupMenu.addSeparator();
        popupMenu.add(quitItem);

        trayIcon.setPopupMenu(popupMenu);

        tray.add(trayIcon);
    }

    private Menu initQrMenu() {
        Menu qrMenu = new Menu("QR ->");
        MenuItem scanQrThen = new MenuItem("扫描并核销");
        scanQrThen.addActionListener(event -> {
            Platform.runLater(() -> {
                try {
                    Result result = QrService.scanScreen();
                    String text = result.getText();

                } catch (NotFoundException e) {
                    AlertUtil.warning("未扫描到二维码！");
                }
            });
        });

        MenuItem scanQrItem = new MenuItem("Scan");
        scanQrItem.addActionListener(event -> Platform.runLater(this::scanQr));
        MenuItem generateQrItem = new MenuItem("Generate ...");
        generateQrItem.addActionListener(event -> Platform.runLater(this::showQrGeneratePane));
        MenuItem qrResultItem = new MenuItem("Result ...");
        qrResultItem.addActionListener(event -> Platform.runLater(this::showQrTableView));
        qrMenu.add(scanQrThen);
        qrMenu.add(scanQrItem);
        qrMenu.add(generateQrItem);
        qrMenu.add(qrResultItem);
        return qrMenu;
    }

    private Menu initRestMenu() {
        Menu notifyMenu = new Menu("Have a Rest ->");
        MenuItem everyHalfAnHourItem = new MenuItem("Every Half An Hour");
        MenuItem switchItem = new MenuItem("Start");
        everyHalfAnHourItem.addActionListener(e -> {
            NotifySetting.INSTANCE.setPeriod(30);
            NotifySetting.INSTANCE.setTimeUnit(TimeUnit.MINUTES);
            NotifySetting.INSTANCE.setStop(false);
            NotifySetting.INSTANCE.setLoop(true);
            MenuUtil.activeItem(notifyMenu, 0);
            switchItem.setLabel("Stop");
            scheduleNotify("HALF_AN_HOUR");
        });
        MenuItem everyHourItem = new MenuItem("Every Hour");
        everyHourItem.addActionListener(e -> {
            NotifySetting.INSTANCE.setPeriod(1);
            NotifySetting.INSTANCE.setTimeUnit(TimeUnit.HOURS);
            NotifySetting.INSTANCE.setStop(false);
            NotifySetting.INSTANCE.setLoop(true);
            MenuUtil.activeItem(notifyMenu, 1);
            switchItem.setLabel("Stop");
            scheduleNotify("ONE_HOUR");
        });
        MenuItem everyTwoHourItem = new MenuItem("Every Two Hour");
        everyTwoHourItem.addActionListener(e -> {
            NotifySetting.INSTANCE.setPeriod(2);
            NotifySetting.INSTANCE.setTimeUnit(TimeUnit.HOURS);
            NotifySetting.INSTANCE.setStop(false);
            NotifySetting.INSTANCE.setLoop(true);
            MenuUtil.activeItem(notifyMenu, 2);
            switchItem.setLabel("Stop");
            scheduleNotify("TWO_HOUR");
        });
        MenuItem everyFourHourItem = new MenuItem("Every Four Hour");
        everyFourHourItem.addActionListener(e -> {
            NotifySetting.INSTANCE.setPeriod(4);
            NotifySetting.INSTANCE.setTimeUnit(TimeUnit.HOURS);
            NotifySetting.INSTANCE.setStop(false);
            NotifySetting.INSTANCE.setLoop(true);
            MenuUtil.activeItem(notifyMenu, 3);
            switchItem.setLabel("Stop");
            scheduleNotify("FOUR_HOUR");
        });
        switchItem.addActionListener(e -> {
            if (NotifySetting.INSTANCE.isStop()) {
                NotifySetting.INSTANCE.setStop(false);
                switchItem.setLabel("Stop");
                scheduleNotify("RE_START");
            } else {
                JOB_MAP.forEach((name, future) -> future.cancel(false));
                NotifySetting.INSTANCE.setStop(true);
                switchItem.setLabel("Restart");
            }
        });
        MenuItem settingsItem = new MenuItem("Settings ...");
        settingsItem.addActionListener(event -> Platform.runLater(() -> {
            Boolean ok = showNotifySettingView();
            if (null != ok && ok) {
                switchItem.setLabel("Stop");
                NotifySetting.INSTANCE.setStop(false);
                scheduleNotify("SETTING_JOB");
                MenuUtil.activeItem(notifyMenu, 6);
            }
        }));

        notifyMenu.add(everyHalfAnHourItem);
        notifyMenu.add(everyHourItem);
        notifyMenu.add(everyTwoHourItem);
        notifyMenu.add(everyFourHourItem);
        notifyMenu.addSeparator();
        notifyMenu.add(switchItem);
        notifyMenu.add(settingsItem);
        return notifyMenu;
    }

    private void scanQr() {
        try {
            Result result = QrService.scanScreen();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            QrResult qrResult = new QrResult(result.getText(), dateFormat.format(new Date()));
            showQrTableView();
            qrResultController.qrTableView.getItems().add(0, qrResult);
        } catch (NotFoundException e) {
            AlertUtil.warning("QR Not Found !");
        }
    }

    private <T> T load(String res) throws IOException {
        return load(res, App.primaryStage);
    }

    private <T> T load(String res, Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(res));
        Parent root = fxmlLoader.load();
        assert root != null;
        stage.setScene(new Scene(root));
        return fxmlLoader.getController();
    }

    private void setCommonIcon(Stage stage) {
        URL iconUrl = getClass().getResource("/lightning.png");
        stage.getIcons().add(new Image(iconUrl.toString()));
        stage.setTitle("Lightning");
    }

    public void scheduleNotify(final String jobName) {
        JOB_MAP.forEach((name, future) -> future.cancel(false));
        NotifySetting notifySetting = NotifySetting.INSTANCE;
        ScheduledFuture<?> future;
        if (notifySetting.isLoop()) {
            future = App.NOTIFY_EXECUTOR.scheduleWithFixedDelay(() -> {
                        if (!notifySetting.isStop()) {
                            App.tray.getTrayIcons()[0].displayMessage(notifySetting.getTitle(), notifySetting.getContent(), TrayIcon.MessageType.INFO);
                        }
                    },
                    notifySetting.getPeriod(), notifySetting.getPeriod(), notifySetting.getTimeUnit());
        } else {
            future = App.NOTIFY_EXECUTOR.schedule(() -> {
                        if (!notifySetting.isStop()) {
                            App.tray.getTrayIcons()[0].displayMessage(notifySetting.getTitle(), notifySetting.getContent(), TrayIcon.MessageType.INFO);
                        }
                    },
                    notifySetting.getPeriod(), notifySetting.getTimeUnit());
        }
        JOB_MAP.put(jobName, future);
    }
}
