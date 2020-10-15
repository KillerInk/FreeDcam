package freed.settings.mode;

import com.troop.freedcam.utils.XmlElement;

/**
 * Created by KillerInk on 04.01.2018.
 */

public interface SettingInterface {
    String getXmlString();
    void loadXmlNode(XmlElement node);
}
