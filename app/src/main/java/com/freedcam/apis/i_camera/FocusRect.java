package com.freedcam.apis.i_camera;

/**
 * Created by troop on 07.01.2015.
 */
public class FocusRect
{
    public int left;
    public int right;
    public int bottom;
    public int top;
    public int x;
    public int y;

    public FocusRect(){}

    public FocusRect(int left, int rigt, int top, int bottom,int x,int y)
    {
        this.left =left;
        this.right = rigt;
        this.top  = top;
        this.bottom = bottom;
        this.x = x;
        this.y = y;
    }

}
