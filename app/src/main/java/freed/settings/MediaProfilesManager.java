package freed.settings;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import freed.cam.apis.sonyremote.sonystuff.XmlElement;
import freed.utils.Log;
import freed.utils.StringUtils;
import freed.utils.VideoMediaProfile;
import freed.utils.XmlUtil;

public class MediaProfilesManager {

    private HashMap<String, HashMap<Integer, HashMap<String, VideoMediaProfile>>> allMediaProfiles = new HashMap<>();

    public MediaProfilesManager()
    {

    }

    public HashMap<String, VideoMediaProfile> getMediaProfilesForApiAndCamera(String api, int camid)
    {
        return allMediaProfiles.get(api).get(camid);
    }

    public void addMediaProfileToApiAndCamera(String api, int camid,VideoMediaProfile profile)
    {
        allMediaProfiles.get(api).get(camid).put(profile.ProfileName,profile);
    }

    public void addMediaProfilesToApiAndCamera(String api, int camid,HashMap<String,VideoMediaProfile> profiles)
    {
        if (!allMediaProfiles.containsKey(api))
            allMediaProfiles.put(api,new HashMap<>());
        allMediaProfiles.get(api).put(camid,profiles);
    }

    public void reset()
    {
        allMediaProfiles.clear();
    }

    public void load(File appdata)
    {
        File configFile = new File(appdata.getAbsolutePath()+"/media_profiles.xml");
        if (configFile.exists())
        {
            try {
                String xmlsource = StringUtils.getString(new FileInputStream(configFile));
                XmlElement xmlElement = XmlElement.parse(xmlsource);
                List<XmlElement> apilist = xmlElement.findChildren("api");
                for (XmlElement element: apilist)
                {
                    String api_name = element.getAttribute("name","camera1");
                    HashMap<Integer,HashMap<String, VideoMediaProfile>> cameraids = new HashMap<>();
                    allMediaProfiles.put(api_name,cameraids);
                    getCameraIdMaps(cameraids, element);
                }
            }
            catch (IOException ex)
            {
                Log.WriteEx(ex);
            }
        }
    }

    private void getCameraIdMaps(HashMap<Integer, HashMap<String, VideoMediaProfile>> api, XmlElement element) {
        List<XmlElement> cameralist = element.findChildren("id");
        for(XmlElement cameraid : cameralist)
        {
            String camid = cameraid.getAttribute("name","0");
            HashMap<String,VideoMediaProfile> hashMap = new HashMap<>();
            api.put(Integer.parseInt(camid), hashMap);
            List<XmlElement> xmlprofiles = cameraid.findChildren("mediaprofile");
            for (XmlElement profile : xmlprofiles)
            {
                VideoMediaProfile videoMediaProfile = new VideoMediaProfile(profile);
                hashMap.put(videoMediaProfile.ProfileName, videoMediaProfile);
            }
        }
    }


    public void save(File appdata)
    {
        File configFile = new File( appdata.getAbsolutePath()+"/media_profiles.xml");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(configFile));
            XmlUtil.writeLine(writer,"<apis>");
            for(String api: allMediaProfiles.keySet())
            {
                XmlUtil.writeNodeWithName(writer,"api",api);
                writeApiNode(writer,api, allMediaProfiles.get(api));
                XmlUtil.writeTagEnd(writer,"api");
            }
            XmlUtil.writeLine(writer,"</apis>");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeApiNode(BufferedWriter writer, String api, HashMap<Integer, HashMap<String, VideoMediaProfile>> integerHashMapHashMap) throws IOException {
        for (int s : integerHashMapHashMap.keySet()) {
            XmlUtil.writeNodeWithName(writer, "id", String.valueOf(s));
            writeMediaProfiles(writer, integerHashMapHashMap.get(s));
            XmlUtil.writeTagEnd(writer,"id");
        }

    }

    private void writeMediaProfiles(BufferedWriter writer, HashMap<String, VideoMediaProfile> stringVideoMediaProfileHashMap) throws IOException {
        for (String s : stringVideoMediaProfileHashMap.keySet())
        {
            writer.write(stringVideoMediaProfileHashMap.get(s).getXmlString());
        }
    }



}
