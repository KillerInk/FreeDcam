package com.troop.freedcam.utils;

public class Size {

    private int width;
    private int height;

    public Size(int width, int height)
    {
        this.width = width;
        this.height = height;
    }

    public Size(String s)
    {
        String split[] = s.split("x");
        this.width = Integer.parseInt(split[0]);
        this.height =  Integer.parseInt(split[1]);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
