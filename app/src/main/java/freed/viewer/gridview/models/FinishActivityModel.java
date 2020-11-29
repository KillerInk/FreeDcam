package freed.viewer.gridview.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.troop.freedcam.BR;

public class FinishActivityModel extends BaseObservable {
    private Object ob;

    public void setOb(Object ob) {
        this.ob = ob;
        notifyPropertyChanged(BR.ob);
    }

    @Bindable
    public Object getOb() {
        return ob;
    }
}
