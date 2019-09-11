package freed.cam.events;

public class ModuleHasChangedEvent {
    public final String NewModuleName;

    public ModuleHasChangedEvent(String newModuleName)
    {
        this.NewModuleName = newModuleName;
    }
}
