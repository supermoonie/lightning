package com.github.supermoonie;

import com.github.supermoonie.constant.StageKey;
import com.github.supermoonie.controller.qr.ResultController;
import com.github.supermoonie.imageproc.AppendFilter;
import com.github.supermoonie.model.NotifySetting;
import com.github.supermoonie.model.QrResult;
import com.github.supermoonie.service.QrService;
import com.github.supermoonie.util.AlertUtil;
import com.github.supermoonie.util.MenuUtil;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.sun.javafx.PlatformUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.io.FileUtils;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.Sensors;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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

    private final SystemInfo systemInfo = new SystemInfo();

    @Override
    public void start(Stage primaryStage) throws Exception {

        Platform.setImplicitExit(false);
        boolean addedAppToTray = addAppToTray();
        if (!addedAppToTray) {
            Platform.exit();
            return;
        }
        if (!initLighting()) {
            AlertUtil.warning("Initial Failed");
            Platform.exit();
            return;
        }
        App.primaryStage = primaryStage;
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setFullScreenExitHint("");
        STAGE_MAP.put(StageKey.PRIMARY, primaryStage);
        App.scene = new Scene(new VBox());
        App.scene.setCursor(Cursor.CROSSHAIR);
        App.primaryStage.setScene(scene);
        setCommonIcon(App.primaryStage);
    }

    private void showQrGeneratePane() {
        Stage stage = STAGE_MAP.computeIfAbsent(StageKey.QR_GENERATE, stageKey -> {
            Stage s = new Stage();
            s.setMinWidth(800);
            s.setMinHeight(600);
            s.setResizable(true);
            s.setOnHiding(windowEvent -> STAGE_MAP.remove(StageKey.QR_GENERATE));
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
        stage.show();
        stage.toFront();
    }

    private void showGifGenerateView() {
        Stage stage = STAGE_MAP.computeIfAbsent(StageKey.GIF_GENERATE, stageKey -> {
            Stage s = new Stage();
            setCommonIcon(s);
            s.setMinWidth(600);
            s.setMinHeight(820);
            s.setResizable(true);
            s.setOnHiding(windowEvent -> STAGE_MAP.remove(StageKey.GIF_GENERATE));
            return s;
        });
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/ui/image/GifGenerate.fxml"));
            Parent parent = fxmlLoader.load();
            Scene scene = new Scene(parent);
            scene.getStylesheets().add("/style/image/GifGenerate.css");
            stage.setScene(scene);
            fxmlLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.error(e);
            return;
        }
        stage.show();
        stage.toFront();
        stage.requestFocus();
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
        URL iconUrl;
        if (PlatformUtil.isWindows()) {
            iconUrl = getClass().getResource("/lightning-win.png");
        } else {
            iconUrl = getClass().getResource("/lightning-mac.png");
        }
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

        NOTIFY_EXECUTOR.scheduleAtFixedRate(() -> {
//            Font font = new Font("Times New Roman", Font.PLAIN, 60);
            Font font = new Font("", Font.PLAIN, 60);
//            System.out.println("ow: " + icon.getWidth() + ", oh: " + icon.getHeight());
            BufferedImage bi = new BufferedImage(icon.getWidth() * 6, icon.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = (Graphics2D) bi.getGraphics();

            g2.setComposite(AlphaComposite.Clear);
            g2.fillRect(0, 0, icon.getWidth() * 6, icon.getHeight());

            g2.setColor(Color.darkGray);
            g2.setComposite(AlphaComposite.Src);
            HardwareAbstractionLayer hardware = systemInfo.getHardware();
            Sensors sensors = hardware.getSensors();
            double cpuTemperature = sensors.getCpuTemperature();
            int[] fanSpeeds = sensors.getFanSpeeds();
//            drawCenteredString(g2, s, new Rectangle(icon.getWidth() * 6, icon.getHeight()), font);
            drawTwoLine(g2, String.valueOf((int) cpuTemperature), Arrays.toString(fanSpeeds), new Rectangle(icon.getWidth() * 6, icon.getHeight()), font);
            AppendFilter appendFilter = new AppendFilter(icon);
            BufferedImage bufferedImage = appendFilter.appendRight(bi);
//            System.out.println("w: " + bufferedImage.getWidth() + ", h: " + bufferedImage.getHeight());
            trayIcon.setImage(bufferedImage);
            g2.dispose();
        }, 1000, 1000, TimeUnit.MILLISECONDS);
        popupMenu.add(initQrMenu());
        popupMenu.addSeparator();
        popupMenu.add(initRestMenu());
        popupMenu.addSeparator();
        popupMenu.add(initImageMenu());
        popupMenu.addSeparator();
        popupMenu.add(quitItem);


        trayIcon.setPopupMenu(popupMenu);

        tray.add(trayIcon);
    }

    public void drawTwoLine(Graphics g, String line1, String lin2, Rectangle rect, Font font) {
        // Get the FontMetrics
        FontMetrics metrics = g.getFontMetrics(font);
        // Determine the X coordinate for the text
        int x = rect.x + (rect.width - metrics.stringWidth(line1)) / 2;
        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent() + 5;
        int yy = (rect.height / 2 - y) / 2;
        // Set the font
        g.setFont(font);
        // Draw the String
        g.drawString(line1 + " ℃", 10, 60);
        g.drawString(lin2 + " rpm", 10, 120);
    }

    /**
     * Draw a String centered in the middle of a Rectangle.
     *
     * @param g    The Graphics instance.
     * @param text The String to draw.
     * @param rect The Rectangle to center the text in.
     */
    public void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
        // Get the FontMetrics
        FontMetrics metrics = g.getFontMetrics(font);
        // Determine the X coordinate for the text
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent() + 5;
        // Set the font
        g.setFont(font);
        // Draw the String
        g.drawString(text, x, y);
    }

    private Menu initImageMenu() {
        Menu imageMenu = new Menu("Image ->");
        MenuItem generateGifFromImg = new MenuItem("Gif From Img ...");
        generateGifFromImg.addActionListener(event -> Platform.runLater(this::showGifGenerateView));
        MenuItem oneTap = new MenuItem("One Tap ...");
        oneTap.addActionListener(event -> Platform.runLater(() -> {

        }));
        imageMenu.add(generateGifFromImg);
        imageMenu.addSeparator();
//        imageMenu.add(oneTap);
        return imageMenu;
    }

    private Menu initQrMenu() {
        Menu qrMenu = new Menu("QR ->");
//        MenuItem captureItem = new MenuItem("Capture");
//        captureItem.addActionListener(event -> Platform.runLater(() -> {
//            Robot robot = new Robot();
//            WritableImage image = robot.getScreenCapture(null, Screen.getPrimary().getBounds(), true);
//            Rectangle2D bounds = Screen.getPrimary().getBounds();
//            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber("1:none");
//            grabber.setFormat("avfoundation");
//            grabber.setImageWidth(Double.valueOf(bounds.getWidth()).intValue());
//            grabber.setImageHeight(Double.valueOf(bounds.getHeight()).intValue());
//            Image image;
//            try {
//                grabber.start();
//                Frame frame = grabber.grab();
//                JavaFXFrameConverter converter = new JavaFXFrameConverter();
//                image = converter.convert(frame);
//                grabber.stop();
//            } catch (FrameGrabber.Exception e) {
//                e.printStackTrace();
//                return;
//            }
//
//            primaryStage.setWidth(bounds.getWidth());
//            primaryStage.setHeight(bounds.getHeight());
//            ImageView imageView = new ImageView(image);
//            final AnchorPane hBox = new AnchorPane(imageView);
//            scene.setRoot(hBox);
//            AtomicReference<Double> start = new AtomicReference<>((double) 0);
//            AtomicReference<Double> end = new AtomicReference<>((double) 0);
//            scene.setOnMousePressed(mouseEvent -> {
//                start.set(mouseEvent.getX());
//                end.set(mouseEvent.getY());
//                System.out.println("start: " + start.get() + ", end: " + end.get());
//            });
//            scene.setOnMouseDragged(mouseEvent -> {
//                final Rectangle rect = new Rectangle();
//                rect.setFill(null);
//                rect.setStroke(Color.web("firebrick", 0.4));
//                rect.setX(start.get());
//                rect.setY(end.get());
//                rect.setWidth(mouseEvent.getX() - start.get());
//                rect.setHeight(mouseEvent.getY() - end.get());
//                hBox.getChildren().clear();
//                hBox.getChildren().add(imageView);
//                hBox.getChildren().add(rect);
//                rect.toFront();
//            });
//            scene.setOnMouseReleased(mouseEvent -> {
//                System.out.println("start: " + start.get() + ", end: " + end.get());
//
//            });
////            primaryStage.setFullScreen(true);
//            primaryStage.show();
//            primaryStage.setAlwaysOnTop(true);
//            primaryStage.toFront();
//        }));
        MenuItem scanQrItem = new MenuItem("Scan");
        scanQrItem.addActionListener(event -> Platform.runLater(this::scanQr));
        MenuItem generateQrItem = new MenuItem("Generate ...");
        generateQrItem.addActionListener(event -> Platform.runLater(this::showQrGeneratePane));
        MenuItem qrResultItem = new MenuItem("Result ...");
        qrResultItem.addActionListener(event -> Platform.runLater(this::showQrTableView));
//        qrMenu.add(captureItem);
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

    public static final String USER_HOME = System.getProperty("user.home");

    public static final String LIGHTNING_HOME = USER_HOME + File.separator + ".lightning";

    public static final String TEMP_DIR = LIGHTNING_HOME + File.separator + "temp";

    private boolean initLighting() throws IOException {
        File lightningHome = new File(LIGHTNING_HOME);
        File tempDir = new File(TEMP_DIR);
        if (!lightningHome.exists() && !lightningHome.mkdir()) {
            AlertUtil.warning("Initial Failed!");
            return false;
        }
        if (tempDir.exists()) {
            FileUtils.deleteDirectory(tempDir);
        }
        if (!tempDir.mkdir()) {
            AlertUtil.warning("Initial Failed!");
            return false;
        }
        return true;
    }

    private void setCommonIcon(Stage stage) {
        URL iconUrl;
        if (PlatformUtil.isWindows()) {
            iconUrl = getClass().getResource("/lightning-win.png");
        } else {
            iconUrl = getClass().getResource("/lightning-mac.png");
        }
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
