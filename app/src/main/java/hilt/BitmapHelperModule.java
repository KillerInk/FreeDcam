package hilt;

import android.content.Context;

import com.troop.freedcam.R;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import freed.viewer.helper.BitmapHelper;

@Module
@InstallIn(SingletonComponent.class)
public class BitmapHelperModule {

    @Provides
    @Singleton
    public static BitmapHelper bitmapHelper(@ApplicationContext Context context)
    {
        return new BitmapHelper(context,context.getResources().getDimensionPixelSize(R.dimen.image_thumbnails_size));
    }
}
