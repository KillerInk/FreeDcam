package freed.update;

import com.troop.freedcam.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import freed.cam.apis.sonyremote.sonystuff.SimpleHttpClient;

public class ReleaseChecker {

    public static final boolean isGithubRelease = false;

    public interface UpdateEvent
    {
        void onUpdateAvailable();
    }

    private static final String TAG = ReleaseChecker.class.getSimpleName();
    private final String githubrepo = "https://api.github.com/repos/killerink/freedcam/releases/latest";

    private UpdateEvent event;

    public ReleaseChecker(UpdateEvent eventListner)
    {
        this.event = eventListner;
    }

    public void isUpdateAvailable()  {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    checkupdate();
                } catch (IOException | JSONException | NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    private void checkupdate() throws IOException, JSONException, NumberFormatException
    {
        String ret = SimpleHttpClient.httpGet(githubrepo);
        JSONObject jsonObject = new JSONObject(ret);
        String version = jsonObject.getString("tag_name");
        String curVersion = BuildConfig.VERSION_NAME;
        int v = Integer.parseInt(version.replace(".",""));
        int cv = Integer.parseInt(curVersion.replace(".",""));
        if (v > cv)
        {
            if (event != null)
                event.onUpdateAvailable();
        }
    }
}
