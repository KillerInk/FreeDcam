package freed.cam.apis.sonyremote.runner;

import java.io.IOException;

import freed.cam.apis.sonyremote.sonystuff.SimpleRemoteApi;
import freed.utils.Log;

/**
 * Created by KillerInk on 27.12.2017.
 */

public class StopPreviewRunner extends StopContShotRunner {
    private final String TAG = StopPreviewRunner.class.getSimpleName();
    public StopPreviewRunner(SimpleRemoteApi simpleRemoteApi) {
        super(simpleRemoteApi);
    }

    @Override
    public boolean process() {
        try {
            SimpleRemoteApi mRemoteApi = simpleRemoteApiWeakReference.get();
            if (mRemoteApi != null)
                mRemoteApi.stopLiveview();

        } catch (IOException e) {
            Log.w(TAG, "stopLiveview IOException: " + e.getMessage());

        }
        return false;
    }
}
