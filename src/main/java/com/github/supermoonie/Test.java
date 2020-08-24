package com.github.supermoonie;

import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.Sensors;

import java.util.Arrays;

/**
 * @author supermoonie
 * @since 2020/8/23
 */
public class Test {

    public static void main(String[] args) throws InterruptedException {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardware = systemInfo.getHardware();
        Sensors sensors = hardware.getSensors();
        for (int i = 0; i < 10; i ++) {
            double cpuTemperature = sensors.getCpuTemperature();
            System.out.println(cpuTemperature);
            int[] fanSpeeds = sensors.getFanSpeeds();
            System.out.println(Arrays.toString(fanSpeeds));
            Thread.sleep(1000L);
        }

    }
}
