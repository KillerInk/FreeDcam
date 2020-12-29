package freed.cam.ui.videoprofileeditor.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.troop.freedcam.BR;

import java.util.List;

public class PopupModel extends BaseObservable {

    public interface PopUpItemClick
    {
        void onPopupItemClick(String item);
        List<String> getStrings();
    }

    private PopUpItemClick popUpItemClick;

    public void setPopUpItemClick(PopUpItemClick popUpItemClick) {
        this.popUpItemClick = popUpItemClick;
        notifyPropertyChanged(BR.popUpItemClick);
    }

    @Bindable
    public PopUpItemClick getPopUpItemClick() {
        return popUpItemClick;
    }
}
