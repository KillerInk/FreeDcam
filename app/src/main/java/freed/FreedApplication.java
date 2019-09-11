package freed;

import android.app.Application;

import com.troop.freedcam.BuildConfig;

import org.greenrobot.eventbus.EventBus;

public class FreedApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.builder().throwSubscriberException(BuildConfig.DEBUG).installDefaultEventBus();
    }
}
