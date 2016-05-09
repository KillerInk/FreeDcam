package com.troop.androiddng;

import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.Logger;
import com.freedcam.utils.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by troop on 02.05.2016.
 */
public class CustomMatrix
{
    public float[] ColorMatrix1;
    public float[] ColorMatrix2;
    public float[] NeutralMatrix;
    public float[] ForwardMatrix1;
    public float[] ForwardMatrix2;
    public float[] ReductionMatrix1;
    public float[] ReductionMatrix2;
    public float[] NoiseReductionMatrix;


    final public static String MEDIAPROFILESPATH = StringUtils.GetFreeDcamConfigFolder+"matrix/";
    private final String TAG = CustomMatrix.class.getSimpleName();
    public CustomMatrix(AppSettingsManager appSettingsManager)
    {
        String CUSTOMATRIXNAME = appSettingsManager.getString(AppSettingsManager.SETTTING_CUSTOMMATRIX);
        File customMAtrix = new File(MEDIAPROFILESPATH+CUSTOMATRIXNAME);
        if (customMAtrix.exists()) {
            loadCustomMatrix(customMAtrix);
        }
    }

    public CustomMatrix(AppSettingsManager appSettingsManager ,float[]matrix1, float[] matrix2, float[]neutral,float[]fmatrix1, float[] fmatrix2,float[]rmatrix1, float[] rmatrix2,float[]noise)
    {
        String CUSTOMATRIXNAME = appSettingsManager.getString(AppSettingsManager.SETTTING_CUSTOMMATRIX);
        if (CUSTOMATRIXNAME != null && !CUSTOMATRIXNAME.equals("off") && !CUSTOMATRIXNAME.equals(""))
        {
            File customMAtrix = new File(MEDIAPROFILESPATH+CUSTOMATRIXNAME);
            if (customMAtrix.exists()) {
                loadCustomMatrix(customMAtrix);
            }
            else
            {
                Logger.d(TAG, "No CustomMediaProfiles found");
                this.ColorMatrix1 = matrix1;
                this.ColorMatrix2 = matrix2;
                this.NeutralMatrix = neutral;
                this.ForwardMatrix1 = fmatrix1;
                this.ForwardMatrix2 = fmatrix2;
                this.ReductionMatrix1 = rmatrix1;
                this.ReductionMatrix2 = rmatrix2;
                this.NoiseReductionMatrix = noise;
            }
        }
        else
        {
            Logger.d(TAG, "No CustomMediaProfiles found");
            this.ColorMatrix1 = matrix1;
            this.ColorMatrix2 = matrix2;
            this.NeutralMatrix = neutral;
            this.ForwardMatrix1 = fmatrix1;
            this.ForwardMatrix2 = fmatrix2;
            this.ReductionMatrix1 = rmatrix1;
            this.ReductionMatrix2 = rmatrix2;
            this.NoiseReductionMatrix = noise;
        }
    }


    /**
     * Loads a customMatrix textfile
     * wich overrides that default one from DngSupportedDevices
     * NOTE: The order of the Items is important
     * ColorMatrix1;
     * ColorMatrix2;
     * NeutralMatrix;
     * ForwardMatrix1;
     * ForwardMatrix2;
     * ReductionMatrix1;
     * ReductionMatrix2;
     * NoiseReductionMatrix;
     */
    public void  loadCustomMatrix(File customMAtrix)
    {

            try {
                Logger.d(TAG, "CustomMediaProfile exists loading....");
                BufferedReader br = null;

                br = new BufferedReader(new FileReader(customMAtrix));

                String line;
                int count = 0;
                while ((line = br.readLine()) != null)
                {
                    if (!line.startsWith("#")) {
                        switch (count)
                        {
                            case 0:
                                ColorMatrix1 = getMatrixFromString(line);
                                break;
                            case 1:
                                ColorMatrix2 = getMatrixFromString(line);
                                break;
                            case 2:
                                NeutralMatrix = getMatrixFromString(line);
                                break;
                            case 3:
                                ForwardMatrix1 = getMatrixFromString(line);
                                break;
                            case 4:
                                ForwardMatrix2 = getMatrixFromString(line);
                                break;
                            case 5:
                                ReductionMatrix1 = getMatrixFromString(line);
                                break;
                            case 6:
                                ReductionMatrix2 = getMatrixFromString(line);
                                break;
                            case 7:
                                NoiseReductionMatrix = getMatrixFromString(line);
                                break;
                        }
                        count++;
                    }
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


    }

    private float[] getMatrixFromString(String m)
    {
        String[] split = m.split(",");
        float[] ar = new float[split.length];
        for (int i = 0; i< split.length; i++)
        {
            ar[i] = Float.parseFloat(split[i]);
        }
        return ar;
    }
}
