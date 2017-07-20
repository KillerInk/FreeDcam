package freed.utils;

import android.content.res.Resources;
import android.os.Build;
import android.support.v4.util.LongSparseArray;

import com.troop.freedcam.BuildConfig;
import com.troop.freedcam.R;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import freed.cam.apis.sonyremote.sonystuff.XmlElement;
import freed.cam.featuredetector.Camera1FeatureDetectorTask;
import freed.dng.CustomMatrix;
import freed.dng.DngProfile;
import freed.dng.ToneMapProfile;

/**
 * Created by troop on 25.06.2017.
 */

public class XmlParserWriter
{
    private final String TAG = XmlParserWriter.class.getSimpleName();



    public void parseAndFindSupportedDevice(Resources resources, AppSettingsManager appSettingsManager,HashMap<String, CustomMatrix> matrixHashMap)
    {
        try {
            String xmlsource = getString(resources.openRawResource(R.raw.supported_devices));
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

                            appSettingsManager.setDevice(device_element.getAttribute("name",""));
                            Log.d(TAG, "Found Device:" + appSettingsManager.getDeviceString());

                            XmlElement camera1element = device_element.findChild("camera1");


                            if (!camera1element.isEmpty()) {
                                Log.d(TAG, "Found camera1 overrides");
                                Log.v(TAG, camera1element.dumpChildElementsTagNames());
                                if (!camera1element.findChild("framework").isEmpty())
                                {
                                    appSettingsManager.setFramework(Integer.parseInt(camera1element.findChild("framework").getValue()));
                                }
                                else
                                    appSettingsManager.setFramework(Camera1FeatureDetectorTask.getFramework());

                                if (!camera1element.findChild("dngmanual").isEmpty())
                                    appSettingsManager.setDngManualsSupported(Boolean.parseBoolean(camera1element.findChild("dngmanual").getValue()));
                                else
                                    appSettingsManager.setDngManualsSupported(true);
                                Log.d(TAG, "dng manual supported:" + appSettingsManager.getDngManualsSupported());

                                if (!camera1element.findChild("opencameralegacy").isEmpty()) {
                                    appSettingsManager.opencamera1Legacy.setBoolean(Boolean.parseBoolean(camera1element.findChild("opencameralegacy").getValue()));
                                    appSettingsManager.opencamera1Legacy.setIsPresetted(true);
                                }

                                Log.d(TAG, "OpenLegacy: " + appSettingsManager.opencamera1Legacy.getBoolean() + " isPresetted:" + appSettingsManager.opencamera1Legacy.isPresetted());

                                if (!camera1element.findChild("zteae").isEmpty())
                                    appSettingsManager.setZteAe(Boolean.parseBoolean(camera1element.findChild("zte").getValue()));
                                else
                                    appSettingsManager.setZteAe(false);

                                Log.d(TAG, "isZteAE:" + appSettingsManager.isZteAe());

                                if (!camera1element.findChild("needrestartaftercapture").isEmpty())
                                    appSettingsManager.setNeedRestartAfterCapture(Boolean.parseBoolean(camera1element.findChild("needrestartaftercapture").getValue()));
                                else
                                    appSettingsManager.setNeedRestartAfterCapture(false);

                                if (!camera1element.findChild("burst").isEmpty()) {
                                    appSettingsManager.manualBurst.setIsSupported(true);
                                    int max = Integer.parseInt(camera1element.findChild("burst").getValue());
                                    appSettingsManager.manualBurst.setValues(createStringArray(1, max, 1));
                                    appSettingsManager.manualBurst.set(1 + "");
                                } else
                                    appSettingsManager.manualBurst.setIsSupported(false);
                                appSettingsManager.manualBurst.setIsPresetted(true);

                                if (!camera1element.findChild("nightmode").isEmpty()) {
                                    appSettingsManager.nightMode.setIsSupported(true);
                                    int type = Integer.parseInt(camera1element.findChild("nightmode").getValue());
                                    appSettingsManager.nightMode.setType(type);
                                } else
                                    appSettingsManager.nightMode.setIsSupported(false);
                                appSettingsManager.nightMode.setIsPresetted(true);

                                if (!camera1element.findChild("whitebalance").isEmpty())
                                {
                                    //TODO handel sdk specific
                                    Log.d(TAG, "override manual whiteblalance");
                                    int min = camera1element.findChild("whitebalance").findChild("min").getIntValue(2000);
                                    int max  = camera1element.findChild("whitebalance").findChild("max").getIntValue(8000);
                                    int step = camera1element.findChild("whitebalance").findChild("step").getIntValue(100);
                                    appSettingsManager.manualWhiteBalance.setKEY(camera1element.findChild("whitebalance").findChild("key").getValue());
                                    appSettingsManager.manualWhiteBalance.setMode(camera1element.findChild("whitebalance").findChild("mode").getValue());
                                    appSettingsManager.manualWhiteBalance.setValues(Camera1FeatureDetectorTask.createWBStringArray(min,max,step,appSettingsManager));
                                    appSettingsManager.manualWhiteBalance.setIsSupported(true);
                                    appSettingsManager.manualWhiteBalance.setIsPresetted(true);
                                }

                                if (!camera1element.findChild("manualiso").isEmpty())
                                {
                                    Log.d(TAG, "override manual iso");
                                    if (!camera1element.findChild("manualiso").getAttribute("supported","false").isEmpty())
                                    {
                                        if (camera1element.findChild("manualiso").getAttribute("supported","false").equals("false")) {
                                            appSettingsManager.manualIso.setIsSupported(false);
                                            appSettingsManager.manualIso.setIsPresetted(true);
                                        }
                                        else
                                        {
                                            appSettingsManager.manualIso.setIsSupported(true);
                                            appSettingsManager.manualIso.setIsPresetted(true);
                                            setManualIso(camera1element.findChild("manualiso"),appSettingsManager);
                                        }
                                    }
                                    else
                                    {
                                        if(!camera1element.findChild("manualiso").findChildren("framework").isEmpty())
                                        {
                                            List<XmlElement> frameworksiso = camera1element.findChild("manualiso").findChildren("framework");
                                            for(XmlElement framiso : frameworksiso)
                                            {
                                                if (Integer.parseInt(framiso.getAttribute("type","0")) == appSettingsManager.getFrameWork())
                                                    setManualIso(framiso,appSettingsManager);
                                            }
                                        }
                                        else
                                            setManualIso(camera1element.findChild("manualiso"),appSettingsManager);
                                        appSettingsManager.manualIso.setIsPresetted(true);
                                    }
                                }

                                if (!camera1element.findChild("exposuretime").isEmpty())
                                {
                                    Log.d(TAG, "override manual exposuretime");
                                    if (!camera1element.findChild("exposuretime").findChild("values").isEmpty())
                                    {
                                        String name = camera1element.findChild("exposuretime").findChild("values").getValue();
                                        appSettingsManager.manualExposureTime.setValues(resources.getStringArray(resources.getIdentifier(name, "array", BuildConfig.APPLICATION_ID)));
                                    }
                                    if (!camera1element.findChild("exposuretime").findChild("key").isEmpty())
                                    {
                                        appSettingsManager.manualExposureTime.setKEY(camera1element.findChild("exposuretime").findChild("key").getValue());
                                    }
                                    if (!camera1element.findChild("exposuretime").findChild("key").isEmpty())
                                    {
                                        appSettingsManager.manualExposureTime.setType(camera1element.findChild("exposuretime").findChild("type").getIntValue(0));
                                        appSettingsManager.manualExposureTime.setIsSupported(true);
                                    }
                                    else {
                                        appSettingsManager.manualExposureTime.setIsSupported(false);
                                        appSettingsManager.manualExposureTime.setKEY("unsupported");
                                    }
                                    appSettingsManager.manualExposureTime.setIsPresetted(true);
                                }

                                if (!camera1element.findChild("hdrmode").isEmpty())
                                {
                                    Log.d(TAG, "override hdr");
                                    if (camera1element.findChild("hdrmode").getAttribute("supported","false") != null)
                                    {
                                        if (!Boolean.parseBoolean(camera1element.findChild("hdrmode").getAttribute("supported","false")))
                                            appSettingsManager.hdrMode.setIsSupported(false);
                                        else{
                                            appSettingsManager.hdrMode.setIsSupported(true);
                                            appSettingsManager.hdrMode.setType(camera1element.findChild("hdrmode").getIntValue(1));
                                        }
                                    }
                                    appSettingsManager.hdrMode.setIsPresetted(true);
                                }

                                if (!camera1element.findChild("virtuallensfilter").isEmpty())
                                {
                                    appSettingsManager.virtualLensfilter.setIsSupported(true);
                                }

                                if (!camera1element.findChild("denoise").isEmpty())
                                {
                                    if (!camera1element.findChild("denoise").getBooleanValue())
                                    {
                                        appSettingsManager.denoiseMode.setIsSupported(false);
                                        appSettingsManager.denoiseMode.setIsPresetted(true);
                                    }
                                }

                                if (!camera1element.findChild("digitalimagestab").isEmpty())
                                {
                                    if (!camera1element.findChild("digitalimagestab").getBooleanValue())
                                    {
                                        appSettingsManager.digitalImageStabilisationMode.setIsSupported(false);
                                        appSettingsManager.digitalImageStabilisationMode.setIsPresetted(true);
                                    }
                                }

                                if (!camera1element.findChild("manualfocus").isEmpty())
                                {
                                    Log.d(TAG, "override manual focus");
                                    List<XmlElement> mfs = camera1element.findChildren("manualfocus");
                                    if (mfs.size() > 1) {
                                        for (XmlElement mf : mfs) {
                                            if (mf.getIntAttribute("version", 0) == Build.VERSION.SDK_INT) {
                                                setManualFocus(mf,appSettingsManager);
                                            }
                                        }
                                    }
                                    else
                                        setManualFocus(mfs.get(0),appSettingsManager);
                                    appSettingsManager.manualFocus.setIsPresetted(true);
                                }

                                if (!camera1element.findChild("rawformat").isEmpty())
                                {
                                    Log.d(TAG, "override rawpictureformat");
                                    appSettingsManager.rawPictureFormat.set(camera1element.findChild("rawformat").getValue());
                                    appSettingsManager.rawPictureFormat.setIsPresetted(true);
                                    appSettingsManager.rawPictureFormat.setIsSupported(true);
                                }

                                if (!camera1element.findChild("opticalimagestab").isEmpty())
                                {
                                    appSettingsManager.opticalImageStabilisation.set(camera1element.findChild("opticalimagestab").findChild("key").getValue());
                                    appSettingsManager.opticalImageStabilisation.setValues(camera1element.findChild("opticalimagestab").findChild("values").getValue().split(","));
                                    appSettingsManager.opticalImageStabilisation.setIsSupported(true);
                                    appSettingsManager.opticalImageStabilisation.setIsPresetted(true);
                                }
                            }

                            XmlElement camera2element = device_element.findChild("camera2");
                            if (!camera2element.isEmpty()) {
                                Log.d(TAG,"Found Camera2 overrides");
                                if (!camera2element.findChild("forcerawtodng").isEmpty())
                                    appSettingsManager.setForceRawToDng(camera2element.findChild("forcerawtodng").getBooleanValue());
                                if (!camera2element.findChild("overrideprofile").isEmpty())
                                    appSettingsManager.setsOverrideDngProfile(camera2element.findChild("overrideprofile").getBooleanValue());

                                if (!camera2element.findChild("maxexposuretime").isEmpty())
                                {
                                    appSettingsManager.setCamera2MaxExposureTime(camera2element.findChild("maxexposuretime").getLongValue());
                                }
                                if (!camera2element.findChild("minexposuretime").isEmpty())
                                {
                                    appSettingsManager.setCamera2MinExposureTime(camera2element.findChild("minexposuretime").getLongValue());
                                }
                                if (!camera2element.findChild("maxiso").isEmpty())
                                    appSettingsManager.setCamera2MaxIso(camera2element.findChild("maxiso").getIntValue(0));
                            }

                            LongSparseArray<DngProfile> dngProfileHashMap = new LongSparseArray<>();
                            getDngStuff(dngProfileHashMap, device_element,matrixHashMap,appSettingsManager);
                            Log.d(TAG, "Save Dng Profiles:" + dngProfileHashMap.size());
                            saveDngProfiles(dngProfileHashMap,appSettingsManager.getDeviceString());

                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            Log.WriteEx(e);
        }
    }

