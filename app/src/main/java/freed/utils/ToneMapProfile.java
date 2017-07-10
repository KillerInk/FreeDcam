package freed.utils;

import freed.cam.apis.sonyremote.sonystuff.XmlElement;

/**
 * Created by troop on 10.07.2017.
 */

public class ToneMapProfile {
    private String name;
    private float toneCurve[];
    private float hueSatMap[];


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
        String split[] = element.findChild("tonecurve").getValue().split(" ");
        toneCurve = new float[split.length];
        for (int i = 0; i< split.length; i++)
            toneCurve[i] = Float.parseFloat(split[i]);

        if (!element.findChild("huesatmap").isEmpty()) {
            split = element.findChild("huesatmap").getValue().split(" ");
            hueSatMap = new float[split.length];
            for (int i = 0; i < split.length; i++)
                hueSatMap[i] = Float.parseFloat(split[i]);
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

    public float[] getHueSatMap()
    {
        return hueSatMap;
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
        String t = new String();
        t += "<tonemapprofile name= " +String.valueOf("\"") +String.valueOf(name) +String.valueOf("\"")  +">" + "\r\n";
        t += "<tonecurve>" + tonecurve + "</tonecurve>" + "\r\n";
        t += "<huesatmap>" + huesatmap + "</huesatmap>" + "\r\n";
        t += "</tonemapprofile>"  + "\r\n";


        return t;
    }
}
