package com.troop.freecam.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.troop.freecam.R;

/**
 * Created by troop on 01.01.14.
 */
public class LayoutActivity extends FragmentActivity
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initMenu();
    }

    private void initUI() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        //setContentView(R.layout.activity_main);
        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        appViewGroup = (ViewGroup) inflater.inflate(R.layout.main, null);
        setContentView(R.layout.main);
    }

    public void initMenu()
    {
        baseMenuLayout = (LinearLayout)findViewById(R.id.baseMenuLayout);
        autoMenuLayout = (LinearLayout)findViewById(R.id.LayoutAuto);
        manualMenuLayout = (LinearLayout)findViewById(R.id.Layout_Manual);
        settingsMenuLayout = (LinearLayout)findViewById(R.id.LayoutSettings);


        manualLayoutButton = (Button)findViewById(R.id.buttonManualMode);
        manualLayoutButton.setOnClickListener(new View.OnClickListener() {
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
        });

        autoLayoutButton = (Button)findViewById(R.id.buttonAutoMode);
        autoLayoutButton.setOnClickListener(new View.OnClickListener() {
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
                    //if (baseMenuLayout.findViewById(R.id.LayoutAuto) == null)
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
        });
        settingLayoutButton = (Button)findViewById(R.id.buttonSettingsMode);
        settingLayoutButton.setOnClickListener(new View.OnClickListener() {
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
                    //if (baseMenuLayout.findViewById(R.id.LayoutSettings) == null)
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
        });

        autoMenuLayout.setVisibility(View.GONE);
        manualMenuLayout.setVisibility(View.GONE);
        settingsMenuLayout.setVisibility(View.GONE);


    }
}
