package freed.viewer.gridview.models;

import android.view.View;

import androidx.databinding.Bindable;

import com.troop.freedcam.BR;

public class PopupMenuModel extends VisibilityModel
{
    private String[] strings;
    private Popup buttonOptionsModel;

    public PopupMenuModel(ButtonOptionsModel buttonOptionsModel)
    {
        setVisibility(false);
        this.buttonOptionsModel = buttonOptionsModel;
    }

    public void setButtonOptionsModel(Popup buttonOptionsModel) {
        this.buttonOptionsModel = buttonOptionsModel;
    }

    public void setStrings(String[] strings) {
        this.strings = strings;
        notifyPropertyChanged(BR.strings);
        if (strings != null && strings.length > 0)
            setVisibility(true);
        notifyChange();
    }

    @Bindable
    public String[] getStrings() {
        return strings;
    }

    public View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (buttonOptionsModel != null)
                buttonOptionsModel.getOnPopupChildClickListner().onClick(v);
            setVisibility(false);
        }
    };
}
