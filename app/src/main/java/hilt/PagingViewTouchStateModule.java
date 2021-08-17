package hilt;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.scopes.ActivityScoped;
import freed.cam.ui.themesample.PagingViewTouchState;

@Module
@InstallIn(ActivityComponent.class)
public class PagingViewTouchStateModule {
    @ActivityScoped
    @Provides
    public PagingViewTouchState pagingViewTouchState()
    {
        return new PagingViewTouchState();
    }
}
