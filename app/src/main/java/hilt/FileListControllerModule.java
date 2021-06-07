package hilt;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import freed.file.FileListController;

@Module
@InstallIn(SingletonComponent.class)
public class FileListControllerModule {

    @Provides
    @Singleton
    public static FileListController fileListController(@ApplicationContext Context context)
    {
        return new FileListController(context);
    }
}
