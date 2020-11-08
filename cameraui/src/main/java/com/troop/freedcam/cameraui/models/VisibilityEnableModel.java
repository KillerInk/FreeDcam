package com.troop.freedcam.cameraui.models;

import android.view.View;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.troop.freedcam.camera.basecamera.parameters.AbstractParameter;
import com.troop.freedcam.camera.basecamera.parameters.ParameterInterface;
import com.troop.freedcam.cameraui.BR;

public class VisibilityEnableModel extends BaseObservable implements ParameterInterface.ViewStateEvent {
    private int visibility = View.GONE;
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

    protected void setViewState(AbstractParameter.ViewState parameterInterface) {
        switch (parameterInterface)
        {
            case Hidden:
                setVisibility(View.GONE);
                break;
            case Visible:
                setVisibility(View.VISIBLE);
                break;
            case Enabled:
                if (getVisibility() == View.GONE)
                    setVisibility(View.VISIBLE);
                setEnabled(true);
                break;
            case Disabled:
                if (getVisibility() == View.GONE)
                    setVisibility(View.VISIBLE);
                setEnabled(false);
        }
    }

    @Override
    public void onViewStateChanged(AbstractParameter.ViewState viewState) {
        setViewState(viewState);
    }
}
