package freed.cam.apis.camera2.parameters.ae;

import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.troop.freedcam.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import freed.FreedApplication;
import freed.cam.ActivityFreeDcamMain;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.basecamera.parameters.ae.AeStates;
import freed.cam.apis.basecamera.parameters.manual.AbstractManualShutter;
import freed.cam.apis.camera2.Camera2;
import freed.cam.ui.themesample.handler.UserMessageHandler;
import freed.gl.MeteringProcessor;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.BackgroundHandlerThread;
import freed.utils.Log;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class FreedAeManger extends AeManagerCamera2 implements MeteringProcessor.MeteringEvent
{
    private final String TAG = FreedAeManger.class.getSimpleName();
    private final BackgroundHandlerThread backgroundHandlerThread;
    private final MeteringProcessor meteringProcessor;
    private final Camera2 cameraWrapperInterface;

    private int iso;
    private long exposuretime;
    private final long default_exposuretime_max = (long)((1f/15f) * 1000000 * 1000);
    private final long default_exposuretime = (long)((1f/120f) * 1000000 * 1000);
    private long min_exposuretime;
    private long max_exposuretime;
    private int max_iso;
    private int min_iso;
    //expecting there is only one aperture size.
    private float aperture;
    private float focal_length;
    private final UserMessageHandler userMessageHandler;
    private float exposureCompensationValue = 0;
    private boolean expotime_enable = false;
    private boolean iso_enabled = false;
    private long forcedExposureTime;
    private int forcedIso;
    private final SettingsManager settingsManager;
    private final AeMath aeMath;

    public FreedAeManger(Camera2 cameraWrapperInterface,UserMessageHandler userMessageHandler,SettingsManager settingsManager) {
        super(cameraWrapperInterface);
        this.cameraWrapperInterface = cameraWrapperInterface;
        this.userMessageHandler = userMessageHandler;
        this.settingsManager =settingsManager;
        aeMath = new AeMath();
        backgroundHandlerThread = new BackgroundHandlerThread(TAG);
        meteringProcessor = ActivityFreeDcamMain.histogramController().getMeteringProcessor();
        manualExposureTime.setViewState(AbstractParameter.ViewState.Visible);
        exposureCompensation.setViewState(AbstractParameter.ViewState.Visible);
        List<String> evs = new ArrayList<>();
        for (float i = -5; i <= 5; i +=0.1)
        {
            String t = String.format("%.1f", i);
            evs.add(t);
        }
        exposureCompensation.fireStringValuesChanged(evs.toArray(new String[evs.size()]));
        exposureCompensation.setValue(evs.size()/2,false);

        evs = Arrays.asList(manualExposureTime.getStringValues());
        if (evs.size() >0) {
            evs.set(0, "auto");
            manualExposureTime.fireStringValuesChanged(evs.toArray(new String[evs.size()]));
        }
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
        meteringProcessor.setMeteringEventListener(this);

    }

    public void stop()
    {
        backgroundHandlerThread.destroy();
        meteringProcessor.setMeteringEventListener(null);
        Log.d(TAG, "stop");
    }

    public void turnDefaultAeOff()
    {
        if(cameraWrapperInterface.getParameterHandler().get(SettingKeys.FLASH_MODE) != null) {
            cameraWrapperInterface.captureSessionHandler.SetParameterRepeating(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF,true);
            cameraWrapperInterface.getParameterHandler().get(SettingKeys.FLASH_MODE).setViewState(AbstractParameter.ViewState.Hidden);
        }
        cameraWrapperInterface.getParameterHandler().get(SettingKeys.EXPOSURE_MODE).setStringValue(FreedApplication.getContext().getString(R.string.off),true);
        manualExposureTime.setViewState(AbstractParameter.ViewState.Visible);
        manualExposureTime.setViewState(AbstractParameter.ViewState.Enabled);
        exposureCompensation.setViewState(AbstractParameter.ViewState.Visible);
    }

    private String getShutterStringNS(long val)
    {
        if (val > 1000000000) {
            return "" + val / 1000000000;
        }
        int i = (int)(0.5D + 1.0E9F / val);
        return "1/" + i;
    }

    @Override
    public void setExposureTime(int valueToSet, boolean setToCamera) {
        if (valueToSet > 0) {
            expotime_enable = true;
            long val = AbstractManualShutter.getMilliSecondStringFromShutterString(manualExposureTime.getStringValues()[valueToSet]) * 1000;
            if (val > MAX_PREVIEW_EXPOSURETIME && !settingsManager.GetCurrentModule().equals(FreedApplication.getStringFromRessources(R.string.module_video))) {
                Log.d(manualExposureTime.TAG, "ExposureTime Exceed 100000000 for preview, set it to 100000000");
                val = MAX_PREVIEW_EXPOSURETIME;
            }
            forcedExposureTime = val;
            cameraWrapperInterface.captureSessionHandler.SetParameterRepeating(CaptureRequest.SENSOR_EXPOSURE_TIME, val, setToCamera);
        }
        else
        {
            expotime_enable = false;
        }
    }

    @Override
    public void setIso(int iso, boolean setToCam)
    {
        if (iso > 0)
        {
            iso_enabled = true;
            int s = Integer.parseInt(manualIso.getStringValues()[iso]);
            forcedIso = s;
            cameraWrapperInterface.captureSessionHandler.SetParameterRepeating(CaptureRequest.SENSOR_SENSITIVITY, s ,setToCam);
        }
        else
            iso_enabled = false;
    }

    @Override
    public void setExposureCompensation(int valueToSet, boolean setToCamera) {
        valueToSet = exposureCompensation.getStringValues().length/2 + valueToSet;
        String ev =exposureCompensation.getStringValues()[valueToSet];
        exposureCompensationValue  = -Float.parseFloat(ev.replace(",","."));
        Log.d(TAG,"EVString:" + ev + " parsed:" + exposureCompensationValue);
    }

    @Override
    public void setAeMode(AeStates aeState) {
        //super.setAeMode(aeState);
    }

    @Override
    public boolean isExposureCompensationWriteable() {
        return true;
    }

    @Override
    public boolean isExposureTimeWriteable() {
        return true;
    }

    @Override
    public void onMeteringDataChanged(int[] meters) {
        if (!this.measureMeter.isWorking) {
            this.measureMeter.setMeter(meters);
            backgroundHandlerThread.execute(measureMeter);
        }
    }

    @Override
    public void onLumaChanged(float luma) {
        if (this.measureMeter.isWorking)
            return;
        measureMeter.addLuma(luma);
        backgroundHandlerThread.execute(measureMeter);
    }

    private final MeasureMeter measureMeter = new MeasureMeter();

    private class MeasureMeter implements Runnable {

        private int[] meter;
        private int logcounter = 0;
        private final Object lock = new Object();
        private double luminance = 0;

        private void addLuma(float luma)
        {
            luminance = 0.95 * luminance + 0.05 * luma;
        }

        private double getAverageLuma()
        {
            return luminance;
        }

        private boolean isWorking = false;
        public boolean isWorking()
        {
            return isWorking;
        }

        public void setMeter(int[] meter) {
            synchronized (lock) {
                this.meter = meter.clone();
                lock.notify();
            }
        }

        public void setLuma(float luma)
        {
            addLuma(luma);
        }

        double evboost = 0;
        @Override
        public void run() {
            if (isWorking)
                return;
            isWorking = true;
            // represent the average scene luminance input comes from opengl measured as 0 black 255 white
            double luminance = getAverageLuma()/255.;

            double currentValuesEV = aeMath.getCurrentEV(aperture, exposuretime, iso);
            int user_max_iso = getUserMaxIso();
            int user_min_iso = getUserMinIso();
            long user_max_expotime = getUserMaxExpoTime();
            long user_min_expotime = getUserMinExpoTime();

            if (luminance < 0.46 && evboost >= 0.5)
                evboost -= 0.5 -luminance;
            else if(luminance > 0.54)
                evboost += luminance -0.5;

            double ev = aeMath.getTargetEv(luminance, 100) + (exposureCompensationValue) + (evboost);

            if (!iso_enabled && !expotime_enable) {
                exposuretime = default_exposuretime;
                double ciso = aeMath.getIso(aperture, exposuretime, ev);
                double newiso = clampIso(ciso, user_min_iso, user_max_iso);
                double iso_applied_ev = aeMath.getCurrentEV(aperture, exposuretime, newiso);
                double ev_diff = (ev - iso_applied_ev);
                double expotimeSec = aeMath.getExposureTime(exposuretime, ev_diff);
                double newexpotime = clampExposureTime(expotimeToNano(expotimeSec), user_min_expotime, user_max_expotime);
                double finalEV = aeMath.getCurrentEV(aperture, newexpotime, newiso);

                if (logcounter++ == 11) {
                    String msg = "L:" + luminance + "\nevb:" +evboost+
                            "\nI:" + iso + "/" + (int) ciso + "/" + (int) newiso +
                            "\nS:" + getShutterStringNS(exposuretime) + "/" + getShutterStringNS((long) newexpotime) +
                            "\nEV:\n"
                            +  ev + "\n"
                            +  currentValuesEV + "\n"
                            +  finalEV + "\n"
                            +  ev_diff;
                    userMessageHandler.sendMSG(msg, false);
                    logcounter = 0;
                }
                exposuretime = (long) newexpotime;
                iso = (int) newiso;
                setExposuretime(exposuretime, false);
                setiso(iso, true);
            } else if (iso_enabled && !expotime_enable) {
                iso = forcedIso;
                exposuretime = default_exposuretime;
                double iso_applied_ev = aeMath.getCurrentEV(aperture, exposuretime, iso);
                double ev_diff = (ev - iso_applied_ev);
                double expotimeSec = aeMath.getExposureTime(exposuretime, ev_diff);
                double newexpotime = clampExposureTime(expotimeToNano(expotimeSec), user_min_expotime, user_max_expotime);
                exposuretime = (long) newexpotime;

                if (logcounter++ == 11) {
                    String msg = "L:" + luminance +
                            "\nI:" + iso +
                            "\nS:" + getShutterStringNS(exposuretime) + "/" + getShutterStringNS((long) newexpotime) +
                            "\nEV:\n"
                            + (float) ev + "\n"
                            + (float) currentValuesEV;
                    userMessageHandler.sendMSG(msg, false);
                    logcounter = 0;
                }
                setExposuretime(exposuretime, true);
            } else if (!iso_enabled && expotime_enable) {
                exposuretime = forcedExposureTime;
                double is = aeMath.getIso(aperture, exposuretime, ev);
                iso = (int) clampIso(is, user_min_iso, user_max_iso);

                if (logcounter++ == 11) {
                    String msg = "L:" + luminance +
                            "\nI:" + iso +
                            "\nS:" + getShutterStringNS(exposuretime) +
                            "\nEV:\n" + (float) ev + "\n" + (float) currentValuesEV;
                    userMessageHandler.sendMSG(msg, false);
                    logcounter = 0;
                }
                setiso(iso, true);
            } else {
                iso = forcedIso;
                exposuretime = forcedExposureTime;
            }
            isWorking = false;

        }

        private int getUserMaxIso()
        {
            try {
                String s = settingsManager.get(SettingKeys.MAX_ISO).get();
                if (s.equals("auto"))
                    return Integer.parseInt(cameraWrapperInterface.getParameterHandler().get(SettingKeys.M_MANUAL_ISO).getStringValues()[cameraWrapperInterface.getParameterHandler().get(SettingKeys.M_MANUAL_ISO).getStringValues().length-1]);
                int index = Integer.parseInt(s);
                return index;
            }
            catch (NullPointerException exception)
            {
            }
            return 0;
        }

        private int getUserMinIso()
        {
            try {
                String s = settingsManager.get(SettingKeys.MIN_ISO).get();
                if (s.equals("auto"))
                    return Integer.parseInt(cameraWrapperInterface.getParameterHandler().get(SettingKeys.M_MANUAL_ISO).getStringValues()[1]);
                int index = Integer.parseInt(s);
                return index;
            }
            catch (NullPointerException exception)
            {
            }
            return 0;
        }

        private long getUserMinExpoTime()
        {
            try {

                String s = settingsManager.get(SettingKeys.MIN_EXPOSURE).get();
                if (s.equals("auto"))
                    return 0;
                return AbstractManualShutter.getMilliSecondStringFromShutterString(s) * 1000;
            }
            catch (NullPointerException exception)
            {
            }
            return 0;
        }

        private long getUserMaxExpoTime()
        {
            try {
                String s = settingsManager.get(SettingKeys.MAX_EXPOSURE).get();
                if (s.equals("auto"))
                    return 0;
                return AbstractManualShutter.getMilliSecondStringFromShutterString(s) * 1000;
            }
            catch (NullPointerException exception)
            {
            }
            return 0;
        }

        private void setExposuretime(long valueToSet, boolean setToCamera) {
            cameraWrapperInterface.captureSessionHandler.SetParameterRepeating(CaptureRequest.SENSOR_EXPOSURE_TIME, valueToSet,setToCamera);
        }

        private void setiso(int iso, boolean setToCam)
        {
            cameraWrapperInterface.captureSessionHandler.SetParameterRepeating(CaptureRequest.SENSOR_SENSITIVITY, iso,setToCam);
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

        private double clampIso(double iso, int isomin, int isomax)
        {
            if (isomin == 0 && isomax == 0)
                return clamp(iso,min_iso, max_iso);
            else if (isomin == 0 && isomax != 0)
                return clamp(iso,min_iso, isomax);
            else if (isomin != 0 && isomax == 0)
                return clamp(iso,isomin,max_iso);
            else
                return clamp(iso,isomin,isomax);
        }

        private double clampExposureTime(double expotime, double expotimeMin, double expoTimeMax)
        {
            if (expotimeMin == 0 && expoTimeMax == 0)
                return clamp(expotime,min_exposuretime, default_exposuretime_max);
            else if (expotimeMin == 0 && expoTimeMax != 0)
                return clamp(expotime,min_exposuretime, expoTimeMax);
            else if (expotimeMin != 0 && expoTimeMax == 0)
                return clamp(expotime,expotimeMin, default_exposuretime_max);
            else
                return clamp(expotime,expotimeMin,expoTimeMax);
        }

    }
}
