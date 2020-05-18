package freed.settings;

import android.app.Application;
import android.util.Xml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.NavigableSet;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import freed.cam.apis.sonyremote.sonystuff.XmlElement;
import freed.settings.mode.ApiBooleanSettingMode;
import freed.settings.mode.GlobalBooleanSettingMode;
import freed.settings.mode.GlobalStringSetting;
import freed.settings.mode.SettingInterface;
import freed.settings.mode.SettingMode;
import freed.settings.mode.TypedSettingMode;
import freed.utils.Log;
import freed.utils.StringUtils;
import freed.utils.VideoMediaProfile;
import freed.utils.XmlUtil;

public class SettingsStorage
{
    private static final String TAG = SettingsStorage.class.getSimpleName();
    private ConcurrentHashMap<String,Object> settingStore;
    //private HashMap<Integer,HashMap<String, VideoMediaProfile>>mediaProfileHashMap;
    public final File appdataFolder;
    private MediaProfilesManager mediaProfilesManager;
    // api > camera id > setting
    private SettingLayout settings;
    private HashMap<String, HashMap<Integer, HashMap<SettingKeys.Key, SettingInterface>>> allSettings = new HashMap<>();
    private HashMap<SettingKeys.Key, SettingInterface> globalSettings = new HashMap<>();

    public SettingsStorage(File appdataFolder)
    {
        this.appdataFolder = appdataFolder;
        settingStore = new ConcurrentHashMap<>();
        //mediaProfileHashMap = new HashMap<>();
        mediaProfilesManager = new MediaProfilesManager();
        settings = new SettingLayout();
    }

    public <T> T get(SettingKeys.Key<T> key) {
        T settingInterface = null;
        if (key.getType() == GlobalBooleanSettingMode.class || key.getType() == GlobalStringSetting.class) {
            settingInterface = (T)globalSettings.get(key);
            if (settingInterface == null) {
                settingInterface = getNewSetting(key);
                globalSettings.put(key, (SettingInterface) settingInterface);
            }
        }
        else {
            String api = getApi();
            settingInterface = key.getType().cast(allSettings.get(api).get(id).get(key));
            if (settingInterface == null) {
                settingInterface = getNewSetting(key);
                allSettings.get(api).get(id).put(key, (SettingInterface) settingInterface);
            }
        }

        if (settingInterface == null)
            settingInterface = getNewSetting(key);
        return settingInterface;
    }

    private String getApi()
    {
        return getGlobalSetting(SettingKeys.ApiSettingsMode).get();
    }

    private <T> T getGlobalSetting(SettingKeys.Key<T> key)
    {
        return key.getType().cast(globalSettings.get(key));
    }

