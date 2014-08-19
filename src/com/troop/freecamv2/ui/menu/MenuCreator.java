package com.troop.freecamv2.ui.menu;

import android.content.Context;

import com.troop.freecamv2.camera.CameraUiWrapper;
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
}
