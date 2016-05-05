package com.freedcam.apis.basecamera.camera;

/**
 * Created by George on 1/21/2015.
 */
public class ExposureRect {
    private int left;
    private int right;
    private int bottom;
    private int top;

    public ExposureRect(){}

    public ExposureRect(int left, int rigt, int top, int bottom)
    {
        this.left =left;
        this.right = rigt;
        this.top  = top;
        this.bottom = bottom;
    }
}
