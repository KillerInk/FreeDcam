package freed.cam.ui.themenextgen.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.troop.freedcam.R;

import java.util.ArrayList;
import java.util.List;

import freed.cam.apis.CameraApiManager;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.basecamera.parameters.ParameterHandler;
import freed.cam.apis.basecamera.parameters.ParameterInterface;
import freed.cam.apis.basecamera.parameters.modes.ApiParameter;
import freed.cam.apis.basecamera.parameters.modes.SettingModeParamter;
import freed.cam.apis.camera2.Camera2;
import freed.cam.previewpostprocessing.PreviewPostProcessingModes;
import freed.cam.ui.themenextgen.adapter.customclicks.AspectRatioCheckedChanged;
import freed.cam.ui.themenextgen.adapter.customclicks.Camera2VendorButtonClick;
import freed.cam.ui.themenextgen.adapter.customclicks.RunFeatureDetectorClick;
import freed.cam.ui.themenextgen.adapter.customclicks.SaveCameraParametersClick;
import freed.cam.ui.themenextgen.adapter.customclicks.VideoEditorClick;
import freed.cam.ui.themenextgen.layoutconfig.SettingItemConfig;
import freed.cam.ui.themenextgen.view.button.NextGenSettingBoolItem;
import freed.cam.ui.themenextgen.view.button.NextGenSettingButton;
import freed.cam.ui.themenextgen.view.button.NextGenSettingItem;
import freed.cam.ui.themesample.settings.childs.SettingsChildMenuSDSave;
import freed.cam.ui.themesample.settings.childs.SettingsChildMenuTimeLapseFrames;
import freed.cam.ui.themesample.settings.childs.SettingsChild_FreedAe;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.settings.mode.AbstractSettingMode;
import freed.settings.mode.BooleanSettingModeInterface;
import freed.utils.Log;

public class NextGenSettingItemAdapterTools
{

    private static final String TAG = NextGenSettingItemAdapterTools.class.getSimpleName();

    public List<SettingItemConfig> getValidSettingItemConfigList(List<SettingItemConfig> keyList, CameraApiManager cameraApiManager, SettingsManager settingsManager)
    {
        List<SettingItemConfig> validList = new ArrayList<>();
        for (SettingItemConfig conf : keyList)
        {
            ParameterInterface p = null;
            AbstractSettingMode s = null;
            if (conf.getHeader() == R.string.setting_dump_vendor_keys_header) {
                if (cameraApiManager.getCamera() instanceof Camera2)
                    validList.add(conf);
            }
            else if (conf.getHeader() == R.string.setting_api_header)
                validList.add(conf);
            else if (conf.getHeader() == R.string.setting_savecamparams_header && settingsManager.getCamApi().equals(SettingsManager.API_1))
                validList.add(conf);
            else if (conf.getKey() == SettingKeys.USE_FREEDCAM_AE || conf.getKey() == SettingKeys.MAX_ISO || conf.getKey() == SettingKeys.MIN_ISO || conf.getKey() == SettingKeys.MAX_EXPOSURE || conf.getKey() == SettingKeys.MIN_EXPOSURE)
            {
                if(settingsManager.getGlobal(SettingKeys.PREVIEW_POST_PROCESSING_MODE) != null)
                    if (settingsManager.getGlobal(SettingKeys.PREVIEW_POST_PROCESSING_MODE).get() != null &&
                            settingsManager.getGlobal(SettingKeys.PREVIEW_POST_PROCESSING_MODE).get().equals(PreviewPostProcessingModes.OpenGL.name()))
                        validList.add(conf);
            }
            else if (conf.getKey() == null) {
                if (conf.getViewType() != SettingItemConfig.ViewType.Custom)
                    validList.add(conf);
            }
            else {
                if (!conf.getFromSettingManager()) {
                    p = cameraApiManager.getCamera().getParameterHandler().get(conf.getKey());
                }
                else
                    s = (AbstractSettingMode) settingsManager.get(conf.getKey());
                if (p != null || s != null)
                    validList.add(conf);
            }
        }
        return validList;
    }

    public void fillConfigsWithViews(SettingItemConfig[] configs, CameraApiManager cameraApiManager, SettingsManager settingsManager, Context context)
    {
        for (SettingItemConfig config : configs)
        {
            View v = getViewFromKey(config, cameraApiManager,settingsManager,context);

            if (v == null) {
                if (config.getKey() != null)
                    Log.e(TAG, "Failed to get view for:" + config.getKey().toString());
                else
                    Log.e(TAG, "Failed to get view for:" + context.getResources().getText(config.getHeader()));
            }
            else
            {
                config.setView(v);
            }
        }
    }

