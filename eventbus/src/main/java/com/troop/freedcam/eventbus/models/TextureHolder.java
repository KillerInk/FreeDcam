package com.troop.freedcam.eventbus.models;

import android.graphics.SurfaceTexture;
import android.view.TextureView;

public class TextureHolder {
    private TextureView textureView;

    public TextureHolder(TextureView textureView)
    {
        this.textureView = textureView;
    }

    public int getWidth()
    {
        return textureView.getWidth();
    }

    public int getHeight()
    {
        return textureView.getHeight();
    }

    public SurfaceTexture getSurfaceTexture()
    {
        return textureView.getSurfaceTexture();
    }

    public TextureView getTextureView() {
        return textureView;
    }
}
