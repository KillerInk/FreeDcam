package troop.com.themesample.views.menu;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.troop.filelogger.Logger;
import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_Activity;
import com.troop.freedcam.utils.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import troop.com.themesample.subfragments.Interfaces;

/**
 * Created by Ingo on 06.09.2015.
 */
public class MenuItemSaveCamParams extends MenuItem
{
    AbstractCameraUiWrapper cameraUiWrapper;
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
    public void SetStuff(I_Activity i_activity, String settingvalue) {

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
            setVisibility(VISIBLE);
        else
            setVisibility(GONE);
    }

    private void saveCamParameters()
    {
        String[] paras = null;
        BaseCameraHolder holder = (BaseCameraHolder)cameraUiWrapper.cameraHolder;
        if (holder.DeviceFrameWork == BaseCameraHolder.Frameworks.LG)
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
