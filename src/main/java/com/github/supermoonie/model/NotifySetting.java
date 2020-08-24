package com.github.supermoonie.model;

import java.util.concurrent.TimeUnit;

/**
 * @author supermoonie
 * @since 2020/8/23
 */
public class NotifySetting {

    public static final NotifySetting INSTANCE = new NotifySetting();

    private String title = "Lightning";

    private String content = "Hello Lightning!";

    private int period = 10;

    private TimeUnit timeUnit = TimeUnit.SECONDS;

    private boolean isLoop = true;

    private boolean stop = true;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public boolean isLoop() {
        return isLoop;
    }

    public void setLoop(boolean loop) {
        isLoop = loop;
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }
}
