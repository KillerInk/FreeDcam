package freed.viewer.screenslide.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import freed.file.holder.BaseHolder;
import freed.viewer.helper.BitmapHelper;
import freed.viewer.screenslide.BitmapLoader;

public class ImageFragmentModel extends BaseObservable {
    private boolean progressBarVisible = false;
    private BaseHolder baseHolder;
    private BitmapHelper bitmapHelper;

    public BitmapLoader bitmapLoader;
    private int[] histodata;

    public ImageFragmentModel(BitmapHelper bitmapHelper, BaseHolder baseHolder) {
        this.bitmapHelper = bitmapHelper;
        this.baseHolder = baseHolder;
    }

    public BitmapHelper getBitmapHelper() {
        return bitmapHelper;
    }

    @Bindable
    public boolean getProgressBarVisible() {
        return progressBarVisible;
    }

    public void setProgressBarVisible(boolean progressBarVisible) {
        this.progressBarVisible = progressBarVisible;
        notifyPropertyChanged(BR.progressBarVisible);
    }

    @Bindable
    public BaseHolder getBaseHolder() {
        return baseHolder;
    }

    public void setBaseHolder(BaseHolder baseHolder) {
        this.baseHolder = baseHolder;
        notifyPropertyChanged(BR.baseHolder);
    }

    @Bindable
    public int[] getHistodata() {
        return histodata;
    }


    public void setHistodata(int[] histodata) {
        this.histodata = histodata;
        notifyPropertyChanged(BR.histodata);
    }
}
