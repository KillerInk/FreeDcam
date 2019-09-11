package freed.cam.apis.basecamera.parameters;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.events.ValueChangedEvent;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.settings.mode.SettingMode;

/**
 * Created by troop on 18.06.2017.
 */

public abstract class AbstractParameter implements ParameterInterface {

    public enum ViewState{
        Visible,
        Hidden,
        Disabled,
        Enabled
    }

    private ViewState viewState = ViewState.Hidden;

    /**
     * the parameterhandler
     */
    protected CameraWrapperInterface cameraUiWrapper;
    /**
     * contains the values that are supported by the parameters
     */
    protected String[] stringvalues;
    /**
     * the key_value that is currently in use by the parameters
     */
    protected String currentString ="";
    /**
     * the true integer key_value that represents the the currentstring in the array stringvalues
     * so on negative values  -1 = stringarray[stringarray/2 + -1] must get used
     */
    protected int currentInt;

    protected SettingKeys.Key key;
    protected SettingMode settingMode;

    public AbstractParameter(SettingKeys.Key  key)
    {
        this.key = key;
        if (key == null || SettingsManager.get(key) == null)
            return;
        if (SettingsManager.get(key) instanceof  SettingMode) {
            this.settingMode = (SettingMode) SettingsManager.get(key);
            stringvalues = settingMode.getValues();
            if (settingMode.isSupported())
                setViewState(ViewState.Visible);
            currentString = settingMode.get();
        }
    }

    @Override
    public SettingKeys.Key getKey()
    {
        return key;
    }

    @Override
    public void startListning() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void stopListning() {
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void setViewState(ViewState viewState) {
        this.viewState = viewState;
        fireViewStateChanged(viewState);
    }

    public AbstractParameter(CameraWrapperInterface cameraUiWrapper, SettingKeys.Key  settingMode)
    {
        this(settingMode);
        this.cameraUiWrapper = cameraUiWrapper;
    }

    public void fireIntValueChanged(int current)
    {
        currentInt = current;
        EventBus.getDefault().post(new ValueChangedEvent<>(key,current, Integer.class));
    }

    public void fireStringValueChanged(String value)
    {
        currentString = value;
        EventBus.getDefault().post(new ValueChangedEvent<>(key,value, String.class));
    }

    @Override
    public void fireViewStateChanged(ViewState value)
    {
        viewState = value;
        EventBus.getDefault().post(new ValueChangedEvent<>(key,value, ViewState.class));
    }

    @Override
    public void fireStringValuesChanged(String[] value)
    {

        stringvalues = value;
        EventBus.getDefault().post(new ValueChangedEvent<>(key,value, String[].class));
    }

    /**
     *
     * @return true if the parameter is supported
     */
    @Override
    public ViewState getViewState() {
        return viewState;
    }

    /**
     *
     * @return returns the current key_value as int
     */
    @Override
    public int GetValue() {
        return currentInt;
    }

    /**
     *
     * @return returns the current key_value as string
     */
    @Override
    public String GetStringValue()
    {
        if (currentString == null)
            return "";
        return currentString;
    }

    /**
     *
     * @return returns all values as StringArray
     */
    @Override
    public String[] getStringValues() { return stringvalues;}




    /**
     * set value to camera async
       override that when you need dont need to run in it background
     * @param valueToSet the int value to set
     * @param setToCamera
     */
    @Override
    public void SetValue(int valueToSet, boolean setToCamera)
    {
        setValue(valueToSet,setToCamera);
    }

    /**
     * runs async gets called from SetValue
     * override that when you want to set stuff in background
     * @param valueToSet
     * @param setToCamera
     */
    protected void setValue(int valueToSet, boolean setToCamera)
    {
        fireIntValueChanged(valueToSet);
        currentInt = valueToSet;
        if (settingMode != null)
            settingMode.set(String.valueOf(valueToSet));
    }

    /**
     * set value to camera async
     override that when you need dont need to run in it background
     * @param valueToSet to the camera
     * @param setToCamera not needed anymore?
     */
    @Override
    public void SetValue(String valueToSet, boolean setToCamera) {
        setValue(valueToSet,setToCamera);
    }

    /**
     * runs async gets called from SetValue
     * override that when you want to set stuff in background
     * @param valueToSet
     * @param setToCamera
     */
    protected void setValue(String valueToSet, boolean setToCamera)
    {
        currentString = valueToSet;
        fireStringValueChanged(currentString);
        if (settingMode != null)
            settingMode.set(valueToSet);
    }

    /**
     * Creates a string array from the passed arguments
     * @param min the minimum value to start
     * @param max the maximum value to end
     * @param step the step wich get applied from min to max
     * @return the string array created
     */
    protected String[] createStringArray(int min,int max, float step)
    {
        ArrayList<String> ar = new ArrayList<>();
        if (step == 0)
            step = 1;
        for (int i = min; i <= max; i+=step)
        {
            ar.add(i+"");
        }
        return ar.toArray(new String[ar.size()]);
    }
}
