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

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.SurfaceView;
import android.view.View;

import com.drew.lang.StringUtil;
import com.troop.freedcam.R;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract;
import freed.cam.apis.basecamera.parameters.AbstractParameterHandler;
import freed.utils.AppSettingsManager;
import freed.utils.RenderScriptHandler;
import freed.utils.StringUtils;

/**
 * Created by troop on 06.06.2015.
 * That Fragment is used as base for all camera apis added.
 */
public abstract class CameraFragmentAbstract extends Fragment implements CameraWrapperInterface {
    private final String TAG = CameraFragmentAbstract.class.getSimpleName();

    protected View view;
    //the event listner when the camerauiwrapper is rdy to get attached to ui
    protected CameraFragmentAbstract.CamerUiWrapperRdy onrdy;
    //holds the appsettings
    protected RenderScriptHandler renderScriptHandler;

    public ModuleHandlerAbstract moduleHandler;
    /**
     * parameters for avail for the cameraHolder
     */
    public AbstractParameterHandler parametersHandler;
    /**
     * holds the current camera
     */
    public CameraHolderAbstract cameraHolder;
    /**
     * handels focus releated stuff for the current camera
     */
    public AbstractFocusHandler Focus;

    protected boolean PreviewSurfaceRdy;

    /**
     * holds handler to invoke stuff in ui thread
     */
    protected Handler uiHandler;
    /**
     * holds the appsettings for the current camera
     */
    public AppSettingsManager appSettingsManager;


    public abstract String CameraApiName();


    public CameraFragmentAbstract()
    {
        uiHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onDestroyView()
    {
        moduleHandler.CLEAR();
        super.onDestroyView();

    }

    public void SetRenderScriptHandler(RenderScriptHandler renderScriptHandler)
    {
        this.renderScriptHandler = renderScriptHandler;
    }

    public void SetAppSettingsManager(AppSettingsManager appSettingsManager)
    {
        this.appSettingsManager = appSettingsManager;
    }

    /**
     *
     * @return the current instance of the cameruiwrapper
     */
    public CameraWrapperInterface GetCameraUiWrapper()
    {
        return this;
    }

    /**
     *
     * @param rdy the listner that gets thrown when the cameraUIwrapper
     *            has loaded all stuff and is rdy to get attached to ui.
     */
    public void Init(CameraFragmentAbstract.CamerUiWrapperRdy rdy)
    {
        onrdy = rdy;
    }


    /**
     * inteface for event listning when the camerauiwrapper is rdy
     */
    public interface CamerUiWrapperRdy
    {
        void onCameraUiWrapperRdy(CameraWrapperInterface cameraUiWrapper);
    }


    @Override
    public void StartCamera()
    {
    }

    @Override
    public void StopCamera()
    {
    }

    @Override
    public void StopPreview()
    {
    }


    @Override
    public void StartPreview()
    {
    }

    /**
     * Starts a new work with the current active module
     * the module must handle the workstate on its own if it gets hit twice while work is already in progress
     */
    @Override
    public void DoWork()
    {
        moduleHandler.DoWork();
    }


    public void onCameraOpen(final String message) {
        sendCameraStatusIntent(CAMERA_OPEN, message);
    }

    public void onCameraError(final String error) {
        sendCameraStatusIntent(CAMERA_ERROR,error);
    }

    public void onCameraStatusChanged(final String status) {
        sendCameraStatusIntent(CAMERA_STATUS_CHANGED,status);
    }

    public void onCameraClose(final String message) {
        sendCameraStatusIntent(CAMERA_CLOSE,message);
    }

    public void onPreviewOpen(final String message) {
        sendCameraStatusIntent(PREVIEW_OPEN,message);
    }

    public void onPreviewClose(final String message) {
        sendCameraStatusIntent(PREVIEW_CLOSE,message);
    }

    public void onCameraOpenFinish(final String message)
    {
        sendCameraStatusIntent(CAMERA_OPEN_FINISH,message);
    }

    private void sendCameraStatusIntent(int status, String msg) {
        if (isAdded()) {
            Intent intent = new Intent(getString(R.string.INTENT_CAMERASTATE));
            intent.putExtra(getString(R.string.INTENT_EXTRA_CAMERAESTATE), status);
            if (msg != null)
                intent.putExtra(getString(R.string.INTENT_EXTRA_CAMERAESTATEMSG), msg);
            getContext().getApplicationContext().sendBroadcast(intent);
        }
    }

    public abstract int getMargineLeft();
    public abstract int getMargineRight();
    public abstract int getMargineTop();
    public abstract int getPreviewWidth();
    public abstract int getPreviewHeight();
    public abstract SurfaceView getSurfaceView();



    @Override
    public AppSettingsManager GetAppSettingsManager() {
        return appSettingsManager;
    }

}
