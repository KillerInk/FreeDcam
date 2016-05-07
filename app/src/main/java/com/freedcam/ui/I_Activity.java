package com.freedcam.ui;

import android.net.Uri;

/**
 * Created by troop on 22.03.2015.
 */
public interface I_Activity
{
    void SwitchCameraAPI(String Api);
    void SetTheme(String Theme);
    void closeActivity();
    void ChooseSDCard(I_OnActivityResultCallback callback);
    interface I_OnActivityResultCallback
    {
        void onActivityResultCallback(Uri uri);
    }
}


