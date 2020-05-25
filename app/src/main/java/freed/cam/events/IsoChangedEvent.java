package freed.cam.events;

import freed.settings.SettingKeys;

public class IsoChangedEvent {

    public final String  newValue;
    public final SettingKeys.Key key;
    public final Class<String> type;

    public IsoChangedEvent(SettingKeys.Key key, String newValue, Class type) {
        this.key =key;
        this.newValue = newValue;
        this.type = type;
    }
}
