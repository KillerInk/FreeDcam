package freed.cam.ui.themesample.cameraui.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;


public class InfoOverlayModel extends BaseObservable
{
    private String batteryLvl;
    private String storageSpace;
    private String time;
    private String size;
    private String gps;
    private String format;

    public void setBatteryLvl(String batteryLvl) {
        this.batteryLvl = batteryLvl;
        notifyPropertyChanged(BR.batteryLvl);
    }

    @Bindable
    public String getBatteryLvl() {
        return batteryLvl;
    }

    public void setStorageSpace(String formatSize) {
        this.storageSpace = formatSize;
        notifyPropertyChanged(BR.storageSpace);
    }

    @Bindable
    public String getStorageSpace() {
        return storageSpace;
    }

    public void setTime(String time) {
        this.time = time;
        notifyPropertyChanged(BR.time);
    }

    @Bindable
    public String getTime() {
        return time;
    }

    public void setSize(String size) {
        this.size = size;
        notifyPropertyChanged(BR.size);
    }

    @Bindable
    public String getSize() {
        return size;
    }

    public void setGps(String gps) {
        this.gps = gps;
        notifyPropertyChanged(BR.gps);
    }

    @Bindable
    public String getGps() {
        return gps;
    }

    public void setFormat(String format) {
        this.format = format;;
        notifyPropertyChanged(BR.format);
    }

    @Bindable
    public String getFormat() {
        return format;
    }
}
