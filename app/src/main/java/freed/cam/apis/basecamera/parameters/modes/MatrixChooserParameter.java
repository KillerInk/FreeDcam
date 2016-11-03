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

import android.content.res.Resources;
import android.util.Log;

import com.troop.freedcam.R;

import java.io.File;
import java.util.HashMap;

import freed.dng.CustomMatrix;
import freed.utils.Logger;
import freed.utils.StringUtils;

/**
 * Created by troop on 02.05.2016.
 */
public class MatrixChooserParameter extends AbstractModeParameter
{
    public static final String NEXUS6 = "Nexus6";
    public static final String G4 = "G4";
    public static final String IMX214 = "IMX214";
    public static final String IMX230 = "IMX230";
    public static final String OmniVision = "OmniVision";
    public static final String OV5648 = "OV5648";
    public static final String LumingonOV = "LumingonOV";
    public static final String Neutral = "Neutral";
    public static final String IMX135 = "IMX135";
    public static final String IMX298 = "IMX298";
    public static final String IMX220 = "IMX220";
    public static final String IMX230_QC = "IMX230_QC";
    public static final String OV4688 = "OV4688";
    private final HashMap<String, CustomMatrix> custommatrixes;
    private String currentval = "off";
    private boolean isSupported;

    final String TAG = MatrixChooserParameter.class.getSimpleName();
    public MatrixChooserParameter(Resources resources)
    {
        custommatrixes = new HashMap<>();
        custommatrixes.put("off",null);
        addDefaultMatrixes(custommatrixes, resources);
        File confFolder = new File(StringUtils.GetFreeDcamConfigFolder+"matrix/");
        if (confFolder != null) {
            if (!confFolder.exists())
                confFolder.mkdir();
            File[] files = confFolder.listFiles();
            try {
                if (files != null && files.length > 0) {
                    for (File f : files) {
                        custommatrixes.put(f.getName(), CustomMatrix.loadCustomMatrixFromFile(f));
                    }
                }
            }
            catch (NullPointerException ex)
            {
                Logger.exception(ex);
            }

        }
        if (custommatrixes.size() >0)
            isSupported = true;
    }

    private void addDefaultMatrixes(HashMap map,Resources resources)
    {
        map.put(NEXUS6, CustomMatrix.getMatrixFromStringArray(resources.getStringArray(R.array.matrix_nexus6)));
        map.put(G4, CustomMatrix.getMatrixFromStringArray(resources.getStringArray(R.array.matrix_lg_g4)));
        map.put(IMX214, CustomMatrix.getMatrixFromStringArray(resources.getStringArray(R.array.matrix_imx214)));
        map.put(IMX230, CustomMatrix.getMatrixFromStringArray(resources.getStringArray(R.array.matrix_imx230)));
        map.put(OV5648, CustomMatrix.getMatrixFromStringArray(resources.getStringArray(R.array.matrix_ov5648)));
        map.put(OmniVision,CustomMatrix.getMatrixFromStringArray(resources.getStringArray(R.array.matrix_omnivision)));
        map.put(Neutral,CustomMatrix.getMatrixFromStringArray(resources.getStringArray(R.array.matrix_neutral)));
        map.put(LumingonOV,CustomMatrix.getMatrixFromStringArray(resources.getStringArray(R.array.matrix_ovlumingon)));

        map.put(IMX135,CustomMatrix.getMatrixFromStringArray(resources.getStringArray(R.array.IMX135_MATRIX_F2_2)));
        map.put(IMX298,CustomMatrix.getMatrixFromStringArray(resources.getStringArray(R.array.IMX298_MATRIX_F2_0)));
        map.put(IMX220,CustomMatrix.getMatrixFromStringArray(resources.getStringArray(R.array.IMX220_MATRIX_F2_0)));
        map.put(IMX230_QC,CustomMatrix.getMatrixFromStringArray(resources.getStringArray(R.array.IMX230_MATRIX_F2_0)));
        map.put(OV4688,CustomMatrix.getMatrixFromStringArray(resources.getStringArray(R.array.OV4688_F_2)));
    }

    @Override
    public boolean IsSupported() {
        return isSupported;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        currentval = valueToSet;
        BackgroundValueHasChanged(currentval);
    }

    @Override
    public String GetValue() {
        return currentval;
    }

    @Override
    public String[] GetValues()
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
        if (currentval.equals("off"))
            return custommatrixes.get(key);
        else
            return custommatrixes.get(currentval);
    }

    public CustomMatrix GetCustomMatrixNotOverWritten(String key)
    {
            return custommatrixes.get(key);
    }

 }
