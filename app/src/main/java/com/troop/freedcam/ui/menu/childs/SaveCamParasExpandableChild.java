package com.troop.freedcam.ui.menu.childs;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.camera.parameters.modes.SimpleModeParameter;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.menu.ExpandableGroup;

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
    CameraUiWrapper cameraUiWrapper;
    SimpleModeParameter parameterHolder;
    public SaveCamParasExpandableChild(Context context, ExpandableGroup group, String name, AppSettingsManager appSettingsManager, String settingsname)
    {
        super(context, group, name, appSettingsManager, settingsname);

    }

    public void setParameterHolder(AbstractModeParameter parameterHolder, ArrayList<String> modulesToShow, AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.parameterHolder = (SimpleModeParameter)parameterHolder;

        super.setParameterHolder(this.parameterHolder, modulesToShow);

        this.cameraUiWrapper = (CameraUiWrapper)cameraUiWrapper;
        nameTextView.setText("Save CamParameter");
        valueTextView.setText("");
        onValueChanged("");
    }

    @Override
    public void onValueChanged(String val) {
        if (appSettingsManager.getCamApi().equals(AppSettingsManager.API_1))
        {
            if (!isVisible)
            {
                group.submenu.addView(this);
                isVisible = true;
                this.parameterHolder.setIsSupported(true);
            }
        }
        else
        {
            if (isVisible)
            {
                group.submenu.removeView(this);
                isVisible = false;
                this.parameterHolder.setIsSupported(false);
            }
        }
        group.ModuleChanged("");
    }

    public void SaveCamParameters()
    {
        String[] paras = null;
        BaseCameraHolder holder = (BaseCameraHolder)cameraUiWrapper.cameraHolder;
        if (holder.hasSamsungFrameWork)
        {
            paras = holder.GetSamsungCamera().getParameters().flatten().split(";");
        }
        else if (holder.hasLGFrameWork)
        {
            paras = holder.getLgParameters().split(";");
        }
        else
        {
            paras = holder.GetCamera().getParameters().flatten().split(";");
        }

        Arrays.sort(paras);

        FileOutputStream outputStream;

        File freedcamdir = new File(Environment.getExternalStorageDirectory() +"/DCIM/FreeCam/");
        if (!freedcamdir.exists())
            freedcamdir.mkdirs();
        File file = new File(Environment.getExternalStorageDirectory() + "/DCIM/FreeCam/"+ Build.MODEL + "_CameraParameters.txt");
        try {
            //file.mkdirs();
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
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
}
