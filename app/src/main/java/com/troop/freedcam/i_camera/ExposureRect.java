package com.troop.freedcam.i_camera;

/**
 * Created by George on 1/21/2015.
 */
public class ExposureRect {
    public int left;
    public int right;
    public int bottom;
    public int top;

    public ExposureRect(){};

    public ExposureRect(int left, int rigt, int top, int bottom)
    {
        this.left =left;
        this.right = rigt;
        this.top  = top;
        this.bottom = bottom;
    }
}
