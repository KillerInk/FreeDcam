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

package freed.cam.ui.themesample.cameraui.childs;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.troop.freedcam.R.drawable;
import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;
import com.troop.freedcam.R.string;
import com.troop.freedcam.R.styleable;

import freed.cam.apis.basecamera.parameters.modes.ModeParameterInterface;
import freed.cam.ui.themesample.SettingsChildAbstract;
import freed.utils.AppSettingsManager;
import freed.utils.Log;

/**
 * Created by troop on 11.06.2015.
 */
public class UiSettingsChild extends SettingsChildAbstract
{
    private String headerText;
    private LinearLayout laybg;



    private String TAG;


    public UiSettingsChild(Context context) {
        super(context);
        init(context);
    }

    public UiSettingsChild(Context context, AppSettingsManager.SettingMode settingsMode, ModeParameterInterface parameter) {
        super(context, settingsMode,parameter);
        init(context);
    }

    public UiSettingsChild(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
        //get custom attributs
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                styleable.UiSettingsChild,
                0, 0
        );
        //try to set the attributs
        try
        {
            TAG = (String)a.getText(styleable.UiSettingsChild_HeaderText);

            headerText = String.valueOf(a.getText(styleable.UiSettingsChild_HeaderText));

            valueText.setText(a.getText(styleable.UiSettingsChild_ValueText));
        }
        finally {
            a.recycle();
        }
        sendLog("Ctor done");
    }

    @Override
    protected void sendLog(String log)
    {
        boolean logging = false;
        if (logging)
            Log.d(TAG, log);
    }


    @Override
    protected void init(Context context)
    {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflateTheme(inflater);
        laybg = (LinearLayout) findViewById(id.LAYbg);
        if(context.getResources().getString(string.uisetting_wb_header) == headerText)
        {
            laybg.setBackgroundDrawable(context.getResources().getDrawable(drawable.quck_set_wb));
        }

        valueText = (TextView) findViewById(id.textView2);
        valueText.setSelected(true);
        setOnClickListener(this);

    }

    @Override
    public void setBackgroundResource(int resid) {
        laybg.setBackgroundResource(resid);
    }

    @Override
    protected void inflateTheme(LayoutInflater inflater)
    {
        inflater.inflate(layout.cameraui_uisettingschild, this);
    }

    @Override
    public void SetParameter(ModeParameterInterface parameter)
    {
        super.SetParameter(parameter);
        setTextToTextBox(parameter);
    }

    protected void setTextToTextBox(ModeParameterInterface parameter)
    {
        if (parameter != null && parameter.IsSupported())
        {
            onParameterIsSupportedChanged(true);
            String campara = parameter.GetValue();
            if (campara != null && !campara.equals(""))
                onParameterValueChanged(campara);
        }
        else
            onParameterIsSupportedChanged(false);
    }



    //AbstractModeParameter.I_ModeParameterEvent implementation
    @Override
    public void onParameterValueChanged(String val)
    {
        sendLog("Set Value to:" + val);
        valueText.setText(val);
    }

    @Override
    public void onParameterIsSupportedChanged(boolean isSupported)
    {
        sendLog("isSupported:" + isSupported);
        if (isSupported) {
            setVisibility(View.VISIBLE);
            animate().setListener(null).scaleY(1f).setDuration(300);
        }
        else
            animate().setListener(hideListner).scaleY(0f).setDuration(300);
    }

    private final AnimatorListener hideListner = new AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            setVisibility(View.GONE);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    @Override
    public void onParameterIsSetSupportedChanged(boolean isSupported)
    {
        sendLog("isSetSupported:" + isSupported);
        if (isSupported) {
            setEnabled(true);
            this.getBackground().setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC_ATOP);
        } else {
            setEnabled(false);
            this.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
        }
        setEnabled(isSupported);
    }

    @Override
    public void onParameterValuesChanged(String[] values) {

    }

    @Override
    public void onModuleChanged(String module) {
    }

    @Override
    public void onClick(View v) {
        if (onItemClick != null)
            onItemClick.onSettingsChildClick(this, fromleft);
    }
}
