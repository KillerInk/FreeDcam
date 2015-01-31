package com.troop.freedcam.i_camera;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.SurfaceView;

import com.troop.freedcam.i_camera.interfaces.I_CameraChangedListner;
import com.troop.freedcam.i_camera.interfaces.I_CameraUiWrapper;
import com.troop.freedcam.i_camera.interfaces.I_Module;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.StringUtils;

/**
 * Created by troop on 09.12.2014.
 */
public abstract class AbstractCameraUiWrapper implements I_CameraUiWrapper, I_CameraChangedListner
{
    private static String TAG = StringUtils.TAG + AbstractCameraUiWrapper.class.getSimpleName();
    public AbstractModuleHandler moduleHandler;
    public AbstractParameterHandler camParametersHandler;
    public AbstractCameraHolder cameraHolder;
    public AbstractFocusHandler Focus;
    public AbstractExposureMeterHandler ExposureM;

    protected boolean PreviewSurfaceRdy = false;

    protected I_CameraChangedListner cameraChangedListner;

    protected Handler uiHandler;

    public AbstractCameraUiWrapper(){};
    public AbstractCameraUiWrapper(SurfaceView preview, AppSettingsManager appSettingsManager)
    {
        /*if (backGroundThread == null) {
            backGroundThread = new HandlerThread(TAG);
            backGroundThread.start();
            backGroundHandler = new Handler(backGroundThread.getLooper());

        }*/
        uiHandler = new Handler(appSettingsManager.context.getMainLooper());
    };

    public void SetCameraChangedListner(I_CameraChangedListner cameraChangedListner)
    {
        this.cameraChangedListner = cameraChangedListner;
    }

    @Override
    public void SwitchModule(String moduleName)
    {
        moduleHandler.SetModule(moduleName);
    }


    //start the camera and preview in the background
    @Override
    public void StartCamera()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                startCamera();
            }
        }).start();
        /*backGroundHandler.post(new Runnable() {
            @Override
            public void run() {
                startCamera();
            }
        });*/
    }

    //override this to handle what happens in the background when StartCamera() is called
    protected void startCamera()
    {

    }

    @Override
    public void StopCamera()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                stopCamera();
            }
        }).start();


        /*backGroundHandler.post(new Runnable() {
            @Override
            public void run() {

            }
        });*/
    }

    //override this to handle what happens in the background when StopPreviewAndCamera() is called
    protected void stopCamera()
    {

    }

    @Override
    public void StopPreview()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                stopPreview();
            }
        }).start();

        /*backGroundHandler.post(new Runnable() {
            @Override
            public void run() {

            }
        });*/
    }

    protected void stopPreview()
    {

    }

    @Override
    public void StartPreview()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                startPreview();
            }
        }).start();
        /*backGroundHandler.post(new Runnable() {
            @Override
            public void run() {

            }
        });*/
    }

    protected void startPreview()
    {}

    @Override
    public void DoWork() {
        moduleHandler.DoWork();
    }

    @Override
    public void onCameraOpen(final String message)
    {
        if (cameraChangedListner != null)
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    cameraChangedListner.onCameraOpen(message);
                }
            });


    }

    @Override
    public void onCameraError(final String error) {
        if (cameraChangedListner != null)
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    cameraChangedListner.onCameraError(error);
                }
            });
    }

    @Override
    public void onCameraStatusChanged(final String status)
    {
        if (cameraChangedListner != null)
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    cameraChangedListner.onCameraStatusChanged(status);
                }
            });


    }

    @Override
    public void onCameraClose(final String message)
    {
        if (cameraChangedListner != null)
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    cameraChangedListner.onCameraClose(message);
                }
            });

    }

    @Override
    public void onPreviewOpen(final String message)
    {
        if (cameraChangedListner != null)
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    cameraChangedListner.onPreviewOpen(message);
                }
            });
    }

    @Override
    public void onPreviewClose(final String message) {
        if (cameraChangedListner != null)
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    cameraChangedListner.onPreviewClose(message);
                }
            });
    }

    @Override
    public void onModuleChanged(final I_Module module) {
        if (cameraChangedListner != null)
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    cameraChangedListner.onModuleChanged(module);
                }
            });

    }

    @Override
    public void onCameraOpenFinish(final String message)
    {
        if (cameraChangedListner != null)
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    cameraChangedListner.onCameraOpenFinish(message);
                }
            });

    }


}
