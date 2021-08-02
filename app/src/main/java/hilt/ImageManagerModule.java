package hilt;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.scopes.ActivityScoped;
import dagger.hilt.components.SingletonComponent;
import freed.image.ImageManager;

@Module
@InstallIn(SingletonComponent.class)
public class ImageManagerModule {


    @Provides
    @Singleton
    public ImageManager imageManager()
    {
        return new ImageManager();
    }
}
