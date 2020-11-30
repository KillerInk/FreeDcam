package freed.viewer.screenslide.models;

import android.view.View;

public class InfoButtonModel extends ButtonModel {

    private boolean showExifInfo;

    private ExifViewModel exifViewModel;

    public InfoButtonModel(ExifViewModel exifViewModel)
    {
        this.exifViewModel = exifViewModel;
    }

    @Override
    public void onClick(View v) {
        if (showExifInfo)
        {
            showExifInfo = false;
        }
        else
        {
            showExifInfo = true;
        }
        exifViewModel.setVisibility(showExifInfo);
    }

    public boolean getShowExifInfo() {
        return showExifInfo;
    }
}
