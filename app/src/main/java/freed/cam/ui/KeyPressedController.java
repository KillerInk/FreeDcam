package freed.cam.ui;

import android.view.KeyEvent;

import java.util.ArrayList;
import java.util.List;
import freed.cam.apis.CameraApiManager;
import freed.cam.apis.basecamera.parameters.ParameterInterface;
import freed.cam.ui.themesample.handler.UserMessageHandler;
import freed.settings.SettingKeys;
import freed.utils.Log;

public class KeyPressedController
{
    public interface ManualModeChangedEvent
    {
        void onManualModeChanged(SettingKeys.Key key);
    }

    private static final String TAG = KeyPressedController.class.getSimpleName();
    private CameraApiManager cameraApiManager;
    private final List<SettingKeys.Key> supportedManualsModes = new ArrayList();
    private SettingKeys.Key activeKey = SettingKeys.M_Zoom;
    private int activeKeyEvent;
    private ManualModeChangedEvent manualModeChangedEventListner;
    private UserMessageHandler userMessageHandler;

    public KeyPressedController(CameraApiManager cameraApiManager,UserMessageHandler userMessageHandler)
    {
        supportedManualsModes.add(SettingKeys.M_Zoom);
        supportedManualsModes.add(SettingKeys.M_Focus);
        supportedManualsModes.add(SettingKeys.M_ManualIso);
        supportedManualsModes.add(SettingKeys.M_ExposureTime);
        supportedManualsModes.add(SettingKeys.M_ExposureCompensation);
        this.cameraApiManager = cameraApiManager;
        this.userMessageHandler = userMessageHandler;
    }

    public void setManualModeChangedEventListner(ManualModeChangedEvent manualModeChangedEventListner) {
        this.manualModeChangedEventListner = manualModeChangedEventListner;
    }

    //workaround to simulate a long press
    //if repeat is 1 and HeadsetHook triggered the manual mode get changed that is used for vol+/- buttons
    //if repeat is 2 the camera starts/stops its work
    private int repeat = 0;
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        Log.d(TAG, "onKeyDown:" + getKeyCodeString(keyCode));
        // handel first touch down
        if (keyCode != activeKeyEvent) {
            activeKeyEvent = keyCode;
            repeat++;
            return true;
        }
        //handel repeating event when button keep pushed
        else if (activeKeyEvent == KeyEvent.KEYCODE_HEADSETHOOK) {
            repeat++;
            if(repeat == 2 && startWork(activeKeyEvent))
                return true;
            else if (repeat > 2)
                return true;
        }
        return handelVolUpDown(keyCode,2);
    }

    public boolean onKeyUp(int keyCode, KeyEvent keyEvent)
    {
        Log.d(TAG, "onKeyUp:" + getKeyCodeString(keyCode));
        boolean ret = false;
        if (cameraApiManager.getCamera() != null && cameraApiManager.getCamera().getModuleHandler() != null)
        {
            if(keyCode == KeyEvent.KEYCODE_HEADSETHOOK && repeat == 1)
                changeManualMode();
            handelVolUpDown(keyCode,1);
            ret = true;
        }
        activeKeyEvent = 0;
        repeat = 0;
        return ret;
    }

    //seems not to get triggered
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event)
    {
        Log.d(TAG, "onKeyMultiple:" + getKeyCodeString(keyCode) + " repeatCount:" +repeatCount);
        return false;
    }

    //seems not to get triggered
    public boolean onKeyLongPressed(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyLongPressed:" + getKeyCodeString(keyCode));
        return false;  //startWork(keyCode);
    }

    private void fireOnVolDown(int sensitivity)
    {
        ParameterInterface parameter = cameraApiManager.getCamera().getParameterHandler().get(activeKey);
        if (parameter.getIntValue()-sensitivity >= 0)
            parameter.setIntValue(parameter.getIntValue()-sensitivity,true);
    }

    private void fireOnVolUp(int sensitivity)
    {
        ParameterInterface parameter = cameraApiManager.getCamera().getParameterHandler().get(activeKey);
        if (parameter.getIntValue()+sensitivity < parameter.getStringValues().length)
            parameter.setIntValue(parameter.getIntValue()+sensitivity,true);
    }

    private boolean handelVolUpDown(int keyCode,int sensitivity)
    {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
        {
            fireOnVolDown(sensitivity);
            return true;
        }
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
        {
            fireOnVolUp(sensitivity);
            return true;
        }
        return false;
    }

    private boolean startWork(int keyCode)
    {
        if (keyCode == KeyEvent.KEYCODE_3D_MODE
            || keyCode == KeyEvent.KEYCODE_POWER
            || keyCode == KeyEvent.KEYCODE_UNKNOWN
            || keyCode == KeyEvent.KEYCODE_CAMERA
            || keyCode == KeyEvent.KEYCODE_HEADSETHOOK)
        {
            userMessageHandler.sendMSG("Start Work",false);
            cameraApiManager.getCamera().getModuleHandler().startWork();
            return true;
        }
        return false;
    }



    private String getKeyCodeString(int keyCode)
    {
        switch (keyCode)
        {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                return "Vol Down";
            case KeyEvent.KEYCODE_VOLUME_UP:
                return "Vol Up";
            case KeyEvent.KEYCODE_HEADSETHOOK:
                return "Headset Hook";
            default:
                return String.valueOf(keyCode);
        }
    }

    private void changeManualMode() {
        activeKey = getKey();
        userMessageHandler.sendMSG(activeKey.toString(),false);
        if (manualModeChangedEventListner != null)
            manualModeChangedEventListner.onManualModeChanged(activeKey);
    }

    private SettingKeys.Key getKey() {
        int activPos = getActiveKeyPos();
        if (activPos + 1 == supportedManualsModes.size())
        {
            activPos = 0;
        }
        else
            activPos++;
        return supportedManualsModes.get(activPos);
    }


    private int getActiveKeyPos()
    {
        for (int i =  0; i < supportedManualsModes.size(); i++)
        {
            if (supportedManualsModes.get(i) == activeKey) {
                return i;
            }
        }
        return 0;
    }

}
