package com.troop.freecamv2.ui.menu;

import android.content.Context;

import com.troop.freecamv2.camera.CameraUiWrapper;
import com.troop.freecamv2.camera.parameters.modes.I_ModeParameter;
import com.troop.freecamv2.ui.AppSettingsManager;

import java.util.ArrayList;

/**
 * Created by troop on 19.08.2014.
 */
public class MenuCreator
{
    Context context;
    CameraUiWrapper cameraUiWrapper;
    AppSettingsManager appSettingsManager;
    public MenuCreator(Context context, CameraUiWrapper cameraUiWrapper, AppSettingsManager appSettingsManager)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        this.context = context;
        this.appSettingsManager = appSettingsManager;
    }

    public ExpandableGroup CreatePictureSettings()
    {
        ExpandableGroup picGroup = new ExpandableGroup();
        picGroup.setName("Picture Settings");
        createPictureSettingsChilds(picGroup);
        return picGroup;
    }

    private void createPictureSettingsChilds(ExpandableGroup group)
    {
        ArrayList<ExpandableChild> piclist = new ArrayList<ExpandableChild>();
        ExpandableChild picSize = new ExpandableChild(context);
        picSize.setName("Picture Size");
        picSize.setParameterHolder(cameraUiWrapper.camParametersHandler.PictureSize, appSettingsManager, AppSettingsManager.SETTING_PICTURESIZE);
        piclist.add(picSize);
        group.setItems(piclist);
    }

    public ExpandableGroup CreateModeSettings()
    {
        ExpandableGroup modesGroup = new ExpandableGroup();
        modesGroup.setName("Mode Settings");
        createModesSettingsChilds(modesGroup);
        return modesGroup;
    }

    private void createModesSettingsChilds(ExpandableGroup group)
    {
        ArrayList<ExpandableChild> childlist = new ArrayList<ExpandableChild>();

        if (cameraUiWrapper.camParametersHandler.ColorMode.IsSupported()) {
            ExpandableChild color = new ExpandableChild(context);
            color.setName("Color");
            color.setParameterHolder(cameraUiWrapper.camParametersHandler.ColorMode, appSettingsManager, AppSettingsManager.SETTING_COLORMODE);
            childlist.add(color);
        }

        if (cameraUiWrapper.camParametersHandler.IsoMode.IsSupported()) {
            ExpandableChild iso = new ExpandableChild(context);
            iso.setName("Iso");
            iso.setParameterHolder(cameraUiWrapper.camParametersHandler.IsoMode, appSettingsManager, AppSettingsManager.SETTING_ISOMODE);
            childlist.add(iso);
        }

        if (cameraUiWrapper.camParametersHandler.ExposureMode.IsSupported())
        {
            ExpandableChild exposure = new ExpandableChild(context);
            exposure.setName("Exposure");
            exposure.setParameterHolder(cameraUiWrapper.camParametersHandler.ExposureMode, appSettingsManager, AppSettingsManager.SETTING_EXPOSUREMODE);
            childlist.add(exposure);
        }

        group.setItems(childlist);
    }

    public ExpandableGroup CreateQualitySettings()
    {
        ExpandableGroup qualityGroup = new ExpandableGroup();
        qualityGroup.setName("Quality Settings");
        createQualitySettingsChilds(qualityGroup);
        return qualityGroup;
    }

    private void  createQualitySettingsChilds(ExpandableGroup group)
    {
        ArrayList<ExpandableChild> childlist = new ArrayList<ExpandableChild>();

        if (cameraUiWrapper.camParametersHandler.AntiBandingMode.IsSupported())
        {
            ExpandableChild antibanding = new ExpandableChild(context);
            antibanding.setName("Antibanding");
            antibanding.setParameterHolder(cameraUiWrapper.camParametersHandler.AntiBandingMode, appSettingsManager, AppSettingsManager.SETTING_ANTIBANDINGMODE);
            childlist.add(antibanding);
        }
        if (cameraUiWrapper.camParametersHandler.ImagePostProcessing.IsSupported())
        {
            ExpandableChild ipp = new ExpandableChild(context);
            ipp.setName("ImagePostProcessing");
            ipp.setParameterHolder(cameraUiWrapper.camParametersHandler.ImagePostProcessing, appSettingsManager, AppSettingsManager.SETTING_IMAGEPOSTPROCESSINGMODE);
            childlist.add(ipp);
        }

        group.setItems(childlist);
    }

    public ExpandableGroup CreatePreviewSettings()
    {
        ExpandableGroup preview = getNewGroup("Preview Settings");
        createPreviewSettingsChilds(preview);
        return preview;
    }

    private void createPreviewSettingsChilds(ExpandableGroup preview)
    {
        ArrayList<ExpandableChild> childlist = new ArrayList<ExpandableChild>();
        ExpandableChild size = getNewChild(cameraUiWrapper.camParametersHandler.PreviewSize, AppSettingsManager.SETTING_PREVIEWSIZE, "Preview Size");
        childlist.add(size);

        ExpandableChild fps = getNewChild(cameraUiWrapper.camParametersHandler.PreviewFPS, AppSettingsManager.SETTING_PREVIEWFPS, "Preview Fps");
        childlist.add(fps);

        preview.setItems(childlist);
    }

    private ExpandableGroup getNewGroup(String name)
    {
        ExpandableGroup group = new ExpandableGroup();
        group.setName(name);
        return group;
    }

    private ExpandableChild getNewChild(I_ModeParameter mode, String appsettingName, String settingName)
    {
        ExpandableChild child = new ExpandableChild(context);
        child.setName(settingName);
        child.setParameterHolder(mode,appSettingsManager,appsettingName);
        return child;
    }
}
