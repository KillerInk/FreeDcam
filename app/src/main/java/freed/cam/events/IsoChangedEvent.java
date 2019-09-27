package freed.cam.events;

import freed.settings.SettingKeys;

public class IsoChangedEvent extends ValueChangedEvent<String> {
    public IsoChangedEvent(SettingKeys.Key key, String newValue, Class type) {
        super(key, newValue, type);
    }
}
