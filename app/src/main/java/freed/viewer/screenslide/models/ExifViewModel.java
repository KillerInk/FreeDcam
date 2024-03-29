package freed.viewer.screenslide.models;

import android.graphics.Typeface;

import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import com.troop.freedcam.R;

import freed.FreedApplication;
import freed.viewer.gridview.models.VisibilityModel;

public class ExifViewModel extends VisibilityModel {

    private final ExifViewItemModel iso;
    private final ExifViewItemModel shutter;
    private final ExifViewItemModel focal;
    private final ExifViewItemModel fnumber;
    private final ExifViewItemModel filename;
    private final ExifViewItemModel image_size;
    private Typeface typeface;

    public ExifViewModel()
    {
        iso = new ExifViewItemModel();
        shutter = new ExifViewItemModel();
        focal = new ExifViewItemModel();
        fnumber = new ExifViewItemModel();
        filename = new ExifViewItemModel();
        image_size = new ExifViewItemModel();
        setTypeface(ResourcesCompat.getFont(FreedApplication.getContext(), R.font.freedcam));
    }

    public ExifViewItemModel getFilename() {
        return filename;
    }

    public ExifViewItemModel getFnumber() {
        return fnumber;
    }

    public ExifViewItemModel getFocal() {
        return focal;
    }

    public ExifViewItemModel getIso() {
        return iso;
    }

    public ExifViewItemModel getShutter() {
        return shutter;
    }

    public ExifViewItemModel getImage_size() {
        return image_size;
    }


    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
        notifyPropertyChanged(BR.typeface);
    }

    @Bindable
    public Typeface getTypeface() {
        return typeface;
    }
}
