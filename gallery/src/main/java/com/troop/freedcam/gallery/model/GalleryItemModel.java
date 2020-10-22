package com.troop.freedcam.gallery.model;

import com.troop.freedcam.file.holder.BaseHolder;


public class GalleryItemModel {
    private final static String TAG = GalleryItemModel.class.getSimpleName();
    private BaseHolder baseHolder;

    public void setBaseHolder(BaseHolder baseHolder)
    {
        this.baseHolder = baseHolder;
    }

    public BaseHolder getBaseHolder()
    {
        return baseHolder;
    }

    public String getName()
    {
        return baseHolder.getName();
    }
}
