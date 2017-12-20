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

package freed.cam.apis.sonyremote;

import android.content.Context;
import android.location.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Set;

import freed.cam.apis.basecamera.CameraHolderAbstract;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.FocusEvents;
import freed.cam.apis.sonyremote.modules.I_PictureCallback;
import freed.cam.apis.sonyremote.parameters.ParameterHandler;
import freed.cam.apis.sonyremote.sonystuff.JsonUtils;
import freed.cam.apis.sonyremote.sonystuff.ServerDevice;
import freed.cam.apis.sonyremote.sonystuff.SimpleRemoteApi;
import freed.cam.apis.sonyremote.sonystuff.SimpleStreamSurfaceView;
import freed.cam.apis.sonyremote.sonystuff.SimpleStreamSurfaceView.StreamErrorListener;
import freed.utils.FreeDPool;
import freed.utils.Log;

/**
 * Created by troop on 11.12.2014.
 */
public class CameraHolderSony extends CameraHolderAbstract
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
    private SimpleStreamSurfaceView mLiveviewSurface;

    public CameraHolderSony(Context context, SimpleStreamSurfaceView simpleStreamSurfaceView, CameraWrapperInterface cameraUiWrapper)
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
    public int CameraCout() {
        return 0;
    }

    @Override
    public boolean IsRdy() {
        return false;
    }


    @Override
    public void StartPreview()
    {
        try {
            JSONObject replyJson = null;
            replyJson = mRemoteApi.startLiveview();

            if (!SimpleRemoteApi.isErrorReply(replyJson)) {
                JSONArray resultsObj = replyJson.getJSONArray("result");
                if (1 <= resultsObj.length()) {
                    // Obtain liveview URL from the result.
                    String liveviewUrl = resultsObj.getString(0);
                    Log.d(TAG,"startLiveview");
                    mLiveviewSurface.start(liveviewUrl, //
                            new StreamErrorListener() {

                                @Override
                                public void onError(StreamErrorReason reason)
                                {
                                    Log.e(TAG, "Error StartingLiveView" + reason.toString());
                                    StopPreview();
                                }
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
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mRemoteApi != null)
                        mRemoteApi.stopLiveview();

                } catch (IOException e) {
                    Log.w(TAG, "stopLiveview IOException: " + e.getMessage());

                }
            }
        });
    }

    /**
     * Stop monitoring Camera events and close liveview connection.
     */
    private void closeConnection() {

        // getEvent stop
        cameraUiWrapper.onCameraClose("");
        Log.d(TAG, "closeConnection(): EventObserver.release()");

        Log.d(TAG, "closeConnection(): exec.");
        // Liveview stop
        Log.d(TAG, "closeConnection(): LiveviewSurface.stop()");
        if (mLiveviewSurface != null)
        {
            if(serverDevice != null
                    &&( serverDevice.getFriendlyName().contains("ILCE-QX1") || serverDevice.getFriendlyName().contains("ILCE-QX30"))
                    && JsonUtils.isApiSupported("setLiveviewFrameInfo", ((SonyCameraRemoteFragment)cameraUiWrapper).getAvailableApiSet()))
            {
                SetLiveViewFrameInfo(false);
            }
            mLiveviewSurface.stop();
            StopPreview();
        }



        // stopRecMode if necessary.
        if (JsonUtils.isCameraApiAvailable("stopRecMode", ((SonyCameraRemoteFragment)cameraUiWrapper).getAvailableApiSet()))
        {
            FreeDPool.Execute(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "closeConnection(): stopRecMode()");
                    try {
                        mRemoteApi.stopRecMode();
                    } catch (IOException e) {
                        Log.w(TAG, "closeConnection: IOException: " + e.getMessage());
                    }
                }
            });
        }

        Log.d(TAG, "closeConnection(): completed.");
    }


    public void TakePicture(I_PictureCallback pictureCallback)
    {
        actTakePicture(pictureCallback);
    }

    public void startContShoot(I_PictureCallback pictureCallback)
    {
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject replyJson = mRemoteApi.startContShoot();
                    JSONArray resultsObj = replyJson.getJSONArray("result");

                } catch (IOException e) {
                    Log.w(TAG, "IOException while closing slicer: " + e.getMessage());

                } catch (JSONException e) {
                    Log.w(TAG, "JSONException while closing slicer");

                }
            }
        });
    }

    public void stopContShoot(I_PictureCallback pictureCallback)
    {
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject replyJson = mRemoteApi.stopContShoot();
                    JSONArray resultsObj = replyJson.getJSONArray("result");

                } catch (IOException e) {
                    Log.w(TAG, "IOException while closing slicer: " + e.getMessage());

                } catch (JSONException e) {
                    Log.w(TAG, "JSONException while closing slicer");

                }
            }
        });
    }

    private void actTakePicture(final I_PictureCallback pictureCallback)
    {
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "####################### ACT TAKE PICTURE");
                    JSONObject replyJson = mRemoteApi.actTakePicture();
                    Log.d(TAG, "####################### ACT TAKE PICTURE REPLY RECIEVED");
                    Log.d(TAG, replyJson.toString());
                    JSONArray resultsObj = replyJson.getJSONArray("result");
                    Log.d(TAG, "####################### ACT TAKE PICTURE PARSED RESULT");
                    JSONArray imageUrlsObj = resultsObj.getJSONArray(0);
                    String postImageUrl = null;
                    if (1 <= imageUrlsObj.length()) {
                        postImageUrl = imageUrlsObj.getString(0);
                    }
                    if (postImageUrl == null) {
                        Log.w(TAG, "takeAndFetchPicture: post image URL is null.");

                        return;
                    }
                    // Show progress indicator


                    URL url = new URL(postImageUrl);
                    pictureCallback.onPictureTaken(url);
                    //InputStream istream = new BufferedInputStream(url.openStream());


                } catch (IOException ex)
                {
                    Log.WriteEx(ex);
                    Log.w(TAG, "IOException while closing slicer: " + ex.getMessage());
                    awaitTakePicture(pictureCallback);
                } catch (JSONException e) {
                    Log.w(TAG, "JSONException while closing slicer");
                    //awaitTakePicture(pictureCallback);
                }
            }
        });
    }


    private void awaitTakePicture(I_PictureCallback pictureCallback)
    {
        Log.d(TAG, "Camerastatus:" + ((ParameterHandler)cameraUiWrapper.getParameterHandler()).GetCameraStatus());
        if (((ParameterHandler)cameraUiWrapper.getParameterHandler()).GetCameraStatus().equals("StillCapturing")) {
            try {
                Log.d(TAG, "####################### AWAIT TAKE");
                JSONObject replyJson = mRemoteApi.awaitTakePicture();
                Log.d(TAG, "####################### AWAIT TAKE PICTURE RECIEVED RESULT");
                JSONArray resultsObj = replyJson.getJSONArray("result");
                Log.d(TAG, "####################### AWAIT TAKE PICTURE PARSED RESULT");
                if (!resultsObj.isNull(0))
                {
                    Log.d(TAG, resultsObj.toString());
                    JSONArray imageUrlsObj = resultsObj.getJSONArray(0);
                    URL url = new URL(imageUrlsObj.getString(0));
                    pictureCallback.onPictureTaken(url);
                }
            } catch (IOException e1)
            {
                awaitTakePicture(pictureCallback);
                Log.WriteEx(e1);
            } catch (JSONException e1) {
                //awaitTakePicture(pictureCallback);
                Log.WriteEx(e1);
            }
        }
    }

    public void SetShootMode(final String mode)
    {
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject replyJson = mRemoteApi.setShootMode(mode);
                    JSONArray resultsObj = replyJson.getJSONArray("result");
                    int resultCode = resultsObj.getInt(0);
                    if (resultCode == 0) {
                        // Success, but no refresh UI at the point.
                        Log.v(TAG, "setShootMode: success.");
                    } else {
                        Log.w(TAG, "setShootMode: error: " + resultCode);

                    }
                } catch (IOException e) {
                    Log.w(TAG, "setShootMode: IOException: " + e.getMessage());
                } catch (JSONException e) {
                    Log.w(TAG, "setShootMode: JSON format error.");
                }
                catch (NullPointerException e) {
                    Log.w(TAG, "remote api null");
                }
            }
        });

    }

    public void StartRecording()
    {
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
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
            }
        });
    }

    public void StopRecording()
    {
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
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
            }
        });
    }

    @Override
    public void CancelFocus()
    {
        if (((SonyCameraRemoteFragment)cameraUiWrapper).getAvailableApiSet().contains("cancelTouchAFPosition"))
        {
            Log.d(TAG, "Cancel Focus");
            FreeDPool.Execute(new Runnable() {
                @Override
                public void run()
                {
                    try
                    {
                        JSONObject ob = mRemoteApi.setParameterToCamera("cancelTouchAFPosition", new JSONArray());
                    } catch (IOException ex) {
                        Log.WriteEx(ex);
                        Log.d(TAG, "Cancel Focus failed");
                    }
                }
            });

        }
        else if (((SonyCameraRemoteFragment)cameraUiWrapper).getAvailableApiSet().contains("cancelTrackingFocus"))
        {
            Log.d(TAG, "Cancel Focus");
            FreeDPool.Execute(new Runnable() {
                @Override
                public void run()
                {
                    try
                    {
                        JSONObject ob = mRemoteApi.setParameterToCamera("cancelTrackingFocus", new JSONArray());
                    } catch (IOException ex) {
                        Log.WriteEx(ex);
                        Log.d(TAG, "Cancel Focus failed");
                    }
                }
            });
        }
    }

    @Override
    public void ResetPreviewCallback() {

    }

    @Override
    public void SetLocation(Location loc) {

    }

    public boolean canCancelFocus()
    {
        if (((SonyCameraRemoteFragment)cameraUiWrapper).getAvailableApiSet().contains("cancelTouchAFPosition") || ((SonyCameraRemoteFragment)cameraUiWrapper).getAvailableApiSet().contains("cancelTrackingFocus"))
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

    public void SetTouchFocus(double x, double y)
    {
        if (((SonyCameraRemoteFragment)cameraUiWrapper).getAvailableApiSet().contains("setTouchAFPosition"))
            runSetTouch(x, y);
        else
            runActObjectTracking(x,y);
    }

    private void runActObjectTracking(final double x,final double y)
    {
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
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
            }
        });
    }

    private void runSetTouch(final double x, final double y) {
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
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
            }
        });
    }

    public void SetLiveViewFrameInfo(final boolean val)
    {
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mRemoteApi.setLiveviewFrameInfo(val);
                } catch (IOException ex) {
                    Log.WriteEx(ex);
                }
            }
        });
    }

}
