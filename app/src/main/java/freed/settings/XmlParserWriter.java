package freed.settings;

import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;

import androidx.collection.LongSparseArray;

import com.troop.freedcam.BuildConfig;
import com.troop.freedcam.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import freed.FreedApplication;
import freed.cam.apis.featuredetector.Camera1FeatureDetectorTask;
import freed.cam.apis.sonyremote.sonystuff.XmlElement;
import freed.dng.CustomMatrix;
import freed.dng.DngProfile;
import freed.dng.ToneMapProfile;
import freed.settings.mode.TypedSettingMode;
import freed.utils.Log;
import freed.utils.StringUtils;

/**
 * Created by troop on 25.06.2017.
 */

public class XmlParserWriter
{
    private final String TAG = XmlParserWriter.class.getSimpleName();
    SettingsManager settingsManager;

    public XmlParserWriter()
    {
        settingsManager = FreedApplication.settingsManager();
    }

    public void parseAndFindSupportedDevice(Resources resources, HashMap<String, CustomMatrix> matrixHashMap, File appDataPath)
    {
        try {
            String xmlsource = StringUtils.getString(resources.openRawResource(R.raw.supported_devices));
            XmlElement rootElement = XmlElement.parse(xmlsource);
            if (rootElement.getTagName().equals("devices"))
            {
                List<XmlElement> devicesList = rootElement.findChildren("device");
                Log.d(TAG, "Found " + devicesList.size() + " Devices in Xml");

                for (XmlElement device_element: devicesList)
                {
                    List<XmlElement> models = device_element.findChild("models").findChildren("item");
                    for (XmlElement mod : models)
                    {
                        if (mod.getValue().equals(Build.MODEL)) {
                            settingsManager.setDevice(device_element.getAttribute("name",""));
                            Log.d(TAG, "Found Device:" + settingsManager.getDeviceString());

                            XmlElement camera1element = device_element.findChild("camera1");


                            if (!camera1element.isEmpty()) {
                                settingsManager.setCamApi(SettingsManager.API_1);
                                Log.d(TAG, "Found camera1 overrides");
                                Log.v(TAG, camera1element.dumpChildElementsTagNames());

                                if (!camera1element.findChild("framework").isEmpty())
                                {
                                    settingsManager.setFramework(Frameworks.valueOf(camera1element.findChild("framework").getValue()));
                                }
                                else
                                    settingsManager.setFramework(FrameworkDetector.getFramework());

                                List<XmlElement> cameraids = camera1element.findChildren("cameraid");
                                if (cameraids != null && cameraids.size() >0)
                                    for (XmlElement id : cameraids)
                                        parseCamera1_IdSettings(resources, id);
                            }

                            XmlElement camera2element = device_element.findChild("camera2");
                            if (!camera2element.isEmpty()) {
                                settingsManager.setCamApi(SettingsManager.API_2);
                                Log.d(TAG,"Found Camera2 overrides");
                                if (!camera2element.findChild("forcerawtodng").isEmpty())
                                    settingsManager.get(SettingKeys.FORCE_RAW_TO_DNG).set(camera2element.findChild("forcerawtodng").getBooleanValue());
                                if (!camera2element.findChild("overrideprofile").isEmpty())
                                    settingsManager.setsOverrideDngProfile(camera2element.findChild("overrideprofile").getBooleanValue());

                                if (!camera2element.findChild("maxexposuretime").isEmpty())
                                {
                                    settingsManager.setCamera2MaxExposureTime(camera2element.findChild("maxexposuretime").getLongValue());
                                }
                                if (!camera2element.findChild("minexposuretime").isEmpty())
                                {
                                    settingsManager.setCamera2MinExposureTime(camera2element.findChild("minexposuretime").getLongValue());
                                }
                                if (!camera2element.findChild("maxiso").isEmpty())
                                    settingsManager.setCamera2MaxIso(camera2element.findChild("maxiso").getIntValue(0));
                                if (!camera2element.findChild("minfocusposition").isEmpty())
                                    settingsManager.setCamera2MinFocusPosition(camera2element.findChild("minfocusposition").getFloatValue());
                            }

                            LongSparseArray<DngProfile> dngProfileHashMap = new LongSparseArray<>();
                            getDngStuff(dngProfileHashMap, device_element,matrixHashMap);
                            Log.d(TAG, "Save Dng Profiles:" + dngProfileHashMap.size());
                            saveDngProfiles(dngProfileHashMap, settingsManager.getDeviceString(), appDataPath);

                            break;
                        }
                    }
                }
                settingsManager.save();
            }
        } catch (IOException e) {
            Log.WriteEx(e);
        }
    }

