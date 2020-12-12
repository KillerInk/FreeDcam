package freed.viewer.gridview.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.troop.freedcam.BR;

import freed.file.holder.BaseHolder;
import freed.viewer.gridview.BitmapLoadRunnable;
import freed.viewer.gridview.enums.ViewStates;
import freed.viewer.gridview.views.GridViewFragment;
import freed.viewer.helper.BitmapHelper;

public class GridImageViewModel extends BaseObservable
{
    private BitmapHelper bitmapHelper;
    private BaseHolder imagePath;
    private String filending;
    private String foldername;
    private boolean isExternalSD;
    private boolean isChecked;
    private boolean isCheckVisible;
    private boolean isProgressBarVisible = false;
    public BitmapLoadRunnable bitmapLoadRunnable;

    public GridImageViewModel(BitmapHelper bitmapHelper, BaseHolder imagePath)
    {
        this.bitmapHelper = bitmapHelper;
        this.imagePath = imagePath;
        if (!imagePath.IsFolder()) {
            setFilending(imagePath.getName().substring(imagePath.getName().length() - 3));
            setFoldername("");
        }
        else {
            setFilending("");
            setFoldername(imagePath.getName());
        }
        setExternalSD(imagePath.isExternalSD());
        setProgressBarVisible(false);
    }

    public void setFilending(String filending)
    {
        this.filending = filending;
        notifyPropertyChanged(BR.filending);
    }

    @Bindable
    public String getFilending() {
        return filending;
    }

    @Bindable
    public String getFoldername() {
        return foldername;
    }

    public void setFoldername(String foldername) {
        this.foldername = foldername;
        notifyPropertyChanged(BR.foldername);
    }

    @Bindable
    public boolean getExternalSD() {
        return isExternalSD;
    }

    public void setExternalSD(boolean externalSD) {
        isExternalSD = externalSD;
        notifyPropertyChanged(BR.externalSD);
    }

    @Bindable
    public boolean getChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
        notifyPropertyChanged(BR.checked);
    }

    @Bindable
    public boolean getCheckVisible() {
        return isCheckVisible;
    }

    public void setCheckVisible(boolean checkVisible) {
        isCheckVisible = checkVisible;
        notifyPropertyChanged(BR.checkVisible);
    }

    @Bindable
    public boolean getProgressBarVisible() {
        return isProgressBarVisible;
    }

    public void setProgressBarVisible(boolean progressBarVisible) {
        isProgressBarVisible = progressBarVisible;
        notifyPropertyChanged(BR.progressBarVisible);
    }

    public BaseHolder getImagePath() {
        return imagePath;
    }

    public BitmapHelper getBitmapHelper() {
        return bitmapHelper;
    }

    public void setViewState(ViewStates state)
    {
        switch (state)
        {
            case normal:
                setCheckVisible(false);
                setChecked(false);
                break;
            case selection:
                setCheckVisible(true);
                if (getChecked())
                {
                    setChecked(true);
                }
                else
                    setChecked(false);
        }
    }

}
