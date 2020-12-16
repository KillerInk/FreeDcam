package freed.cam.ui.videoprofileeditor.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import java.util.List;

public abstract class ButtonModel extends BaseObservable implements PopupModel.PopUpItemClick {

    private PopupModel popupModel;
    private String txt;

    public ButtonModel(PopupModel popupModel)
    {
        this.popupModel = popupModel;
    }

    public void setTxt(String txt) {
        this.txt = txt;
        notifyPropertyChanged(BR.txt);
    }

    @Bindable
    public String getTxt() {
        return txt;
    }

    @Override
    public void onPopupItemClick(String item) {
        setTxt(item);
    }

    public void onClick()
    {
        popupModel.setPopUpItemClick(this);
    }
}
