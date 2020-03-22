package freed.cam.events;

import freed.settings.SettingKeys;

public class FocusPositionChangedEvent extends ValueChangedEvent<String> {
    public FocusPositionChangedEvent(SettingKeys.Key key, String newValue, Class type) {
        super(key, newValue, type);
    }
}
