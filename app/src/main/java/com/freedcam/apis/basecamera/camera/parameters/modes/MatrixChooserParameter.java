package com.freedcam.apis.basecamera.camera.parameters.modes;

import android.os.Handler;
import android.util.Log;

import com.freedcam.apis.basecamera.camera.parameters.modes.AbstractModeParameter;
import com.freedcam.utils.Logger;
import com.freedcam.utils.StringUtils;
import com.troop.androiddng.CustomMatrix;
import com.troop.androiddng.Matrixes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by troop on 02.05.2016.
 */
public class MatrixChooserParameter extends AbstractModeParameter
{
    public final static String NEXUS6 = "Nexus6";
    public final static String G4 = "G4";
    public final static String IMX214 = "IMX214";
    public final static String IMX230 = "IMX230";
    public final static String OmniVision = "OmniVision";
    public final static String Neutral = "Neutral";
    private HashMap<String, CustomMatrix> custommatrixes;
    private String currentval = "off";
    private boolean isSupported =false;

    final String TAG = MatrixChooserParameter.class.getSimpleName();
    public MatrixChooserParameter(Handler uiHandler)
    {
        super(uiHandler);
        File confFolder = new File(StringUtils.GetFreeDcamConfigFolder+"matrix/");
        if (!confFolder.exists())
            confFolder.mkdir();
        File[] files = confFolder.listFiles();
        custommatrixes = new HashMap<>();
        custommatrixes.put("off",null);
        addDefaultMatrixes(custommatrixes);
        if(files != null || files.length > 0)
        {
            for (File f: files)
            {
                custommatrixes.put(f.getName(), CustomMatrix.loadCustomMatrixFromFile(f));
            }
        }
        if (custommatrixes.size() >0)
            isSupported = true;
    }

