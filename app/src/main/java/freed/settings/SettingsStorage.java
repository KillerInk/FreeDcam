package freed.settings;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;

import freed.utils.StringUtils;
import freed.utils.VideoMediaProfile;

public class SettingsStorage
{
    private HashMap<String,Object> settingStore;
    private HashMap<Integer,HashMap<String, VideoMediaProfile>>mediaProfileHashMap;

    public SettingsStorage()
    {
        settingStore = new HashMap<>();
        mediaProfileHashMap = new HashMap<>();
    }

    public void save()
    {
        saveSettings();
        saveVideoMediaProfiles();
    }

    public void load()
    {
        loadSettings();
        loadVideoMediaProfiles();
    }

    private void loadSettings()
    {
        try (InputStreamReader is = new InputStreamReader(new FileInputStream(StringUtils.GetFreeDcamConfigFolder+"freed.conf"))) {
            BufferedReader bufferedReader = new BufferedReader(is);
            String receiveString;
            while ((receiveString = bufferedReader.readLine()) != null ) {
                getSettingFromString(receiveString,settingStore);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getSettingFromString(String input, HashMap<String, Object> map)
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
        try (OutputStreamWriter os = new OutputStreamWriter(new FileOutputStream(StringUtils.GetFreeDcamConfigFolder+"freed.conf"))) {
            for ( String key : settingStore.keySet())
                writeSettingsString(key, settingStore.get(key), os);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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

    private void loadVideoMediaProfiles()
    {
        try (InputStreamReader is = new InputStreamReader(new FileInputStream(StringUtils.GetFreeDcamConfigFolder+"videoProfiles.conf"))) {
            BufferedReader bufferedReader = new BufferedReader(is);
            String receiveString;
            mediaProfileHashMap.clear();
            int cameraid = 0;
            HashMap<String, VideoMediaProfile> activemap  = null;
            while ((receiveString = bufferedReader.readLine()) != null ) {
                if (receiveString.startsWith("#"))
                {
                    cameraid = Integer.parseInt(receiveString.substring(1));
                    activemap = new HashMap<>();
                    mediaProfileHashMap.put(cameraid,activemap);
                }
                else
                {
                    VideoMediaProfile profile = new VideoMediaProfile(receiveString);
                    activemap.put(profile.ProfileName,profile);
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveVideoMediaProfiles()
    {
        if (mediaProfileHashMap == null)
            return;
        try (OutputStreamWriter os = new OutputStreamWriter(new FileOutputStream(StringUtils.GetFreeDcamConfigFolder+"videoProfiles.conf"))) {

            HashMap<String,VideoMediaProfile> map;
            for (int i = 0; i < mediaProfileHashMap.size(); i++) {
                os.write("#" + i +"\n");
                map = mediaProfileHashMap.get(i);
                for (VideoMediaProfile profile : map.values()) {
                    os.write(profile.GetString() + "\n");
                }
                os.flush();
            }
        } catch(FileNotFoundException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void reset()
    {
        if (settingStore != null)
            settingStore.clear();
        if (mediaProfileHashMap != null)
            mediaProfileHashMap.clear();
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
        return getString(SettingsManager.SETTING_API, SettingsManager.API_1)+settingsName+getInt(SettingsManager.CURRENTCAMERA,0);
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

    public void setApiVideoMediaProfiles(String settingName, HashMap<String,VideoMediaProfile> value)
    {
        mediaProfileHashMap.put(getInt(SettingsManager.CURRENTCAMERA,0), value);
    }

    public HashMap<String,VideoMediaProfile> getApiVideoMediaProfiles(String settingName,HashMap<String,VideoMediaProfile> defaultval)
    {
        return mediaProfileHashMap.get(getInt(SettingsManager.CURRENTCAMERA,0));
    }
}
