package com.github.supermoonie;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Box;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author supermoonie
 * @since 2020/8/23
 */
public class Test extends Application {

    public static int[] getRGB(BufferedImage image, int x, int y, int width, int height, int[] pixels) {
        int type = image.getType();
        if (type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB) {
            return (int[]) image.getRaster().getDataElements(x, y, width, height, pixels);
        }
        return image.getRGB(x, y, width, height, pixels, 0, width);
    }

    public static int[][] from(int width, int height, int[] pixels) {
        int[][] mat = new int[width][height];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                mat[col][row] = pixels[row * width + col];
            }
        }
        return mat;
    }

    public static void main(String[] args) throws IOException {

    }

    @Override
    public void start(Stage stage) throws Exception {
        VBox box = new VBox();
        ImageView imageView = new ImageView();

        box.getChildren().add(imageView);

        Button localLoadButton = new Button("Local load");
        Button externalLoadButton = new Button("URL Load");

        localLoadButton.setOnAction(e -> {
            imageView.setImage(new Image(this.getClass().getResource("/315a4d91-d204-4ba1-ad3a-a2d9240cd051426965260710922243.gif").toExternalForm()));
        });

        externalLoadButton.setOnAction(e -> {
            String imageSource = "http://pic.17qq.com/img_biaoqing/68341470.jpeg";
            try {
                imageView.setImage(createImage(imageSource));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        FlowPane buttonPane = new FlowPane();
        buttonPane.setAlignment(Pos.CENTER);
        buttonPane.getChildren().addAll(localLoadButton, externalLoadButton);

        box.getChildren().add(buttonPane);

        stage.setScene(new Scene(box, 500, 400));
        stage.show();
    }

    private Image createImage(String url) throws IOException {
        // You have to set an User-Agent in case you get HTTP Error 403
        // respond while you trying to get the Image from URL.
        URLConnection conn = new URL(url).openConnection();
        conn.setRequestProperty("User-Agent", "Wget/1.13.4 (linux-gnu)");

        try (InputStream stream = conn.getInputStream()) {
            return new Image(stream);
        }
    }
}
