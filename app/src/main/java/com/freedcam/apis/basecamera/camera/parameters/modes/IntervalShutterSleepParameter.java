package com.freedcam.apis.basecamera.camera.parameters.modes;

/**
 * Created by troop on 08.01.2016.
 */
public class IntervalShutterSleepParameter extends AbstractModeParameter
{
    private String current = "1 sec";
    public IntervalShutterSleepParameter() {
        super();
    }

    @Override
    public boolean IsSupported() {
        return true;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera) {
        current = valueToSet;
    }

    @Override
    public String GetValue() {
        return current;
    }

    @Override
    public String[] GetValues() {
        return new String[] {/*"off",*/"1 sec","2 sec","3 sec","4 sec","5 sec","6 sec","7 sec","8 sec","9 sec",
                "10 sec","11 sec","12 sec","13 sec","14 sec","15 sec","16 sec","17 sec","18 sec","19 sec","20 sec",
                "21 sec","22 sec","23 sec","24 sec","25 sec","26 sec","27 sec","28 sec","29 sec","30 sec","60 sec","120 sec","240 sec"};
    }
}
