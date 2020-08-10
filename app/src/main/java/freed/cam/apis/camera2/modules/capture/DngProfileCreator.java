package freed.cam.apis.camera2.modules.capture;

import android.graphics.Rect;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.params.ColorSpaceTransform;
import android.os.Build;
import android.util.Pair;
import android.util.Rational;

import androidx.annotation.RequiresApi;

import freed.dng.CustomMatrix;
import freed.dng.DngProfile;
import freed.utils.Log;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class DngProfileCreator {

    private final static String TAG = DngProfileCreator.class.getSimpleName();


    public static DngProfile getDngProfile(int rawFormat, int width, int height, CameraCharacteristics characteristics, CustomMatrix customMatrix, CaptureResult captureResult) {
        int black, white,c;
        try {
            black = characteristics.get(CameraCharacteristics.SENSOR_BLACK_LEVEL_PATTERN).getOffsetForIndex(0,0);
        } catch (NullPointerException e) {
            Log.WriteEx(e);
            black = 64;
        }
        try {
            white = characteristics.get(CameraCharacteristics.SENSOR_INFO_WHITE_LEVEL);
        } catch (Exception e) {
            Log.WriteEx(e);
            white = 1023;
        }
        try {
            c = characteristics.get(CameraCharacteristics.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT);
        } catch (Exception e) {
            Log.WriteEx(e);
            c = 0;
        }
        String colorpattern;
        int[] cfaOut = new int[4];
        switch (c)
        {
            case 1:
                colorpattern = DngProfile.GRBG;
                cfaOut[0] = 1;
                cfaOut[1] = 0;
                cfaOut[2] = 2;
                cfaOut[3] = 1;
                break;
            case 2:
                colorpattern = DngProfile.GBRG;
                cfaOut[0] = 1;
                cfaOut[1] = 2;
                cfaOut[2] = 0;
                cfaOut[3] = 1;
                break;
            case 3:
                colorpattern = DngProfile.BGGR;
                cfaOut[0] = 2;
                cfaOut[1] = 1;
                cfaOut[2] = 1;
                cfaOut[3] = 0;
                break;
            default:
                colorpattern = DngProfile.RGGB;
                cfaOut[0] = 0;
                cfaOut[1] = 1;
                cfaOut[2] = 1;
                cfaOut[3] = 2;
                break;
        }
        float[] color2;
        float[] color1;
        float[] neutral = new float[3];
        float[] forward2 = null;
        float[] forward1 = null;
        float[] reduction1 = null;
        float[] reduction2 = null;
        double[]finalnoise = null;
        if (customMatrix == null)
        {
            Log.d(TAG, "No Custom Matrix set, get it from the characteristics");
            //dont catch errors on cc1 cc2 and neutral, these 3 are needed and that case should never happen
            color1 = getFloatMatrix(characteristics.get(CameraCharacteristics.SENSOR_COLOR_TRANSFORM1));
            color2 = getFloatMatrix(characteristics.get(CameraCharacteristics.SENSOR_COLOR_TRANSFORM2));
            Rational[] n = captureResult.get(CaptureResult.SENSOR_NEUTRAL_COLOR_POINT);
            neutral[0] = n[0].floatValue();
            neutral[1] = n[1].floatValue();
            neutral[2] = n[2].floatValue();
            try {
                forward2  = getFloatMatrix(characteristics.get(CameraCharacteristics.SENSOR_FORWARD_MATRIX2));
            } catch (NullPointerException e) {
                Log.WriteEx(e);
                forward2 = null;
            }
            try {
                forward1  = getFloatMatrix(characteristics.get(CameraCharacteristics.SENSOR_FORWARD_MATRIX1));
            } catch (Exception e) {
                Log.WriteEx(e);
                forward1 = null;
            }
            try {
                reduction1 = getFloatMatrix(characteristics.get(CameraCharacteristics.SENSOR_CALIBRATION_TRANSFORM1));
            } catch (Exception e) {
                Log.WriteEx(e);
                reduction1 = null;
            }
            try {
                reduction2 = getFloatMatrix(characteristics.get(CameraCharacteristics.SENSOR_CALIBRATION_TRANSFORM2));
            } catch (Exception e) {
                Log.WriteEx(e);
                reduction2 = null;
            }
            try {
                finalnoise = new double[6];
                getNoiseMatrix(cfaOut, finalnoise,captureResult);
            } catch (Exception e) {
                Log.WriteEx(e);
                finalnoise = null;
            }
            customMatrix = new CustomMatrix(color1,color2,neutral,forward1,forward2,reduction1,reduction2,finalnoise);

        }

        DngProfile profile = new DngProfile(black,white,width, height,rawFormat, colorpattern, 0,
                customMatrix,
                ""
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                Rect activar = characteristics.get(CameraCharacteristics.SENSOR_INFO_PRE_CORRECTION_ACTIVE_ARRAY_SIZE);
                int ar[] = {activar.left, activar.top,activar.right,activar.bottom};
                profile.setActiveArea(ar);
            } catch (Exception ex) {
                Log.WriteEx(ex);
            }
        }


        return profile;
    }

    private static void getNoiseMatrix(int[] cfaOut, double[] finalnoise,CaptureResult captureResult) {
        //noise
        Pair[] p = captureResult.get(CaptureResult.SENSOR_NOISE_PROFILE);
        double[] noiseys = new double[p.length*2];
        int i = 0;
        for (int h = 0; h < p.length; h++)
        {
            noiseys[i++] = (double)p[h].first;
            noiseys[i++] = (double)p[h].second;
        }
        double[] noise = new double[6];
        int[] cfaPlaneColor = {0, 1, 2};
        generateNoiseProfile(noiseys,cfaOut, cfaPlaneColor,3,noise);

        for (i = 0; i < noise.length; i++)
            if (noise[i] > 2 || noise[i] < -2)
                finalnoise[i] = 0;
            else
                finalnoise[i] = (float)noise[i];
        //noise end
    }

    private static void generateNoiseProfile(double[] perChannelNoiseProfile, int[] cfa,
                                             int[] planeColors, int numPlanes,
            /*out*/double[] noiseProfile) {

        for (int p = 0; p < 3; ++p) {
            int S = p * 2;
            int O = p * 2 + 1;

            noiseProfile[S] = 0;
            noiseProfile[O] = 0;
            boolean uninitialized = true;
            for (int c = 0; c < 4; ++c) {
                if (cfa[c] == planeColors[p] && perChannelNoiseProfile[c * 2] > noiseProfile[S]) {
                    noiseProfile[S] = perChannelNoiseProfile[c * 2];
                    noiseProfile[O] = perChannelNoiseProfile[c * 2 + 1];
                    uninitialized = false;
                }
            }
            if (uninitialized) {
                Log.d(TAG, "%s: No valid NoiseProfile coefficients for color plane %zu");
            }
        }
    }

    private static float[]getFloatMatrix(ColorSpaceTransform transform)
    {
        float[] ret = new float[9];
        ret[0] = roundTo6Places(transform.getElement(0, 0).floatValue());
        ret[1] = roundTo6Places(transform.getElement(1, 0).floatValue());
        ret[2] = roundTo6Places(transform.getElement(2, 0).floatValue());
        ret[3] = roundTo6Places(transform.getElement(0, 1).floatValue());
        ret[4] = roundTo6Places(transform.getElement(1, 1).floatValue());
        ret[5] = roundTo6Places(transform.getElement(2, 1).floatValue());
        ret[6] = roundTo6Places(transform.getElement(0, 2).floatValue());
        ret[7] = roundTo6Places(transform.getElement(1, 2).floatValue());
        ret[8] = roundTo6Places(transform.getElement(2, 2).floatValue());
        return ret;
    }

    private static float roundTo6Places(float f )
    {
        return Math.round(f*1000000f)/1000000f;
    }
}
