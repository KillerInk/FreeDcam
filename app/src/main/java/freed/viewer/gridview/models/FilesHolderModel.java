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
    private List<GridImageViewModel> visibleGridImageViewModels;
    private BitmapHelper bitmapHelper;
    private FileListController.FormatTypes formatType = FileListController.FormatTypes.all;

    public FilesHolderModel()
    {
        gridImageViewModels = new ArrayList<>();
        visibleGridImageViewModels = new ArrayList<>();
    }

    public synchronized void setFiles(List<BaseHolder> files) {
        this.files = files;
        gridImageViewModels.clear();
        visibleGridImageViewModels.clear();
        notifyPropertyChanged(BR.files);
        for (BaseHolder baseHolder : files)
        {
            GridImageViewModel model =new GridImageViewModel(bitmapHelper,baseHolder);
            gridImageViewModels.add(model);
            visibleGridImageViewModels.add(model);
        }
        notifyPropertyChanged(BR.files);
    }

    public void setFormatType(FileListController.FormatTypes formatType)
    {
        this.formatType = formatType;
        visibleGridImageViewModels.clear();
        if (formatType != FileListController.FormatTypes.all) {
            for (int i = 0; i < gridImageViewModels.size(); i++) {
                if (gridImageViewModels.get(i).getImagePath().getFileformat() == formatType)
                    visibleGridImageViewModels.add(gridImageViewModels.get(i));
            }
        }
        else {
            for (int i = 0; i < gridImageViewModels.size(); i++) {
                visibleGridImageViewModels.add(gridImageViewModels.get(i));
            }
        }
        notifyPropertyChanged(BR.files);
        notifyPropertyChanged(BR.formatType);
    }

    @Bindable
    public FileListController.FormatTypes getFormatType() {
        return formatType;
    }

    public List<GridImageViewModel> getGridImageViewModels()
    {
        return visibleGridImageViewModels;
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
        fileListController.setNotifyFilesChanged(this);
    }

    public FileListController getFileListController() {
        return fileListController;
    }

    @Override
    public void onFilesChanged() {
        Log.d(TAG,"onFilesChanged");
        if (fileListController.getFiles() != null && fileListController.getFiles().size() > 0)
            setFiles(fileListController.getFiles());
    }

    @Override
    public void onFileDeleted(int id) {
        Log.d(TAG,"onFileDeleted " +id);
        if (id < gridImageViewModels.size())
            gridImageViewModels.remove(id);
        notifyPropertyChanged(BR.files);
    }

    public void setBitmapHelper(BitmapHelper bitmapHelper)
    {
        this.bitmapHelper = bitmapHelper;
    }
}
