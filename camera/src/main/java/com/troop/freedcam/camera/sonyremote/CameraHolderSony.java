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

package com.troop.freedcam.camera.sonyremote;

import android.content.Context;

import com.troop.freedcam.camera.basecamera.CameraControllerInterface;
import com.troop.freedcam.camera.basecamera.cameraholder.CameraHolderAbstract;
import com.troop.freedcam.camera.basecamera.focus.FocusEvents;
import com.troop.freedcam.camera.sonyremote.modules.I_PictureCallback;
import com.troop.freedcam.camera.sonyremote.parameters.ParameterHandler;
import com.troop.freedcam.camera.sonyremote.runner.ActTakePictureRunner;
import com.troop.freedcam.camera.sonyremote.runner.SetShootModeRunner;
import com.troop.freedcam.camera.sonyremote.runner.StartBulbCaptureRunner;
import com.troop.freedcam.camera.sonyremote.runner.StartContShotRunner;
import com.troop.freedcam.camera.sonyremote.runner.StopBulbCaptureRunner;
import com.troop.freedcam.camera.sonyremote.runner.StopContShotRunner;
import com.troop.freedcam.camera.sonyremote.runner.StopPreviewRunner;
import com.troop.freedcam.camera.sonyremote.sonystuff.JsonUtils;
import com.troop.freedcam.camera.sonyremote.sonystuff.ServerDevice;
import com.troop.freedcam.camera.sonyremote.sonystuff.SimpleRemoteApi;
import com.troop.freedcam.eventbus.events.CameraStateEvents;
import com.troop.freedcam.image.ImageManager;
import com.troop.freedcam.utils.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Set;

/**
 * Created by troop on 11.12.2014.
 */
public class CameraHolderSony extends CameraHolderAbstract<CameraControllerSonyRemote> implements CameraHolderSonyInterface
{

    public interface CameraRemoteEvents
    {
        void onApiSetChanged(Set<String> mAvailableCameraApiSet);
    }

    public interface I_CameraShotMode
    {
        void onShootModeChanged(String mode);
        void onShootModeValuesChanged(String[] modes);
    }

    private final String TAG =CameraHolderSony.class.getSimpleName();

    Context context;
    ServerDevice serverDevice;
    FocusEvents autoFocusCallback;
    private JSONObject FullUiSetup;

    public CameraRemoteEvents cameraRemoteEventsListner;

    private SimpleRemoteApi mRemoteApi;
    private PreviewStreamDrawer mLiveviewSurface;

    public CameraHolderSony(Context context, PreviewStreamDrawer simpleStreamSurfaceView, CameraControllerSonyRemote cameraUiWrapper)
    {
        super(cameraUiWrapper);
        this.context = context;
        mLiveviewSurface = simpleStreamSurfaceView;
    }

    public void setRemoteApi(SimpleRemoteApi remoteApi)
    {
        this.mRemoteApi =remoteApi;
    }

    @Override
    public boolean OpenCamera(int camera) {
        return false;
    }

    @Override
    public void CloseCamera()
    {
        closeConnection();
    }

    @Override
    public void StartPreview()
    {
        try {
            JSONObject replyJson = null;
            if (cameraUiWrapper.getAvailableApiSet().contains("startLiveviewWithSize"))
                replyJson = mRemoteApi.startLiveviewWithSize("L");
            else
                replyJson = mRemoteApi.startLiveview();

            if (!SimpleRemoteApi.isErrorReply(replyJson)) {
                JSONArray resultsObj = replyJson.getJSONArray("result");
                if (1 <= resultsObj.length()) {
                    // Obtain liveview URL from the result.
                    String liveviewUrl = resultsObj.getString(0);
                    Log.d(TAG,"startLiveview");
                    mLiveviewSurface.start(liveviewUrl, //
                            reason -> {
                                Log.e(TAG, "Error StartingLiveView" + reason.toString());
                                StopPreview();
                            });
                }
            }
            else
                Log.d(TAG, "Error : " + replyJson);
        } catch (IOException e) {
            Log.w(TAG, "startLiveview IOException: " + e.getMessage());
        } catch (JSONException e) {
            Log.w(TAG, "startLiveview JSONException: " + e.getMessage());
        }
    }

