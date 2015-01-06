package com.troop.freedcam.ui.menu.childs;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.menu.ExpandableGroup;
import com.troop.freedcam.utils.DeviceUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Ingo on 02.11.2014.
 */
public class SaveCamParasExpandableChild extends ExpandableChild
{
    AbstractCameraUiWrapper cameraUiWrapper;
    public SaveCamParasExpandableChild(Context context, ExpandableGroup group, String name, AppSettingsManager appSettingsManager, String settingsname)
    {
        super(context, group, name, appSettingsManager, settingsname);

    }

    public void setParameterHolder(AbstractModeParameter parameterHolder, ArrayList<String> modulesToShow, AbstractCameraUiWrapper cameraUiWrapper)
    {
        super.setParameterHolder(new simpleModeParam(), modulesToShow);

        this.cameraUiWrapper = cameraUiWrapper;
        nameTextView.setText("Save CamParameter");
        valueTextView.setText("");
    }



    public void SaveCamParameters()
    {
        String[] paras = null;
        if (DeviceUtils.isSamsungADV())
        {
            paras = ((BaseCameraHolder)cameraUiWrapper.cameraHolder).GetSamsungCamera().getParameters().flatten().split(";");
        }
        else
        {
            paras = ((BaseCameraHolder)cameraUiWrapper.cameraHolder).GetCamera().getParameters().flatten().split(";");
        }

        Arrays.sort(paras);

        FileOutputStream outputStream;

        File file = new File(Environment.getExternalStorageDirectory() + "/DCIM/FreeCam/CameraParameters.txt");
        if (!file.exists())
        {
            try {
                file.mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        try {
            outputStream = new FileOutputStream(file);
            outputStream.write((Build.MODEL + "\r\n").getBytes());
            for (String s : paras)
            {
                outputStream.write((s+"\r\n").getBytes());
            }

            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    class simpleModeParam extends AbstractModeParameter
    {
        @Override
        public boolean IsSupported()
        {
            if (appSettingsManager.getCamApi().equals(AppSettingsManager.API_1))
                return true;
            else
                return false;
        }

        @Override
        public void SetValue(String valueToSet, boolean setToCamera) {

        }

        @Override
        public String GetValue() {
            return null;
        }

        @Override
        public String[] GetValues() {
            return new String[0];
        }
    }
}
