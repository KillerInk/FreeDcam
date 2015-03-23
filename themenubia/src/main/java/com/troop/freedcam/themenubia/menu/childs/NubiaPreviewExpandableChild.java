package com.troop.freedcam.themenubia.menu.childs;

import android.content.Context;

import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_PreviewSizeEvent;
import com.troop.freedcam.ui.menu.themes.classic.menu.ExpandableGroup;

/**
 * Created by troop on 23.03.2015.
 */
public class NubiaPreviewExpandableChild extends NubiaExpandableChild
{
        private I_PreviewSizeEvent previewSizeEvent;

        public NubiaPreviewExpandableChild(Context context, ExpandableGroup group, String name, AppSettingsManager appSettingsManager, String settingsname) {
            super(context, group, name, appSettingsManager, settingsname);
        }

        public NubiaPreviewExpandableChild(Context context, I_PreviewSizeEvent previewSizeEvent, ExpandableGroup group, String name, AppSettingsManager appSettingsManager, String settingsname)
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