    private void addDefaultMatrixes(HashMap map)
    {
        map.put(NEXUS6, new CustomMatrix(
                Nex6CCM1,
                Nex6CCM2,
                Nex6NM,
                Nexus6_foward_matrix1,
                Nexus6_foward_matrix2,
                Nexus6_reduction_matrix1,
                Nexus6_reduction_matrix2,
                Nexus6_noise_3x1_matrix));
        map.put(G4, new CustomMatrix(
                G4_identity_matrix1,
                G4_identity_matrix2,
                G4_identity_neutra,
                G4_foward_matrix1,
                G4_foward_matrix2,
                G4_reduction_matrix1,
                G4_reduction_matrix2,
                G4_noise_3x1_matrix));
        map.put(IMX214, new CustomMatrix(
                imx214_identity_matrix1,
                imx214_identity_matrix2,
                imx214_identity_neutra,
                imx214_foward_matrix1,
                imx214_foward_matrix2,
                imx214_reduction_matrix1,
                imx214_reduction_matrix2,
                imx214_3x1_matrix));
        map.put(IMX230, new CustomMatrix(
                imx230_identity_matrix1,
                imx230_identity_matrix2,
                imx230_identity_neutra,
                imx230_foward_matrix1,
                imx230_foward_matrix2,
                imx230_reduction_matrix1,
                imx230_reduction_matrix2,
                imx230_3x1_matrix));
        map.put(OmniVision,new CustomMatrix(
                OV_matrix1,
                OV_matrix2,
                OV_ASSHOT,
                OV_Foward,
                OV_Foward2,
                Nexus6_reduction_matrix1,
                Nexus6_reduction_matrix2,
                OV_NREDUCTION_Matrix));
        map.put(Neutral,new CustomMatrix(
                nocal_color1,
                nocal_color2,
                nocal_nutral,
                null,
                null,
                null,
                null,
                null));
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


    ////////////////////////////////////////////IMX214 Adobe Standard ////////////////////////////////////////////////////

    private final float[]  imx214_identity_matrix1 =
            {
                    1.198400f, -0.692100f, 0.181800f, -0.166300f, 0.885600f, 0.333000f, 0.001700f, 0.119500f, 0.573700f
            };
    private final float[] imx214_identity_matrix2 =
            {
                    0.858600f, -0.200200f, -0.068700f, -0.386900f, 1.203400f, 0.205600f, -0.052900f, 0.212400f, 0.521500f
            };

    private final float[] imx214_identity_neutra =
            {
                    0.6295f, 1f, 0.5108f
            };
    private final float[] imx214_foward_matrix1 =
            {
                    0.664800f, 0.256600f, 0.042900f, 0.197000f, 0.999400f, -0.196400f, -0.089400f, -0.230400f, 1.145000f
            };

    private final float[] imx214_foward_matrix2 =
            {
                    0.661700f, 0.384900f, -0.082300f, 0.240000f, 1.113800f, -0.353800f, -0.006200f, -0.114700f, 0.946000f
            };

    private final float[] imx214_reduction_matrix1 =
            {
                    1, -1, -1,
                    -1, 1, 1,
                    -1, 1, 1
            };

    private final float[] imx214_reduction_matrix2 =
            {
                    1, -1, -1,
                    -1, 1, 1,
                    -1, 1, 1
            };

    private final float[] imx214_3x1_matrix =
            {
                    0.00072030654f, (float) 0, (float) 0.00072030654, 0f,0.00072030654f,0f
            };

    //////////////////////////////end 1+ ////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////IMX230 ////////////////////////////////////////////////////

    private final float[]  imx230_identity_matrix1 =
            {
                    0.609375f, 0.015625f, -0.1328125f, -0.9296875f, 1.828125f, 0.0546875f, -0.4375f, 0.6015625f, 0.5f
            };
    private final float[] imx230_identity_matrix2 =
            {
                    0.875f, -0.0390625f, -0.421875f, -0.890625f, 2.1328125f, -0.4140625f, -0.1875f, 0.421875f, 0.453125f
            };

    private final float[] imx230_identity_neutra =
            {
                    0.6295f, 1f, 0.5108f
            };
    private final float[] imx230_foward_matrix1 =
            {
                    0.8046875f, -0.140625f, 0.296875f, 0.3984375f, 0.484375f, 0.1171875f, 0.15625f, -0.5390625f, 1.2109375f
            };

    private final float[] imx230_foward_matrix2 =
            {
                    0.765625f, -0.2734375f, 0.46875f, 0.3203125f, 0.34375f, 0.3359375f, 0.0546875f, -0.9375f, 1.703125f
            };

    private final float[] imx230_reduction_matrix1 =
            {
                    1, -1, -1,
                    -1, 1, 1,
                    -1, 1, 1
            };

    private final float[] imx230_reduction_matrix2 =
            {
                    1, -1, -1,
                    -1, 1, 1,
                    -1, 1, 1
            };

    private final float[] imx230_3x1_matrix =
            {
                    0.00072030654f, 0f, 0.00072030654f, 0f,0.00072030654f,0f
            };

    //////////////////////////////end 1+ ////////////////////////////////////////////////////////////////

    //////////////////////////////NEXUS 6 1+ ////////////////////////////////////////////////////////////////

    private final float[]  Nex6CCM1 =
            {
                    1.140700f, -0.402200f, -0.234000f, -0.431400f, 1.404000f, 0.014600f, -0.043900f, 0.204700f, 0.570400f
            };
    private final float[] Nex6CCM2 =
            {
                    0.722800f, -0.089300f, -0.097500f, -0.479200f, 1.348100f, 0.138100f, -0.113700f, 0.268000f, 0.560400f
            };

    private final float[] Nex6NM =
            {
                    0.5391f, 1.0000f, 0.6641f
            };

    private final float[] Nexus6_foward_matrix1 =
            {
                    0.6328f, 0.0469f, 0.2813f, 0.1641f, 0.7578f, 0.0781f, -0.0469f, -0.6406f, 1.5078f
            };

    private final float[] Nexus6_foward_matrix2 =
            {
                    0.7578f, 0.0859f, 0.1172f, 0.2734f, 0.8281f, -0.1016f, 0.0156f, -0.2813f, 1.0859f
            };

    private final float[] Nexus6_reduction_matrix1 =
            {
                    1, -1, -1,
                    -1, 1, 1,
                    -1, 1, 1
            };

    private final float[] Nexus6_reduction_matrix2 =
            {
                    1, -1, -1,
                    -1, 1, 1,
                    -1, 1, 1
            };

    private final float[] Nexus6_noise_3x1_matrix =
            {
                    0.00051471478f, 0f, 0.00051471478f, 0f, 0.00051471478f, 0f
            };


    //////////////////////////////////eND nEXUS 6//////////////////////////////////////////////////

    private final float[]  G4_identity_matrix1 =
            {
                    1.15625f, -0.2890625f, -0.3203125f, -0.53125f, 1.5625f,.0625f, -0.078125f, 0.28125f, 0.5625f
            };
    private final float[] G4_identity_matrix2 =
            {
                    0.5859375f, 0.0546875f, -0.125f, -0.6484375f, 1.5546875f, 0.0546875f, -0.2421875f, 0.5625f, 0.390625f
            };

    private final float[] G4_identity_neutra =
            {
                    0.53125f, 1f, 0.640625f
            };
    private final float[] G4_foward_matrix1 =
            {
                    0.820300f, -0.218800f, 0.359400f, 0.343800f, 0.570300f,0.093800f, 0.015600f, -0.726600f, 1.539100f
            };

    private final float[] G4_foward_matrix2 =
            {
                    0.679700f, -0.078100f, 0.359400f, 0.210900f, 0.703100f,0.085900f, -0.046900f, -0.828100f, 1.695300f
            };

    private final float[] G4_reduction_matrix1 =
            {
                    1, -1, -1,
                    -1, 1, 1,
                    -1, 1, 1
            };

    private final float[] G4_reduction_matrix2 =
            {
                    1, -1, -1,
                    -1, 1, 1,
                    -1, 1, 1
            };

    private final float[] G4_noise_3x1_matrix =
            {
                    0.8853462669953089f, (float) 0, (float) 0.8853462669953089f, 0f, 0.8853462669953089f,0f
            };



    ////////////////////////////////   END G4 /////////////////////////////////////////////////////

    //////////////////////////////   Omnivision ////////////////////////////////////////////////////////////////
    private final float[]  OV_matrix1 =
            {
                    1.15625f, -0.421875f, -0.328125f, -0.265625f, 1.3359375f, -0.125f, 0f, 0.1640625f, 0.6328125f
            };
    private final float[] OV_matrix2 =
            {
                    0.671875f, -0.125f, -0.1015625f, -0.34375f, 1.15625f, 0.15625f, -0.0390625f, 0.1953125f, 0.5234375f
            };

    private final float[] OV_ASSHOT =
            {
                    0.5546875f, 1f, 0.515625f
            };
    private final float[] OV_Foward =
            {
                    0.5703125f, 0.078125f, 0.3203125f, 0.0625f, 0.8046875f, 0.1328125f, -0.0625f, -0.5390625f, 1.4296875f
            };

    private final float[] OV_Foward2 =
            {
                    0.671875f, 0.171875f, 0.1171875f, 0.2109375f, 0.953125f, -0.1640625f, -0.0234375f, -0.25f, 1.09375f
            };

    private final float[] OV_REDUCTION =
            {
                    1, -1, -1,
                    -1, 1, 1,
                    -1, 1, 1
            };

    private final float[] OV_REDUCTION2 =
            {
                    1, -1, -1,
                    -1, 1, 1,
                    -1, 1, 1
            };

    private float[] OV_NREDUCTION_Matrix =
            {
                    0.003127599148f, 3.56840528e-005f, 0.003127599148f, 3.56840528e-005f, 0.003127599148f, 3.56840528e-005f
            };


    //////////////////////////////////Omnivision END//////////////////////////////////////////////////

    //
    private final float[] nocal_color1 =
            {
                    1,0,0,
                    0,1,0,
                    0,0,1
            };

    private final float[] nocal_color2 =
            {
                    1,0,0,
                    0,1,0,
                    0,0,1
            };

    private final float[] nocal_nutral =
            {
                    1,1,1
            };
 }
