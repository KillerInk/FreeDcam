package com.troop.freedcam.gallery.viewmodel;

import android.app.Application;

import androidx.lifecycle.ViewModel;

import com.troop.freedcam.file.FileListController;
import com.troop.freedcam.file.holder.BaseHolder;
import com.troop.freedcam.gallery.R;
import com.troop.freedcam.gallery.helper.BitmapHelper;

import java.util.List;


public class GalleryViewModel extends ViewModel
{
    private BitmapHelper bitmapHelper;
    private FileListController fileListController;

    public void create(Application application)
    {
        bitmapHelper = new BitmapHelper(application.getApplicationContext(),application.getResources().getDimensionPixelSize(R.dimen.image_thumbnails_size));
        fileListController = new FileListController(application.getApplicationContext());
    }

    public void loadFreeDcamDcimFiles()
    {
        fileListController.LoadFreeDcamDCIMDirsFiles();
    }

    public List<BaseHolder> getFiles()
    {
        return fileListController.getFiles();
    }
}
