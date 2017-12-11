package freed.dng;

import android.text.TextUtils;

import freed.cam.apis.sonyremote.sonystuff.XmlElement;

/**
 * Created by troop on 10.07.2017.
 */

public class ToneMapProfile {
    private String name;

    /*
    This tag contains a default tone curve that can be applied while processing the image as a
    starting point for user adjustments. The curve is specified as a list of 32-bit IEEE
    floating-point value pairs in linear gamma. Each sample has an input value in the range
    of 0.0 to 1.0, and an output value in the range of 0.0 to 1.0.
    The first sample is required to be (0.0, 0.0), and the last sample is required to be (1.0, 1.0).
     Interpolated the curve using a cubic spline.
     */
    private float toneCurve[];

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
    private float hueSatMap[];

    /*
    This tag specifies the number of input samples in each dimension of the hue/saturation/value
    mapping tables. The data for these tables are stored in ProfileHueSatMapData1 and
    ProfileHueSatMapData2 tags. Allowed values include the following:
    •HueDivisions >= 1
    •SaturationDivisions >= 2
    •ValueDivisions >=1
    The most common case has ValueDivisions equal to 1, so only hue and saturation are used as inputs to the mapping table.
     */
    private int hueSatMapDims[];
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
    private Float baselineExposure;
    /*
    Provides a way for color profiles to increase or decrease exposure during raw conversion.
    BaselineExposureOffset specifies the amount (in EV units) to add to the BaselineExposure tag
    during image rendering. For example, if the BaselineExposure value for a given camera model
    is +0.3, and the BaselineExposureOffset value for a given camera profile used to render an
    image for that camera model is -0.7, then the actual default exposure value used during
    rendering will be +0.3 - 0.7 = -0.4.
     */
    private Float baselineExposureOffset;


    /**
     * <tonemapprofile name="linear">
     *     <tonecurve>0 0 0.25 0.25 0.5 0.5 0.75 0.75 1 1</tonecurve>
     *     <huesatmap>0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0</huesatmap> could be empty
     *     </tonemapprofile>
     * @param element
     */
    public ToneMapProfile(XmlElement element)
    {
        name = element.getAttribute("name", "");
        String split[] = null;
        if (!element.findChild("tonecurve").isEmpty()) {
            String curve = element.findChild("tonecurve").getValue();
            curve = curve.replace("\n","").replace(" ","");
            split = curve.split(",");
            toneCurve = new float[split.length];
            for (int i = 0; i < split.length; i++) {
                if (!TextUtils.isEmpty(split[i])) {
                    toneCurve[i] = Float.parseFloat(split[i]);
                    //check if its in range 0-1 if not apply that range
                    //this happens when we extract it with exiftools. it shows it as 0-255 range
                    if (toneCurve[i] > 1)
                        toneCurve[i] = toneCurve[i] / 255;
                }
            }
        }

        if (!element.findChild("huesatmapdims").isEmpty())
        {
            split = element.findChild("huesatmapdims").getValue().split(" ");
            hueSatMapDims = new int[split.length];
            for (int i = 0; i < split.length; i++)
                hueSatMapDims[i] = Integer.parseInt(split[i]);

        }

        if (!element.findChild("huesatmapdata1").isEmpty()) {
            split = element.findChild("huesatmapdata1").getValue().split(" ");
            hueSatMap = new float[split.length];
            for (int i = 0; i < split.length; i++)
                hueSatMap[i] = Float.parseFloat(split[i]);
        }

        if (!element.findChild("baselineexposure").isEmpty())
        {
            baselineExposure = element.findChild("baselineexposure").getFloatValue();
        }

        if (!element.findChild("baselineexposureoffset").isEmpty())
        {
            baselineExposureOffset = element.findChild("baselineexposureoffset").getFloatValue();
        }
    }

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
        String tonecurve = new String();
        for (int i=0; i < toneCurve.length; i++)
        {
            tonecurve += toneCurve[i] + " ";
        }
        String huesatmap = new String();
        for (int i=0; i < hueSatMap.length; i++)
        {
            huesatmap += hueSatMap[i] + " ";
        }
        String huesatmapdim = new String();
        for (int i=0; i < hueSatMapDims.length; i++)
        {
            huesatmapdim += hueSatMapDims[i] + " ";
        }
        String t = new String();
        t += "<tonemapprofile name= " +String.valueOf("\"") +String.valueOf(name) +String.valueOf("\"")  +">" + "\r\n";
        t += "<tonecurve>" + tonecurve + "</tonecurve>" + "\r\n";
        t += "<baselineexposure>" + baselineExposure + "</baselineexposure>" + "\r\n";
        t += "<baselineexposureoffset>" + baselineExposureOffset + "</baselineexposureoffset>" + "\r\n";
        t += "<huesatmapdims>" + huesatmapdim + "</huesatmapdims>" + "\r\n";
        t += "<huesatmap>" + huesatmap + "</huesatmap>" + "\r\n";
        t += "</tonemapprofile>"  + "\r\n";


        return t;
    }
}
