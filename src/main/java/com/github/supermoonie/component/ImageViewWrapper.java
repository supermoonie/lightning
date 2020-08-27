package com.github.supermoonie.component;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * @author supermoonie
 * @date 2020-08-27
 */
public class ImageViewWrapper extends ImageView {

    private String filePath;

    public ImageViewWrapper(String filePath) {
        this.filePath = filePath;
    }

    public ImageViewWrapper(String s, String filePath) {
        super(s);
        this.filePath = filePath;
    }

    public ImageViewWrapper(Image image, String filePath) {
        super(image);
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
