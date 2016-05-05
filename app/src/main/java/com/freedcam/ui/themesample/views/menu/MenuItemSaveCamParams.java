package com.freedcam.ui.themesample.views.menu;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.freedcam.apis.camera1.camera.CameraHolderApi1;
import com.freedcam.apis.camera1.camera.CameraUiWrapper;
import com.freedcam.apis.basecamera.camera.AbstractCameraUiWrapper;
import com.freedcam.apis.basecamera.camera.parameters.modes.AbstractModeParameter;
import com.freedcam.ui.I_Activity;
import com.freedcam.ui.themesample.subfragments.Interfaces;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.Logger;
import com.freedcam.utils.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Ingo on 06.09.2015.
 */
public class MenuItemSaveCamParams extends MenuItem
{
    private AbstractCameraUiWrapper cameraUiWrapper;
    public MenuItemSaveCamParams(Context context) {
        super(context);
    }

    public MenuItemSaveCamParams(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
    }

    @Override
    protected void inflateTheme(LayoutInflater inflater) {
        super.inflateTheme(inflater);
    }

    @Override
    public void onValueChanged(String val) {
    }

    @Override
    public void onClick(View v)
    {
        try {


            saveCamParameters();
            Toast.makeText(context, "Saved CameraParameters", Toast.LENGTH_LONG).show();
        }
        catch (Exception ex) {
            Logger.d("Freedcam", ex.getMessage());
        }
    }

    @Override
    public void onClick(int x, int y) {
        super.onClick(x, y);
        try {


            saveCamParameters();
            Toast.makeText(context, "Saved CameraParameters", Toast.LENGTH_LONG).show();
        }
        catch (Exception ex) {
            Logger.d("Freedcam",ex.getMessage());
        }
    }

    @Override
    public void SetStuff(I_Activity i_activity, String settingvalue, AppSettingsManager appSettingsManager) {

    }

    @Override
    public void SetMenuItemListner(Interfaces.I_MenuItemClick menuItemClick) {

    }

    @Override
    public void SetParameter(AbstractModeParameter parameter) {

    }

    @Override
    public void setTextToTextBox(AbstractModeParameter parameter) {

    }

    @Override
    public String[] GetValues() {
        return null;
    }

    @Override
    public void SetValue(String value) {

    }

    @Override
    public void onIsSupportedChanged(boolean isSupported) {

    }

    @Override
    public void onIsSetSupportedChanged(boolean isSupported) {

    }

    @Override
    public void onValuesChanged(String[] values) {

    }

    @Override
    public String ModuleChanged(String module) {
        return null;
    }

    @Override
    public void ParametersLoaded()
    {
    }


    public void setCameraUiWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        if (cameraUiWrapper instanceof CameraUiWrapper)
            setVisibility(View.VISIBLE);
        else
            setVisibility(View.GONE);
    }

    private void saveCamParameters()
    {
        String[] paras = null;
        CameraHolderApi1 holder = (CameraHolderApi1)cameraUiWrapper.cameraHolder;
        if (holder.DeviceFrameWork == CameraHolderApi1.Frameworks.LG)
        {
            paras = holder.getLgParameters().split(";");
        }
        else
        {
            paras = holder.GetCamera().getParameters().flatten().split(";");
        }

        Arrays.sort(paras);

        FileOutputStream outputStream;

        File freedcamdir = new File(Environment.getExternalStorageDirectory() + StringUtils.freedcamFolder);
        if (!freedcamdir.exists())
            freedcamdir.mkdirs();
        File file = new File(Environment.getExternalStorageDirectory() + StringUtils.freedcamFolder+ Build.MODEL + "_CameraParameters.txt");
        try {
            //file.mkdirs();
            file.createNewFile();
        } catch (IOException e) {
            Logger.exception(e);
        }

        try {
            outputStream = new FileOutputStream(file);
            outputStream.write((Build.MODEL + "\r\n").getBytes());
            outputStream.write((System.getProperty("os.version") + "\r\n").getBytes());
            for (String s : paras)
            {
                outputStream.write((s+"\r\n").getBytes());
            }

            outputStream.close();
        } catch (Exception e) {
            Logger.exception(e);
        }
    }
}
