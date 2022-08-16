package freed.dng;

import android.text.TextUtils;

import freed.cam.apis.sonyremote.sonystuff.XmlElement;

/**
 * Created by troop on 10.07.2017.
 */

public class ToneMapProfile {
    public String name;

    /*
    This tag contains a default tone curve that can be applied while processing the image as a
    starting point for user adjustments. The curve is specified as a list of 32-bit IEEE
    floating-point value pairs in linear gamma. Each sample has an input value in the range
    of 0.0 to 1.0, and an output value in the range of 0.0 to 1.0.
    The first sample is required to be (0.0, 0.0), and the last sample is required to be (1.0, 1.0).
     Interpolated the curve using a cubic spline.
     */
    public float[] toneCurve;

    /*
    This tag contains the data for the first hue/saturation/value mapping table.
    Each entry of the table contains three 32-bit IEEE floating-point values.
    The first entry is hue shift in degrees; the second entry is saturation scale factor;
    and the third entry is a value scale factor. The table entries are stored in the tag in nested
    loop order, with the value divisions in the outer loop, the hue divisions in the middle loop,
    and the saturation divisions in the inner loop. All zero input saturation entries are required
    to have a value scale factor of 1.0. The hue/saturation/value table application is described
    in detail in Chapter 6 DNG SDK.
     */
    public float[] hueSatMap;
    public float[] hueSatMap2;

    /*
    This tag specifies the number of input samples in each dimension of the hue/saturation/value
    mapping tables. The data for these tables are stored in ProfileHueSatMapData1 and
    ProfileHueSatMapData2 tags. Allowed values include the following:
    •HueDivisions >= 1
    •SaturationDivisions >= 2
    •ValueDivisions >=1
    The most common case has ValueDivisions equal to 1, so only hue and saturation are used as inputs to the mapping table.
     */
    public int[] hueSatMapDims;
    /*
    Camera models vary in the trade-off they make between highlight headroom and shadow noise.
    Some leave a significant amount of highlight headroom during a normal exposure.
    This allows significant negative exposure compensation to be applied during raw conversion,
    but also means normal exposures will contain more shadow noise. Other models leave less
    headroom during normal exposures. This allows for less negative exposure compensation,
    but results in lower shadow noise for normal exposures.
    Because of these differences, a raw converter needs to vary the zero point of its exposure
    compensation control from model to model. BaselineExposure specifies by how much (in EV units)
    to move the zero point. Positive values result in brighter default results, while negative values
    result in darker default results.

    It is important to note is that Baseline Exposure (BLE) may depend on the ISO setting, not only on the camera make/model.
     */
    public Float baselineExposure;
    /*
    Provides a way for color profiles to increase or decrease exposure during raw conversion.
    BaselineExposureOffset specifies the amount (in EV units) to add to the BaselineExposure tag
    during image rendering. For example, if the BaselineExposure value for a given camera model
    is +0.3, and the BaselineExposureOffset value for a given camera profile used to render an
    image for that camera model is -0.7, then the actual default exposure value used during
    rendering will be +0.3 - 0.7 = -0.4.
     */
    public Float baselineExposureOffset;


    public ToneMapProfile(){}

    public String getName()
    {
        return name;
    }

    public float[] getToneCurve()
    {
        return toneCurve;
    }

    public float[] getHueSatMapData1()
    {
        return hueSatMap;
    }
    public float[] getHueSatMapData2()
    {
        return hueSatMap2;
    }
    public int[] getHueSatMapDims(){
        return hueSatMapDims;
    }

    public Float getBaselineExposure()
    {
        return baselineExposure;
    }

    public Float getBaselineExposureOffset()
    {
        return baselineExposureOffset;
    }

    public String getXmlString()
    {
        StringBuilder tonecurve = new StringBuilder();
        for (int i=0; i < toneCurve.length; i++)
        {
            tonecurve.append(toneCurve[i]).append(" ");
        }
        StringBuilder huesatmap = new StringBuilder();
        for (int i=0; i < hueSatMap.length; i++)
        {
            huesatmap.append(hueSatMap[i]).append(" ");
        }

        StringBuilder huesatmap2 = new StringBuilder();
        for (int i=0; i < hueSatMap2.length; i++)
        {
            huesatmap2.append(hueSatMap2[i]).append(" ");
        }
        StringBuilder huesatmapdim = new StringBuilder();
        for (int i=0; i < hueSatMapDims.length; i++)
        {
            huesatmapdim.append(hueSatMapDims[i]).append(" ");
        }
        String t = "";
        t += "<tonemapprofile name= " + "\"" + name + "\"" +">" + "\r\n";
        t += "<tonecurve>" + tonecurve + "</tonecurve>" + "\r\n";
        t += "<baselineexposure>" + baselineExposure + "</baselineexposure>" + "\r\n";
        t += "<baselineexposureoffset>" + baselineExposureOffset + "</baselineexposureoffset>" + "\r\n";
        t += "<huesatmapdims>" + huesatmapdim + "</huesatmapdims>" + "\r\n";
        t += "<huesatmap>" + huesatmap + "</huesatmap>" + "\r\n";
        t += "<huesatmap2>" + huesatmap2 + "</huesatmap2>" + "\r\n";
        t += "</tonemapprofile>"  + "\r\n";


        return t;
    }
}
