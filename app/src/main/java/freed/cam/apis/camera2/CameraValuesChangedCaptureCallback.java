package freed.cam.apis.camera2;

import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Pair;

import com.troop.freedcam.R;

import freed.cam.apis.basecamera.parameters.ParameterInterface;
import freed.settings.Frameworks;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;
import freed.utils.StringUtils;

/**
 * Created by KillerInk on 23.12.2017.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CameraValuesChangedCaptureCallback extends CameraCaptureSession.CaptureCallback
{

    public interface AeCompensationListner
    {
        void onAeCompensationChanged(int aecompensation);
    }

    public interface WaitForFirstFrameCallback
    {
        void onFirstFrame();
    }

    private final String TAG = CameraValuesChangedCaptureCallback.class.getSimpleName();
    private Camera2Fragment camera2Fragment;
    public boolean flashRequired = false;
    int afState;
    int aeState;
    public long currentExposureTime;
    public int currentIso;
    private Pair<Float,Float> focusRanges;
    private float focus_distance;
    private AeCompensationListner aeCompensationListner;

    private boolean waitForFirstFrame = false;
    private WaitForFirstFrameCallback waitForFirstFrameCallback;

    public CameraValuesChangedCaptureCallback(Camera2Fragment camera2Fragment)
    {
        this.camera2Fragment =camera2Fragment;
    }

    public void setWaitForFirstFrame()
    {
        waitForFirstFrame = true;
    }

    public void setWaitForFirstFrameCallback(WaitForFirstFrameCallback callback)
    {
        this.waitForFirstFrameCallback = callback;
    }

    public float[] GetFocusRange()
    {
        float ar[] = new float[3];
        if (focusRanges != null)
        {
            ar[0] = 1/focusRanges.first.floatValue();
            ar[2] = 1/focusRanges.second.floatValue();
            ar[1] = 1/focus_distance;
        }
        return ar;
    }

    public void SetAeCompensationListner(AeCompensationListner aeCompensationListner)
    {
        this.aeCompensationListner = aeCompensationListner;
    }


    @Override
    public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
        if (result == null)
            return;
        if (waitForFirstFrame)
        {
            if (waitForFirstFrameCallback != null)
                waitForFirstFrameCallback.onFirstFrame();
            waitForFirstFrame = false;
        }

        ParameterInterface expotime = camera2Fragment.getParameterHandler().get(SettingKeys.M_ExposureTime);
        ParameterInterface iso = camera2Fragment.getParameterHandler().get(SettingKeys.M_ManualIso);
        if (SettingsManager.getInstance().getFrameWork() == Frameworks.HuaweiCamera2Ex)
        {
            if (expotime.GetValue() == 0) {
                Long expoTime = result.get(CaptureResult.SENSOR_EXPOSURE_TIME);
                if (expoTime != null) {
                    currentExposureTime = expoTime;
                    expotime.fireStringValueChanged(getShutterStringNS(expoTime));
                }
            }
            if (iso.GetValue() == 0)
            {
                Integer isova = result.get(CaptureResult.SENSOR_SENSITIVITY);
                if(isova != null) {
                    currentIso = isova;
                    iso.fireStringValueChanged(String.valueOf(isova));
                }
            }
        }
        else {
            if (expotime != null && expotime.IsSupported()) {
                if (result != null && result.getKeys().size() > 0) {
                    try {
                        if (!camera2Fragment.getParameterHandler().get(SettingKeys.ExposureMode).GetStringValue().equals(camera2Fragment.getContext().getString(R.string.off))
                                && !camera2Fragment.getParameterHandler().get(SettingKeys.CONTROL_MODE).equals(camera2Fragment.getContext().getString(R.string.off))) {
                            try {
                                long expores = result.get(TotalCaptureResult.SENSOR_EXPOSURE_TIME);
                                currentExposureTime = expores;
                                if (expores != 0) {
                                    expotime.fireStringValueChanged(getShutterStringNS(expores));
                                } else
                                    expotime.fireStringValueChanged("1/60");

                                //Log.v(TAG, "ExposureTime: " + result.get(TotalCaptureResult.SENSOR_EXPOSURE_TIME));
                            } catch (Exception ex) {
                                Log.WriteEx(ex);
                            }
                            try {
                                int isova = result.get(TotalCaptureResult.SENSOR_SENSITIVITY);
                                currentIso = isova;
                                iso.fireStringValueChanged("" + isova);
                                //Log.v(TAG, "Iso: " + result.get(TotalCaptureResult.SENSOR_SENSITIVITY));
                            } catch (NullPointerException ex) {
                                Log.WriteEx(ex);
                            }
                            try {
                                focus_distance = result.get(TotalCaptureResult.LENS_FOCUS_DISTANCE);
                                camera2Fragment.getParameterHandler().get(SettingKeys.M_Focus).fireStringValueChanged(StringUtils.getMeterString(1 / focus_distance));
                            } catch (NullPointerException ex) {
                                Log.WriteEx(ex);
                            }
                        }
                    } catch (NullPointerException ex) {
                        Log.WriteEx(ex);
                    }
                }
            }
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
        if (result.get(CaptureResult.CONTROL_AF_STATE) != null && afState != result.get(CaptureResult.CONTROL_AF_STATE))
        {
            afState =  result.get(CaptureResult.CONTROL_AF_STATE);
            String state = "";
            switch (afState)
            {
                case 0:
                    state ="INACTIVE";
                    break;
                case 1:
                    state = "PASSIVE_SCAN";
                    break;
                case 2:
                    state = "PASSIVE_FOCUSED";
                    break;
                case 3:
                    state="ACTIVE_SCAN";
                    break;
                case 4:
                    state = "FOCUSED_LOCKED";
                    camera2Fragment.captureSessionHandler.SetParameterRepeating(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_IDLE,true);
                    if (camera2Fragment.getFocusHandler().focusEvent != null)
                        camera2Fragment.getFocusHandler().focusEvent.FocusFinished(true);
                    break;
                case 5:
                    state = "NOT_FOCUSED_LOCKED";
                    camera2Fragment.captureSessionHandler.SetParameterRepeating(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_IDLE,true);
                    if (camera2Fragment.getFocusHandler().focusEvent != null)
                        camera2Fragment.getFocusHandler().focusEvent.FocusFinished(false);
                    break;
                case 6:
                    state ="PASSIVE_UNFOCUSED";
                    break;
            }
            Log.d(TAG, "new AF_STATE :"+state);
        }
        if(result.get(CaptureResult.CONTROL_AE_STATE) != null && aeState != result.get(CaptureResult.CONTROL_AE_STATE))
        {

            aeState = result.get(CaptureResult.CONTROL_AE_STATE);
            flashRequired = false;
            switch (aeState)
            {
                case CaptureResult.CONTROL_AE_STATE_CONVERGED:
                    //SetParameter(CaptureRequest.CONTROL_AE_LOCK, true);
                    Log.v(TAG, "AESTATE: Converged");
                    break;
                case CaptureResult.CONTROL_AE_STATE_FLASH_REQUIRED:
                    flashRequired = true;
                    //SetParameter(CaptureRequest.CONTROL_AE_LOCK, true);
                    Log.v(TAG, "AESTATE: FLASH_REQUIRED");
                    break;
                case CaptureResult.CONTROL_AE_STATE_INACTIVE:
                    Log.v(TAG, "AESTATE: INACTIVE");

                    break;
                case CaptureResult.CONTROL_AE_STATE_LOCKED:
                    Log.v(TAG, "AESTATE: LOCKED");
                    break;
                case CaptureResult.CONTROL_AE_STATE_PRECAPTURE:
                    Log.v(TAG, "AESTATE: PRECAPTURE");
                    break;
                case CaptureResult.CONTROL_AE_STATE_SEARCHING:
                    Log.v(TAG, "AESTATE: SEARCHING");
                    break;
            }
        }
        if (result.get(CaptureResult.CONTROL_AE_EXPOSURE_COMPENSATION)!= null && aeCompensationListner != null)
        {
            aeCompensationListner.onAeCompensationChanged(result.get(CaptureResult.CONTROL_AE_EXPOSURE_COMPENSATION));
            //Log.d(TAG,"ExpoCompensation:" + );
        }

        if (camera2Fragment.getParameterHandler().get(SettingKeys.ExposureLock) != null && result.get(CaptureResult.CONTROL_AE_LOCK) != null) {
            String expolock = result.get(CaptureResult.CONTROL_AE_LOCK).toString();
            if (expolock != null)
                camera2Fragment.getParameterHandler().get(SettingKeys.ExposureLock).fireStringValueChanged(expolock);
        }
    }

    private String getShutterStringNS(long val)
    {
        if (val > 1000000000) {
            return "" + val / 1000000000;
        }
        int i = (int)(0.5D + 1.0E9F / val);
        return "1/" + Integer.toString(i);
    }

}
