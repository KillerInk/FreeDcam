package freed.settings;

import android.content.res.Resources;
import android.os.Build;
import android.support.v4.util.LongSparseArray;
import android.text.TextUtils;

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

import freed.cam.apis.featuredetector.Camera1FeatureDetectorTask;
import freed.cam.apis.sonyremote.sonystuff.XmlElement;
import freed.dng.CustomMatrix;
import freed.dng.DngProfile;
import freed.dng.ToneMapProfile;
import freed.utils.Log;
import freed.utils.StringUtils;
import freed.utils.VideoMediaProfile;

/**
 * Created by troop on 25.06.2017.
 */

public class XmlParserWriter
{
    private final String TAG = XmlParserWriter.class.getSimpleName();



    public void parseAndFindSupportedDevice(Resources resources, HashMap<String, CustomMatrix> matrixHashMap)
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

                            AppSettingsManager.getInstance().setDevice(device_element.getAttribute("name",""));
                            Log.d(TAG, "Found Device:" + AppSettingsManager.getInstance().getDeviceString());

                            XmlElement camera1element = device_element.findChild("camera1");


                            if (!camera1element.isEmpty()) {
                                Log.d(TAG, "Found camera1 overrides");
                                Log.v(TAG, camera1element.dumpChildElementsTagNames());
                                if (!camera1element.findChild("framework").isEmpty())
                                {
                                    AppSettingsManager.getInstance().setFramework(Integer.parseInt(camera1element.findChild("framework").getValue()));
                                }
                                else
                                    AppSettingsManager.getInstance().setFramework(Camera1FeatureDetectorTask.getFramework());

                                if (!camera1element.findChild("dngmanual").isEmpty())
                                    AppSettingsManager.getInstance().dngSupportManualModes.setBoolean(Boolean.parseBoolean(camera1element.findChild("dngmanual").getValue()));
                                else
                                    AppSettingsManager.getInstance().dngSupportManualModes.setBoolean(true);
                                Log.d(TAG, "dng manual supported:" + AppSettingsManager.getInstance().dngSupportManualModes.getBoolean());

                                if (!camera1element.findChild("opencameralegacy").isEmpty()) {
                                    AppSettingsManager.getInstance().opencamera1Legacy.setBoolean(Boolean.parseBoolean(camera1element.findChild("opencameralegacy").getValue()));
                                    AppSettingsManager.getInstance().opencamera1Legacy.setIsPresetted(true);
                                }

                                Log.d(TAG, "OpenLegacy: " + AppSettingsManager.getInstance().opencamera1Legacy.getBoolean() + " isPresetted:" + AppSettingsManager.getInstance().opencamera1Legacy.isPresetted());

                                if (!camera1element.findChild("zteae").isEmpty())
                                    AppSettingsManager.getInstance().setZteAe(Boolean.parseBoolean(camera1element.findChild("zte").getValue()));
                                else
                                    AppSettingsManager.getInstance().setZteAe(false);

                                Log.d(TAG, "isZteAE:" + AppSettingsManager.getInstance().isZteAe());

                                if (!camera1element.findChild("needrestartaftercapture").isEmpty())
                                    AppSettingsManager.getInstance().needRestartAfterCapture.setBoolean(Boolean.parseBoolean(camera1element.findChild("needrestartaftercapture").getValue()));
                                else
                                    AppSettingsManager.getInstance().needRestartAfterCapture.setBoolean(false);

                                if (!camera1element.findChild("burst").isEmpty()) {
                                    AppSettingsManager.getInstance().manualBurst.setIsSupported(true);
                                    int max = Integer.parseInt(camera1element.findChild("burst").getValue());
                                    AppSettingsManager.getInstance().manualBurst.setValues(createStringArray(1, max, 1));
                                    AppSettingsManager.getInstance().manualBurst.set(1 + "");
                                } else
                                    AppSettingsManager.getInstance().manualBurst.setIsSupported(false);
                                AppSettingsManager.getInstance().manualBurst.setIsPresetted(true);

                                if (!camera1element.findChild("nightmode").isEmpty()) {
                                    AppSettingsManager.getInstance().nightMode.setIsSupported(true);
                                    int type = Integer.parseInt(camera1element.findChild("nightmode").getValue());
                                    AppSettingsManager.getInstance().nightMode.setType(type);
                                } else
                                    AppSettingsManager.getInstance().nightMode.setIsSupported(false);
                                AppSettingsManager.getInstance().nightMode.setIsPresetted(true);

                                if (!camera1element.findChild("whitebalance").isEmpty())
                                {
                                    //TODO handel sdk specific
                                    Log.d(TAG, "override manual whiteblalance");
                                    int min = camera1element.findChild("whitebalance").findChild("min").getIntValue(2000);
                                    int max  = camera1element.findChild("whitebalance").findChild("max").getIntValue(8000);
                                    int step = camera1element.findChild("whitebalance").findChild("step").getIntValue(100);
                                    AppSettingsManager.getInstance().manualWhiteBalance.setKEY(camera1element.findChild("whitebalance").findChild("key").getValue());
                                    AppSettingsManager.getInstance().manualWhiteBalance.setMode(camera1element.findChild("whitebalance").findChild("mode").getValue());
                                    AppSettingsManager.getInstance().manualWhiteBalance.setValues(Camera1FeatureDetectorTask.createWBStringArray(min,max,step));
                                    AppSettingsManager.getInstance().manualWhiteBalance.setIsSupported(true);
                                    AppSettingsManager.getInstance().manualWhiteBalance.setIsPresetted(true);
                                }

                                if (!camera1element.findChild("manualiso").isEmpty())
                                {
                                    Log.d(TAG, "override manual iso");
                                    if (!camera1element.findChild("manualiso").getAttribute("supported","false").isEmpty())
                                    {
                                        if (camera1element.findChild("manualiso").getAttribute("supported","false").equals("false")) {
                                            AppSettingsManager.getInstance().manualIso.setIsSupported(false);
                                            AppSettingsManager.getInstance().manualIso.setIsPresetted(true);
                                        }
                                        else
                                        {
                                            AppSettingsManager.getInstance().manualIso.setIsSupported(true);
                                            AppSettingsManager.getInstance().manualIso.setIsPresetted(true);
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
                                                if (Integer.parseInt(framiso.getAttribute("type","0")) == AppSettingsManager.getInstance().getFrameWork())
                                                    setManualIso(framiso);
                                            }
                                        }
                                        else
                                            setManualIso(camera1element.findChild("manualiso"));
                                        AppSettingsManager.getInstance().manualIso.setIsPresetted(true);
                                    }
                                }

