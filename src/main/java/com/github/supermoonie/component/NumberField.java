package com.github.supermoonie.component;

import javafx.scene.control.TextField;


/**
 * @author supermoonie
 * @since 2020/8/23
 */
public class NumberField extends TextField {

    private Long max = Long.MAX_VALUE;

    private Long min = 0L;

    public NumberField() {
        textProperty().addListener((observable, oldValue, newValue) -> {
            if ("".equals(newValue)) {
                setText("");
                return;
            }
            try {
                long value = Long.parseLong(newValue);
                if (value >= min && value <= max) {
                    setText(newValue);
                } else {
                    setText(oldValue);
                }
            } catch (NumberFormatException e) {
                setText(oldValue);
            }
        });
    }

    public Long getMax() {
        return max;
    }

    public void setMax(Long max) {
        this.max = max;
    }

    public Long getMin() {
        return min;
    }

    public void setMin(Long min) {
        this.min = min;
    }
}
