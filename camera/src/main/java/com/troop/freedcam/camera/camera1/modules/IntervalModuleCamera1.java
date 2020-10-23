package com.troop.freedcam.camera.camera1.modules;

import android.os.Handler;

import com.troop.freedcam.camera.R;
import com.troop.freedcam.camera.basecamera.CameraControllerInterface;
import com.troop.freedcam.camera.basecamera.modules.IntervalHandler;
import com.troop.freedcam.camera.camera1.Camera1Controller;
import com.troop.freedcam.camera.camera2.modules.IntervalApi2;
import com.troop.freedcam.file.holder.BaseHolder;
import com.troop.freedcam.utils.ContextApplication;
import com.troop.freedcam.utils.Log;

import java.util.ArrayList;
import java.util.List;

public class IntervalModuleCamera1 extends PictureModule implements IntervalHandler.SuperDoWork {

    protected final IntervalHandler intervalHandler;
    protected   final String TAG  = IntervalApi2.class.getSimpleName();
    private boolean module_isWorking;
    private List<BaseHolder> files =new ArrayList<BaseHolder>();

    public IntervalModuleCamera1(Camera1Controller cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler) {
        super(cameraUiWrapper, mBackgroundHandler, mainHandler);
        name = ContextApplication.getStringFromRessources(R.string.module_interval);
        intervalHandler = new IntervalHandler(this);
    }

    @Override
    public String ShortName() {
        return "Int";
    }

    @Override
    public String LongName() {
        return "Interval";
    }

    @Override
    public void InitModule() {
        super.InitModule();
        Log.d(TAG, "Init");
        module_isWorking = false;
        intervalHandler.Init();
        //changeCaptureState(ModuleHandlerAbstract.CaptureStates.continouse_capture_stop);
    }
    @Override
    public void DestroyModule() {
        Log.d(TAG, "Destroy");
        intervalHandler.Destroy();
    }

    @Override
    public void DoWork()
    {
        if (!module_isWorking)
        {
            Log.d(TAG, "StartInterval");
            module_isWorking = true;
            files.clear();
            intervalHandler.StartInterval();
            //changeCaptureState(ModuleHandlerAbstract.CaptureStates.continouse_capture_start);
        } else {
            Log.d(TAG, "Stop Interval");

            intervalHandler.CancelInterval();
            BaseHolder file[] = new BaseHolder[files.size()];
            files.toArray(file);
            fireOnWorkFinish(file);
            if (module_isWorking)
            {
                Log.d(TAG, "changeWorkstate to cont_capture_stop_while_working");
                //changeCaptureState(ModuleHandlerAbstract.CaptureStates.cont_capture_stop_while_working);
            }
            else {
                Log.d(TAG, "changeWorkstate to cont_capture_stop_while_notworking");
                //changeCaptureState(ModuleHandlerAbstract.CaptureStates.cont_capture_stop_while_notworking);
            }
            module_isWorking = false;
        }
    }

    @Override
    public void SuperDoTheWork() {
        super.DoWork();
    }

    @Override
    public boolean isWorking() {
        return super.isWorking;
    }

    @Override
    public void IntervalCaptureIsDone() {
        BaseHolder file[] = new BaseHolder[files.size()];
        files.toArray(file);
        fireOnWorkFinish(file);
    }

    @Override
    public void internalFireOnWorkDone(BaseHolder file) {
        files.add(file);
        intervalHandler.notifyImageCaptured();
    }
}
