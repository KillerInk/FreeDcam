package freed.cam.event.module;

import freed.cam.event.BaseEventHandler;
import freed.cam.event.camera.CameraHolderEvent;
import freed.cam.event.camera.CameraHolderEventHandler;
import freed.utils.Log;

public class ModuleChangedEventHandler extends BaseEventHandler<ModuleChangedEvent> {

    private static final String TAG = ModuleChangedEventHandler.class.getSimpleName();

    public void fireOnModuleChanged(String module)
    {
        Log.d(TAG, "fireOnModuleChanged " + module);
        for (ModuleChangedEvent event : eventListners)
            event.onModuleChanged(module);
    }
}
