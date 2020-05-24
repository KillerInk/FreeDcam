package freed;

import android.app.Application;
import android.content.Context;

import com.troop.freedcam.BuildConfig;

import org.greenrobot.eventbus.EventBus;

public class FreedApplication extends Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.builder().throwSubscriberException(BuildConfig.DEBUG).installDefaultEventBus();
        context = getApplicationContext();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        context = null;
    }
}
