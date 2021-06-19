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

package freed.cam.ui.themesample.settings;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.databinding.DataBindingUtil;

import com.troop.freedcam.R.layout;
import com.troop.freedcam.databinding.SettingsRightmenufragmentBinding;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import freed.cam.apis.CameraApiManager;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.Size;
import freed.cam.event.camera.CameraHolderEvent;
import freed.cam.ui.themesample.AbstractFragment;
import freed.cam.ui.themesample.SettingsChildAbstract;
import freed.cam.ui.themesample.SettingsChildAbstract.SettingsChildClick;
import freed.cam.ui.themesample.settings.childs.GroupChild;

/**
 * Created by troop on 15.06.2015.
 */
@AndroidEntryPoint
public class RightMenuFragment extends AbstractFragment implements SettingsChildClick, CameraHolderEvent
{
    private static final String TAG = RightMenuFragment.class.getSimpleName();
    private SettingsChildClick onMenuItemClick;

    private LinearLayout settingchildholder;
    @Inject
    public SettingsMenuItemFactory settingsMenuItemFactory;

    private SettingsRightmenufragmentBinding binding;

    @Inject
    CameraApiManager cameraApiManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,savedInstanceState);
        binding = DataBindingUtil.inflate(inflater,layout.settings_rightmenufragment,container,false);
        cameraApiManager.addEventListner(this);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cameraApiManager.removeEventListner(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        settingchildholder = binding.SettingChildHolder;
    }

    private void setCameraToUi(CameraWrapperInterface wrapper)
    {
        if (settingchildholder == null)
            return;
        settingchildholder.removeAllViews();
        GroupChild settingsgroup = settingsMenuItemFactory.fillRightSettingsMenu(wrapper,getContext(),RightMenuFragment.this);
        if (settingsgroup != null)
            settingchildholder.addView(settingsgroup);
    }

    public void SetMenuItemClickListner(SettingsChildClick menuItemClick)
    {
        onMenuItemClick = menuItemClick;
    }

    @Override
    public void onSettingsChildClick(SettingsChildAbstract item, boolean fromLeftFragment) {
        onMenuItemClick.onSettingsChildClick(item, false);
    }

    @Override
    public void onCameraOpen() {

    }

    private Handler handler = new Handler();
    @Override
    public void onCameraOpenFinished() {
        handler.post(() -> setCameraToUi(cameraApiManager.getCamera()));
    }

    @Override
    public void onCameraClose() {
        handler.post(() -> setCameraToUi(null));
    }

    @Override
    public void onCameraError(String error) {

    }

    @Override
    public void onCameraChangedAspectRatioEvent(Size size) {

    }
}
