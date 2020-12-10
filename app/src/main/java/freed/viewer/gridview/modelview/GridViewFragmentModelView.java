package freed.viewer.gridview.modelview;

import android.app.RecoverableSecurityException;
import android.os.Build;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.PopupMenu;

import androidx.lifecycle.ViewModel;

import com.troop.freedcam.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import freed.file.FileListController;
import freed.file.holder.BaseHolder;
import freed.file.holder.FileHolder;
import freed.file.holder.UriHolder;
import freed.image.ImageManager;
import freed.utils.FreeDPool;
import freed.utils.Log;
import freed.utils.StringUtils;
import freed.viewer.dngconvert.DngConvertingActivity;
import freed.viewer.gridview.enums.RequestModes;
import freed.viewer.gridview.enums.ViewStates;
import freed.viewer.gridview.models.ButtonDoAction;
import freed.viewer.gridview.models.ButtonFileTypeModel;
import freed.viewer.gridview.models.ButtonOptionsModel;
import freed.viewer.gridview.models.FilesHolderModel;
import freed.viewer.gridview.models.FilesSelectedModel;
import freed.viewer.gridview.models.FinishActivityModel;
import freed.viewer.gridview.models.GridImageViewModel;
import freed.viewer.gridview.models.IntentModel;
import freed.viewer.gridview.models.IntentSenderModel;
import freed.viewer.gridview.models.PopupMenuModel;
import freed.viewer.gridview.models.ViewStateModel;
import freed.viewer.gridview.models.VisibilityModel;
import freed.viewer.helper.BitmapHelper;
import freed.viewer.screenslide.views.ScreenSlideFragment;
import freed.viewer.stack.DngStackActivity;
import freed.viewer.stack.StackActivity;

public class GridViewFragmentModelView extends ViewModel
{
    private final String TAG = GridViewFragmentModelView.class.getSimpleName();
    private final ViewStateModel viewStateModel;
    private final FilesHolderModel filesHolderModel;
    private boolean isRootDir = true;
    private final List<BaseHolder> filesSelectedList = new ArrayList<>();
    private final List<UriHolder> urisToDelte = new ArrayList<>();
    private final ButtonFileTypeModel buttonFiletype;
    private final ButtonDoAction buttonDoAction;
    private final ButtonOptionsModel buttonOptions;

    public FileListController.FormatTypes formatsToShow = FileListController.FormatTypes.all;
    private FileListController.FormatTypes lastFormat = FileListController.FormatTypes.all;
    private RequestModes requestMode = RequestModes.none;
    private BaseHolder folderToShow;
    private final IntentModel intentModel;
    private final FinishActivityModel finishActivityModel;
    private final FinishActivityModel alterDialogModel;
    private final FilesSelectedModel filesSelectedModel;
    private ScreenSlideFragment.ButtonClick onGridItemClick;
    private PopupMenuModel popupMenuModel;

    private final IntentSenderModel intentSenderModel;

    public GridViewFragmentModelView()
    {
        viewStateModel = new ViewStateModel();
        filesHolderModel = new FilesHolderModel();
        buttonFiletype = new ButtonFileTypeModel(this);
        buttonDoAction = new ButtonDoAction();
        buttonOptions = new ButtonOptionsModel(onDeltedButtonClick,onStackClick,onRawToDngClick,onDngStackClick,this);
        if (isRootDir) {
            buttonOptions.setVisibility(false);
            buttonFiletype.setVisibility(false);
        }
        buttonFiletype.setText("ALL");
        intentModel = new IntentModel();
        finishActivityModel = new FinishActivityModel();
        alterDialogModel = new FinishActivityModel();
        filesSelectedModel = new FilesSelectedModel();
        intentSenderModel = new IntentSenderModel();
        popupMenuModel = new PopupMenuModel(buttonOptions);
    }

    public void setFileListController(FileListController fileListController)
    {
        filesHolderModel.setFileListController(fileListController);
    }

    public void setButtonClick(ScreenSlideFragment.ButtonClick onGridItemClick)
    {
        this.onGridItemClick = onGridItemClick;
    }

    public void setBitmapHelper(BitmapHelper bitmapHelper)
    {
        filesHolderModel.setBitmapHelper(bitmapHelper);
    }

    public List<GridImageViewModel> getGridImageViewModels()
    {
        return filesHolderModel.getGridImageViewModels();
    }

    public ViewStateModel getViewStateModel() {
        return viewStateModel;
    }

    public FilesHolderModel getFilesHolderModel() {
        return filesHolderModel;
    }

    public IntentModel getIntentModel() {
        return intentModel;
    }

    public FinishActivityModel getFinishActivityModel() {
        return finishActivityModel;
    }

    public FinishActivityModel getAlterDialogModel() {
        return alterDialogModel;
    }

