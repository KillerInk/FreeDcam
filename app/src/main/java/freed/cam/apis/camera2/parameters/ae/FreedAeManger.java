package freed.cam.apis.camera2.parameters.ae;

import static java.lang.Math.sqrt;

import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.troop.freedcam.R;

import java.util.ArrayList;
import java.util.List;

import freed.FreedApplication;
import freed.cam.ActivityFreeDcamMain;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.basecamera.parameters.ae.AeManager;
import freed.cam.apis.basecamera.parameters.ae.AeStates;
import freed.cam.apis.camera2.Camera2;
import freed.cam.ui.themesample.handler.UserMessageHandler;
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
    private final long default_exposuretime = (long)((1f/15f) * 1000000 * 1000);
    private long min_exposuretime;
    private long max_exposuretime;
    private int max_iso;
    private int min_iso;
    //expecting there is only one aperture size.
    private float aperture;
    private float focal_length;
    private UserMessageHandler userMessageHandler;


    public FreedAeManger(Camera2 cameraWrapperInterface,UserMessageHandler userMessageHandler) {
        this.cameraWrapperInterface = cameraWrapperInterface;
        this.userMessageHandler = userMessageHandler;
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
        aperture = cameraWrapperInterface.getCameraHolder().characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES)[0];
        focal_length = cameraWrapperInterface.getCameraHolder().characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)[0];
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
        //cameraWrapperInterface.getParameterHandler().get(SettingKeys.M_ExposureTime).fireStringValueChanged(getShutterStringNS(exposuretime));
    }

    private String getShutterStringNS(long val)
    {
        if (val > 1000000000) {
            return "" + val / 1000000000;
        }
        int i = (int)(0.5D + 1.0E9F / val);
        return "1/" + Integer.toString(i);
    }

    private void setIso(int iso, boolean setToCam)
    {
        cameraWrapperInterface.captureSessionHandler.SetParameterRepeating(CaptureRequest.SENSOR_SENSITIVITY, iso,setToCam);
        //cameraWrapperInterface.getParameterHandler().get(SettingKeys.M_ManualIso).fireStringValueChanged(iso+"");
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
        List<Float> lumas = new ArrayList<>();
        private int logcounter = 0;

        private void addLuma(float luma)
        {
            if (lumas.size() > 60)
                lumas.remove(lumas.size()-1);
            lumas.add(0,luma);
        }

        private float getAvarageLuma()
        {
            float out = 0;
            for (int i = 0; i< lumas.size();i++)
                out += lumas.get(i);
            out /= lumas.size();
            return out;
        }

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

            luminance = luminance / (float)meter.length /*- 0.2126f*/;
            addLuma(luminance);

            luminance = getAvarageLuma();

            double currentValuesEV = getCurrentEV(aperture,exposuretime,iso);
            double EV100 = getEv100(aperture,exposuretime);
            double ev = getTargetEv(luminance);

            ev = ev + ((luminance) * 12.5);
            //long expotime = (long) expotimeToNano(1.0f / (focal_length * 1000.0f));
            double ciso = getIso(aperture,exposuretime, ev);
            double newiso = clamp(ciso,min_iso,max_iso);
            double iso_applied_ev = getCurrentEV(aperture,exposuretime,newiso);
            double newexpotime = clamp(expotimeToNano(getExposureTime(exposuretime,(ev -iso_applied_ev))),min_exposuretime,default_exposuretime);
            double finalEV = getCurrentEV(aperture,newexpotime,newiso);
            if (logcounter++ == 11) {
                String msg = "L:" + luminance +
                        "\nI:" + iso + "/" + (int)ciso + "/" + (int) newiso +
                        "\nS:" + exposuretime + "/" + (long) newexpotime +
                        "\nEV:\n" + (float) ev + "\n" + (float) currentValuesEV + "\n" + (float) finalEV+ "\n" + (float) EV100;
                userMessageHandler.sendMSG(msg, false);
                logcounter = 0;
            }
            exposuretime = (long) newexpotime;
            iso = (int) newiso;

            setExposuretime(exposuretime,false);
            setIso(iso,true);
        }

        private double getLuminance(int color)
        {
            //float a = Color.alpha(color)/255;
            float r = Color.red(color) / 255f;
            float g = Color.green(color) / 255f;
            float b = Color.blue(color) / 255f;
            return (float) ((0.2126 * r) + (0.7152 * g) + (0.0722 * b));
        }

        private double getTargetEv(double luma)
        {
            return log2(luma * 100.0 / 12.5);
        }

        private double getEv100(double luma, double expotime)
        {
            double expotime_sec = getExpotimeInSec(expotime);
            return log2((luma*100) / (expotime_sec*100));
        }

        private double getCurrentEV(double aperture, double exposuretime, double iso)
        {
            double expotime_sec = getExpotimeInSec(exposuretime);
            double tmp =  ((sqrt(aperture)*100)/(expotime_sec*iso));
            return log2(tmp);
        }

        private double getIso(double aperture, double exposuretime, double ev)
        {
            double expotime_sec = getExpotimeInSec(exposuretime);
            return (sqrt(aperture) * 100.0) / (expotime_sec * Math.pow(2.0, ev));
        }

        private double getExposureTime(double exposuretime,double evdif)
        {
            double expotime_sec = getExpotimeInSec(exposuretime);
            return expotime_sec * Math.pow(2.0, -evdif);
        }

        private double log2(double l)
        {
            return (Math.log(l) / Math.log(2));
        }

        private double getExpotimeInSec(double exposuretime)
        {
            return exposuretime/1000000000;
        }

        private double expotimeToNano(double expotime)
        {
            return expotime * 1000000000;
        }

        private double clamp(double val,double min, double max)
        {
            if (val < min)
                return min;
            else if (val>max)
                return max;
            else
                return val;
        }

        private double sqr(double in)
        {
            return in*in;
        }

    };
}
