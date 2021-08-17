package freed.settings;

import com.troop.freedcam.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import freed.FreedApplication;
import freed.cam.apis.sonyremote.sonystuff.XmlElement;
import freed.utils.Log;
import freed.utils.StringUtils;

public class VideoToneCurveXmlParserWriter {
    private final String TAG = VideoToneCurveXmlParserWriter.class.getSimpleName();
    /**
     * Read the tonemap profiles from toneMapProfiles.xml
     * @param
     * @return
     */
    public HashMap<String, VideoToneCurveProfile> getToneCurveProfiles(File appDataFolder)
    {
        HashMap<String,VideoToneCurveProfile>  hashMap = new HashMap<>();

        try {
            String xmlsource = StringUtils.getString(FreedApplication.getContext().getResources().openRawResource(R.raw.tonecurveprofiles));
            XmlElement xmlElement = XmlElement.parse(xmlsource);
            getToneCurveProfiles(hashMap, xmlElement);
        } catch (IOException e) {
            e.printStackTrace();
        }

        File configFile = new File(appDataFolder.getAbsolutePath()+"/tonecurveprofiles.xml");
        if (configFile.exists())
        {
            try {
                String xmlsource = StringUtils.getString(new FileInputStream(configFile));
                XmlElement xmlElement = XmlElement.parse(xmlsource);
                getToneCurveProfiles(hashMap, xmlElement);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return hashMap;
    }

    private void getToneCurveProfiles(HashMap<String, VideoToneCurveProfile> hashMap, XmlElement xmlElement) {

        List<XmlElement> tonemapchilds = xmlElement.findChildren("tonecurve");
        if (tonemapchilds.size() > 0){
            for (XmlElement element : tonemapchilds)
            {
                VideoToneCurveProfile profile = new VideoToneCurveProfile(element);
                hashMap.put(profile.name, profile);
            }
        }
    }

    public void saveToneCurveProfiles(HashMap<String, VideoToneCurveProfile> hashMap, File appData)
    {
        if (hashMap == null ||hashMap.size() == 0)
            return;
        BufferedWriter writer = null;
        try {

            File configFile = new File(appData.getAbsolutePath()+"/tonecurveprofiles.xml");
            Log.d(TAG, configFile.getAbsolutePath() + " exists:" + configFile.exists());
            Log.d(TAG, configFile.getParentFile().getAbsolutePath() + " exists:" + configFile.getParentFile().exists());
            if (!configFile.getParentFile().exists())
                configFile.getParentFile().mkdirs();
            Log.d(TAG, configFile.getParentFile().getAbsolutePath() + " exists:" + configFile.getParentFile().exists());
            configFile.createNewFile();
            writer = new BufferedWriter(new FileWriter(configFile));
            writer.write("<tonecurves>" + "\r\n");
            String profiles[] = new String[hashMap.size()];
            hashMap.keySet().toArray(profiles);
            for (int i =0; i< profiles.length;i++)
            {
                Log.d(TAG, "Write Profile: " + profiles[i]);
                writer.write(hashMap.get(profiles[i]).getXmlString());
            }

            writer.write("</tonecurves>" + "\r\n");
            writer.flush();

        } catch (IOException e) {
            Log.WriteEx(e);
        }
        finally {
            if (writer != null)
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
}
