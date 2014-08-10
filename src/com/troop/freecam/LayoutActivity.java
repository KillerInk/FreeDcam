package com.troop.freecam;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.troop.freecam.manager.AppSettingsManager;

/**
 * Created by troop on 01.01.14.
 * Handels how the popupmenu is Shown and wich items are shown
 */
public class LayoutActivity extends Activity
{
    LinearLayout baseMenuLayout;
    LinearLayout manualMenuLayout;
    LinearLayout autoMenuLayout;
    LinearLayout settingsMenuLayout;
    Button manualLayoutButton;
    Button autoLayoutButton;
    Button settingLayoutButton;
    public boolean hideManualMenu = true;
    public boolean hideSettingsMenu = true;
    public boolean hideAutoMenu = true;
    public  ViewGroup appViewGroup;
    protected AppSettingsManager appSettingsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initMenu();
    }

    /**
     * inflates the main.xml and inits the settingsmanager
     */
    private void initUI() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        //setContentView(R.layout.activity_main);
        appSettingsManager = new AppSettingsManager(PreferenceManager.getDefaultSharedPreferences(this));
        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        appViewGroup = (ViewGroup) inflater.inflate(R.layout.main, null);
        setContentView(R.layout.main);
    }

    /**
     * inits the popupmenu and handel the click events
     */
    public void initMenu()
    {
        baseMenuLayout = (LinearLayout)findViewById(R.id.baseMenuLayout);
        autoMenuLayout = (LinearLayout)findViewById(R.id.LayoutAuto);
        manualMenuLayout = (LinearLayout)findViewById(R.id.Layout_Manual);
        settingsMenuLayout = (LinearLayout)findViewById(R.id.LayoutSettings);


        manualLayoutButton = (Button)findViewById(R.id.buttonManualMode);
        manualLayoutButton.setOnClickListener(onManualLayoutButtonClick);

        autoLayoutButton = (Button)findViewById(R.id.buttonAutoMode);
        autoLayoutButton.setOnClickListener(onAutoLayoutButtonClick);

        settingLayoutButton = (Button)findViewById(R.id.buttonSettingsMode);
        settingLayoutButton.setOnClickListener(onSettingLayoutButtonClick);

        autoMenuLayout.setVisibility(View.GONE);
        manualMenuLayout.setVisibility(View.GONE);
        settingsMenuLayout.setVisibility(View.GONE);


    }

    private View.OnClickListener onManualLayoutButtonClick = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (hideManualMenu == false)
            {
                hideManualMenu = true;
                manualMenuLayout.setVisibility(View.GONE);
            }
            else
            {
                hideManualMenu = false;
                //if (baseMenuLayout.findViewById(R.id.Layout_Manual) == null)
                manualMenuLayout.setVisibility(View.VISIBLE);
                if (hideAutoMenu == false)
                {
                    hideAutoMenu = true;
                    autoMenuLayout.setVisibility(View.GONE);
                }
                if (hideSettingsMenu == false)
                {
                    hideSettingsMenu = true;
                    settingsMenuLayout.setVisibility(View.GONE);
                }
            }
        }
    };

    private View.OnClickListener onAutoLayoutButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            if (hideAutoMenu == false)
            {
                hideAutoMenu = true;
                autoMenuLayout.setVisibility(View.GONE);
            }
            else
            {
                hideAutoMenu = false;
                autoMenuLayout.setVisibility(View.VISIBLE);
                if (hideSettingsMenu == false)
                {
                    hideSettingsMenu = true;
                    settingsMenuLayout.setVisibility(View.GONE);
                }
                if (hideManualMenu == false)
                {
                    hideManualMenu = true;
                    manualMenuLayout.setVisibility(View.GONE);
                }
            }
        }
    };

    private View.OnClickListener onSettingLayoutButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            if (hideSettingsMenu == false)
            {
                hideSettingsMenu = true;
                settingsMenuLayout.setVisibility(View.GONE);
            }
            else
            {
                hideSettingsMenu = false;
                settingsMenuLayout.setVisibility(View.VISIBLE);
                if (hideAutoMenu == false)
                {
                    hideAutoMenu = true;
                    autoMenuLayout.setVisibility(View.GONE);
                }
                if (hideManualMenu == false)
                {
                    hideManualMenu = true;
                    manualMenuLayout.setVisibility(View.GONE);
                }
            }
        }
    };

}
