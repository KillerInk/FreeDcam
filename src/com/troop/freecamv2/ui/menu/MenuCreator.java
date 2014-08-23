package com.troop.freecamv2.ui.menu;

import android.content.Context;

import com.troop.freecamv2.camera.CameraUiWrapper;
import com.troop.freecamv2.camera.modules.ModuleHandler;
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
        cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(picSize);
        picSize.setParameterHolder(cameraUiWrapper.camParametersHandler.PictureSize, appSettingsManager, AppSettingsManager.SETTING_PICTURESIZE, cameraUiWrapper.moduleHandler.PictureModules);
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
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(color);
            color.setParameterHolder(cameraUiWrapper.camParametersHandler.ColorMode, appSettingsManager, AppSettingsManager.SETTING_COLORMODE, cameraUiWrapper.moduleHandler.AllModules);
            childlist.add(color);
        }

        if (cameraUiWrapper.camParametersHandler.IsoMode.IsSupported()) {
            ExpandableChild iso = new ExpandableChild(context);
            iso.setName("Iso");
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(iso);
            iso.setParameterHolder(cameraUiWrapper.camParametersHandler.IsoMode, appSettingsManager, AppSettingsManager.SETTING_ISOMODE, cameraUiWrapper.moduleHandler.AllModules);
            childlist.add(iso);
        }

        if (cameraUiWrapper.camParametersHandler.ExposureMode.IsSupported())
        {
            ExpandableChild exposure = new ExpandableChild(context);
            exposure.setName("Exposure");
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(exposure);
            exposure.setParameterHolder(cameraUiWrapper.camParametersHandler.ExposureMode,
                    appSettingsManager,
                    AppSettingsManager.SETTING_EXPOSUREMODE,
                    cameraUiWrapper.moduleHandler.AllModules);
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
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(antibanding);
            antibanding.setParameterHolder(cameraUiWrapper.camParametersHandler.AntiBandingMode, appSettingsManager, AppSettingsManager.SETTING_ANTIBANDINGMODE, cameraUiWrapper.moduleHandler.AllModules);
            childlist.add(antibanding);
        }
        if (cameraUiWrapper.camParametersHandler.ImagePostProcessing.IsSupported())
        {
            ExpandableChild ipp = new ExpandableChild(context);
            ipp.setName("ImagePostProcessing");
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(ipp);
            ipp.setParameterHolder(cameraUiWrapper.camParametersHandler.ImagePostProcessing, appSettingsManager, AppSettingsManager.SETTING_IMAGEPOSTPROCESSINGMODE, cameraUiWrapper.moduleHandler.AllModules);
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
        ExpandableChild size = getNewChild(cameraUiWrapper.camParametersHandler.PreviewSize, AppSettingsManager.SETTING_PREVIEWSIZE, "Preview Size", cameraUiWrapper.moduleHandler.AllModules);
        childlist.add(size);

        ExpandableChild fps = getNewChild(cameraUiWrapper.camParametersHandler.PreviewFPS, AppSettingsManager.SETTING_PREVIEWFPS, "Preview Fps", cameraUiWrapper.moduleHandler.AllModules);
        childlist.add(fps);

        preview.setItems(childlist);
    }

    private ExpandableGroup getNewGroup(String name)
    {
        ExpandableGroup group = new ExpandableGroup();
        group.setName(name);
        return group;
    }

    private ExpandableChild getNewChild(I_ModeParameter mode, String appsettingName, String settingName, ArrayList<String> modules)
    {
        ExpandableChild child = new ExpandableChild(context);
        child.setName(settingName);
        cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(child);
        child.setParameterHolder(mode,appSettingsManager,appsettingName, modules);
        return child;
    }
}
