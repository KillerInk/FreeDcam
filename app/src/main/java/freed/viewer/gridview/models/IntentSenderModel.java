package freed.viewer.gridview.models;

import android.content.IntentSender;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.troop.freedcam.BR;

public class IntentSenderModel extends BaseObservable {
    private IntentSender intentSender;

    @Bindable
    public IntentSender getIntentSender() {
        return intentSender;
    }

    public void setIntentSender(IntentSender intentSender) {
        this.intentSender = intentSender;
        notifyPropertyChanged(BR.intentSender);
    }
}
