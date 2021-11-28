package com.example.timetosleep;
/**
 *@author TAO Yiduo
 *@version 1.0
 * ESIGELEC-ISE-OC
 */
public class Info {
    private String appName;
    private String lastTime;
    private String Duration;

    public Info(String appName, String startTime, String duration) {
        this.appName = appName;
        this.lastTime = startTime;
        Duration = duration;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getStartTime() {
        return lastTime;
    }

    public void setStartTime(String startTime) {
        this.lastTime = startTime;
    }

    public String getDuration() {
        return Duration;
    }

    public void setDuration(String duration) {
        Duration = duration;
    }
}
