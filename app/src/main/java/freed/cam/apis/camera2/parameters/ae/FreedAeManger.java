package freed.cam.apis.camera2.parameters.ae;

import android.graphics.Color;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.troop.freedcam.R;

import freed.FreedApplication;
import freed.cam.ActivityFreeDcamMain;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.basecamera.parameters.ae.AeManager;
import freed.cam.apis.basecamera.parameters.ae.AeStates;
import freed.cam.apis.camera2.Camera2;
import freed.gl.MeteringProcessor;
import freed.settings.SettingKeys;
import freed.utils.BackgroundHandlerThread;
import freed.utils.Log;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class FreedAeManger implements MeteringProcessor.MeteringEvent
{
    private final String TAG = FreedAeManger.class.getSimpleName();
    private BackgroundHandlerThread backgroundHandlerThread;
    private MeteringProcessor meteringProcessor;
    private Camera2 cameraWrapperInterface;

    private int iso;
    private long exposuretime;
    private final long default_exposuretime = (long)((1f/30f) * 1000000 * 1000);
    private long min_exposuretime;
    private long max_exposuretime;
    private int max_iso;
    private int min_iso;


    public FreedAeManger(Camera2 cameraWrapperInterface) {
        this.cameraWrapperInterface = cameraWrapperInterface;

        backgroundHandlerThread = new BackgroundHandlerThread(TAG);
        meteringProcessor = ActivityFreeDcamMain.histogramController().getMeteringProcessor();
    }


    public void start()
    {
        Log.d(TAG, "start");
        iso = 100;
        exposuretime = default_exposuretime;
        min_exposuretime = cameraWrapperInterface.getCameraHolder().characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE).getLower();
        max_exposuretime = cameraWrapperInterface.getCameraHolder().characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE).getUpper();
        max_iso = cameraWrapperInterface.getCameraHolder().characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE).getUpper();
        min_iso = cameraWrapperInterface.getCameraHolder().characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE).getLower();
        backgroundHandlerThread.create();
        meteringProcessor.setMeteringEventListener(this::onMeteringDataChanged);

    }

    public void stop()
    {
        backgroundHandlerThread.destroy();
        meteringProcessor.setMeteringEventListener(null);
        Log.d(TAG, "stop");
    }

    public void turnDefaultAeOff()
    {
        if(cameraWrapperInterface.getParameterHandler().get(SettingKeys.FlashMode) != null) {
            cameraWrapperInterface.captureSessionHandler.SetParameterRepeating(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF,true);
            cameraWrapperInterface.getParameterHandler().get(SettingKeys.FlashMode).setViewState(AbstractParameter.ViewState.Hidden);
        }
        cameraWrapperInterface.getParameterHandler().get(SettingKeys.ExposureMode).setStringValue(FreedApplication.getContext().getString(R.string.off),true);
    }

    private void setExposuretime(long exposuretime, boolean setToCam)
    {
        cameraWrapperInterface.captureSessionHandler.SetParameterRepeating(CaptureRequest.SENSOR_EXPOSURE_TIME, exposuretime,setToCam);
    }

    private void setIso(int iso, boolean setToCam)
    {
        cameraWrapperInterface.captureSessionHandler.SetParameterRepeating(CaptureRequest.SENSOR_SENSITIVITY, iso,setToCam);
    }

    @Override
    public void onMeteringDataChanged(int[] meters) {
        if (backgroundHandlerThread.getThread().isAlive()) {
            this.measureMeter.setMeter(meters);
            backgroundHandlerThread.execute(measureMeter);
        }
    }

    private MeasureMeter measureMeter = new MeasureMeter();

    private class MeasureMeter implements Runnable {

        private int[] meter;
        private final  float luminance_factor = 0.2226f;
        //private float lastluma;

        public void setMeter(int[] meter) {
            this.meter = meter.clone();
        }

        @Override
        public void run() {
            if (meter == null)
                return;
            float luminance  = 0f;
            for (int i = 0; i < meter.length;i++)
            {
                luminance += getLuminance(meter[i]);
            }

            luminance = luminance / (float)meter.length;
            /*if (lastluma != 0)
                luminance = (luminance + lastluma)/2;

            lastluma = luminance;*/

            if (luminance > luminance_factor)
            {
                float lumdif = (luminance - luminance_factor);
                if (iso > min_iso)
                {
                    int newiso = iso - (int)(lumdif * (float)iso);
                    if (newiso < min_iso)
                        iso = min_iso;
                    else
                        iso = newiso;
                }
                else
                {
                    long newexpotime = exposuretime - ((long) lumdif * exposuretime);
                    if (newexpotime < min_exposuretime)
                        exposuretime = min_exposuretime;
                    else
                        exposuretime = newexpotime;
                }
            }
            else if (luminance < luminance_factor)
            {
                float lumdif = (luminance_factor - luminance);
                if (exposuretime < default_exposuretime)
                {
                    long newexpotime = exposuretime + ((long) lumdif * exposuretime);
                    if (newexpotime > default_exposuretime)
                        exposuretime = default_exposuretime;
                    else
                        exposuretime = newexpotime;
                }
                else
                {
                    if (iso < max_iso)
                    {
                        int newiso = iso + (int)(lumdif * (float)iso);
                        if (newiso > max_iso)
                            iso = max_iso;
                        else
                            iso = newiso;
                    }
                }
            }

            Log.d(TAG, "Luminance:" + luminance + " expotime:" +exposuretime + " iso:" +iso);

            setExposuretime(exposuretime,false);
            setIso(iso,true);
        }

        private float getLuminance(int color)
        {
            return (float) ((0.2126 * (Color.alpha(color)/255)) + (0.7152 * (Color.red(color)/255)) + (0.0722 * (Color.green(color)/255)));
        }
    };
}
