package freed.dng;

import freed.cam.apis.sonyremote.sonystuff.XmlElement;

/**
 * Created by troop on 10.07.2017.
 */

public class ToneMapProfile {
    private String name;
    private float toneCurve[];
    private float hueSatMap[];
    private int hueSatMapDims[];


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
        for (int i = 0; i< split.length; i++) {
            if (!split[i].equals("")) {
                toneCurve[i] = Float.parseFloat(split[i]);
                //check if its in range 0-1 if not apply that range
                //this happens when we extract it with exiftools. it shows it as 0-255 range
                if (toneCurve[i] > 1)
                    toneCurve[i] = toneCurve[i] / 255;
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
        t += "<huesatmapdims>" + huesatmapdim + "</huesatmapdims>" + "\r\n";
        t += "<huesatmap>" + huesatmap + "</huesatmap>" + "\r\n";
        t += "</tonemapprofile>"  + "\r\n";


        return t;
    }
}