    private void setManualFocus(XmlElement element, AppSettingsManager appSettingsManager)
    {
        if (element.findChild("min") != null)
        {
            appSettingsManager.manualFocus.setMode(element.findChild("mode").getValue());
            appSettingsManager.manualFocus.setType(element.findChild("type").getIntValue(-1));
            appSettingsManager.manualFocus.setIsSupported(true);
            appSettingsManager.manualFocus.setIsPresetted(true);
            appSettingsManager.manualFocus.setKEY(element.findChild("key").getValue());
            appSettingsManager.manualFocus.setValues(Camera1FeatureDetectorTask.createManualFocusValues(element.findChild("min").getIntValue(0),element.findChild("max").getIntValue(0),element.findChild("step").getIntValue(0),appSettingsManager));
        }
        else
            appSettingsManager.manualFocus.setIsSupported(false);
    }

    private void setManualIso(XmlElement element, AppSettingsManager appSettingsManager)
    {
        if (!element.findChild("min").isEmpty()) {
            int min = element.findChild("min").getIntValue(100);
            int max = element.findChild("max").getIntValue(1600);
            int step = element.findChild("step").getIntValue(50);
            int type = element.findChild("type").getIntValue(0);
            appSettingsManager.manualIso.setType(type);
            appSettingsManager.manualIso.setKEY(element.findChild("key").getValue());
            appSettingsManager.manualIso.setValues(Camera1FeatureDetectorTask.createIsoValues(min, max, step, appSettingsManager));
            appSettingsManager.manualIso.setIsSupported(true);
            appSettingsManager.manualIso.setIsPresetted(true);
        }
        else if (!element.findChild("values").isEmpty())
        {
            String name = element.findChild("values").getValue();
            appSettingsManager.manualIso.setValues(appSettingsManager.getResources().getStringArray(appSettingsManager.getResources().getIdentifier(name, "array", BuildConfig.APPLICATION_ID)));
            appSettingsManager.manualIso.setKEY(element.findChild("key").getValue());
            int type = element.findChild("type").getIntValue(0);
            appSettingsManager.manualIso.setType(type);
            appSettingsManager.manualIso.setIsSupported(true);
            appSettingsManager.manualIso.setIsPresetted(true);
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

    protected LongSparseArray<DngProfile> getDngProfiles(HashMap<String, CustomMatrix> matrixHashMap, AppSettingsManager appSettingsManager)
    {
        LongSparseArray<DngProfile> map = new LongSparseArray<>();
        try {
            File configFile = new File(StringUtils.GetFreeDcamConfigFolder+"dngprofiles.xml");
            Log.d(TAG, configFile.getAbsolutePath() + " exists:" + configFile.exists());

            String xmlsource = getString(new FileInputStream(configFile));
            Log.d(TAG, xmlsource);
            XmlElement rootElement = XmlElement.parse(xmlsource);
            if (rootElement.getTagName().equals("devices"))
            {
                List<XmlElement> devicesList = rootElement.findChildren("device");
                XmlElement device = devicesList.get(0);
                getDngStuff(map, device,matrixHashMap,appSettingsManager);
            }
        } catch (IOException e) {
            Log.WriteEx(e);
        }
        return map;
    }

    private void getDngStuff(LongSparseArray<DngProfile> map, XmlElement device_element, HashMap<String, CustomMatrix> matrixHashMap,AppSettingsManager appSettingsManager) {
        if (!device_element.getAttribute("opcode2", "").isEmpty())
            appSettingsManager.opcodeUrlList[0] = device_element.getAttribute("opcode2", "");
        if (!device_element.getAttribute("opcode3", "").isEmpty())
            appSettingsManager.opcodeUrlList[1] = device_element.getAttribute("opcode3", "");

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
        int width = Integer.parseInt(element.findChild("width").getValue());
        int height = Integer.parseInt(element.findChild("height").getValue());
        int rawType = Integer.parseInt(element.findChild("rawtype").getValue());
        String colorpattern = element.findChild("colorpattern").getValue();
        int rowsize = Integer.parseInt(element.findChild("rowsize").getValue());
        String matrixset = element.findChild("matrixset").getValue();

        return new DngProfile(blacklvl,width,height,rawType,colorpattern,rowsize,matrixes.get(matrixset), matrixset);
    }

    protected HashMap<String, CustomMatrix> getMatrixes(Resources resources)
    {
        HashMap<String, CustomMatrix> matrixHashMap = new HashMap<>();
        try {
            matrixHashMap.put("off", null);
            String xmlsource = getString(resources.openRawResource(R.raw.matrixes));
            parseMatrixeXml(matrixHashMap, xmlsource);
            File configFile = new File(StringUtils.GetFreeDcamConfigFolder+"matrixes.xml");
            if (configFile.exists())
            {
                xmlsource = getString(new FileInputStream(configFile));
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

    private String getString(InputStream inputStream) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int result = bis.read();
        while(result != -1) {
            buf.write((byte) result);
            result = bis.read();
        }
        return buf.toString();
    }

    public void saveDngProfiles(LongSparseArray<DngProfile> dngProfileList, String mDevice)
    {
        BufferedWriter writer = null;
        try {

            File configFile = new File(StringUtils.GetFreeDcamConfigFolder+"dngprofiles.xml");
            Log.d(TAG, configFile.getAbsolutePath() + " exists:" + configFile.exists());
            Log.d(TAG, configFile.getParentFile().getAbsolutePath() + " exists:" + configFile.getParentFile().exists());
            if (!configFile.getParentFile().exists())
                configFile.getParentFile().mkdirs();
            Log.d(TAG, configFile.getParentFile().getAbsolutePath() + " exists:" + configFile.getParentFile().exists());
            configFile.createNewFile();
            writer = new BufferedWriter(new FileWriter(configFile));
            writer.write("<devices>" + "\r\n");
            writer.write("<device name = \""+ mDevice +"\">\r\n");

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

    public HashMap<String,VideoMediaProfile> getMediaProfiles(AppSettingsManager appSettingsManager)
    {
        HashMap<String,VideoMediaProfile>  hashMap = new HashMap<>();
        File configFile = new File(StringUtils.GetFreeDcamConfigFolder+"videoProfiles.xml");
        if (configFile.exists())
        {
            try {
                String xmlsource = getString(new FileInputStream(configFile));
                XmlElement xmlElement = XmlElement.parse(xmlsource);
                if (appSettingsManager.getCamApi().equals(AppSettingsManager.API_1)){
                    XmlElement camera1node = xmlElement.findChild("camera1");
                    if (appSettingsManager.getIsFrontCamera())
                    {
                        XmlElement frontnode = camera1node.findChild("front");
                        getMediaProfilesFromXmlNode(hashMap,frontnode);
                    }
                    else
                    {
                        XmlElement backnode = camera1node.findChild("back");
                        getMediaProfilesFromXmlNode(hashMap,backnode);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return hashMap;
    }

    private void getMediaProfilesFromXmlNode(HashMap<String,VideoMediaProfile> map,XmlElement element)
    {
        List<XmlElement> xmlprofiles = element.findChildren("mediaprofile");
        for (XmlElement profile : xmlprofiles)
        {
            VideoMediaProfile videoMediaProfile = new VideoMediaProfile(profile);
            map.put(videoMediaProfile.ProfileName, videoMediaProfile);
        }
    }


    /**
     * Read the tonemap profiles from toneMapProfiles.xml
     * @param appSettingsManager
     * @return
     */
    public HashMap<String,ToneMapProfile> getToneMapProfiles(AppSettingsManager appSettingsManager)
    {
        HashMap<String,ToneMapProfile>  hashMap = new HashMap<>();
        hashMap.put("off", null);

        try {
            String xmlsource = getString(appSettingsManager.getResources().openRawResource(R.raw.tonemapprofiles));
            XmlElement xmlElement = XmlElement.parse(xmlsource);
            getTonemapProfiles(hashMap, xmlElement);
        } catch (IOException e) {
            e.printStackTrace();
        }

        File configFile = new File(StringUtils.GetFreeDcamConfigFolder+"tonemapprofiles.xml");
        if (configFile.exists())
        {
            try {
                String xmlsource = getString(new FileInputStream(configFile));
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
                ToneMapProfile profile = new ToneMapProfile(element);
                hashMap.put(profile.getName(), profile);
            }
        }
    }
}
