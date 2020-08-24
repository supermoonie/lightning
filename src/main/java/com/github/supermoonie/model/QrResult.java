package com.github.supermoonie.model;

/**
 * @author supermoonie
 * @since 2020/8/22
 */
public class QrResult {

    private String result;

    private String scanDate;

    public QrResult() {
    }

    public QrResult(String result, String scanDate) {
        this.result = result;
        this.scanDate = scanDate;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getScanDate() {
        return scanDate;
    }

    public void setScanDate(String scanDate) {
        this.scanDate = scanDate;
    }
}
