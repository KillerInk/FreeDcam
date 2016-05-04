package com.freedcam.ui.themesample.views.menu;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;

import com.freedcam.ui.themesample.subfragments.VideoProfileEditorActivity;

/**
 * Created by troop on 17.02.2016.
 */
public class MenuItem_VideoProfEditor extends MenuItem {
    public MenuItem_VideoProfEditor(Context context) {
        super(context);
    }

    public MenuItem_VideoProfEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onClick(int x, int y) {
        final Intent i = new Intent(context, VideoProfileEditorActivity.class);
        context.startActivity(i);
    }
}
