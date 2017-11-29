/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package freed.cam.ui.themesample.cameraui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import freed.cam.ui.themesample.cameraui.CameraUiFragment.i_HelpFragment;
import freed.settings.AppSettingsManager;

/**
 * Created by troop on 29.02.2016.
 */
public class HelpFragment extends Fragment
{
    private ImageView finger;
    private TextView description;
    private Button nextButton;
    private int helpState;
    private i_HelpFragment closer;
    private CheckBox dontshowagain;

    public static HelpFragment getFragment(i_HelpFragment closer)
    {
        HelpFragment h = new HelpFragment();
        h.closer = closer;
        return h;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(layout.help_fragment,container,false);
        finger = (ImageView)view.findViewById(id.imageView_finger);
        description = (TextView)view.findViewById(id.textView_description);
        dontshowagain =(CheckBox)view.findViewById(id.checkBox_dontShowAgain);
        dontshowagain.setVisibility(View.GONE);
        nextButton =(Button)view.findViewById(id.button_nextHelp);
        nextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                helpState++;
                switch (helpState)
                {
                    case 1: //close settings
                        showCloseSettingsMenu();
                        break;
                    case 2: //open manual
                        showOpenManualMenu();
                        break;
                    case 3: //close manual
                        showCloseManualMenu();
                        break;
                    case 4:
                        if (dontshowagain.isChecked())
                        {
                            AppSettingsManager.getInstance().setshowHelpOverlay(false);
                        }
                        else
                        {
                            AppSettingsManager.getInstance().setshowHelpOverlay(true);
                        }
                        closer.Close(HelpFragment.this);
                }
            }
        });
        showOpenSettingsMenu();
        return view;
    }


    private void showOpenSettingsMenu()
    {
        TranslateAnimation animation = new TranslateAnimation(0.0f, 400.0f,
                0.0f, 0.0f);
        animation.setDuration(1000);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.INFINITE);
        animation.setFillAfter(true);
        finger.startAnimation(animation);
        description.setText("Swipe from left to right to open Settings");
        description.setTextColor(Color.WHITE);
    }

    private void showCloseSettingsMenu()
    {
        TranslateAnimation animation = new TranslateAnimation(400.0f, 0.0f,
                0.0f, 0.0f);
        animation.setDuration(1000);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.INFINITE);
        animation.setFillAfter(true);
        finger.startAnimation(animation);
        description.setText("Swipe from right to left to close Settings");
        description.setTextColor(Color.WHITE);
    }

    private void showOpenManualMenu()
    {
        TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f,
                400.0f, 0.0f);
        animation.setDuration(1000);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.INFINITE);
        animation.setFillAfter(true);
        finger.startAnimation(animation);
        description.setText("Swipe from bottom to top to open Manuals");
        description.setTextColor(Color.WHITE);
    }

    private void showCloseManualMenu()
    {
        TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f,
                0.0f, 400.0f);
        animation.setDuration(1000);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.INFINITE);
        animation.setFillAfter(true);
        finger.startAnimation(animation);
        description.setText("Swipe from top to bottom to close Manual)");
        description.setTextColor(Color.WHITE);
        nextButton.setText("Close");
        dontshowagain.setVisibility(View.VISIBLE);
        dontshowagain.setTextColor(Color.WHITE);
    }
}
