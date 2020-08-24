package com.github.supermoonie.task;

/**
 * @author supermoonie
 * @since 2020/8/23
 */
public interface Job extends Runnable {

    /**
     * return jobName
     *
     * @return job name
     */
    String name();
}
