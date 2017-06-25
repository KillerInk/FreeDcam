package freed.cam.apis.basecamera.parameters;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;

import freed.cam.apis.basecamera.CameraWrapperInterface;

/**
 * Created by troop on 18.06.2017.
 */

public abstract class AbstractParameter implements ParameterInterface {

    /**
     * Listners that attached to that parameter
     */
    private final List<ParameterEvents> listners;
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
    /**
     * holds the state if the parameter is supported
     */
    protected boolean isSupported;
    /**
     * holds the state if the parameter should be visible to ui
     */
    protected boolean isVisible = true;

    protected boolean isNotReadOnly;

    private Handler mainHandler;

    public AbstractParameter()
    {
        mainHandler = new Handler(Looper.getMainLooper());
        listners = new ArrayList<>();
    }


    public AbstractParameter(CameraWrapperInterface cameraUiWrapper)
    {
        this();
        this.cameraUiWrapper = cameraUiWrapper;

    }

    /**
     * Add and listner that get informed when somthings happen
     * @param eventListner that gets informed
     */
    public void addEventListner(ParameterEvents eventListner)
    {
        if (!listners.contains(eventListner))
            listners.add(eventListner);
    }
    /**
     * Remove the listner
     * @param eventListner that gets informed
     */
    public void removeEventListner(ParameterEvents eventListner)
    {
        if (listners.contains(eventListner))
            listners.remove(eventListner);
    }

    public void fireIntValueChanged(int current)
    {
        currentInt = current;
        for (int i = 0; i< listners.size(); i ++)
        {
            if (listners.get(i) == null)
            {
                listners.remove(i);
                i--;

            }
            else {
                final ParameterEvents lis = listners.get(i);
                final int cur = current;
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        lis.onIntValueChanged(cur);
                    }
                });
            }

        }
    }

    public void fireStringValueChanged(String value)
    {
        currentString = value;
        for (int i = 0; i< listners.size(); i ++)
        {
            if (listners.get(i) == null)
            {
                listners.remove(i);
                i--;

            }
            else {
                final ParameterEvents lis = listners.get(i);
                final String cur = value;
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        lis.onStringValueChanged(cur);
                    }
                });
            }
        }
    }

    public void fireIsSupportedChanged(boolean value)
    {
        isSupported = value;
        for (int i = 0; i< listners.size(); i ++)
        {
            if (listners.get(i) == null)
            {
                listners.remove(i);
                i--;

            }
            else {
                final ParameterEvents lis = listners.get(i);
                final boolean cur = value;
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        lis.onIsSupportedChanged(cur);
                    }
                });
            }
        }
    }
    public void fireIsReadOnlyChanged(boolean value)
    {
        isNotReadOnly = value;
        for (int i = 0; i< listners.size(); i ++)
        {
            if (listners.get(i) == null)
            {
                listners.remove(i);
                i--;

            }
            else {
            final ParameterEvents lis = listners.get(i);
            final boolean cur = value;
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    lis.onIsSetSupportedChanged(cur);
                }
            });
            }
        }
    }

    public void fireStringValuesChanged(String[] value)
    {
        stringvalues = value;
        for (int i = 0; i< listners.size(); i ++)
        {
            if (listners.get(i) == null)
            {
                listners.remove(i);
                i--;

            }
            else {
                final ParameterEvents lis = listners.get(i);
                final String[] cur = value;
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        lis.onValuesChanged(cur);
                    }
                });
            }
        }
    }

    /**
     *
     * @return true if the parameter is supported
     */
    @Override
    public boolean IsSupported() {
        return isSupported;
    }

    /**
     * if true the parameter can get set and is readable
     * if false the parameter is read only
     * @return  parameter can set
     */
    public boolean IsSetSupported() {return isNotReadOnly;}

    /**
     *
     * @return the visiblity state for the ui item
     */
    @Override
    public boolean IsVisible() {
        return isVisible;
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
        /*if (stringvalues == null || stringvalues.length == 0)
            return null;
        if (currentInt > stringvalues.length)
            return stringvalues[currentInt - stringvalues.length/2];
        else
            return stringvalues[currentInt];*/
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

    @Override
    public void SetValue(int valueToSet)
    {
        fireIntValueChanged(valueToSet);
        currentInt = valueToSet;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera) {
        currentString = valueToSet;
        fireStringValueChanged(currentString);
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
