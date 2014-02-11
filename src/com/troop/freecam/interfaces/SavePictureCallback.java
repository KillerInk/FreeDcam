package com.troop.freecam.interfaces;

import android.os.Handler;

import java.io.File;

/**
 * Created by troop on 18.10.13.
 */
public interface SavePictureCallback
{
    void onPictureSaved(File file);
}
