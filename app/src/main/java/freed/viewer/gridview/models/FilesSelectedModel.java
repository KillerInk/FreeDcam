package freed.viewer.gridview.models;

import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;


public class FilesSelectedModel extends VisibilityModel {

    private int filesSelectedCount = 0;
    private String filesSelectedString;

    public void setFilesSelectedCount(int filesSelectedCount) {
        this.filesSelectedCount = filesSelectedCount;
        setFilesSelectedString(String.valueOf(filesSelectedCount));
    }


    public int getFilesSelectedCount() {
        return filesSelectedCount;
    }

    public void setFilesSelectedString(String filesSelectedString) {
        this.filesSelectedString = filesSelectedString;
        notifyPropertyChanged(BR.filesSelectedString);
    }

    @Bindable
    public String getFilesSelectedString() {
        return filesSelectedString;
    }
}
