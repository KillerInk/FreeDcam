package freed.viewer.gridview.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.troop.freedcam.BR;

import java.util.List;

import freed.file.FileListController;
import freed.file.holder.BaseHolder;

public class FilesHolderModel extends BaseObservable {

    private List<BaseHolder> files;
    private FileListController fileListController;

    public FilesHolderModel(FileListController fileListController)
    {
        this.fileListController = fileListController;
    }

    public void setFiles(List<BaseHolder> files) {
        this.files = files;
        notifyPropertyChanged(BR.files);
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
}
