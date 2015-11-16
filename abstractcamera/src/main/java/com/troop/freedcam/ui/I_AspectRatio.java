package com.troop.freedcam.ui;

import android.graphics.Canvas;
import android.view.TextureView;

/**
 * Created by troop on 16.11.2015.
 */
public interface I_AspectRatio
{
    void setAspectRatio(int width, int height);
    void setSurfaceTextureListener(TextureView.SurfaceTextureListener listner);
    void draw(Canvas canvas);
}
