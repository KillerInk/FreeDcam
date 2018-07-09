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
    void onViewStateChanged(AbstractParameter.ViewState value);

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

}