                                if (!camera1element.findChild("exposuretime").isEmpty())
                                {
                                    Log.d(TAG, "override manual exposuretime");
                                    if (!camera1element.findChild("exposuretime").findChild("values").isEmpty())
                                    {
                                        String name = camera1element.findChild("exposuretime").findChild("values").getValue();
                                        AppSettingsManager.getInstance().manualExposureTime.setValues(resources.getStringArray(resources.getIdentifier(name, "array", BuildConfig.APPLICATION_ID)));
                                    }
                                    if (!camera1element.findChild("exposuretime").findChild("key").isEmpty())
                                    {
                                        AppSettingsManager.getInstance().manualExposureTime.setKEY(camera1element.findChild("exposuretime").findChild("key").getValue());
                                    }
                                    if (!camera1element.findChild("exposuretime").findChild("key").isEmpty())
                                    {
                                        AppSettingsManager.getInstance().manualExposureTime.setType(camera1element.findChild("exposuretime").findChild("type").getIntValue(0));
                                        AppSettingsManager.getInstance().manualExposureTime.setIsSupported(true);
                                    }
                                    else {
                                        AppSettingsManager.getInstance().manualExposureTime.setIsSupported(false);
                                        AppSettingsManager.getInstance().manualExposureTime.setKEY("unsupported");
                                    }
                                    AppSettingsManager.getInstance().manualExposureTime.setIsPresetted(true);
                                }

