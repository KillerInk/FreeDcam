package freed.cam.events;

import freed.settings.SettingKeys;

public class ShutterSpeedChangedEvent extends ValueChangedEvent<String> {
    public ShutterSpeedChangedEvent(SettingKeys.Key key, String newValue, Class type) {
        super(key, newValue, type);
    }
}
