package com.muedsa.intellij.textReader.ui;

import java.util.ArrayList;
import java.util.List;

public class GraphicsDrawDebugger {

    public static boolean DEBUG = Boolean.getBoolean("debug");

    private final List<Long> frameTimeList = new ArrayList<>();
    private long lastFrameStartTime = 0;
    private long lastFrameEndTime = 0;
    private double avgFrameTime = 0;

    private final List<Double> fpsList = new ArrayList<>();
    private double avgFps = 0;

    public void startFrame() {
        lastFrameStartTime = System.currentTimeMillis();
    }

    public void endFrame() {
        lastFrameEndTime = System.currentTimeMillis();
        long frameTime = lastFrameEndTime - lastFrameStartTime;
        double fps = 1000d / frameTime;
        avgFrameTime = (avgFrameTime * frameTimeList.size() + frameTime) / (frameTimeList.size() + 1);
        avgFps = (avgFps * fpsList.size() + fps) / (fpsList.size() + 1);
        fpsList.add(fps);
    }

    public double getAvgFrameTime() {
        return avgFrameTime;
    }

    public String formatAvgFrameTime(String format) {
        return String.format(format, avgFrameTime);
    }

    public double getAvgFps() {
        return avgFps;
    }

    public String formatAvgFps(String format) {
        return String.format(format, avgFps);
    }
}
