package com.troop.freedcam.ui.handler;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.troop.freedcam.R;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by troop on 09.05.2015.
 */
public class ImageViewerFragment extends Fragment
{
    View view;
    Button closeButton;
    ImageView imageView;
    File[] files;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.imageviewer_fragment, container, false);
        this.closeButton = (Button)view.findViewById(R.id.button_closeView);
        this.imageView = (ImageView)view.findViewById(R.id.imageView_PicView);
        return view;
    }


    private void loadFilePaths()
    {

        File directory = new File(Environment.getExternalStorageDirectory() + "/DCIM/FreeCam/");
        files = directory.listFiles();

        Arrays.sort(files, new Comparator<File>(){
            public int compare(File f1, File f2)
            {
                return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
            } });
    }


}
