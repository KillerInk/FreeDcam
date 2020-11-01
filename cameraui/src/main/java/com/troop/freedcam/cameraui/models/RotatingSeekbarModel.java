package com.troop.freedcam.cameraui.models;

public class RotatingSeekbarModel extends VisibilityEnableModel {
    private int progress = 0;
    private String[] values;

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getProgress() {
        return progress;
    }

    public void setValues(String[] values) {
        this.values = values;
    }

    public String[] getValues() {
        return values;
    }
}
