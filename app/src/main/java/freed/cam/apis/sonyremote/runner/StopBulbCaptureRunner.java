package freed.cam.apis.sonyremote.runner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import freed.cam.apis.sonyremote.sonystuff.SimpleRemoteApi;
import freed.utils.Log;

/**
 * Created by KillerInk on 28.12.2017.
 */

public class StopBulbCaptureRunner extends StopContShotRunner {

    private final String TAG = StartBulbCaptureRunner.class.getSimpleName();

    public StopBulbCaptureRunner(SimpleRemoteApi simpleRemoteApi) {
        super(simpleRemoteApi);
    }

    @Override
    public boolean process() {
        try {
            SimpleRemoteApi api = simpleRemoteApiWeakReference.get();
            if (api == null)
                return false;
            JSONObject replyJson = api.stopBulbCapture();
            JSONArray resultsObj = replyJson.getJSONArray("result");

        } catch (IOException e) {
            Log.w(TAG, "IOException while closing slicer: " + e.getMessage());

        } catch (JSONException e) {
            Log.w(TAG, "JSONException while closing slicer");

        }
        return false;
    }
}