    public ButtonDoAction getButtonDoAction() {
        return buttonDoAction;
    }

    public FilesSelectedModel getFilesSelectedModel() {
        return filesSelectedModel;
    }

    public ButtonFileTypeModel getButtonFiletype() {
        return buttonFiletype;
    }

    public ButtonOptionsModel getButtonOptions() {
        return buttonOptions;
    }

    public IntentSenderModel getIntentSenderModel() {
        return intentSenderModel;
    }

    public PopupMenuModel getPopupMenuModel() {
        return popupMenuModel;
    }

    public boolean isRootDir() {
        return isRootDir;
    }

    public void setViewMode(ViewStates viewState)
    {
        Log.d(TAG,"setViewMode:  isRootDir" + isRootDir);
        viewStateModel.setCurrentViewState(viewState);
        if (isRootDir)
        {
            buttonFiletype.setVisibility(false);
            buttonOptions.setVisibility(false);
            filesSelectedModel.setVisibility(false);
        }
        else
        {
            switch (viewState)
            {
                case normal:
                    if (formatsToShow == FileListController.FormatTypes.raw && lastFormat != FileListController.FormatTypes.raw) {
                        formatsToShow = lastFormat;
                        if (filesHolderModel.getFormatType() != formatsToShow)
                            filesHolderModel.setFormatType(formatsToShow);
                    }
                    //resetFilesSelected();
                    requestMode = RequestModes.none;
                    buttonFiletype.setVisibility(true);
                    buttonOptions.setVisibility(true);
                    buttonDoAction.setVisibility(false);
                    filesSelectedModel.setVisibility(false);
                    break;
                case selection:
                    resetFilesSelected();
                    filesSelectedModel.setVisibility(true);
                    updateFilesSelected();
                    switch (requestMode) {
                        case none:
                            buttonFiletype.setVisibility(true);
                            buttonOptions.setVisibility(true);
                            buttonDoAction.setVisibility(false);
                            buttonDoAction.setOnClickListener(null);
                            break;
                        case delete:
                            buttonFiletype.setVisibility(false);
                            buttonOptions.setVisibility(false);
                            buttonDoAction.setText("Delete");
                            buttonDoAction.setOnClickListener(onDeltedButtonClick);
                            buttonDoAction.setVisibility(true);
                            break;
                        case rawToDng:
                            lastFormat = formatsToShow;
                            formatsToShow = FileListController.FormatTypes.raw;
                            if (filesHolderModel.getFormatType() != formatsToShow)
                                filesHolderModel.setFormatType(formatsToShow);
                            buttonOptions.setVisibility(false);
                            buttonFiletype.setVisibility(false);
                            buttonDoAction.setText("RawToDng");
                            buttonDoAction.setOnClickListener(onRawToDngClick);
                            buttonDoAction.setVisibility(true);
                            break;
                        case stack:
                            lastFormat = formatsToShow;
                            formatsToShow = FileListController.FormatTypes.jpg;
                            if (filesHolderModel.getFormatType() != formatsToShow)
                                filesHolderModel.setFormatType(formatsToShow);
                            buttonOptions.setVisibility(false);
                            buttonFiletype.setVisibility(false);
                            buttonDoAction.setText("Stack");
                            buttonDoAction.setOnClickListener(onStackClick);
                            buttonDoAction.setVisibility(true);
                            break;
                        case dngstack:
                            lastFormat = formatsToShow;
                            formatsToShow = FileListController.FormatTypes.dng;
                            if (folderToShow == null)
                                folderToShow = filesHolderModel.getFiles().get(0);
                            if (filesHolderModel.getFormatType() != formatsToShow)
                                filesHolderModel.setFormatType(formatsToShow);
                            buttonOptions.setVisibility(false);
                            buttonFiletype.setVisibility(false);
                            buttonDoAction.setText("DngStack");
                            buttonDoAction.setOnClickListener(onDngStackClick);
                            buttonDoAction.setVisibility(true);
                            break;
                    }
                    break;
            }
        }
    }