    private void parseCamera1_IdSettings(Resources resources, XmlElement camera1element) {

        int id = Integer.parseInt(camera1element.getAttribute("name",""));
        settingsManager.SetCurrentCamera(id);
        if (!camera1element.findChild("opencameralegacy").isEmpty()) {
            settingsManager.get(SettingKeys.OPEN_CAMERA_1_LEGACY).set(Boolean.parseBoolean(camera1element.findChild("opencameralegacy").getValue()));
            settingsManager.get(SettingKeys.OPEN_CAMERA_1_LEGACY).setIsPresetted(true);
        }

        //Log.d(TAG, "OpenLegacy: " + SettingsManager.get(SettingKeys.openCamera1Legacy).get() + " isPresetted:" + SettingsManager.get(SettingKeys.openCamera1Legacy).isPresetted());

        if (!camera1element.findChild("zteae").isEmpty())
            settingsManager.setZteAe(Boolean.parseBoolean(camera1element.findChild("zte").getValue()));
        else
            settingsManager.setZteAe(false);

        Log.d(TAG, "isZteAE:" + settingsManager.isZteAe());

        if (!camera1element.findChild("needrestartaftercapture").isEmpty())
            settingsManager.get(SettingKeys.NEED_RESTART_AFTER_CAPTURE).set(Boolean.parseBoolean(camera1element.findChild("needrestartaftercapture").getValue()));
                                /*else
                                    settingsManager.get(SettingKeys.needRestartAfterCapture).set(false);*/

        if (!camera1element.findChild("burst").isEmpty()) {
            settingsManager.get(SettingKeys.M_BURST).setIsSupported(true);
            int max = Integer.parseInt(camera1element.findChild("burst").getValue());
            settingsManager.get(SettingKeys.M_BURST).setValues(createStringArray(1, max, 1));
            settingsManager.get(SettingKeys.M_BURST).set(0+ "");
        } else
            settingsManager.get(SettingKeys.M_BURST).setIsSupported(false);
        settingsManager.get(SettingKeys.M_BURST).setIsPresetted(true);

        if (!camera1element.findChild("nightmode").isEmpty()) {
            settingsManager.get(SettingKeys.NIGHT_MODE).setIsSupported(true);
            int type = Integer.parseInt(camera1element.findChild("nightmode").getValue());
            settingsManager.get(SettingKeys.NIGHT_MODE).setType(type);
        } else
            settingsManager.get(SettingKeys.NIGHT_MODE).setIsSupported(false);
        settingsManager.get(SettingKeys.NIGHT_MODE).setIsPresetted(true);

        if (!camera1element.findChild("whitebalance").isEmpty())
        {
            //TODO handel sdk specific
            Log.d(TAG, "override manual whiteblalance");
            int min = camera1element.findChild("whitebalance").findChild("min").getIntValue(2000);
            int max  = camera1element.findChild("whitebalance").findChild("max").getIntValue(8000);
            int step = camera1element.findChild("whitebalance").findChild("step").getIntValue(100);
            settingsManager.get(SettingKeys.M_WHITEBALANCE).setCamera1ParameterKEY(camera1element.findChild("whitebalance").findChild("key").getValue());
            settingsManager.get(SettingKeys.M_WHITEBALANCE).setMode(camera1element.findChild("whitebalance").findChild("mode").getValue());
            settingsManager.get(SettingKeys.M_WHITEBALANCE).setValues(Camera1FeatureDetectorTask.createWBStringArray(min,max,step));
            settingsManager.get(SettingKeys.M_WHITEBALANCE).setIsSupported(true);
            settingsManager.get(SettingKeys.M_WHITEBALANCE).setIsPresetted(true);
        }

        if (!camera1element.findChild("manualiso").isEmpty())
        {
            Log.d(TAG, "override manual iso");
            if (!camera1element.findChild("manualiso").getAttribute("supported","false").isEmpty())
            {
                if (camera1element.findChild("manualiso").getAttribute("supported","false").equals("false")) {
                    settingsManager.get(SettingKeys.M_MANUAL_ISO).setIsSupported(false);
                    settingsManager.get(SettingKeys.M_MANUAL_ISO).setIsPresetted(true);
                }
                else
                {
                    settingsManager.get(SettingKeys.M_MANUAL_ISO).setIsSupported(true);
                    settingsManager.get(SettingKeys.M_MANUAL_ISO).setIsPresetted(true);
                    setManualIso(camera1element.findChild("manualiso"));
                }
            }
            else
            {
                if(!camera1element.findChild("manualiso").findChildren("framework").isEmpty())
                {
                    List<XmlElement> frameworksiso = camera1element.findChild("manualiso").findChildren("framework");
                    for(XmlElement framiso : frameworksiso)
                    {
                        if (Frameworks.valueOf(framiso.getAttribute("type",Frameworks.Default.toString())) == settingsManager.getFrameWork())
                            setManualIso(framiso);
                    }
                }
                else
                    setManualIso(camera1element.findChild("manualiso"));
                settingsManager.get(SettingKeys.M_MANUAL_ISO).setIsPresetted(true);
            }
        }

        if (!camera1element.findChild("exposuretime").isEmpty())
        {
            Log.d(TAG, "override manual exposuretime");
            if (!camera1element.findChild("exposuretime").findChild("values").isEmpty())
            {
                String name = camera1element.findChild("exposuretime").findChild("values").getValue();
                settingsManager.get(SettingKeys.M_EXPOSURE_TIME).setValues(resources.getStringArray(resources.getIdentifier(name, "array", BuildConfig.APPLICATION_ID)));
            }
            if (!camera1element.findChild("exposuretime").findChild("key").isEmpty())
            {
                settingsManager.get(SettingKeys.M_EXPOSURE_TIME).setCamera1ParameterKEY(camera1element.findChild("exposuretime").findChild("key").getValue());
            }
            if (!camera1element.findChild("exposuretime").findChild("key").isEmpty())
            {
                settingsManager.get(SettingKeys.M_EXPOSURE_TIME).setType(camera1element.findChild("exposuretime").findChild("type").getIntValue(0));
                settingsManager.get(SettingKeys.M_EXPOSURE_TIME).setIsSupported(true);
            }
            else {
                settingsManager.get(SettingKeys.M_EXPOSURE_TIME).setIsSupported(false);
                settingsManager.get(SettingKeys.M_EXPOSURE_TIME).setCamera1ParameterKEY("unsupported");
            }
            settingsManager.get(SettingKeys.M_EXPOSURE_TIME).setIsPresetted(true);
        }

        if (!camera1element.findChild("hdrmode").isEmpty())
        {
            Log.d(TAG, "override hdr");
            if (camera1element.findChild("hdrmode").getAttribute("supported","false") != null)
            {
                if (!Boolean.parseBoolean(camera1element.findChild("hdrmode").getAttribute("supported","false")))
                    settingsManager.get(SettingKeys.HDR_MODE).setIsSupported(false);
                else{
                    settingsManager.get(SettingKeys.HDR_MODE).setIsSupported(true);
                    settingsManager.get(SettingKeys.HDR_MODE).setType(camera1element.findChild("hdrmode").getIntValue(1));
                }
            }
            settingsManager.get(SettingKeys.HDR_MODE).setIsPresetted(true);
        }

        if (!camera1element.findChild("virtuallensfilter").isEmpty())
        {
            settingsManager.get(SettingKeys.LENS_FILTER).setIsSupported(true);
        }

        if (!camera1element.findChild("denoise").isEmpty())
        {
            if (!camera1element.findChild("denoise").getBooleanValue())
            {
                settingsManager.get(SettingKeys.DENOISE).setIsSupported(false);
                settingsManager.get(SettingKeys.DENOISE).setIsPresetted(true);
            }
        }

        if (!camera1element.findChild("digitalimagestab").isEmpty())
        {
            if (!camera1element.findChild("digitalimagestab").getBooleanValue())
            {
                settingsManager.get(SettingKeys.DIGITAL_IMAGE_STABILIZATION).setIsSupported(false);
                settingsManager.get(SettingKeys.DIGITAL_IMAGE_STABILIZATION).setIsPresetted(true);
            }
        }

        if (!camera1element.findChild("manualfocus").isEmpty())
        {
            Log.d(TAG, "override manual focus");
            List<XmlElement> mfs = camera1element.findChildren("manualfocus");
            if (mfs.size() > 1) {
                for (XmlElement mf : mfs) {
                    if (mf.getIntAttribute("version", 0) == Build.VERSION.SDK_INT) {
                        setManualFocus(mf);
                    }
                }
            }
            else
                setManualFocus(mfs.get(0));
            settingsManager.get(SettingKeys.M_FOCUS).setIsPresetted(true);
        }

        if (!camera1element.findChild("rawformat").isEmpty())
        {
            Log.d(TAG, "override rawpictureformat");
            settingsManager.get(SettingKeys.RAW_PICTURE_FORMAT_SETTING).set(camera1element.findChild("rawformat").getValue());
            settingsManager.get(SettingKeys.RAW_PICTURE_FORMAT_SETTING).setIsPresetted(true);
            settingsManager.get(SettingKeys.RAW_PICTURE_FORMAT_SETTING).setIsSupported(true);
        }

        if (!camera1element.findChild("opticalimagestab").isEmpty())
        {
            settingsManager.get(SettingKeys.OIS_MODE).set(camera1element.findChild("opticalimagestab").findChild("key").getValue());
            settingsManager.get(SettingKeys.OIS_MODE).setValues(camera1element.findChild("opticalimagestab").findChild("values").getValue().split(","));
            settingsManager.get(SettingKeys.OIS_MODE).setIsSupported(true);
            settingsManager.get(SettingKeys.OIS_MODE).setIsPresetted(true);
        }
    }