    private <T extends SettingInterface> T getNewSetting(SettingKeys.Key key)
    {
        Constructor ctr = key.getType().getConstructors()[0];
        T settingInterface = null;
        try {
            settingInterface = (T)ctr.newInstance(SettingsManager.getInstance().getResString(key.getRessourcesStringID()));
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return settingInterface;
    }


    public void save()
    {
        //saveSettings();
        saveSettingsXml();
        mediaProfilesManager.save(appdataFolder);
        //saveVideoMediaProfiles();
    }

    public void load()
    {
        //loadSettings();
        loadSettingsXml();
        mediaProfilesManager.load(appdataFolder);
        //loadVideoMediaProfiles();
    }

    private void loadSettings()
    {
        Log.d(TAG, "load Settings()");
        File f = new File(appdataFolder.getAbsolutePath(),"freed.txt");
        boolean exists = f.exists();
        try (FileReader is = new FileReader(f)) {
            BufferedReader bufferedReader = new BufferedReader(is);
            String receiveString;
            while ((receiveString = bufferedReader.readLine()) != null ) {
                getSettingFromString(receiveString,settingStore);
            }
        } catch (FileNotFoundException e) {
           Log.WriteEx(e);
        } catch (IOException e) {
            Log.WriteEx(e);
        }

        Log.d(TAG, "loaded Settings()");
    }

    private void saveSettingsXml()
    {
        File configFile = new File(appdataFolder.getAbsolutePath() + "/settings.xml");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(configFile));
            XmlUtil.writeLine(writer,"<apis>");
            for(String api: allSettings.keySet())
            {
                XmlUtil.writeNodeWithName(writer,"api",api);
                writeApiNode(writer,api, allSettings.get(api));
                XmlUtil.writeTagEnd(writer,"api");
            }
            XmlUtil.writeTagEnd(writer,"apis");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeApiNode(BufferedWriter writer, String api, HashMap<Integer, HashMap<SettingKeys.Key, SettingInterface>> integerHashMapHashMap) throws IOException {
        for (int s : integerHashMapHashMap.keySet()) {
            XmlUtil.writeNodeWithName(writer, "id", String.valueOf(s));
            writeCameraIdSettings(writer, integerHashMapHashMap.get(s));
            XmlUtil.writeTagEnd(writer,"id");
        }
    }

    private void writeCameraIdSettings(BufferedWriter writer, HashMap<SettingKeys.Key, SettingInterface> stringVideoMediaProfileHashMap) throws IOException {
        for (SettingKeys.Key s : stringVideoMediaProfileHashMap.keySet())
        {
            writer.write(stringVideoMediaProfileHashMap.get(s).getXmlString());
        }
    }

    private void loadSettingsXml() {
        File configFile = new File(appdataFolder.getAbsolutePath() + "/settings.xml");
        if (configFile.exists()) {
            try {
                String xmlsource = StringUtils.getString(new FileInputStream(configFile));
                XmlElement xmlElement = XmlElement.parse(xmlsource);
                List<XmlElement> apilist = xmlElement.findChildren("api");
                for (XmlElement element: apilist)
                {
                    String api_name = element.getAttribute("name","camera1");
                    HashMap<Integer,HashMap<SettingKeys.Key, SettingInterface>> cameraids = new HashMap<>();
                    allSettings.put(api_name,cameraids);
                    getCameraIdMaps(cameraids, element);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void getCameraIdMaps(HashMap<Integer, HashMap<SettingKeys.Key, SettingInterface>> api, XmlElement element) {
        List<XmlElement> cameralist = element.findChildren("id");
        for(XmlElement cameraid : cameralist)
        {
            String camid = cameraid.getAttribute("name","0");
            HashMap<SettingKeys.Key,SettingInterface> hashMap = new HashMap<>();
            api.put(Integer.parseInt(camid), hashMap);
            List<XmlElement> xmlprofiles = cameraid.findChildren("setting");
            for (XmlElement profile : xmlprofiles)
            {
                addSettingElement(hashMap, profile);
            }
        }
    }

    //<setting type = AbstractSettingMode name = manualmf > </setting>
    private void addSettingElement(HashMap<SettingKeys.Key, SettingInterface> hashMap, XmlElement profile) {
        String type  = profile.getAttribute("type","AbstractSettingMode");
        String key = profile.getAttribute("name","manualmf");
        SettingKeys.Key foundKey = findKey(key);
        if (type.equals(ApiBooleanSettingMode.class.getSimpleName()))
        {
            ApiBooleanSettingMode apiBooleanSettingMode = new ApiBooleanSettingMode(key);
            hashMap.put(foundKey,apiBooleanSettingMode);
            apiBooleanSettingMode.set(profile.findChild("value").getBooleanValue());
        }
        else if (type.equals(GlobalBooleanSettingMode.class.getSimpleName()))
        {
            GlobalBooleanSettingMode apiBooleanSettingMode = new GlobalBooleanSettingMode(key);
            hashMap.put(foundKey,apiBooleanSettingMode);
            apiBooleanSettingMode.set(profile.findChild("value").getBooleanValue());
        }
        else if (type.equals(SettingMode.class.getSimpleName()))
        {
            TypedSettingMode apiBooleanSettingMode = new TypedSettingMode(key);
            hashMap.put(foundKey,apiBooleanSettingMode);
            apiBooleanSettingMode.set(profile.findChild("value").getValue());
            List<XmlElement> strinarr = profile.findChild("values").findChildren("val");
            String[] tosetar = new String[strinarr.size()];
            for (int i = 0; i < strinarr.size(); i++)
            {
                tosetar[i] = strinarr.get(i).getValue();
            }
            apiBooleanSettingMode.setValues(tosetar);
            apiBooleanSettingMode.setIsPresetted(profile.findChild("preseted").getBooleanValue());
            apiBooleanSettingMode.setIsSupported(profile.findChild("supported").getBooleanValue());
            apiBooleanSettingMode.setType(profile.findChild("type").getIntValue(0));
            apiBooleanSettingMode.setMode(profile.findChild("mode").getValue());
        }
        else if (type.equals(SettingMode.class.getSimpleName()))
        {
            SettingMode apiBooleanSettingMode = new SettingMode(key);
            hashMap.put(foundKey,apiBooleanSettingMode);
            apiBooleanSettingMode.set(profile.findChild("value").getValue());
            List<XmlElement> strinarr = profile.findChild("values").findChildren("val");
            String[] tosetar = new String[strinarr.size()];
            for (int i = 0; i < strinarr.size(); i++)
            {
                tosetar[i] = strinarr.get(i).getValue();
            }
            apiBooleanSettingMode.setValues(tosetar);
            apiBooleanSettingMode.setIsPresetted(profile.findChild("preseted").getBooleanValue());
            apiBooleanSettingMode.setIsSupported(profile.findChild("supported").getBooleanValue());
        }
    }

    private SettingKeys.Key findKey(String val)
    {
        SettingKeys.Key[] key = SettingKeys.getKeyList();
        for (SettingKeys.Key k : key)
            if (SettingsManager.getInstance().getResString(k.getRessourcesStringID()).equals(val))
                return k;
        return null;
    }

    private void getSettingFromString(String input, ConcurrentHashMap<String, Object> map)
    {
        String split[] = input.split(";");
        String key = split[0];
        String type = split[1];
        if (type.equals("String"))
            map.put(key, split[2]);
        if (type.equals("Integer"))
            map.put(key, Integer.parseInt(split[2]));
        if (type.equals("Boolean"))
            map.put(key, Boolean.parseBoolean(split[2]));
        if (type.equals("Long"))
            map.put(key, Long.parseLong(split[2]));
        if (type.equals("Float"))
            map.put(key, Float.parseFloat(split[2]));
        if (type.equals("String[]")) {
            String[] out = new String[split.length - 2];
            for (int i = 2; i< split.length;i++)
                out[i-2] = split[i];
            map.put(key, out);
        }

    }

    private void saveSettings()
    {
        File out =new File(appdataFolder.getAbsolutePath()+"/freed.txt");
        if (!out.exists())
        {
            out.getParentFile().mkdirs();
        }
        try {
            if (!out.exists()) {
                Log.d(TAG, "Config file does not exists, create it");
                out.createNewFile();
            }
        } catch (IOException e) {
            Log.WriteEx(e);
        }
        try (OutputStreamWriter os = new OutputStreamWriter(new FileOutputStream(out))) {
            TreeMap<String, Object> treeMap = new TreeMap<>(settingStore);
            NavigableSet<String> set = treeMap.descendingKeySet();
            for (String key : set)
                writeSettingsString(key, settingStore.get(key), os);
        } catch (FileNotFoundException e) {
            Log.WriteEx(e);
        } catch (IOException e) {
            Log.WriteEx(e);
        }
    }

    private void writeSettingsString(String key, Object settings,OutputStreamWriter os) throws IOException {
        if (settings instanceof String)
            os.write( key +";String;" + (String)settings+"\n");
        if (settings instanceof Integer)
            os.write(key + ";Integer;" + settings +"\n" );
        if (settings instanceof String[])
        {
            os.write(key + ";String[]");
            for (int i = 0; i<((String[]) settings).length;i++)
                os.write(";" +((String[])settings)[i]);
            os.write("\n");
        }
        if (settings instanceof Boolean)
        {
            os.write(key +";Boolean;" + settings +"\n");
        }
        if (settings instanceof Float)
            os.write(key + ";Float;" + settings +"\n");
        if (settings instanceof Long)
            os.write(key + ";Long;" + settings +"\n");
    }


    public void reset()
    {
        if (settingStore != null)
            settingStore.clear();
        mediaProfilesManager.reset();
    }

    private <T> T get(String settingName,T defaultVal)
    {
        if (settingStore.get(settingName) != null)
            return (T)settingStore.get(settingName);
        return defaultVal;
    }

    private <T> void set(String settingName, T value)
    {
        settingStore.put(settingName,value);
    }

    private String getApiSettingString(String settingsName) {
        return getString(SettingsManager.SETTING_API, SettingsManager.API_1)+settingsName+getInt(getString(SettingsManager.SETTING_API, SettingsManager.API_1)+SettingsManager.CURRENTCAMERA,0);
    }

    public void setApiString(String settingName, String value)
    {
        set(getApiSettingString(settingName),value);
    }

    public String getApiString(String settingName,String def)
    {
        return get(getApiSettingString(settingName),def);
    }

    public void setApiStringArray(String settingName, String[] value)
    {
        set(getApiSettingString(settingName),value);
    }

    public String[] getApiStringArray(String settingName,String[] def)
    {
        return get(getApiSettingString(settingName),def);
    }

    public void setStringArray(String settingName, String[] value)
    {
        set(settingName,value);
    }

    public String[] getStringArray(String settingName,String[] def)
    {
        return get(settingName,def);
    }


    public void setString(String settingName, String value)
    {
        set(settingName,value);
    }

    public String getString(String settingName,String defaultValue)
    {
        return get(settingName,defaultValue);
    }

    public void setApiInt(String settingName, int value)
    {
        set(getApiSettingString(settingName),value);
    }

    public int getApiInt(String settingName, int defaultval)
    {
        return get(getApiSettingString(settingName),defaultval);
    }

    public void setInt(String settingName, int value)
    {
        set(settingName,value);
    }

    public int getInt(String settingName, int defaultval)
    {
        return get(settingName,defaultval);
    }

    public void setApiBoolean(String settingName, boolean value)
    {
        set(getApiSettingString(settingName),value);
    }

    public boolean getApiBoolean(String settingName, boolean defaultval)
    {
        return get(getApiSettingString(settingName),defaultval);
    }

    public void setBoolean(String settingName, boolean value)
    {
        set(settingName,value);
    }

    public boolean getBoolean(String settingName,boolean defaultval)
    {
        return get(settingName,defaultval);
    }

    public void setLong(String settingName, long value)
    {
        set(settingName,value);
    }

    public long getLong(String settingName,long defaultval)
    {
        return get(settingName,defaultval);
    }

    public void setFloat(String settingName, float value)
    {
        set(settingName,value);
    }

    public float getFloat(String settingName,float defaultval)
    {
        return get(settingName,defaultval);
    }

    public void setApiVideoMediaProfiles(HashMap<String,VideoMediaProfile> value)
    {
        String api = getString(SettingsManager.SETTING_API, SettingsManager.API_1);
        int  camid = getInt(getString(SettingsManager.SETTING_API, SettingsManager.API_1)+SettingsManager.CURRENTCAMERA,0);
        mediaProfilesManager.addMediaProfilesToApiAndCamera(api,camid,value);
        //mediaProfileHashMap.put(getInt(getString(SettingsManager.SETTING_API, SettingsManager.API_1)+SettingsManager.CURRENTCAMERA,0), value);
    }

    public HashMap<String,VideoMediaProfile> getApiVideoMediaProfiles()
    {
        String api = getString(SettingsManager.SETTING_API, SettingsManager.API_1);
        int  camid = getInt(getString(SettingsManager.SETTING_API, SettingsManager.API_1)+SettingsManager.CURRENTCAMERA,0);
        return mediaProfilesManager.getMediaProfilesForApiAndCamera(api,camid);
    }
}
