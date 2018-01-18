package freed.cam.apis.sonyremote.runner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;

import freed.cam.apis.sonyremote.sonystuff.SimpleRemoteApi;
import freed.image.ImageTask;
import freed.utils.Log;

/**
 * Created by KillerInk on 27.12.2017.
 */

public class StopContShotRunner extends ImageTask {

    private final String TAG = StopContShotRunner.class.getSimpleName();
    WeakReference<SimpleRemoteApi> simpleRemoteApiWeakReference;
    public StopContShotRunner(SimpleRemoteApi simpleRemoteApi)
    {
        this.simpleRemoteApiWeakReference = new WeakReference<SimpleRemoteApi>(simpleRemoteApi);
    }
    @Override
    public boolean process() {
        try {
            SimpleRemoteApi api = simpleRemoteApiWeakReference.get();
            if (api == null)
                return false;
            JSONObject replyJson = api.stopContShoot();
            JSONArray resultsObj = replyJson.getJSONArray("result");

        } catch (IOException e) {
            Log.w(TAG, "IOException while closing slicer: " + e.getMessage());

        } catch (JSONException e) {
            Log.w(TAG, "JSONException while closing slicer");

        }
        return false;
    }
}
