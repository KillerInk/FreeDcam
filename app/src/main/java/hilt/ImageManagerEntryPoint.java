package hilt;

import dagger.hilt.EntryPoint;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.components.SingletonComponent;
import freed.image.ImageManager;

@EntryPoint
@InstallIn(SingletonComponent.class)
public interface ImageManagerEntryPoint {
    ImageManager imageManager();
}
