package hilt;

import dagger.hilt.EntryPoint;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import freed.cam.ui.ThemeManager;

@EntryPoint
@InstallIn(ActivityComponent.class)
public interface ThemeManagerEntryPoint
{
    ThemeManager themeManager();
}
