package com.troop.freedcam.camera.parameters.modes;

import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;

/**
 * Created by troop on 11.01.2015.
 */
public class SimpleModeParameter extends AbstractModeParameter
{

        public boolean IsSupported() {
            return true;
        }

        public void SetValue(String valueToSet, boolean setToCamera) {

        }

        public String GetValue() {
            return null;
        }

        public String[] GetValues() {
            return new String[0];
        }

}
