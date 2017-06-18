package freed.cam.apis.basecamera.parameters;

/**
 * Created by troop on 18.06.2017.
 */

public interface ParameterEvents
{
    /**
     * Notify the listner that the parameter support state has changed
     * freed.cam.ui.themesample.cameraui.ManualButton.java
     * @param value if true the parameter shown in ui is visible
     *              if false the parameter is hidden
     */
    void onIsSupportedChanged(boolean value);
    /**
     * Notify the listner that the parameter can changed/set state has changed
     * freed.cam.ui.themesample.cameraui.ManualButton.java
     * @param value if true the parameter shown in ui is accessible
     *              if false the parameter is not accessible
     */
    void onIsSetSupportedChanged(boolean value);
    /**
     * Notify the listner that the parameter has changed in the background
     * freed.cam.ui.themesample.cameraui.ManualButton.java
     * @param current int key_value representing the array state
     */
    void onIntValueChanged(int current);
    /**
     * Notify the listner that the parameter has changed its values
     * freed.cam.ui.themesample.cameraui.ManualButton.java
     * @param values the new values
     */
    void onValuesChanged(String[] values);
    /**
     * Notify the listner that the parameter has changed its key_value
     * freed.cam.ui.themesample.cameraui.ManualButton.java
     * @param value the new string key_value
     */
    void onStringValueChanged(String value);

    /**
     * Gets fired when the parameter has changed in background
     * @param values
     */
    void onStringValuesChanged(String[] values);
}
