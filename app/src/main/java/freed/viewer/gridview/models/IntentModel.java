package freed.viewer.gridview.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import java.util.ArrayList;

public class IntentModel extends BaseObservable {

    private Class intentClass;
    private ArrayList<String> ar;

    public void setIntentClass(Class intentClass) {
        this.intentClass = intentClass;
        notifyPropertyChanged(BR.intentClass);
    }

    @Bindable
    public Class getIntentClass() {
        return intentClass;
    }

    public void setAr(ArrayList<String> ar) {
        this.ar = ar;
    }

    public ArrayList<String> getAr() {
        return ar;
    }
}