    @Override
    public void StopPreview()
    {
        ImageManager.putImageLoadTask(new StopPreviewRunner(mRemoteApi));
    }

    /**
     * Stop monitoring Camera events and close liveview connection.
     */
    private void closeConnection() {

        // getEvent stop
        CameraStateEvents.fireCameraCloseEvent();
        Log.d(TAG, "closeConnection(): EventObserver.release()");

        Log.d(TAG, "closeConnection(): exec.");
        // Liveview stop
        Log.d(TAG, "closeConnection(): LiveviewSurface.stop()");
        if (mLiveviewSurface != null)
        {
            if(serverDevice != null
                    &&( serverDevice.getFriendlyName().contains("ILCE-QX1") || serverDevice.getFriendlyName().contains("ILCE-QX30"))
                    && JsonUtils.isApiSupported("setLiveviewFrameInfo", cameraUiWrapper.getAvailableApiSet()))
            {
                SetLiveViewFrameInfo(false);
            }
            mLiveviewSurface.stop();
            StopPreview();
        }



        // stopRecMode if necessary.
        if (JsonUtils.isCameraApiAvailable("stopRecMode", cameraUiWrapper.getAvailableApiSet()))
        {
            Log.d(TAG, "closeConnection(): stopRecMode()");
            try {
                mRemoteApi.stopRecMode();
            } catch (IOException e) {
                Log.w(TAG, "closeConnection: IOException: " + e.getMessage());
            }
        }

        Log.d(TAG, "closeConnection(): completed.");
    }


    public void TakePicture(I_PictureCallback pictureCallback)
    {
        ImageManager.putImageLoadTask(new ActTakePictureRunner(mRemoteApi,pictureCallback,((ParameterHandler)cameraUiWrapper.getParameterHandler())));
    }

    public void startContShoot(I_PictureCallback pictureCallback)
    {
        ImageManager.putImageLoadTask(new StartContShotRunner(mRemoteApi));
    }

    public void stopContShoot(I_PictureCallback pictureCallback)
    {
        ImageManager.putImageLoadTask(new StopContShotRunner(mRemoteApi));
    }

    public void startBulbCapture(I_PictureCallback pictureCallback)
    {
        ImageManager.putImageLoadTask(new StartBulbCaptureRunner(mRemoteApi));
    }

    public void stopBulbCapture(I_PictureCallback pictureCallback)
    {
        ImageManager.putImageLoadTask(new StopBulbCaptureRunner(mRemoteApi));
    }


    public void SetShootMode(final String mode)
    {
        ImageManager.putImageLoadTask(new SetShootModeRunner(mRemoteApi,mode));
    }

    public void StartRecording()
    {
        new Thread(() -> {
            try {
                JSONObject replyJson = mRemoteApi.startMovieRec();
                JSONArray resultsObj = replyJson.getJSONArray("result");
                int resultCode = resultsObj.getInt(0);
                if (resultCode == 0) {
                    // Success, but no refresh UI at the point.
                    Log.v(TAG, "startRecording: success.");
                } else {
                    Log.w(TAG, "startRecording: error: " + resultCode);

                }
            } catch (IOException e) {
                Log.w(TAG, "startRecording: IOException: " + e.getMessage());
            } catch (JSONException e) {
                Log.w(TAG, "startRecording: JSON format error.");
            }
        }).start();
    }

