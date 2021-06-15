package hilt;

import dagger.hilt.EntryPoint;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import freed.settings.SettingsManager;

@EntryPoint
@InstallIn(SingletonComponent.class)
public interface SettingsManagerEntryPoint {
    SettingsManager settingsManager();
}
