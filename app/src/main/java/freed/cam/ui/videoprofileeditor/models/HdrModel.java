package freed.cam.ui.videoprofileeditor.models;

import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import java.util.ArrayList;
import java.util.List;

import freed.cam.ui.videoprofileeditor.enums.HdrModes;

public class HdrModel extends ButtonModel {

    private boolean visibility = false;
    public HdrModel(PopupModel popupModel) {
        super(popupModel);
    }

    @Override
    public List<String> getStrings() {
        List<String> strings = new ArrayList<>();
        strings.add(HdrModes.off.toString());
        strings.add(HdrModes.hlg.toString());
        strings.add(HdrModes.pq.toString());
        return strings;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
        notifyPropertyChanged(BR.visibility);
    }

    @Bindable
    public boolean getVisibility() {
        return visibility;
    }
}
