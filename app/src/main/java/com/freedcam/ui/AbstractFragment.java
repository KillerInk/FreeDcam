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

package com.freedcam.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.freedcam.apis.basecamera.interfaces.I_CameraUiWrapper;
import com.freedcam.utils.AppSettingsManager;

/**
 * Created by troop on 25.03.2015.
 */
public abstract class AbstractFragment extends Fragment implements I_Fragment
{
    protected I_CameraUiWrapper cameraUiWrapper;
    protected I_Activity i_activity;
    protected View view;
    protected AppSettingsManager appSettingsManager;

    public void SetCameraUIWrapper(I_CameraUiWrapper wrapper)
    {
        cameraUiWrapper = wrapper;
        if (cameraUiWrapper != null)
            setCameraUiWrapperToUi();
    }

    protected void setCameraUiWrapperToUi()
    {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