                                if (!camera1element.findChild("hdrmode").isEmpty())
                                {
                                    Log.d(TAG, "override hdr");
                                    if (camera1element.findChild("hdrmode").getAttribute("supported","false") != null)
                                    {
                                        if (!Boolean.parseBoolean(camera1element.findChild("hdrmode").getAttribute("supported","false")))
                                            AppSettingsManager.getInstance().hdrMode.setIsSupported(false);
                                        else{
                                            AppSettingsManager.getInstance().hdrMode.setIsSupported(true);
                                            AppSettingsManager.getInstance().hdrMode.setType(camera1element.findChild("hdrmode").getIntValue(1));
                                        }
                                    }
                                    AppSettingsManager.getInstance().hdrMode.setIsPresetted(true);
                                }

                                if (!camera1element.findChild("virtuallensfilter").isEmpty())
                                {
                                    AppSettingsManager.getInstance().virtualLensfilter.setIsSupported(true);
                                }

                                if (!camera1element.findChild("denoise").isEmpty())
                                {
                                    if (!camera1element.findChild("denoise").getBooleanValue())
                                    {
                                        AppSettingsManager.getInstance().denoiseMode.setIsSupported(false);
                                        AppSettingsManager.getInstance().denoiseMode.setIsPresetted(true);
                                    }
                                }

                                if (!camera1element.findChild("digitalimagestab").isEmpty())
                                {
                                    if (!camera1element.findChild("digitalimagestab").getBooleanValue())
                                    {
                                        AppSettingsManager.getInstance().digitalImageStabilisationMode.setIsSupported(false);
                                        AppSettingsManager.getInstance().digitalImageStabilisationMode.setIsPresetted(true);
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
                                    AppSettingsManager.getInstance().manualFocus.setIsPresetted(true);
                                }

                                if (!camera1element.findChild("rawformat").isEmpty())
                                {
                                    Log.d(TAG, "override rawpictureformat");
                                    AppSettingsManager.getInstance().rawPictureFormat.set(camera1element.findChild("rawformat").getValue());
                                    AppSettingsManager.getInstance().rawPictureFormat.setIsPresetted(true);
                                    AppSettingsManager.getInstance().rawPictureFormat.setIsSupported(true);
                                }

                                if (!camera1element.findChild("opticalimagestab").isEmpty())
                                {
                                    AppSettingsManager.getInstance().opticalImageStabilisation.set(camera1element.findChild("opticalimagestab").findChild("key").getValue());
                                    AppSettingsManager.getInstance().opticalImageStabilisation.setValues(camera1element.findChild("opticalimagestab").findChild("values").getValue().split(","));
                                    AppSettingsManager.getInstance().opticalImageStabilisation.setIsSupported(true);
                                    AppSettingsManager.getInstance().opticalImageStabilisation.setIsPresetted(true);
                                }
                            }

                            XmlElement camera2element = device_element.findChild("camera2");
                            if (!camera2element.isEmpty()) {
                                Log.d(TAG,"Found Camera2 overrides");
                                if (!camera2element.findChild("forcerawtodng").isEmpty())
                                    AppSettingsManager.getInstance().forceRawToDng.setBoolean(camera2element.findChild("forcerawtodng").getBooleanValue());
                                if (!camera2element.findChild("overrideprofile").isEmpty())
                                    AppSettingsManager.getInstance().setsOverrideDngProfile(camera2element.findChild("overrideprofile").getBooleanValue());

                                if (!camera2element.findChild("maxexposuretime").isEmpty())
                                {
                                    AppSettingsManager.getInstance().setCamera2MaxExposureTime(camera2element.findChild("maxexposuretime").getLongValue());
                                }
                                if (!camera2element.findChild("minexposuretime").isEmpty())
                                {
                                    AppSettingsManager.getInstance().setCamera2MinExposureTime(camera2element.findChild("minexposuretime").getLongValue());
                                }
                                if (!camera2element.findChild("maxiso").isEmpty())
                                    AppSettingsManager.getInstance().setCamera2MaxIso(camera2element.findChild("maxiso").getIntValue(0));
                            }