    private View getViewFromKey(SettingItemConfig key, CameraApiManager cameraApiManager, SettingsManager settingsManager, Context context)
    {
        if (key.getHeader() == R.string.setting_videoprofileeditor_header)
            return NextGenSettingButton.getInstance(context, key.getHeader(),key.getDescription(), new VideoEditorClick(context));
        if (key.getHeader() == R.string.setting_sdcard_header)
            return new SettingsChildMenuSDSave(context, key.getHeader(), key.getDescription());
        if (key.getHeader() == R.string.setting_savecamparams_header)
            return NextGenSettingButton.getInstance(context,key.getHeader(),key.getDescription(), new SaveCameraParametersClick(cameraApiManager,settingsManager,context));
        if (key.getHeader() == R.string.setting_featuredetector_header)
            return NextGenSettingButton.getInstance(context,R.string.setting_featuredetector_header,R.string.setting_featuredetector_description, new RunFeatureDetectorClick());
        if (key.getHeader() == R.string.setting_dump_vendor_keys_header && cameraApiManager.getCamera() instanceof Camera2)
            return NextGenSettingButton.getInstance(context,R.string.setting_dump_vendor_keys_header,R.string.setting_dump_vendor_keys_description, new Camera2VendorButtonClick((Camera2) cameraApiManager.getCamera()));
        if (key.getHeader() == R.string.setting_api_header)
            return NextGenSettingItem.getInstance(context,R.string.setting_api_header,R.string.setting_api_description,new ApiParameter(settingsManager,cameraApiManager));

        if (key.getKey() != null) {
            if (key.getViewType() == SettingItemConfig.ViewType.Custom) {
                if (key.getKey() == SettingKeys.TIMELAPSE_FRAMES)
                    return new SettingsChildMenuTimeLapseFrames(context);
                if ((key.getKey() == SettingKeys.USE_FREEDCAM_AE))
                    return new SettingsChild_FreedAe(context, settingsManager.getGlobal(SettingKeys.USE_FREEDCAM_AE), key.getHeader(), key.getDescription());
                if (key.getKey() == SettingKeys.SWITCH_ASPECT_RATIO)
                    return NextGenSettingBoolItem.getInstance(context, R.string.setting_switch_aspect_header, R.string.setting_switch_aspect_text, settingsManager.get(SettingKeys.SWITCH_ASPECT_RATIO), new AspectRatioCheckedChanged());
            }
            return getView(key,cameraApiManager.getCamera().getParameterHandler(),settingsManager,context);
        }
        return null;
    }

    private View getView(SettingItemConfig key, ParameterHandler parameterHandler, SettingsManager settingsManager,Context c)
    {
        if (key.getFromSettingManager()) {
            return getViewFromSetting(key,settingsManager,c);
        } else {
            return getViewFromParameterHandler(key, parameterHandler,c);
        }
    }

    @Nullable
    private View getViewFromParameterHandler(SettingItemConfig key, ParameterHandler parameterHandler,Context context) {
        ParameterInterface parameterInterface = parameterHandler.get(key.getKey());
        if (parameterInterface != null) {
            if (parameterInterface instanceof BooleanSettingModeInterface)
                return NextGenSettingBoolItem.getInstance(context, key.getHeader(), key.getDescription(), (BooleanSettingModeInterface) parameterInterface);
            return NextGenSettingItem.getInstance(context, key.getHeader(), key.getDescription(), (AbstractParameter) parameterInterface);
        }
        return null;
    }

    @NonNull
    private ViewGroup getViewFromSetting(SettingItemConfig key, SettingsManager settingsManager, Context context) {
        AbstractSettingMode parameterInterface = (AbstractSettingMode) settingsManager.get(key.getKey());
        if (parameterInterface instanceof BooleanSettingModeInterface)
            return NextGenSettingBoolItem.getInstance(context, key.getHeader(), key.getDescription(), (BooleanSettingModeInterface) parameterInterface);
        return NextGenSettingItem.getInstance(context, key.getHeader(), key.getDescription(), new SettingModeParamter(key.getKey()));
    }

}
