package com.freedcam.apis.camera2.camera2.modules;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

import com.freedcam.apis.camera2.camera2.BaseCameraHolderApi2;
import com.freedcam.apis.i_camera.modules.AbstractModule;
import com.freedcam.apis.i_camera.modules.ModuleEventHandler;
import com.freedcam.apis.i_camera.parameters.AbstractParameterHandler;
import com.freedcam.utils.AppSettingsManager;


/**
 * Created by troop on 12.12.2014.
 */
public abstract class AbstractModuleApi2 extends AbstractModule implements I_PreviewWrapper
{
    protected BaseCameraHolderApi2 baseCameraHolder;
    protected AbstractParameterHandler ParameterHandler;

    protected boolean isWorking = false;

    protected Point displaySize;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public AbstractModuleApi2(BaseCameraHolderApi2 cameraHandler, ModuleEventHandler eventHandler,Context context, AppSettingsManager appSettingsManager)
    {
        super(cameraHandler,eventHandler,context,appSettingsManager);
        this.baseCameraHolder = cameraHandler;
        this.ParameterHandler = baseCameraHolder.GetParameterHandler();
        Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        displaySize = new Point();
        display.getRealSize(displaySize);
    }

    @Override
    public String ModuleName() {
        return name;
    }

    @Override
    public boolean DoWork() {
        return true;
    }

    @Override
    public boolean IsWorking() {
        return isWorking;
    }

    @Override
    public void LoadNeededParameters()
    {
        baseCameraHolder.ModulePreview = this;
    }

    @Override
    public void UnloadNeededParameters() {

    }
}