                            LongSparseArray<DngProfile> dngProfileHashMap = new LongSparseArray<>();
                            getDngStuff(dngProfileHashMap, device_element,matrixHashMap);
                            Log.d(TAG, "Save Dng Profiles:" + dngProfileHashMap.size());
                            saveDngProfiles(dngProfileHashMap,AppSettingsManager.getInstance().getDeviceString());

                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            Log.WriteEx(e);
        }
    }

    private void setManualFocus(XmlElement element)
    {
        if (element.findChild("min") != null)
        {
            AppSettingsManager.getInstance().manualFocus.setMode(element.findChild("mode").getValue());
            AppSettingsManager.getInstance().manualFocus.setType(element.findChild("type").getIntValue(-1));
            AppSettingsManager.getInstance().manualFocus.setIsSupported(true);
            AppSettingsManager.getInstance().manualFocus.setIsPresetted(true);
            AppSettingsManager.getInstance().manualFocus.setKEY(element.findChild("key").getValue());
            AppSettingsManager.getInstance().manualFocus.setValues(Camera1FeatureDetectorTask.createManualFocusValues(element.findChild("min").getIntValue(0),element.findChild("max").getIntValue(0),element.findChild("step").getIntValue(0)));
        }
        else
            AppSettingsManager.getInstance().manualFocus.setIsSupported(false);
    }

    private void setManualIso(XmlElement element)
    {
        if (!element.findChild("min").isEmpty()) {
            int min = element.findChild("min").getIntValue(100);
            int max = element.findChild("max").getIntValue(1600);
            int step = element.findChild("step").getIntValue(50);
            int type = element.findChild("type").getIntValue(0);
            AppSettingsManager.getInstance().manualIso.setType(type);
            AppSettingsManager.getInstance().manualIso.setKEY(element.findChild("key").getValue());
            AppSettingsManager.getInstance().manualIso.setValues(Camera1FeatureDetectorTask.createIsoValues(min, max, step));
            AppSettingsManager.getInstance().manualIso.setIsSupported(true);
            AppSettingsManager.getInstance().manualIso.setIsPresetted(true);
        }
        else if (!element.findChild("values").isEmpty())
        {
            String name = element.findChild("values").getValue();
            AppSettingsManager.getInstance().manualIso.setValues(AppSettingsManager.getInstance().getResources().getStringArray(AppSettingsManager.getInstance().getResources().getIdentifier(name, "array", BuildConfig.APPLICATION_ID)));
            AppSettingsManager.getInstance().manualIso.setKEY(element.findChild("key").getValue());
            int type = element.findChild("type").getIntValue(0);
            AppSettingsManager.getInstance().manualIso.setType(type);
            AppSettingsManager.getInstance().manualIso.setIsSupported(true);
            AppSettingsManager.getInstance().manualIso.setIsPresetted(true);
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

    protected LongSparseArray<DngProfile> getDngProfiles(HashMap<String, CustomMatrix> matrixHashMap)
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
                getDngStuff(map, device,matrixHashMap);
            }
        } catch (IOException e) {
            Log.WriteEx(e);
        }
        return map;
    }

    private void getDngStuff(LongSparseArray<DngProfile> map, XmlElement device_element, HashMap<String, CustomMatrix> matrixHashMap) {
        if (!device_element.getAttribute("opcode2", "").isEmpty())
            AppSettingsManager.getInstance().opcodeUrlList[0] = device_element.getAttribute("opcode2", "");
        if (!device_element.getAttribute("opcode3", "").isEmpty())
            AppSettingsManager.getInstance().opcodeUrlList[1] = device_element.getAttribute("opcode3", "");

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

    public HashMap<String,VideoMediaProfile> getMediaProfiles()
    {
        HashMap<String,VideoMediaProfile>  hashMap = new HashMap<>();
        File configFile = new File(StringUtils.GetFreeDcamConfigFolder+"videoProfiles.xml");
        if (configFile.exists())
        {
            try {
                String xmlsource = getString(new FileInputStream(configFile));
                XmlElement xmlElement = XmlElement.parse(xmlsource);
                if (AppSettingsManager.getInstance().getCamApi().equals(AppSettingsManager.API_1)){
                    XmlElement camera1node = xmlElement.findChild("camera1");
                    if (AppSettingsManager.getInstance().getIsFrontCamera())
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
     * @param
     * @return
     */
    public HashMap<String,ToneMapProfile> getToneMapProfiles()
    {
        HashMap<String,ToneMapProfile>  hashMap = new HashMap<>();
        hashMap.put("off", null);

        try {
            String xmlsource = getString(AppSettingsManager.getInstance().getResources().openRawResource(R.raw.tonemapprofiles));
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
