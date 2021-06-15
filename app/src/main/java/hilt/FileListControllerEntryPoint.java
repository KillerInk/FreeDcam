package hilt;

import dagger.hilt.EntryPoint;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import freed.file.FileListController;

@EntryPoint
@InstallIn(SingletonComponent.class)
public interface FileListControllerEntryPoint {
    FileListController fileListController();
}