    private void setManualFocus(XmlElement element)
    {
        if (element.findChild("min") != null)
        {
            TypedSettingMode settingInterface = settingsManager.get(SettingKeys.M_FOCUS);
            settingInterface.setMode(element.findChild("mode").getValue());
            settingInterface.setType(element.findChild("type").getIntValue(-1));
            settingInterface.setIsSupported(true);
            settingInterface.setIsPresetted(true);
            settingInterface.setCamera1ParameterKEY(element.findChild("key").getValue());
            settingInterface.setValues(Camera1FeatureDetectorTask.createManualFocusValues(element.findChild("min").getIntValue(0),element.findChild("max").getIntValue(0),element.findChild("step").getIntValue(0)));
        }
        else
            settingsManager.get(SettingKeys.M_FOCUS).setIsSupported(false);
    }

    private void setManualIso(XmlElement element)
    {
        if (!element.findChild("min").isEmpty()) {
            int min = element.findChild("min").getIntValue(100);
            int max = element.findChild("max").getIntValue(1600);
            int step = element.findChild("step").getIntValue(50);
            int type = element.findChild("type").getIntValue(0);
            settingsManager.get(SettingKeys.M_MANUAL_ISO).setType(type);
            settingsManager.get(SettingKeys.M_MANUAL_ISO).setCamera1ParameterKEY(element.findChild("key").getValue());
            settingsManager.get(SettingKeys.M_MANUAL_ISO).setValues(Camera1FeatureDetectorTask.createIsoValues(min, max, step,settingsManager.getFrameWork() == Frameworks.Xiaomi));
            settingsManager.get(SettingKeys.M_MANUAL_ISO).setIsSupported(true);
            settingsManager.get(SettingKeys.M_MANUAL_ISO).setIsPresetted(true);
        }
        else if (!element.findChild("values").isEmpty())
        {
            String name = element.findChild("values").getValue();
            settingsManager.get(SettingKeys.M_MANUAL_ISO).setValues(FreedApplication.getContext().getResources().getStringArray(FreedApplication.getContext().getResources().getIdentifier(name, "array", BuildConfig.APPLICATION_ID)));
            settingsManager.get(SettingKeys.M_MANUAL_ISO).setCamera1ParameterKEY(element.findChild("key").getValue());
            int type = element.findChild("type").getIntValue(0);
            settingsManager.get(SettingKeys.M_MANUAL_ISO).setType(type);
            settingsManager.get(SettingKeys.M_MANUAL_ISO).setIsSupported(true);
            settingsManager.get(SettingKeys.M_MANUAL_ISO).setIsPresetted(true);
        }
    }

