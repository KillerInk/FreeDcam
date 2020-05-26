package freed.cam.events;

import freed.settings.SettingKeys;

public class ShutterSpeedChangedEvent{
    public final String  newValue;
    public final SettingKeys.Key key;
    public final Class<String> type;

    public ShutterSpeedChangedEvent(SettingKeys.Key key, String newValue, Class type) {
        this.key =key;
        this.newValue = newValue;
        this.type = type;
    }
}
