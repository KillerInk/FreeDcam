package com.freedcam.ui.themesample.subfragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.StringUtils;
import com.troop.freedcam.R;

/**
 * Created by troop on 29.02.2016.
 */
public class HelpFragment extends Fragment
{
    private ImageView finger;
    private TextView description;
    private Button nextButton;
    private int helpState;
    private CameraUiFragment.i_HelpFragment closer;
    private CheckBox dontshowagain;
    private AppSettingsManager appSettingsManager;

    public static HelpFragment getFragment(CameraUiFragment.i_HelpFragment closer, AppSettingsManager appSettingsManager)
    {
        HelpFragment h = new HelpFragment();
        h.closer = closer;
        h.appSettingsManager = appSettingsManager;
        return h;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(R.layout.help_fragment,container,false);
        this.finger = (ImageView)view.findViewById(R.id.imageView_finger);
        this.description = (TextView)view.findViewById(R.id.textView_description);
        this.dontshowagain =(CheckBox)view.findViewById(R.id.checkBox_dontShowAgain);
        dontshowagain.setVisibility(View.GONE);
        this.nextButton =(Button)view.findViewById(R.id.button_nextHelp);
        nextButton.setOnClickListener(new View.OnClickListener() {
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
                            appSettingsManager.setshowHelpOverlay(false);
                        }
                        else
                        {
                            appSettingsManager.setshowHelpOverlay(true);
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
        description.setText("Swipe from top to bottom to close Manuals\r\n\r\nif you can't the heat use Google camera :)");
        nextButton.setText("Close");
        dontshowagain.setVisibility(View.VISIBLE);
    }
}
