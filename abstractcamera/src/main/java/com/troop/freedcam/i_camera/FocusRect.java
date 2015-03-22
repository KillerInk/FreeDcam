package com.troop.freedcam.i_camera;

/**
 * Created by troop on 07.01.2015.
 */
public class FocusRect
{
    public int left;
    public int right;
    public int bottom;
    public int top;

    public FocusRect(){};

    public FocusRect(int left, int rigt, int top, int bottom)
    {
        this.left =left;
        this.right = rigt;
        this.top  = top;
        this.bottom = bottom;
    }

}
