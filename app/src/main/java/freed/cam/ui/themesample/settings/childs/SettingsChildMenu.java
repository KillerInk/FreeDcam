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

package freed.cam.ui.themesample.settings.childs;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.troop.freedcam.R.layout;
import com.troop.freedcam.R.styleable;
import com.troop.freedcam.databinding.CamerauiUisettingschildBinding;
import com.troop.freedcam.databinding.SettingsMenuItemBinding;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.basecamera.parameters.ParameterInterface;
import freed.cam.events.ValueChangedEvent;
import freed.cam.ui.themesample.SettingsChildAbstract;

/**
 * Created by troop on 14.06.2015.
 */
public class SettingsChildMenu extends SettingsChildAbstract
{

    protected SettingsMenuItemBinding binding;

    public SettingsChildMenu(Context context) {
        super(context);
        init(context);
    }

    public SettingsChildMenu(Context context,int headerid, int descriptionid)
    {
        super(context);
        init(context);
        binding.textviewMenuitemHeader.setText(getResources().getText(headerid));
        binding.textviewMenuitemDescription.setText(getResources().getText(descriptionid));
    }

    public SettingsChildMenu(Context context, ParameterInterface parameter) {
        super(context, parameter);
        init(context);
        SetParameter(parameter);
    }

    public SettingsChildMenu(Context context, ParameterInterface parameter, int headerid, int descriptionid)
    {
        super(context,parameter);
        init(context);
        binding.textviewMenuitemHeader.setText(getResources().getText(headerid));
        binding.textviewMenuitemDescription.setText(getResources().getText(descriptionid));
        SetParameter(parameter);
    }

    public SettingsChildMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        //get custom attributs
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                styleable.SettingsChildMenu,
                0, 0
        );
        TypedArray b = context.getTheme().obtainStyledAttributes(
                attrs,
                styleable.UiSettingsChild,
                0, 0
        );
        //try to set the attributs
        try
        {

            binding.textviewMenuitemHeader.setText(b.getText(styleable.UiSettingsChild_HeaderText));

            binding.textviewMenuitemDescription.setText(a.getText(styleable.SettingsChildMenu_Description));
            binding.textviewMenuitemHeaderValue.setText("binding....");
        }
        finally {
            a.recycle();
        }
        sendLog("Ctor done");
    }

    @Override
    protected void sendLog(String log) {

    }

    @Override
    protected void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = SettingsMenuItemBinding.inflate(inflater,this,true);
        binding.getRoot().setOnClickListener(this);
        binding.menuItemToplayout.getLayoutParams().width= ViewGroup.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected void inflateTheme(LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater,layout.settings_menu_item,this,true);
    }

    @Override
    public void onClick(View v) {
        if (onItemClick != null)
            onItemClick.onSettingsChildClick(this, false);
    }

    @Override
    public void SetParameter(ParameterInterface parameter) {
        super.SetParameter(parameter);
        binding.setParameter((AbstractParameter) parameter);
        binding.notifyChange();
    }

    @Override
    public void onModuleChanged(String module) {

    }
}
