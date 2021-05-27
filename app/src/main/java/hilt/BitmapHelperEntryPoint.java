package hilt;

import dagger.hilt.EntryPoint;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import freed.viewer.helper.BitmapHelper;

@EntryPoint
@InstallIn(SingletonComponent.class)
public interface BitmapHelperEntryPoint {
    BitmapHelper bitmapHelper();
}
