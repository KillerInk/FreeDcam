package hilt;

import dagger.hilt.EntryPoint;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import freed.utils.SoundPlayer;
@EntryPoint
@InstallIn(ActivityComponent.class)
public interface SoundPlayerEntryPoint {
    SoundPlayer soundPlayer();
}
