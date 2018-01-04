/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package freed.cam.apis.basecamera.parameters.modes;

import android.text.TextUtils;

import java.util.HashMap;

import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.dng.CustomMatrix;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;

/**
 * Created by troop on 02.05.2016.
 */
public class MatrixChooserParameter extends AbstractParameter
{
    public static final String NEXUS6 = "Nexus6";
    private final HashMap<String, CustomMatrix> custommatrixes;
    private String currentval = "off";
    private boolean isSupported;

    final String TAG = MatrixChooserParameter.class.getSimpleName();

    public MatrixChooserParameter(HashMap<String, CustomMatrix> matrixHashMap)
    {
        super(null);
        this.custommatrixes = matrixHashMap;
        isSupported = true;
        currentval = SettingsManager.get(SettingKeys.MATRIX_SET).get();
        if (TextUtils.isEmpty(currentval))
            currentval = "off";
    }

    @Override
    public boolean IsSupported() {
        return isSupported;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        if (TextUtils.isEmpty(valueToSet))
            return;
        currentval = valueToSet;
        fireStringValueChanged(currentval);
        SettingsManager.get(SettingKeys.MATRIX_SET).set(valueToSet);
    }

    @Override
    public String GetStringValue() {
        return currentval;
    }

    @Override
    public String[] getStringValues()
    {
        return custommatrixes.keySet().toArray(new String[custommatrixes.size()]);
    }

    @Override
    public boolean IsVisible() {
        return isSupported;
    }

    public CustomMatrix GetCustomMatrix(String key)
    {
        Log.d(TAG, "Key: " +key + " Currentvalue: " + currentval);
        return custommatrixes.get(currentval);
    }

    public CustomMatrix GetCustomMatrixNotOverWritten(String key)
    {
            return custommatrixes.get(key);
    }

 }
