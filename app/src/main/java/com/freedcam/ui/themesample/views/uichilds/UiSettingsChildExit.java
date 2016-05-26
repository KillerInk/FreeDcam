package com.freedcam.ui.themesample.views.uichilds;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by troop on 13.06.2015.
 */
public class UiSettingsChildExit extends UiSettingsChild
{

    public UiSettingsChildExit(Context context) {
        super(context);
    }

    public UiSettingsChildExit(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (i_activity != null)
                    i_activity.closeActivity();
            }
        });
    }

    @Override
    public void onValueChanged(String val) {

    }

    @Override
    public void onIsSupportedChanged(boolean isSupported) {

    }

    @Override
    public void onIsSetSupportedChanged(boolean isSupported) {

    }

    @Override
    public void onValuesChanged(String[] values) {

    }

    @Override
    public void ModuleChanged(String module) {
    }
}
