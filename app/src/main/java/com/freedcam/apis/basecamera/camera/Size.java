package com.freedcam.apis.basecamera.camera;

/**
 * Created by troop on 24.08.2015.
 */
public class Size
{
    public int width;
    public int height;
    public Size(int w, int h)
    {
        this.height = h;
        this.width = w;
    }
    public Size(String s)
    {
        String[] split = s.split("x");
        this.height = Integer.parseInt(split[1]);
        this.width = Integer.parseInt(split[0]);
    }

}