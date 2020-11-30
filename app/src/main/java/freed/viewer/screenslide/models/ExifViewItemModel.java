package freed.viewer.screenslide.models;

import android.graphics.Typeface;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.troop.freedcam.BR;

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
