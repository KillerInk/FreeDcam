package freed.views;

import android.graphics.PointF;
import android.text.TextUtils;

import freed.cam.apis.sonyremote.sonystuff.XmlElement;

public class VideoToneCurveProfile {
    public String name;
    public PointF[] rgb;
    public PointF[] r;
    public PointF[] g;
    public PointF[] b;

    public VideoToneCurveProfile(){}

    public VideoToneCurveProfile(XmlElement element)
    {
        name = element.getAttribute("name", "");
        String split[] = null;
        if (!element.findChild("rgb").isEmpty()) {
            String curve = element.findChild("rgb").getValue();
            curve = curve.replace("\n","").replace(" ","");
            split = curve.split(",");
            rgb = new PointF[split.length];
            for (int i = 0; i < split.length; i++) {
                if (!TextUtils.isEmpty(split[i])) {
                    float toset = Float.parseFloat(split[i]);
                    if (toset > 1)
                        toset = toset / 255;
                    rgb[i] = new PointF(toset,toset);
                    //check if its in range 0-1 if not apply that range
                    //this happens when we extract it with exiftools. it shows it as 0-255 range

                }
            }
        }
        if (!element.findChild("r").isEmpty()) {
            String curve = element.findChild("r").getValue();
            curve = curve.replace("\n","").replace(" ","");
            split = curve.split(",");
            r = new PointF[split.length];
            for (int i = 0; i < split.length; i++) {
                if (!TextUtils.isEmpty(split[i])) {
                    float toset = Float.parseFloat(split[i]);
                    if (toset > 1)
                        toset = toset / 255;
                    r[i] = new PointF(toset,toset);
                    //check if its in range 0-1 if not apply that range
                    //this happens when we extract it with exiftools. it shows it as 0-255 range

                }
            }
        }
        if (!element.findChild("g").isEmpty()) {
            String curve = element.findChild("g").getValue();
            curve = curve.replace("\n","").replace(" ","");
            split = curve.split(",");
            g = new PointF[split.length];
            for (int i = 0; i < split.length; i++) {
                if (!TextUtils.isEmpty(split[i])) {
                    float toset = Float.parseFloat(split[i]);
                    if (toset > 1)
                        toset = toset / 255;
                    g[i] = new PointF(toset,toset);
                    //check if its in range 0-1 if not apply that range
                    //this happens when we extract it with exiftools. it shows it as 0-255 range

                }
            }
        }
        if (!element.findChild("b").isEmpty()) {
            String curve = element.findChild("b").getValue();
            curve = curve.replace("\n","").replace(" ","");
            split = curve.split(",");
            b = new PointF[split.length];
            for (int i = 0; i < split.length; i++) {
                if (!TextUtils.isEmpty(split[i])) {
                    float toset = Float.parseFloat(split[i]);
                    if (toset > 1)
                        toset = toset / 255;
                    b[i] = new PointF(toset,toset);
                    //check if its in range 0-1 if not apply that range
                    //this happens when we extract it with exiftools. it shows it as 0-255 range

                }
            }
        }
    }

    public String getXmlString()
    {
        StringBuilder rgbs = new StringBuilder();
        for (int i=0; i < rgb.length; i++)
        {
            rgbs.append(rgb[i].x).append(",");
        }
        StringBuilder rs = new StringBuilder();
        for (int i=0; i < r.length; i++)
        {
            rs.append(r[i].x).append(",");
        }
        StringBuilder gs = new StringBuilder();
        for (int i=0; i < g.length; i++)
        {
            gs.append(g[i].x).append(",");
        }

        StringBuilder bs = new StringBuilder();
        for (int i=0; i < b.length; i++)
        {
            bs.append(b[i].x).append(",");
        }
        String t = "";
        t += "<tonecurve name= " +String.valueOf("\"") +String.valueOf(name) +String.valueOf("\"")  +">" + "\r\n";
        t += "<rgb>" + rgbs + "</rgb>" + "\r\n";
        t += "<r>" + rs + "</r>" + "\r\n";
        t += "<g>" + gs + "</g>" + "\r\n";
        t += "<b>" + bs + "</b>" + "\r\n";
        t += "</tonecurve>"  + "\r\n";

        return t;
    }
}