    private String[] createStringArray(int min, int max, float step) {
        ArrayList<String> ar = new ArrayList<>();
        if (step == 0)
            step = 1;
        for (int i = min; i < max; i+=step)
        {
            ar.add(i+"");
        }
        return ar.toArray(new String[ar.size()]);
    }

    protected LongSparseArray<DngProfile> getDngProfiles(HashMap<String, CustomMatrix> matrixHashMap,File appDataFolder)
    {
        LongSparseArray<DngProfile> map = new LongSparseArray<>();
        try {
            File configFile = new File(appDataFolder.getAbsolutePath()+"/dngprofiles.xml");
            Log.d(TAG, configFile.getAbsolutePath() + " exists:" + configFile.exists());

            if (configFile.exists()){
                String xmlsource = StringUtils.getString(new FileInputStream(configFile));
                Log.d(TAG, xmlsource);
                XmlElement rootElement = XmlElement.parse(xmlsource);
                if (rootElement.getTagName().equals("devices"))
                {
                    List<XmlElement> devicesList = rootElement.findChildren("device");
                    XmlElement device = devicesList.get(0);
                    getDngStuff(map, device,matrixHashMap);
                }
            }
        } catch (IOException e) {
            Log.WriteEx(e);
        }
        return map;
    }

    private void getDngStuff(LongSparseArray<DngProfile> map, XmlElement device_element, HashMap<String, CustomMatrix> matrixHashMap) {
        XmlElement opcodesList = device_element.findChild("opcodes");
        List<XmlElement> opcodes = opcodesList.findChildren("camera");
        settingsManager.opcodeUrlList = new ArrayList<>();
        for (XmlElement opcodeItem : opcodes)
        {
            int camid = opcodeItem.getIntAttribute("id", 0);
            String op2url = opcodeItem.findChild("opcode2").getValue();
            String op3url = opcodeItem.findChild("opcode3").getValue();
            OpCodeUrl url =new OpCodeUrl(camid,op2url,op3url);
            settingsManager.opcodeUrlList.add(url);
        }

        Log.d(TAG, device_element.dumpChildElementsTagNames());
        List<XmlElement> fsizeList = device_element.findChildren("filesize");
        Log.d(TAG, "Found Dng Profiles:" + fsizeList.size());
        for (XmlElement filesize_element : fsizeList) {
            long filesize = Long.parseLong(filesize_element.getAttribute("size", "0"));
            Log.d(TAG, filesize_element.dumpChildElementsTagNames());
            DngProfile profile = getProfile(filesize_element, matrixHashMap);
            map.put(filesize, profile);
        }
    }

