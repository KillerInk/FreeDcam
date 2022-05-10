package freed.cam.apis.basecamera.parameters.ae;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.settings.SettingKeys;
import freed.utils.Log;

/**
 * Created by KillerInk on 29.12.2017.
 */

public abstract class AeManager<C extends CameraWrapperInterface> implements AeManagerInterface
{
    protected C cameraWrapperInterface;
    protected ManualIso manualIso;
    protected ManualExposureTime manualExposureTime;
    protected ExposureCompensation exposureCompensation;
    protected AeStates activeAeState = AeStates.auto;

    public AeManager(C cameraWrapperInterface)
    {
        this.cameraWrapperInterface =cameraWrapperInterface;
        manualIso = new ManualIso(cameraWrapperInterface);
        manualExposureTime = new ManualExposureTime(cameraWrapperInterface);
        exposureCompensation =new ExposureCompensation(cameraWrapperInterface);
    }

    public AbstractParameter getIso()
    {
        return manualIso;
    }

    public AbstractParameter getExposureTime()
    {
        return manualExposureTime;
    }

    public AbstractParameter getExposureCompensation()
    {
        return exposureCompensation;
    }

    @Override
    public abstract void setExposureTime(int valueToSet, boolean setToCamera);

    @Override
    public abstract void setIso(int valueToSet, boolean setToCamera);

    @Override
    public abstract void setExposureCompensation(int valueToSet, boolean setToCamera);

    @Override
    public abstract void setAeMode(AeStates aeState);

    @Override
    public AeStates getActiveAeState() {
        return activeAeState;
    }

    @Override
    public boolean isExposureCompensationWriteable() {
        return activeAeState == AeStates.iso_priority || activeAeState == AeStates.shutter_priority || activeAeState == AeStates.auto;
    }

    @Override
    public boolean isExposureTimeWriteable() {
        return activeAeState == AeStates.shutter_priority || activeAeState == AeStates.manual;
    }

    @Override
    public boolean isIsoWriteable() {
        return activeAeState == AeStates.iso_priority || activeAeState == AeStates.manual || activeAeState == AeStates.auto;
    }

    public class ManualExposureTime extends AbstractParameter
    {
        public final String TAG = ManualExposureTime.class.getSimpleName();
        public ManualExposureTime(CameraWrapperInterface cameraUiWrapper) {
            super(cameraUiWrapper,SettingKeys.M_ExposureTime);
        }

        @Override
        public int getIntValue()
        {
            return currentInt;
        }



        @Override
        public String[] getStringValues() {
            return stringvalues;
        }

        @Override
        public void setValue(int valueToSet, boolean setToCamera)
        {
            super.setValue(valueToSet,setToCamera);
            setExposureTime(valueToSet,setToCamera);
        }

        @Override
        public ViewState getViewState() {
            if (isExposureTimeWriteable())
                return ViewState.Enabled;
            else
                return ViewState.Disabled;
        }

    }


    public class ManualIso extends AbstractParameter
    {
        final String TAG = ManualIso.class.getSimpleName();
        public ManualIso(CameraWrapperInterface cameraUiWrapper) {
            super(cameraUiWrapper,SettingKeys.M_ManualIso);
            currentInt = 0;
        }

        @Override
        public void setValue(int valueToSet, boolean setToCamera)
        {
            super.setValue(valueToSet,setToCamera);
            //workaround when value was -1 to avoid outofarray ex
            Log.d(TAG, "set Manual Iso: " +valueToSet);
            if (valueToSet == -1)
                valueToSet = 0;
            //////////////////////
            setIso(valueToSet,setToCamera);
        }

        @Override
        public ViewState getViewState() {
            if (isIsoWriteable())
                return ViewState.Enabled;
            else
                return ViewState.Disabled;
        }
    }

    public class ExposureCompensation extends AbstractParameter
    {
        final String TAG = ExposureCompensation.class.getSimpleName();

        public ExposureCompensation(CameraWrapperInterface cameraUiWrapper) {
            super(cameraUiWrapper,SettingKeys.M_ExposureCompensation);
            currentInt = stringvalues.length / 2;
        }



        @Override
        public int getIntValue() {
            return super.getIntValue();
        }

        @Override
        public String getStringValue() {
            if (currentInt > stringvalues.length)
                currentInt = stringvalues.length/2;
            try {
                return stringvalues[currentInt];
            }
            catch (NullPointerException e)
            {
                return "0";
            }
        }

        @Override
        public void setValue(int valueToSet, boolean setToCamera) {
            if (valueToSet >= stringvalues.length)
                valueToSet = stringvalues.length/2;
            super.setValue(valueToSet,setToCamera);
            currentInt = valueToSet;
            setExposureCompensation(valueToSet - stringvalues.length/2,setToCamera);
            fireStringValueChanged(stringvalues[valueToSet]);
        }

        @Override
        public ViewState getViewState() {
            if (isExposureCompensationWriteable())
                return ViewState.Enabled;
            else
                return ViewState.Disabled;
        }
    }

}
