package freed.settings.mode;

import freed.cam.apis.sonyremote.sonystuff.XmlElement;

/**
 * Created by KillerInk on 04.01.2018.
 */

public interface SettingInterface {
    String getXmlString();
    void loadXmlNode(XmlElement node);
}
