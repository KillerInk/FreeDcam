package freed.viewer.gridview.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.troop.freedcam.BR;

import java.util.ArrayList;
import java.util.List;

import freed.file.FileListController;
import freed.file.holder.BaseHolder;
import freed.utils.Log;
import freed.viewer.helper.BitmapHelper;

public class FilesHolderModel extends BaseObservable implements FileListController.NotifyFilesChanged {

    private final String TAG = FilesHolderModel.class.getSimpleName();
    private List<BaseHolder> files;
    private FileListController fileListController;
    private List<GridImageViewModel> gridImageViewModels;
    private BitmapHelper bitmapHelper;

    public FilesHolderModel()
    {
    }

    public void setFiles(List<BaseHolder> files) {
        this.files = files;
        gridImageViewModels = new ArrayList<>();
        for (BaseHolder baseHolder : files)
        {
            gridImageViewModels.add(new GridImageViewModel(bitmapHelper,baseHolder));
        }
        notifyPropertyChanged(BR.files);
    }

    public List<GridImageViewModel> getGridImageViewModels()
    {
        return gridImageViewModels;
    }

    @Bindable
    public List<BaseHolder> getFiles() {
        return files;
    }

    public void LoadFolder(BaseHolder fileHolder, FileListController.FormatTypes types)
    {
        fileListController.LoadFolder(fileHolder,types);
        setFiles(fileListController.getFiles());
    }

    public void loadDefault()
    {
        fileListController.loadDefaultFiles();
        setFiles(fileListController.getFiles());
    }

    public void deleteFiles(List<BaseHolder> baseHolders)
    {
        fileListController.DeleteFiles(baseHolders);
    }

    public void deleteFile(BaseHolder baseHolders)
    {
        fileListController.DeleteFile(baseHolders);
    }

    public void setFileListController(FileListController fileListController) {
        this.fileListController = fileListController;
        if (fileListController.getFiles() != null && fileListController.getFiles().size() > 0)
            setFiles(fileListController.getFiles());
        fileListController.setNotifyFilesChanged(this::onFilesChanged);
    }

    @Override
    public void onFilesChanged() {
        Log.d(TAG,"onFilesChanged");
        if (fileListController.getFiles() != null && fileListController.getFiles().size() > 0)
            setFiles(fileListController.getFiles());
    }

    public void setBitmapHelper(BitmapHelper bitmapHelper)
    {
        this.bitmapHelper = bitmapHelper;
    }
}