    private DngProfile getProfile(XmlElement element,HashMap<String, CustomMatrix> matrixes )
    {
        int blacklvl = Integer.parseInt(element.findChild("blacklvl").getValue());
        String whlvl = element.findChild("whitelvl").getValue();
        if (TextUtils.isEmpty(whlvl))
            whlvl = 1023+"";
        int whitelvl = Integer.parseInt(whlvl);
        int width = Integer.parseInt(element.findChild("width").getValue());
        int height = Integer.parseInt(element.findChild("height").getValue());
        int rawType = Integer.parseInt(element.findChild("rawtype").getValue());
        String colorpattern = element.findChild("colorpattern").getValue();
        int rowsize = Integer.parseInt(element.findChild("rowsize").getValue());
        String matrixset = element.findChild("matrixset").getValue();

        return new DngProfile(blacklvl,whitelvl,width,height,rawType,colorpattern,rowsize,matrixes.get(matrixset), matrixset);
    }

    protected HashMap<String, CustomMatrix> getMatrixes(Resources resources, File appDataFolder)
    {
        HashMap<String, CustomMatrix> matrixHashMap = new HashMap<>();
        try {
            matrixHashMap.put("off", null);
            String xmlsource = StringUtils.getString(resources.openRawResource(R.raw.matrixes));
            parseMatrixeXml(matrixHashMap, xmlsource);
            File configFile = new File(appDataFolder.getAbsolutePath()+"/matrixes.xml");
            if (configFile.exists())
            {
                xmlsource = StringUtils.getString(new FileInputStream(configFile));
                parseMatrixeXml(matrixHashMap,xmlsource);
            }
        } catch (IOException e) {
            Log.WriteEx(e);
        }
        return matrixHashMap;
    }

