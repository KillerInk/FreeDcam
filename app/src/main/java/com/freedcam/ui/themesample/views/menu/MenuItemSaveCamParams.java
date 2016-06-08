/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package com.freedcam.ui.themesample.views.menu;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.freedcam.apis.basecamera.AbstractCameraUiWrapper;
import com.freedcam.apis.basecamera.parameters.modes.AbstractModeParameter;
import com.freedcam.apis.camera1.CameraHolder;
import com.freedcam.apis.camera1.CameraUiWrapper;
import com.freedcam.ui.I_Activity;
import com.freedcam.ui.themesample.subfragments.Interfaces.I_MenuItemClick;
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
    public void SetMenuItemListner(I_MenuItemClick menuItemClick) {

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
    public void ModuleChanged(String module) {
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
        CameraHolder holder = (CameraHolder)cameraUiWrapper.cameraHolder;

        paras = holder.GetCamera().getParameters().flatten().split(";");

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
