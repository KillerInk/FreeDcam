package freed.viewer.screenslide.models;

import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import freed.viewer.gridview.models.VisibilityModel;


public class ExifViewItemModel extends VisibilityModel {
    private String text;

    public void setText(String text) {
        this.text = text;
        notifyPropertyChanged(BR.text);
    }

    @Bindable
    public String getText() {
        return text;
    }


}
