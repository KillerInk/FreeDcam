package freed.viewer.screenslide.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.troop.freedcam.BR;

import java.util.ArrayList;
import java.util.List;

import freed.file.FileListController;
import freed.file.holder.BaseHolder;
import freed.utils.Log;
import freed.viewer.gridview.models.GridImageViewModel;
import freed.viewer.helper.BitmapHelper;

public class ScreenSlideFilesHolderModel extends BaseObservable implements FileListController.NotifyFilesChanged {

    private static final String TAG = ScreenSlideFilesHolderModel.class.getSimpleName();
    private FileListController fileListController;
    private BitmapHelper bitmapHelper;
    private List<BaseHolder> files;
    private List<ImageFragmentModel> imageFragmentModels;

    private FileListController.FormatTypes formatTypes;

    public void setFileListController(FileListController fileListController)
    {
        this.fileListController = fileListController;
        this.fileListController.setNotifyFilesChanged(this);
        if (fileListController.getFiles() != null && fileListController.getFiles().size() > 0)
            setFiles(fileListController.getFiles());
    }

    public void setFormatTypes(FileListController.FormatTypes formatTypes) {
        this.formatTypes = formatTypes;
        imageFragmentModels = new ArrayList<>();
        if (formatTypes != FileListController.FormatTypes.all)
        {
            for (BaseHolder baseHolder : files)
            {
                if (baseHolder.getFileformat() == formatTypes)
                    imageFragmentModels.add(new ImageFragmentModel(bitmapHelper,baseHolder));
            }
        }
        else
            for (BaseHolder baseHolder : files)
            {
                imageFragmentModels.add(new ImageFragmentModel(bitmapHelper,baseHolder));
            }
        notifyPropertyChanged(BR.files);
    }

    public void setBitmapHelper(BitmapHelper bitmapHelper) {
        this.bitmapHelper = bitmapHelper;
    }

    public void setFiles(List<BaseHolder> files) {
        this.files = files;
        imageFragmentModels = new ArrayList<>();
        for (BaseHolder baseHolder : files)
        {
            imageFragmentModels.add(new ImageFragmentModel(bitmapHelper,baseHolder));
        }
        notifyPropertyChanged(BR.files);
    }

    @Bindable
    public List<BaseHolder> getFiles() {
        return files;
    }

    public List<ImageFragmentModel> getImageFragmentModels() {
        return imageFragmentModels;
    }

    @Override
    public void onFilesChanged() {
        if (fileListController.getFiles() != null && fileListController.getFiles().size() > 0)
            setFiles(fileListController.getFiles());
    }

    @Override
    public void onFileDeleted(int id) {
        Log.d(TAG,"onFileDeleted " +id);
        if (id < imageFragmentModels.size())
            imageFragmentModels.remove(id);
        notifyPropertyChanged(BR.files);
    }

    public FileListController getFileListController() {
        return fileListController;
    }
}
