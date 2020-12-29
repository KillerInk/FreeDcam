package freed.viewer.gridview.models;

import android.view.View;

import androidx.databinding.Bindable;

import com.troop.freedcam.BR;

public class ButtonDoAction extends VisibilityModel {
    private View.OnClickListener onClickListener;
    private String text;

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        notifyPropertyChanged(BR.onClickListener);
    }

    @Bindable
    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    @Bindable
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        notifyPropertyChanged(BR.text);
    }
}
