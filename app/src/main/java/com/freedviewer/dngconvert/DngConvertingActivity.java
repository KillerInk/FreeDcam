package com.freedviewer.dngconvert;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.freedcam.utils.AppSettingsManager;
import com.freedcam.ui.I_Activity;

/**
 * Created by troop on 22.12.2015.
 */
public class DngConvertingActivity extends FragmentActivity implements I_Activity
{
    private final String TAG = DngConvertingActivity.class.getSimpleName();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new AppSettingsManager();
        if (getSupportFragmentManager().findFragmentByTag(TAG) == null) {
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(android.R.id.content, new DngConvertingFragment(), TAG);
            ft.commit();
        }

    }

    @Override
    public void SwitchCameraAPI(String Api) {

    }

    @Override
    public void SetTheme(String Theme) {

    }


    @Override
    public void closeActivity() {

    }

    @Override
    public void ChooseSDCard(I_OnActivityResultCallback callback) {

    }
}
