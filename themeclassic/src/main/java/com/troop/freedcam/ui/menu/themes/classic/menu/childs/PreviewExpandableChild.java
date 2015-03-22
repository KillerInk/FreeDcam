package com.troop.freedcam.ui.menu.themes.classic.menu.childs;

import android.content.Context;

import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.TextureView.I_PreviewSizeEvent;
import com.troop.freedcam.ui.menu.ExpandableGroup;

/**
 * Created by troop on 06.09.2014.
 */
public class PreviewExpandableChild extends ExpandableChild
{

    private I_PreviewSizeEvent previewSizeEvent;

    public PreviewExpandableChild(Context context, ExpandableGroup group, String name, AppSettingsManager appSettingsManager, String settingsname) {
        super(context, group, name, appSettingsManager, settingsname);
    }

    public PreviewExpandableChild(Context context, I_PreviewSizeEvent previewSizeEvent, ExpandableGroup group, String name, AppSettingsManager appSettingsManager, String settingsname)
    {
        super(context, group, name, appSettingsManager, settingsname);
        this.previewSizeEvent = previewSizeEvent;
    }

    @Override
    public void setValue(String value) {
        super.setValue(value);
        if (previewSizeEvent != null)
        {
            String[] widthHeight = value.split("x");
            int w = Integer.parseInt(widthHeight[0]);
            int h = Integer.parseInt(widthHeight[1]);
            previewSizeEvent.OnPreviewSizeChanged(w, h);
        }
    }
}
