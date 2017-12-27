package freed.cam.apis.sonyremote.runner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import freed.cam.apis.sonyremote.sonystuff.SimpleRemoteApi;
import freed.utils.Log;

/**
 * Created by KillerInk on 27.12.2017.
 */

public class SetShootModeRunner extends StopContShotRunner {

    private final String TAG = SetShootModeRunner.class.getSimpleName();
    private final String mode;

    public SetShootModeRunner(SimpleRemoteApi simpleRemoteApi,String mode) {
        super(simpleRemoteApi);
        this.mode = mode;
    }

    @Override
    public boolean process() {
        try {
            SimpleRemoteApi remoteApi = simpleRemoteApiWeakReference.get();
            if (remoteApi == null)
                return false;
            JSONObject replyJson = remoteApi.setShootMode(mode);
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
        return false;
    }
}
