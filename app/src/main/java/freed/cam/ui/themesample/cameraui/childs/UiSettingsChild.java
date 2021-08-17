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
import androidx.lifecycle.LifecycleOwner;

import com.troop.freedcam.R.layout;
import com.troop.freedcam.R.styleable;
import com.troop.freedcam.databinding.CamerauiUisettingschildBinding;

import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.basecamera.parameters.ParameterInterface;
import freed.cam.ui.themesample.SettingsChildAbstract;
import freed.utils.Log;

/**
 * Created by troop on 11.06.2015.
 */
public class UiSettingsChild extends SettingsChildAbstract
{
    private final String TAG = UiSettingsChild.class.getSimpleName();

    CamerauiUisettingschildBinding binding;


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
        bind(context);
    }

    private void bind(Context context)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layout.cameraui_uisettingschild, null);
        binding = CamerauiUisettingschildBinding.bind(view);
        binding.textView2.setSelected(true);
        binding.getRoot().setOnClickListener(this);
        this.addView(binding.getRoot());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

    }

    public void setLifeCycleOwner(LifecycleOwner owner)
    {
        binding.setLifecycleOwner(owner);
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
    public void SetParameter(ParameterInterface parameter)
    {
        super.SetParameter(parameter);
        binding.setParameter((AbstractParameter) parameter);
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
