package com.troop.freecamv2.ui.menu;

import android.content.Context;

import com.troop.freecamv2.camera.CameraUiWrapper;

import java.util.ArrayList;

/**
 * Created by troop on 19.08.2014.
 */
public class MenuCreator
{
    Context context;
    CameraUiWrapper cameraUiWrapper;
    public MenuCreator(Context context, CameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        this.context = context;
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
        picSize.setParameterHolder(cameraUiWrapper.camParametersHandler.PictureSize);
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
            color.setParameterHolder(cameraUiWrapper.camParametersHandler.ColorMode);
            childlist.add(color);
        }

        if (cameraUiWrapper.camParametersHandler.IsoMode.IsSupported()) {
            ExpandableChild iso = new ExpandableChild(context);
            iso.setName("Iso");
            iso.setParameterHolder(cameraUiWrapper.camParametersHandler.IsoMode);
            childlist.add(iso);
        }

        if (cameraUiWrapper.camParametersHandler.ExposureMode.IsSupported())
        {
            ExpandableChild exposure = new ExpandableChild(context);
            exposure.setName("Exposure");
            exposure.setParameterHolder(cameraUiWrapper.camParametersHandler.ExposureMode);
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
        if (cameraUiWrapper.camParametersHandler.AntiBandingMode.IsSupported()) {
            ExpandableChild antibanding = new ExpandableChild(context);
            antibanding.setName("Antibanding");
            antibanding.setParameterHolder(cameraUiWrapper.camParametersHandler.AntiBandingMode);
            childlist.add(antibanding);
        }

        group.setItems(childlist);
    }
}
