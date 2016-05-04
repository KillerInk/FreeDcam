package com.freedviewer;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.freedcam.utils.AppSettingsManager;
import com.freedcam.ui.I_Activity;

import java.io.File;

/**
 * Created by troop on 22.12.2015.
 */
public class DngConvertingActivity extends FragmentActivity implements I_Activity
{
    private final String TAG = DngConvertingActivity.class.getSimpleName();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new AppSettingsManager(PreferenceManager.getDefaultSharedPreferences(this), this);
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
    public int[] GetScreenSize() {
        return new int[0];
    }

    @Override
    public void ShowHistogram(boolean enable) {

    }

    @Override
    public void loadImageViewerFragment(File file) {

    }

    @Override
    public void loadCameraUiFragment() {

    }

    @Override
    public void closeActivity() {

    }

    @Override
    public void ChooseSDCard(I_OnActivityResultCallback callback) {

    }
}
