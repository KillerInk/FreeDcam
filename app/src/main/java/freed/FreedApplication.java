package freed;

import android.app.Application;
import android.content.Context;

import dagger.hilt.android.EntryPointAccessors;
import dagger.hilt.android.HiltAndroidApp;
import freed.file.FileListController;
import freed.image.ImageManager;
import freed.settings.SettingsManager;
import freed.viewer.helper.BitmapHelper;
import hilt.BitmapHelperEntryPoint;
import hilt.FileListControllerEntryPoint;
import hilt.ImageManagerEntryPoint;
import hilt.SettingsManagerEntryPoint;

@HiltAndroidApp
public class FreedApplication extends Application {

    private static Context context;

    public static Context getContext()
    {
        return context;
    }

    public static String getStringFromRessources(int id)
    {
        return context.getResources().getString(id);
    }

    public static String[] getStringArrayFromRessource(int id)
    {
        return context.getResources().getStringArray(id);
    }

    public static SettingsManager settingsManager()
    {
        return getEntryPointFromApplication(SettingsManagerEntryPoint.class).settingsManager();
    }

    public static FileListController fileListController()
    {
        return getEntryPointFromApplication(FileListControllerEntryPoint.class).fileListController();
    }

    public static BitmapHelper bitmapHelper()
    {
        return getEntryPointFromApplication(BitmapHelperEntryPoint.class).bitmapHelper();
    }

    public static ImageManager imageManager()
    {
        return getEntryPointFromApplication(ImageManagerEntryPoint.class).imageManager();
    }

    public static <T> T getEntryPointFromApplication(Class<T> entryPoint) {
        return EntryPointAccessors.fromApplication(context, entryPoint);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //EventBus.builder().throwSubscriberException(BuildConfig.DEBUG).installDefaultEventBus();
        context = getApplicationContext();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        context = null;
    }
}
