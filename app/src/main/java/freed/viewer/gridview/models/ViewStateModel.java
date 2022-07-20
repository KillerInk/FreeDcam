package freed.viewer.gridview.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import freed.viewer.gridview.enums.ViewStates;

public class ViewStateModel extends BaseObservable {
    private ViewStates currentViewState = ViewStates.normal;

    @Bindable
    public ViewStates getCurrentViewState() {
        return currentViewState;
    }

    public void setCurrentViewState(ViewStates currentViewState) {
        this.currentViewState = currentViewState;
        notifyPropertyChanged(BR.currentViewState);
    }
}