    private void parseMatrixeXml(HashMap<String, CustomMatrix> matrixHashMap, String xmlsource) {
        XmlElement rootElement = XmlElement.parse(xmlsource);
        if (rootElement.getTagName().equals("matrixes"))
        {
            List<XmlElement> profileElements = rootElement.findChildren("matrix");
            for (XmlElement xmlElement: profileElements)
            {
                String name  = xmlElement.getAttribute("name", "");
                String c1 = xmlElement.findChild("color1").getValue();
                String c2 = xmlElement.findChild("color2").getValue();
                String neut = xmlElement.findChild("neutral").getValue();
                String forward1 = xmlElement.findChild("forward1").getValue();
                String forward2 = xmlElement.findChild("forward2").getValue();
                String reduction1 = xmlElement.findChild("reduction1").getValue();
                String reduction2 = xmlElement.findChild("reduction2").getValue();
                String noise = xmlElement.findChild("noise").getValue();
                CustomMatrix mat = new CustomMatrix(c1,c2,neut,forward1,forward2,reduction1,reduction2,noise);
                matrixHashMap.put(name,mat);
            }
        }
    }

    public void saveDngProfiles(LongSparseArray<DngProfile> dngProfileList, String mDevice, File appData)
    {
        BufferedWriter writer = null;
        try {

            File configFile = new File(appData.getAbsolutePath()+"/dngprofiles.xml");
            Log.d(TAG, configFile.getAbsolutePath() + " exists:" + configFile.exists());
            Log.d(TAG, configFile.getParentFile().getAbsolutePath() + " exists:" + configFile.getParentFile().exists());
            if (!configFile.getParentFile().exists())
                configFile.getParentFile().mkdirs();
            Log.d(TAG, configFile.getParentFile().getAbsolutePath() + " exists:" + configFile.getParentFile().exists());
            configFile.createNewFile();
            writer = new BufferedWriter(new FileWriter(configFile));
            writer.write("<devices>" + "\r\n");
            writer.write("<device name = \""+ mDevice +"\">\r\n");

            writer.write("<opcodes>\r\n");
            for (OpCodeUrl url : settingsManager.opcodeUrlList)
            {
                writer.write(url.getXml());
            }
            writer.write("</opcodes>\r\n");

            for (int i =0; i< dngProfileList.size();i++)
            {
                long t = dngProfileList.keyAt(i);
                Log.d(TAG, "Write Profile: " + t);
                writer.write(dngProfileList.get(t).getXmlString(t));
            }

            writer.write("</device>" + "\r\n");
            writer.write("</devices>" + "\r\n");
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


    /**
     * Read the tonemap profiles from toneMapProfiles.xml
     * @param
     * @return
     */
    public HashMap<String,ToneMapProfile> getToneMapProfiles(File appDataFolder)
    {
        HashMap<String,ToneMapProfile>  hashMap = new HashMap<>();
        hashMap.put("off", null);

        try {
            String xmlsource = StringUtils.getString(FreedApplication.getContext().getResources().openRawResource(R.raw.tonemapprofiles));
            XmlElement xmlElement = XmlElement.parse(xmlsource);
            getTonemapProfiles(hashMap, xmlElement);
        } catch (IOException e) {
            e.printStackTrace();
        }

        File configFile = new File(appDataFolder.getAbsolutePath()+"/tonemapprofiles.xml");
        if (configFile.exists())
        {
            try {
                String xmlsource = StringUtils.getString(new FileInputStream(configFile));
                XmlElement xmlElement = XmlElement.parse(xmlsource);
                getTonemapProfiles(hashMap, xmlElement);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return hashMap;
    }

    private void getTonemapProfiles(HashMap<String, ToneMapProfile> hashMap, XmlElement xmlElement) {

        List<XmlElement> tonemapchilds = xmlElement.findChildren("tonemapprofile");
        if (tonemapchilds.size() > 0){
            for (XmlElement element : tonemapchilds)
            {
                ToneMapProfile profile = getToneMapProfile(element);
                hashMap.put(profile.getName(), profile);
            }
        }
    }

    public ToneMapProfile getToneMapProfile(XmlElement element)
    {
        ToneMapProfile toneMapProfile = new ToneMapProfile();
        toneMapProfile.name = element.getAttribute("name", "");
        String[] split = null;
        if (!element.findChild("tonecurve").isEmpty()) {
            String curve = element.findChild("tonecurve").getValue();
            curve = curve.replace("\n","").replace(" ","");
            split = curve.split(",");
            toneMapProfile.toneCurve = new float[split.length];
            for (int i = 0; i < split.length; i++) {
                if (!TextUtils.isEmpty(split[i])) {
                    toneMapProfile.toneCurve[i] = Float.parseFloat(split[i]);
                    //check if its in range 0-1 if not apply that range
                    //this happens when we extract it with exiftools. it shows it as 0-255 range
                    if (toneMapProfile.toneCurve[i] > 1)
                        toneMapProfile.toneCurve[i] = toneMapProfile.toneCurve[i] / 255;
                }
            }
        }

        if (!element.findChild("huesatmapdims").isEmpty())
        {
            split = element.findChild("huesatmapdims").getValue().split(" ");
            toneMapProfile.hueSatMapDims = new int[split.length];
            for (int i = 0; i < split.length; i++)
                toneMapProfile.hueSatMapDims[i] = Integer.parseInt(split[i]);
        }

        if (!element.findChild("huesatmapdata1").isEmpty()) {
            split = element.findChild("huesatmapdata1").getValue().split(" ");
            toneMapProfile.hueSatMap = new float[split.length];
            for (int i = 0; i < split.length; i++)
                toneMapProfile.hueSatMap[i] = Float.parseFloat(split[i]);
        }

        if (!element.findChild("huesatmapdata2").isEmpty()) {
            split = element.findChild("huesatmapdata2").getValue().split(" ");
            toneMapProfile.hueSatMap2 = new float[split.length];
            for (int i = 0; i < split.length; i++)
                toneMapProfile.hueSatMap2[i] = Float.parseFloat(split[i]);
        }

        if (!element.findChild("baselineexposure").isEmpty())
        {
            toneMapProfile.baselineExposure = element.findChild("baselineexposure").getFloatValue();
        }

        if (!element.findChild("baselineexposureoffset").isEmpty())
        {
            toneMapProfile.baselineExposureOffset = element.findChild("baselineexposureoffset").getFloatValue();
        }
        return toneMapProfile;
    }

}
