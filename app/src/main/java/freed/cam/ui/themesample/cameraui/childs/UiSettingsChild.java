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

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.troop.freedcam.R.layout;
import com.troop.freedcam.R.styleable;
import com.troop.freedcam.databinding.CamerauiUisettingschildBinding;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.events.EventBusHelper;
import freed.cam.events.ModuleHasChangedEvent;
import freed.cam.ui.themesample.SettingsChildAbstract;
import freed.utils.Log;

/**
 * Created by troop on 11.06.2015.
 */
public class UiSettingsChild extends SettingsChildAbstract
{
    private final String TAG = UiSettingsChild.class.getSimpleName();

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onModuleHasChangedEvent(ModuleHasChangedEvent event)
    {
        onModuleChanged(event.NewModuleName);
    }

    CamerauiUisettingschildBinding binding;

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBusHelper.register(this);
        /*if (parameter != null)
            parameter.fireStringValueChanged(parameter.getStringValue());*/
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBusHelper.unregister(this);
    }

    public UiSettingsChild(Context context) {
        super(context);
        init(context);
    }

    public UiSettingsChild(Context context, AbstractParameter parameter) {
        super(context,parameter);
        init(context);
        SetParameter(parameter);
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
            binding.textView2.setText(a.getText(styleable.UiSettingsChild_ValueText));
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
        binding.textView2.setSelected(true);
        setOnClickListener(this);
    }

    @Override
    public void setBackgroundResource(int resid) {
        binding.LAYbg.setBackgroundResource(resid);
    }

    @Override
    protected void inflateTheme(LayoutInflater inflater)
    {
        binding = DataBindingUtil.inflate(inflater,layout.cameraui_uisettingschild,this,true);
    }

    @Override
    public void SetParameter(AbstractParameter parameter)
    {
        super.SetParameter(parameter);
        binding.setParameter(parameter);
        binding.executePendingBindings();
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
