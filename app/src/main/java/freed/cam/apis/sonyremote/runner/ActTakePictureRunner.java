package freed.cam.apis.sonyremote.runner;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;

import freed.cam.apis.sonyremote.modules.I_PictureCallback;
import freed.cam.apis.sonyremote.parameters.ParameterHandler;
import freed.cam.apis.sonyremote.sonystuff.SimpleRemoteApi;
import freed.utils.Log;

/**
 * Created by KillerInk on 27.12.2017.
 */

public class ActTakePictureRunner extends StopContShotRunner {

    private final String TAG = ActTakePictureRunner.class.getSimpleName();

    private WeakReference<I_PictureCallback> pictureCallbackWeakReference;
    private WeakReference<ParameterHandler> parameterHandlerWeakReference;

    public ActTakePictureRunner(SimpleRemoteApi remoteApi, I_PictureCallback pictureCallback, ParameterHandler parameterHandler)
    {
        super(remoteApi);
        pictureCallbackWeakReference = new WeakReference<I_PictureCallback>(pictureCallback);
        parameterHandlerWeakReference =new WeakReference<ParameterHandler>(parameterHandler);
    }

    @Override
    public boolean process() {
        SimpleRemoteApi mRemoteApi = simpleRemoteApiWeakReference.get();
        I_PictureCallback pictureCallback = pictureCallbackWeakReference.get();
        if (mRemoteApi == null && pictureCallback == null)
            return false;

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

                return false;
            }
            // Show progress indicator


            URL url = new URL(postImageUrl);
            pictureCallback.onPictureTaken(url);
            //InputStream istream = new BufferedInputStream(url.openStream());


        } catch (IOException ex)
        {
            Log.WriteEx(ex);
            Log.w(TAG, "IOException while closing slicer: " + ex.getMessage());
            awaitTakePicture(pictureCallback,mRemoteApi);
        } catch (JSONException e) {
            Log.w(TAG, "JSONException while closing slicer");
            //awaitTakePicture(pictureCallback);
        }
        return false;
    }

    private void awaitTakePicture(I_PictureCallback pictureCallback, SimpleRemoteApi mRemoteApi)
    {
        ParameterHandler parameterHandler = parameterHandlerWeakReference.get();
        if (parameterHandler == null)
            return;
        Log.d(TAG, "Camerastatus:" + parameterHandler.GetCameraStatus());
        if (parameterHandler.GetCameraStatus().equals("StillCapturing")) {
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
                awaitTakePicture(pictureCallback,mRemoteApi);
                Log.WriteEx(e1);
            } catch (JSONException e1) {
                //awaitTakePicture(pictureCallback);
                Log.WriteEx(e1);
            }
        }
    }
}
