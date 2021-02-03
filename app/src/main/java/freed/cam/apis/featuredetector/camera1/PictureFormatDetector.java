package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;

import com.troop.freedcam.R;

import java.util.ArrayList;

import freed.FreedApplication;
import freed.settings.Frameworks;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;

public class PictureFormatDetector extends BaseParameter1Detector {

    private final String TAG = PictureFormatDetector.class.getSimpleName();

    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        detectedPictureFormats(cameraCharacteristics);
    }

    private void detectedPictureFormats(Camera.Parameters parameters)
    {
        //drop raw for front camera
        if (false)
        {
            SettingsManager.get(SettingKeys.PictureFormat).setIsSupported(false);
            SettingsManager.get(SettingKeys.RAW_PICTURE_FORMAT_SETTING).setIsSupported(false);
        }
        else {
            if (SettingsManager.getInstance().getFrameWork() == Frameworks.MTK) {
                SettingsManager.get(SettingKeys.PictureFormat).setIsSupported(true);
                SettingsManager.get(SettingKeys.RAW_PICTURE_FORMAT_SETTING).setIsSupported(true);
            } else {

                String formats = parameters.get(camstring(R.string.picture_format_values));

                if (!SettingsManager.get(SettingKeys.RAW_PICTURE_FORMAT_SETTING).isPresetted()) {
                    Log.d(TAG, "rawpictureformat is not preseted try to find it");
                    if (formats.contains("bayer-mipi") || formats.contains("raw")) {
                        SettingsManager.get(SettingKeys.RAW_PICTURE_FORMAT_SETTING).setIsSupported(true);
                        String[] forms = formats.split(",");
                        for (String s : forms) {
                            if (s.contains("bayer-mipi") || s.contains("raw")) {
                                Log.d(TAG, "rawpictureformat set to:" +s);
                                SettingsManager.get(SettingKeys.RAW_PICTURE_FORMAT_SETTING).set(s);
                                SettingsManager.get(SettingKeys.RAW_PICTURE_FORMAT_SETTING).setIsSupported(true);
                                break;
                            }
                        }
                    }
                }
                else
                {
                    if (!formats.contains(SettingsManager.get(SettingKeys.RAW_PICTURE_FORMAT_SETTING).get()))
                    {
                        SettingsManager.get(SettingKeys.RAW_PICTURE_FORMAT_SETTING).set(SettingsManager.get(SettingKeys.RAW_PICTURE_FORMAT_SETTING).get());
                        SettingsManager.get(SettingKeys.RAW_PICTURE_FORMAT_SETTING).setIsSupported(true);
                    }


                }
                if (formats.contains(FreedApplication.getStringFromRessources(R.string.bayer_)))
                {
                    Log.d(TAG, "create rawformats");
                    ArrayList<String> tmp = new ArrayList<>();
                    String[] forms = formats.split(",");
                    for (String s : forms) {
                        if (s.contains(FreedApplication.getStringFromRessources(R.string.bayer_)))
                        {
                            tmp.add(s);
                        }
                    }
                    String[] rawFormats = new String[tmp.size()];
                    tmp.toArray(rawFormats);
                    SettingsManager.get(SettingKeys.RAW_PICTURE_FORMAT_SETTING).setValues(rawFormats);
                    if (rawFormats.length == 0)
                        SettingsManager.get(SettingKeys.RAW_PICTURE_FORMAT_SETTING).setIsSupported(false);
                    else
                        SettingsManager.get(SettingKeys.RAW_PICTURE_FORMAT_SETTING).setIsSupported(true);
                }
            }
            SettingsManager.get(SettingKeys.PictureFormat).setIsSupported(true);

            if (SettingsManager.getInstance().getDngProfilesMap() != null && SettingsManager.getInstance().getDngProfilesMap().size() > 0)
            {
                Log.d(TAG, "Dng, bayer, jpeg supported");
                SettingsManager.get(SettingKeys.PictureFormat).setValues(new String[]
                        {
                                FreedApplication.getStringFromRessources(R.string.jpeg_),
                                FreedApplication.getStringFromRessources(R.string.dng_),
                                FreedApplication.getStringFromRessources(R.string.bayer_)
                        });
            }
            else if (SettingsManager.get(SettingKeys.RAW_PICTURE_FORMAT_SETTING).isSupported()) {
                Log.d(TAG, "bayer, jpeg supported");
                SettingsManager.get(SettingKeys.PictureFormat).setValues(new String[]{
                        FreedApplication.getStringFromRessources(R.string.jpeg_),
                        FreedApplication.getStringFromRessources(R.string.bayer_)
                });
            }
            else
            {
                Log.d(TAG, "jpeg supported");
                SettingsManager.get(SettingKeys.PictureFormat).setValues(new String[]{
                        FreedApplication.getStringFromRessources(R.string.jpeg_)
                });
            }

        }
    }
}
