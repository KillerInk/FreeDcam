package hilt;

import dagger.hilt.EntryPoint;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import freed.utils.PermissionManager;

@EntryPoint
@InstallIn(ActivityComponent.class)
public interface PermissionManagerEntryPoint {
    PermissionManager permissionManager();
}
