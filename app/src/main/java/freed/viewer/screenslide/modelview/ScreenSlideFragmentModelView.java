package freed.viewer.screenslide.modelview;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import freed.file.FileListController;
import freed.file.holder.BaseHolder;
import freed.image.ImageManager;
import freed.utils.StringUtils;
import freed.viewer.gridview.models.VisibilityModel;
import freed.viewer.helper.BitmapHelper;
import freed.viewer.screenslide.ExifLoader;
import freed.viewer.screenslide.models.ButtonModel;
import freed.viewer.screenslide.models.ExifViewModel;
import freed.viewer.screenslide.models.InfoButtonModel;
import freed.viewer.screenslide.models.ScreenSlideFilesHolderModel;
import freed.viewer.screenslide.views.ScreenSlideFragment;

@HiltViewModel
public class ScreenSlideFragmentModelView extends ViewModel {

    private final ScreenSlideFilesHolderModel filesHolderModel;
    private BaseHolder folder_to_show;
    private final ExifViewModel exifViewModel;
    private final InfoButtonModel infoButtonModel;
    private final VisibilityModel topBar;
    private final VisibilityModel bottomBar;
    private final VisibilityModel histogram;
    private final ButtonModel deleteButton;
    private final ButtonModel playButton;
    private final FileListController fileListController;
    private final ImageManager imageManager;

    @Inject
    public ScreenSlideFragmentModelView(FileListController fileListController,BitmapHelper bitmapHelper, ImageManager imageManager)
    {
        this.fileListController = fileListController;
        this.imageManager = imageManager;
        filesHolderModel = new ScreenSlideFilesHolderModel();
        filesHolderModel.setBitmapHelper(bitmapHelper);
        filesHolderModel.setFileListController(fileListController);
        exifViewModel = new ExifViewModel();
        infoButtonModel = new InfoButtonModel(exifViewModel);
        topBar = new VisibilityModel();
        bottomBar = new VisibilityModel();
        histogram = new VisibilityModel();
        deleteButton = new ButtonModel();
        playButton = new ButtonModel();
    }

    public ScreenSlideFilesHolderModel getFilesHolderModel() {
        return filesHolderModel;
    }


    public ExifViewModel getExifViewModel() {
        return exifViewModel;
    }

    public InfoButtonModel getInfoButtonModel() {
        return infoButtonModel;
    }

    public BaseHolder getFolder_to_show() {
        return folder_to_show;
    }

    public ButtonModel getDeleteButton() {
        return deleteButton;
    }

    public ButtonModel getPlayButton() {
        return playButton;
    }

    public VisibilityModel getBottomBar() {
        return bottomBar;
    }

    public VisibilityModel getHistogram() {
        return histogram;
    }

    public VisibilityModel getTopBar() {
        return topBar;
    }

    //toggle ui items visibility when a single click from the ImageFragment happen
    private final ScreenSlideFragment.FragmentClickClistner fragmentclickListner = new ScreenSlideFragment.FragmentClickClistner() {
        @Override
        public void onFragmentClick(Fragment v) {
            if (!topBar.getVisibility()) {
                topBar.setVisibility(true);
                bottomBar.setVisibility(true);
                histogram.setVisibility(true);
                if (infoButtonModel.getShowExifInfo())
                    exifViewModel.setVisibility(true);
            }
            else {
                topBar.setVisibility(false);
                bottomBar.setVisibility(false);
                histogram.setVisibility(false);
                exifViewModel.setVisibility(false);
            }
        }
    };

    public ScreenSlideFragment.FragmentClickClistner getFragmentclickListner() {
        return fragmentclickListner;
    }

    public void updateUi(BaseHolder file)
    {
        this.folder_to_show = file;
        if (file != null && file.getName() != null)
        {
            exifViewModel.getFilename().setText(file.getName());
            deleteButton.setVisibility(true);
            infoButtonModel.setVisibility(true);
            if (file.getName().toLowerCase().endsWith(StringUtils.FileEnding.JPG) || file.getName().toLowerCase().endsWith(StringUtils.FileEnding.JPS)) {
                processExif(file);
                if (infoButtonModel.getShowExifInfo())
                    exifViewModel.setVisibility(true);
                playButton.setVisibility(true);
            }
            if (file.getName().toLowerCase().endsWith(StringUtils.FileEnding.MP4)) {
                exifViewModel.setVisibility(false);
                playButton.setVisibility(true);
            }
            if (file.getName().toLowerCase().endsWith(StringUtils.FileEnding.DNG)) {
                processExif(file);
                if (infoButtonModel.getShowExifInfo())
                    exifViewModel.setVisibility(true);
                playButton.setVisibility(true);
            }
            if (file.getName().toLowerCase().endsWith(StringUtils.FileEnding.RAW) || file.getName().toLowerCase().endsWith(StringUtils.FileEnding.BAYER)) {
                if (infoButtonModel.getShowExifInfo())
                    exifViewModel.setVisibility(true);
                playButton.setVisibility(false);
            }

        }
        else
        {
            exifViewModel.getFilename().setText("No Files");
            infoButtonModel.setVisibility(false);
            exifViewModel.setVisibility(false);
            histogram.setVisibility(false);
            deleteButton.setVisibility(false);
            playButton.setVisibility(false);
        }
    }

    private void processExif(final BaseHolder file)
    {
        imageManager.putImageLoadTask(new ExifLoader(file,exifViewModel));
    }
}