    public void StopRecording()
    {
        new Thread(() -> {
            try {
                JSONObject replyJson = mRemoteApi.stopMovieRec();
                JSONArray resultsObj = replyJson.getJSONArray("result");
                int resultCode = resultsObj.getInt(0);
                if (resultCode == 0) {
                    // Success, but no refresh UI at the point.
                    Log.v(TAG, "StopRecording: success.");
                } else {
                    Log.w(TAG, "StopRecording: error: " + resultCode);

                }
            } catch (IOException e) {
                Log.w(TAG, "StopRecording: IOException: " + e.getMessage());
            } catch (JSONException e) {
                Log.w(TAG, "StopRecording: JSON format error.");
            }
        }).start();
    }

    @Override
    public void CancelFocus()
    {
        if (cameraUiWrapper.getAvailableApiSet().contains("cancelTouchAFPosition"))
        {
            Log.d(TAG, "Cancel Focus");
            new Thread(() -> {
                try
                {
                    JSONObject ob = mRemoteApi.setParameterToCamera("cancelTouchAFPosition", new JSONArray());
                } catch (IOException ex) {
                    Log.WriteEx(ex);
                    Log.d(TAG, "Cancel Focus failed");
                }
            }).start();

        }
        else if (cameraUiWrapper.getAvailableApiSet().contains("cancelTrackingFocus"))
        {
            Log.d(TAG, "Cancel Focus");
            new Thread(() -> {
                try
                {
                    JSONObject ob = mRemoteApi.setParameterToCamera("cancelTrackingFocus", new JSONArray());
                } catch (IOException ex) {
                    Log.WriteEx(ex);
                    Log.d(TAG, "Cancel Focus failed");
                }
            }).start();
        }
    }


    public boolean canCancelFocus()
    {
        if (cameraUiWrapper.getAvailableApiSet().contains("cancelTouchAFPosition") || cameraUiWrapper.getAvailableApiSet().contains("cancelTrackingFocus"))
        {
            Log.d(TAG, "Throw Focus LOCKED true");
            return true;
        }
        else
        {
            Log.d(TAG, "Throw Focus LOCKED false");
            return false;
        }
    }

    @Override
    public void StartFocus(FocusEvents autoFocusCallback)
    {
        this.autoFocusCallback = autoFocusCallback;
    }

    @Override
    public void SetTouchFocus(double x, double y)
    {
        if (cameraUiWrapper.getAvailableApiSet().contains("setTouchAFPosition"))
            runSetTouch(x, y);
        else
            runActObjectTracking(x,y);
    }

    private void runActObjectTracking(final double x,final double y)
    {
        new Thread(() -> {
            try {
                JSONObject replyJson = mRemoteApi.actObjectTracking(x, y);
                JSONArray resultsObj = replyJson.getJSONArray("result");
            } catch (IOException e) {
                Log.w(TAG, "setShootMode: IOException: " + e.getMessage());
            } catch (JSONException e) {
                Log.w(TAG, "setShootMode: JSON format error.");
            }
            catch (NullPointerException e) {
                Log.w(TAG, "remote api is null");
            }
        }).start();
    }

    private void runSetTouch(final double x, final double y) {
        new Thread(() -> {
            try {
                JSONObject replyJson = mRemoteApi.setTouchToFocus(x,y);
                JSONArray resultsObj = replyJson.getJSONArray("result");
                int resultCode = resultsObj.getInt(0);
                if (resultCode == 0)
                {
                    JSONObject ob = resultsObj.getJSONObject(1);
                    String success = ob.getString("AFResult");
                    boolean suc = false;
                    if (success.equals("true"))
                        suc = true;
                    if (autoFocusCallback != null)
                    {
                        autoFocusCallback.onFocusEvent(suc);
                    }

                }
            } catch (IOException e) {
                Log.w(TAG, "setShootMode: IOException: " + e.getMessage());
            } catch (JSONException e) {
                Log.w(TAG, "setShootMode: JSON format error.");
            }
        });
    }

    public void SetLiveViewFrameInfo(final boolean val)
    {
        new Thread(() -> {
            try {
                mRemoteApi.setLiveviewFrameInfo(val);
            } catch (IOException ex) {
                Log.WriteEx(ex);
            }
        }).start();
    }
}