    public final View.OnClickListener onStackClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (requestMode == RequestModes.none)
            {
                requestMode = RequestModes.stack;
                setViewMode(ViewStates.selection);
            }
            else if (requestMode == RequestModes.stack)
            {
                ArrayList<String> ar = new ArrayList<>();
                for (GridImageViewModel f : filesHolderModel.getGridImageViewModels()) {
                    if (f.getChecked() && f.getImagePath().getName().toLowerCase().endsWith(StringUtils.FileEnding.JPG))
                    {
                        if (f.getImagePath() instanceof FileHolder)
                            ar.add(((FileHolder)f.getImagePath()).getFile().getAbsolutePath());
                        else if (f.getImagePath() instanceof UriHolder)
                            ar.add(((UriHolder)f.getImagePath()).getMediaStoreUri().toString());
                    }

                }
                for (GridImageViewModel f : filesHolderModel.getGridImageViewModels()) {
                    f.setChecked(false);
                }
                setViewMode(ViewStates.normal);
                intentModel.setAr(ar);
                intentModel.setIntentClass(StackActivity.class);
            }
            else
            {
                requestMode = RequestModes.none;
                setViewMode(ViewStates.normal);
            }
        }
    };

    public final View.OnClickListener onDngStackClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (requestMode == RequestModes.none)
            {
                requestMode = RequestModes.dngstack;
                setViewMode(ViewStates.selection);
            }
            else if (requestMode == RequestModes.dngstack)
            {
                ArrayList<String> ar = new ArrayList<>();
                for (GridImageViewModel f : getFilesHolderModel().getGridImageViewModels()) {
                    if (f.getChecked() && f.getImagePath().getName().toLowerCase().endsWith(StringUtils.FileEnding.DNG))
                    {
                        if (f.getImagePath() instanceof FileHolder)
                            ar.add(((FileHolder)f.getImagePath()).getFile().getAbsolutePath());
                        else if (f.getImagePath() instanceof UriHolder)
                            ar.add(((UriHolder)f.getImagePath()).getMediaStoreUri().toString());
                    }

                }
                for (GridImageViewModel f : getFilesHolderModel().getGridImageViewModels()) {
                    f.setChecked(false);
                }
                setViewMode(ViewStates.normal);
                intentModel.setAr(ar);
                intentModel.setIntentClass(DngStackActivity.class);
            }
            else
            {
                requestMode = RequestModes.none;
                setViewMode(ViewStates.normal);
            }
        }
    };

    public final View.OnClickListener onGobBackClick = new View.OnClickListener() {
        @Override
        public void onClick(View view)
        {
            if (viewStateModel.getCurrentViewState() == ViewStates.normal)
            {
                if (getFilesHolderModel().getFiles() != null && getFilesHolderModel().getFiles().size() > 0
                        && getFilesHolderModel().getFiles().get(0) instanceof FileHolder)
                {
                    FileHolder fileHolder = (FileHolder) getFilesHolderModel().getFiles().get(0);
                    File topPath = fileHolder.getFile().getParentFile().getParentFile();
                    if (topPath.getName().equals("DCIM") && !isRootDir)
                    {
                        getFilesHolderModel().loadDefault();
                        isRootDir = true;
                        Log.d(TAG, "onGobBackClick dcim folder rootdir:" +isRootDir);
                        setViewMode(viewStateModel.getCurrentViewState());
                    }
                    else if (isRootDir)
                    {
                        finishActivityModel.setOb(null);
                    }
                    else
                    {
                        isRootDir = false;
                        Log.d(TAG, "onGobBackClick load folder rootdir:" +isRootDir);
                        filesHolderModel.loadDefault();
                        //viewerActivityInterface.LoadFolder(viewerActivityInterface.getFiles().get(0),formatsToShow);
                        setViewMode(viewStateModel.getCurrentViewState());
                    }
                }
                else if (filesHolderModel.getFiles().size() > 0 && filesHolderModel.getFiles().get(0) instanceof UriHolder) {
                    if (filesHolderModel.getFiles().get(0).IsFolder())
                        finishActivityModel.setOb(null);
                    else {
                        filesHolderModel.loadDefault();
                        isRootDir = true;
                        setViewMode(ViewStates.normal);
                    }
                }
                else
                {
                    filesHolderModel.loadDefault();
                    //viewerActivityInterface.LoadDCIMDirs();
                    Log.d(TAG, "onGobBackClick dcim folder rootdir:" +isRootDir);
                    isRootDir = true;

                    setViewMode(viewStateModel.getCurrentViewState());
                    if (filesHolderModel.getFiles().size() == 0)
                        finishActivityModel.setOb(null);
                }
            }
            else if (viewStateModel.getCurrentViewState() == ViewStates.selection)
            {
                for (int i = 0; i< filesHolderModel.getGridImageViewModels().size(); i++)
                {
                    GridImageViewModel f = filesHolderModel.getGridImageViewModels().get(i);
                    f.setChecked(false);
                }
                setViewMode(ViewStates.normal);
            }
        }
    };

    public final View.OnClickListener onRawToDngClick = new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            if (requestMode == RequestModes.none)
            {
                requestMode = RequestModes.rawToDng;
                setViewMode(ViewStates.selection);
            }
            else if (requestMode == RequestModes.rawToDng)
            {
                ArrayList<String> ar = new ArrayList<>();
                for (GridImageViewModel f : getFilesHolderModel().getGridImageViewModels()) {
                    if (f.getChecked() &&
                            (f.getImagePath().getName().toLowerCase().endsWith(StringUtils.FileEnding.RAW) ||f.getImagePath().getName().toLowerCase().endsWith(StringUtils.FileEnding.BAYER))) {
                        if (f.getImagePath() instanceof FileHolder)
                            ar.add(((FileHolder) f.getImagePath()).getFile().getAbsolutePath());
                        else if (f.getImagePath() instanceof UriHolder)
                            ar.add(((UriHolder) f.getImagePath()).getMediaStoreUri().toString());
                    }

                }
                for (GridImageViewModel f : getFilesHolderModel().getGridImageViewModels()) {
                    f.setChecked(false);
                }
                setViewMode(ViewStates.normal);
                intentModel.setAr(ar);
                intentModel.setIntentClass(DngConvertingActivity.class);
            }
            else
            {
                requestMode = RequestModes.none;
                setViewMode(ViewStates.normal);
            }
        }
    };

    public final View.OnClickListener onDeltedButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            if (requestMode == RequestModes.none)
            {
                requestMode = RequestModes.delete;
                setViewMode(ViewStates.selection);
            }
            else if (requestMode == RequestModes.delete)
            {
                //check if files are selected
                boolean hasfilesSelected = false;
                for (GridImageViewModel f : getFilesHolderModel().getGridImageViewModels()) {
                    if (f.getChecked()) {
                        hasfilesSelected = true;
                        break;
                    }
                }
                //if no files selected skip dialog
                if (!hasfilesSelected)
                    return;
                //else show dialog
                alterDialogModel.setOb(null);
                setViewMode(ViewStates.normal);
            }
            else
            {
                requestMode = RequestModes.none;
                setViewMode(ViewStates.normal);
            }
        }
    };

    public void deleteFiles()
    {
        ImageManager.cancelImageLoadTasks();
        urisToDelte.clear();
        if (filesSelectedList.get(0).getHolderType() == FileHolder.class)
        {
            filesHolderModel.getFileListController().DeleteFiles(filesSelectedList);
        }
        else
        {
            for (BaseHolder baseHolder : filesSelectedList)
                urisToDelte.add((UriHolder) baseHolder);
            deleteUriFile();
        }
    }

    public void deleteNextFile()
    {
        deleteUriFile();
    }

    private void deleteUriFile()
    {
        if (urisToDelte.size() > 0)
            try {

                filesHolderModel.getFileListController().DeleteFile(urisToDelte.get(0));
                urisToDelte.remove(0);
                deleteUriFile();
            }
            catch(SecurityException ex){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (ex instanceof RecoverableSecurityException)
                    {
                        RecoverableSecurityException rex = (RecoverableSecurityException)ex;
                        intentSenderModel.setIntentSender(rex.getUserAction().getActionIntent().getIntentSender());
                    }
                }
            }
    }
    private void resetFilesSelected()
    {
        for (int i = 0; i< filesHolderModel.getGridImageViewModels().size(); i++)
        {
            GridImageViewModel f = getFilesHolderModel().getGridImageViewModels().get(i);
            f.setChecked(false);
        }
        filesSelectedList.clear();
    }

    private void updateFilesSelected()
    {
        filesSelectedModel.setFilesSelectedCount(filesSelectedList.size());
    }

    public void setFormatsToShow(FileListController.FormatTypes formatsToShow)
    {
        this.formatsToShow = formatsToShow;
        if (filesHolderModel.getFormatType() != formatsToShow)
            filesHolderModel.setFormatType(formatsToShow);
    }

    public AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (viewStateModel.getCurrentViewState())
            {
                case normal:
                    //handel normal griditem click to open screenslide when its not a folder
                    if (!filesHolderModel.getFiles().get(position).IsFolder())
                    {
                        onGridItemClick.onButtonClick(position, view);
                    }
                    else //handel folder click
                    {
                        //hold the current folder to show if a format is empty
                        folderToShow = filesHolderModel.getFiles().get(position);
                        filesHolderModel.LoadFolder(folderToShow,formatsToShow);
                        isRootDir = false;
                        setViewMode(viewStateModel.getCurrentViewState());

                    }
                    break;
                case selection:
                    if (filesHolderModel.getGridImageViewModels().get(position).getChecked()) {
                        filesHolderModel.getGridImageViewModels().get(position).setChecked(false);
                        filesSelectedList.remove(filesHolderModel.getFiles().get(position));
                    } else {
                        filesHolderModel.getGridImageViewModels().get(position).setChecked(true);
                        filesSelectedList.add(filesHolderModel.getGridImageViewModels().get(position).getImagePath());
                    }
                    updateFilesSelected();
                    break;
            }
        }
    };


    public void refreshCurrentFolder()
    {
        filesHolderModel.LoadFolder(folderToShow,formatsToShow);
    }
}
