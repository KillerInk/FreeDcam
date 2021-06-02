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

package freed.cam.apis.basecamera;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import dagger.hilt.android.EntryPointAccessors;
import freed.cam.previewpostprocessing.PreviewController;
import freed.cam.previewpostprocessing.PreviewControllerInterface;
import freed.utils.Log;
import hilt.PreviewControllerEntryPoint;

/**
 * Created by troop on 06.06.2015.
 * That Fragment is used as base for all camera apis added.
 */
public abstract class CameraFragmentAbstract<C extends CameraWrapperInterface> extends Fragment {
    private final String TAG = CameraFragmentAbstract.class.getSimpleName();


    private static Fragment fragment;
    protected static <T> T getEntryPointFromFragment(Class<T> entryPoint) {
        return EntryPointAccessors.fromFragment(fragment, entryPoint);
    }

    public static PreviewController getPreviewController()
    {
        return getEntryPointFromFragment(PreviewControllerEntryPoint.class).previewController();
    }

    protected View view;
    @Inject
    protected PreviewController preview;
    protected boolean PreviewSurfaceRdy;
    protected C camera;

    public static CameraFragmentAbstract getInstance()
    {
        return null;
    }

    public CameraFragmentAbstract()
    {

    }

    public C getCamera() {
        return camera;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        Log.d(TAG, "onCreateView");
        fragment = this;
        return super.onCreateView(layoutInflater, viewGroup, null);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(null);
    }

    @Override
    public void onDestroyView()
    {
        Log.d(TAG, "onDestroyView");

        super.onDestroyView();
        fragment = null;

    }
}
