package freed.viewer.gridview.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.troop.freedcam.BR;

public class VisibilityModel extends BaseObservable {

    private boolean visibility;

    @Bindable
    public boolean getVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
        notifyPropertyChanged(BR.visibility);
    }

}
