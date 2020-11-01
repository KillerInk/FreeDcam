package com.troop.freedcam.cameraui.models;

import android.view.View;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.troop.freedcam.cameraui.BR;

public class VisibilityEnableModel extends BaseObservable {
    private int visibility = View.VISIBLE;
    private boolean enabled = true;

    public void setVisibility(int visibility) {
        this.visibility = visibility;
        notifyPropertyChanged(BR.visibility);
    }

    @Bindable
    public int getVisibility() {
        return visibility;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        notifyPropertyChanged(BR.enabled);
    }

    @Bindable
    public boolean getEnabled() {
        return enabled;
    }
}
