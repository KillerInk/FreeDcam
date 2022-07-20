package freed.cam.apis.camera2;

import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.os.Build;
import android.util.Pair;

import androidx.annotation.RequiresApi;

import freed.FreedApplication;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.basecamera.parameters.ParameterInterface;
import freed.cam.apis.basecamera.parameters.ae.AeManager;
import freed.cam.apis.basecamera.parameters.ae.AeStates;
import freed.cam.apis.camera2.modules.ring.CaptureResultRingBuffer;
import freed.cam.apis.camera2.parameters.ParameterHandlerApi2;
import freed.cam.histogram.HistogramChangedEvent;
import freed.cam.histogram.HistogramFeed;
import freed.settings.Frameworks;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;
import freed.utils.StringUtils;

/**
 * Created by KillerInk on 23.12.2017.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CameraValuesChangedCaptureCallback extends CameraCaptureSession.CaptureCallback implements HistogramFeed
{
    private final boolean DO_LOG = false;



    private void log(String s)
    {
        if (DO_LOG)
            Log.d(TAG,s);
    }


    public interface WaitForFirstFrameCallback
    {
        void onFirstFrame();
    }

    public interface WaitForAe_Af_Lock
    {
        void on_Ae_Af_Lock(AeAfLocker aeAfLocker);
    }


    private CaptureResult captureResult;

    public CaptureResult getCaptureResult() {
        return captureResult;
    }

    public class AeAfLocker
    {
        private boolean aeLocked;
        private boolean afLocked;

        public synchronized void setAeLocked(boolean locked)
        {
            //Log.d(TAG, "setAeLocked:" + locked);
            this.aeLocked = locked;
        }

        public synchronized void setAfLocked(boolean locked)
        {
            //Log.d(TAG, "setAfLocked:" + locked);
            this.afLocked = locked;
        }

        public synchronized boolean getAfLock()
        {
            return this.afLocked;
        }

        public synchronized boolean getAeLock()
        {
            return this.aeLocked;
        }
    }



    private final String TAG = CameraValuesChangedCaptureCallback.class.getSimpleName();
    private final Camera2 camera2Fragment;
    public boolean flashRequired = false;
    int afState;
    int aeState;
    public long currentExposureTime;
    public int currentIso;
    private Pair<Float,Float> focusRanges;
    private float focus_distance;
    private WaitForAe_Af_Lock waitForAe_af_lock;

    private boolean waitForFirstFrame = false;
    private WaitForFirstFrameCallback waitForFirstFrameCallback;

    private boolean waitForFocusLock = false;

    private final int SCAN = 0;
    private final int FOCUSED= 1;
    private final int WAITFORSCAN= 1;
    private int focusState;
    private final AeAfLocker aeAfLocker;
    private HistogramChangedEvent histogramChangedEventListner;
    private final SettingsManager settingsManager;
    private CaptureResultRingBuffer captureResultRingBuffer;

    public CameraValuesChangedCaptureCallback(Camera2 camera2Fragment)
    {
        this.camera2Fragment =camera2Fragment;
        settingsManager = FreedApplication.settingsManager();
        this.aeAfLocker = new AeAfLocker();
    }

    public void setCaptureResultRingBuffer(CaptureResultRingBuffer captureResultRingBuffer)
    {
        this.captureResultRingBuffer = captureResultRingBuffer;
    }

    @Override
    public void setHistogramFeed(HistogramChangedEvent feed) {
        this.histogramChangedEventListner = feed;
    }

    public void setWaitForFocusLock(boolean idel)
    {
        waitForFocusLock = idel;
        focusState = WAITFORSCAN;
    }

    public void setWaitForFirstFrame()
    {
        waitForFirstFrame = true;
    }

    public void setWaitForFirstFrameCallback(WaitForFirstFrameCallback callback)
    {
        this.waitForFirstFrameCallback = callback;
    }

    public void setWaitForAe_af_lock(WaitForAe_Af_Lock callback)
    {
        if (callback != null) {
            log("rest ae af lock");
            aeAfLocker.setAeLocked(false);
            aeAfLocker.setAfLocked(false);
            focusState = WAITFORSCAN;
        }
        else
            log("clear wait for ae af lock");
        this.waitForAe_af_lock = callback;

    }

    public boolean isAF_Locked()
    {
        return aeAfLocker.getAfLock();
    }

    public float[] GetFocusRange()
    {
        float[] ar = new float[3];
        if (focusRanges != null)
        {
            ar[0] = 1/focusRanges.first.floatValue();
            ar[2] = 1/focusRanges.second.floatValue();
            ar[1] = 1/focus_distance;
        }
        return ar;
    }



    @Override
    public void onCaptureCompleted(CameraCaptureSession session,  CaptureRequest request,  TotalCaptureResult result) {
        if (result == null)
            return;
        if (waitForFirstFrame)
        {
            if (waitForFirstFrameCallback != null)
                waitForFirstFrameCallback.onFirstFrame();
            waitForFirstFrame = false;
        }
        if (captureResultRingBuffer != null)
            captureResultRingBuffer.addCaptureResult(result);

        captureResult = result;
        ParameterInterface expotime = camera2Fragment.getParameterHandler().get(SettingKeys.M_EXPOSURE_TIME);
        ParameterInterface iso = camera2Fragment.getParameterHandler().get(SettingKeys.M_MANUAL_ISO);
        if (settingsManager.getFrameWork() == Frameworks.HuaweiCamera2Ex)
        {
            processHuaweiAEValues(result, expotime, iso);
        }
        else if (settingsManager.get(SettingKeys.USE_QCOM_AE).get())
        {
            processQcomAEValues(result, expotime, iso);
        }
        else {
            processDefaultAEValues(result, expotime, iso);
        }

            /*if (result.get(CaptureResult.TONEMAP_CURVE)!=null)
            {

                TonemapCurve curve = result.get(CaptureResult.TONEMAP_CURVE);
                Log.d(TAG,"Curve:" +curve.toString());
                Log.d(TAG,"Red count"+curve.getPointCount(0));
                Log.d(TAG,"Green count"+curve.getPointCount(1));
                Log.d(TAG,"Blue count"+curve.getPointCount(2));
            }
*/
        if (result.get(CaptureResult.LENS_FOCUS_RANGE) != null)
            focusRanges = result.get(CaptureResult.LENS_FOCUS_RANGE);

        //handel focus callback to ui if it was sucessfull. dont reset focusareas or trigger again afstate.
        //else it could happen that it refocus
        processDefaultFocus(result);

        if(result.get(CaptureResult.CONTROL_AE_STATE) != null /*&& aeState != result.get(CaptureResult.CONTROL_AE_STATE)*/)
        {
            aeState = result.get(CaptureResult.CONTROL_AE_STATE);
            flashRequired = false;
            switch (aeState)
            {
                case CaptureResult.CONTROL_AE_STATE_CONVERGED:
                    //SetParameter(CaptureRequest.CONTROL_AE_LOCK, true);
                    log("AESTATE: Converged");
                    aeAfLocker.setAeLocked(true);
                    break;
                case CaptureResult.CONTROL_AE_STATE_FLASH_REQUIRED:
                    flashRequired = true;

                    //SetParameter(CaptureRequest.CONTROL_AE_LOCK, true);
                    log("AESTATE: FLASH_REQUIRED");
                    break;
                case CaptureResult.CONTROL_AE_STATE_INACTIVE:
                    log( "AESTATE: INACTIVE");
                    break;
                case CaptureResult.CONTROL_AE_STATE_LOCKED:
                    log("AESTATE: LOCKED");
                    aeAfLocker.setAeLocked(true);
                    break;
                case CaptureResult.CONTROL_AE_STATE_PRECAPTURE:
                    log("AESTATE: PRECAPTURE");

                    break;
                case CaptureResult.CONTROL_AE_STATE_SEARCHING:
                    log("AESTATE: SEARCHING");

                    break;
            }
        }

        if (camera2Fragment.getParameterHandler().get(SettingKeys.EXPOSURE_LOCK) != null && result.get(CaptureResult.CONTROL_AE_LOCK) != null) {
            String expolock = result.get(CaptureResult.CONTROL_AE_LOCK).toString();
            if (expolock != null && !expolock.equals(camera2Fragment.getParameterHandler().get(SettingKeys.EXPOSURE_LOCK).getStringValue()))
                camera2Fragment.getParameterHandler().get(SettingKeys.EXPOSURE_LOCK).fireStringValueChanged(expolock);
        }

        if (waitForAe_af_lock != null) {
            log("ae locked: " + aeAfLocker.getAeLock() +" af locked: " + aeAfLocker.getAfLock() + " " +Thread.currentThread().getId());
            waitForAe_af_lock.on_Ae_Af_Lock(aeAfLocker);
        }
       /* try {
            if (settingsManager.get(SettingKeys.HISTOGRAM_STATS_QCOM) != null && settingsManager.get(SettingKeys.HISTOGRAM_STATS_QCOM).get() && result.get(CaptureResultQcom.HISTOGRAM_STATS) != null)
            {
                int[] histo = result.get(CaptureResultQcom.HISTOGRAM_STATS);
                if (histogramChangedEventListner != null)
                {
                    histogramChangedEventListner.onHistogramChanged(histo);
                }
            }
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }*/

        /*if (result.get(CaptureResult.CONTROL_AF_REGIONS) != null)
        {
            MeteringRectangle[] rects = result.get(CaptureResult.CONTROL_AF_REGIONS);
            for (MeteringRectangle rectangle : rects)
                if (rectangle.getMeteringWeight() > 0)
                    Log.d(TAG, rectangle.toString());
        }*/

    }



    private String afStates ="";

    private void setAfState(String afState)
    {
        if (!afStates.equals(afState)) {
            log("af : " + afState);
            afStates = afState;
        }
    }

    private void processDefaultFocus(TotalCaptureResult result) {
        if (result.get(CaptureResult.CONTROL_AF_STATE) != null /*&& afState != result.get(CaptureResult.CONTROL_AF_STATE)*/)
        {
            afState =  result.get(CaptureResult.CONTROL_AF_STATE);
            switch (afState)
            {
                case CaptureRequest.CONTROL_AF_STATE_INACTIVE:
                    setAfState("INACTIVE");
                    break;
                case CaptureRequest.CONTROL_AF_STATE_PASSIVE_SCAN:
                    setAfState("PASSIVE_SCAN");
                    focusState = SCAN;
                    aeAfLocker.setAfLocked(false);
                    break;
                case CaptureRequest.CONTROL_AF_STATE_PASSIVE_FOCUSED:
                    setAfState("PASSIVE_FOCUSED");
                    aeAfLocker.setAfLocked(true);
                    processFocus(true);
                    break;
                case CaptureRequest.CONTROL_AF_STATE_ACTIVE_SCAN:
                    setAfState("ACTIVE_SCAN");
                    aeAfLocker.setAfLocked(false);
                    focusState = SCAN;
                    break;
                case CaptureRequest.CONTROL_AF_STATE_FOCUSED_LOCKED:
                    setAfState("FOCUSED_LOCKED");
                    aeAfLocker.setAfLocked(true);
                    processFocus(true);
                    break;
                case CaptureRequest.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED:
                    setAfState("NOT_FOCUSED_LOCKED");
                    aeAfLocker.setAfLocked(true);
                    processFocus(false);
                    break;
                case CaptureRequest.CONTROL_AF_STATE_PASSIVE_UNFOCUSED:
                    setAfState("PASSIVE_UNFOCUSED");
                    aeAfLocker.setAfLocked(false);
                    break;
            }
            if (result.get(TotalCaptureResult.LENS_FOCUS_DISTANCE) != null && result.get(TotalCaptureResult.CONTROL_AF_MODE) != TotalCaptureResult.CONTROL_AF_MODE_OFF) {
                try {
                    focus_distance = result.get(TotalCaptureResult.LENS_FOCUS_DISTANCE);
                    camera2Fragment.getParameterHandler().get(SettingKeys.M_FOCUS).fireStringValueChanged(StringUtils.getMeterString(1 / focus_distance));
                } catch (NullPointerException ex) {
                    Log.v(TAG, "cant get focus distance");
                }

            }
        }
    }

    private void processFocus(boolean focus_is_locked) {
            if (camera2Fragment.getFocusHandler().focusEvent != null && waitForFocusLock) {

                camera2Fragment.getFocusHandler().focusEvent.FocusFinished(focus_is_locked);
               /* if (focus_is_locked)
                    camera2Fragment.captureSessionHandler.SetPreviewParameter(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_IDLE,true);*/
            }
            waitForFocusLock = false;
    }


    private boolean expotimeVisible(ParameterInterface expotime)
    {
        return expotime.getViewState() == AbstractParameter.ViewState.Visible || expotime.getViewState() == AbstractParameter.ViewState.Enabled;
    }

    private void processDefaultAEValues( TotalCaptureResult result, ParameterInterface expotime, ParameterInterface iso) {
        if (expotime != null && expotimeVisible(expotime) || expotime.getViewState() == AbstractParameter.ViewState.Disabled) {
            if (result != null && result.getKeys().size() > 0) {
                readExpotime(result, expotime);
                readIso(result, iso);
            }
        }
    }

    private void readIso(TotalCaptureResult result, ParameterInterface iso) {
        try {
            int isova = result.get(TotalCaptureResult.SENSOR_SENSITIVITY);
            currentIso = isova;
            iso.fireStringValueChanged("(A) " + isova);
            //Log.v(TAG, "Iso: " + result.get(TotalCaptureResult.SENSOR_SENSITIVITY));
        } catch (NullPointerException ex) {
            //Log.v(TAG, "cant get iso");
        }
    }

    private void processHuaweiAEValues(TotalCaptureResult result, ParameterInterface expotime, ParameterInterface iso) {
        if (expotime.getIntValue() == 0) {
            Long expoTime = result.get(CaptureResult.SENSOR_EXPOSURE_TIME);
            if (expoTime != null) {
                currentExposureTime = expoTime;
                expotime.fireStringValueChanged(getShutterStringNS(expoTime));
            }
        }
        if (iso.getIntValue() == 0)
        {
            Integer isova = result.get(CaptureResult.SENSOR_SENSITIVITY);
            if(isova != null) {
                currentIso = isova;
                iso.fireStringValueChanged(String.valueOf(isova));
            }
        }
    }

    private void processQcomAEValues(TotalCaptureResult result, ParameterInterface expotime, ParameterInterface iso) {
        ParameterHandlerApi2 p = camera2Fragment.getParameterHandler();
        AeManager ae = p.getAeManagerCamera2();
        if (result != null && result.getKeys().size() > 0) {
            if (ae.getActiveAeState() != AeStates.shutter_priority && ae.getActiveAeState() != AeStates.manual) {
                readExpotime(result, expotime);
            }
            if (ae.getActiveAeState() != AeStates.iso_priority && ae.getActiveAeState() != AeStates.manual)
                readIso(result, iso);
        }
    }

    private void readExpotime(TotalCaptureResult result, ParameterInterface expotime) {
        try {
            long expores = result.get(TotalCaptureResult.SENSOR_EXPOSURE_TIME);
            currentExposureTime = expores;
            if (expores != 0) {
                expotime.fireStringValueChanged("(A) "+getShutterStringNS(expores));
            } else
                expotime.fireStringValueChanged("1/60");

            //Log.v(TAG, "ExposureTime: " + result.get(TotalCaptureResult.SENSOR_EXPOSURE_TIME));
        } catch (Exception ex) {
            //Log.v(TAG, "cant get expo time");
        }
    }

    private String getShutterStringNS(long val)
    {
        if (val > 1000000000) {
            return "" + val / 1000000000;
        }
        int i = (int)(0.5D + 1.0E9F / val);
        return "1/" + i;
    }

}
