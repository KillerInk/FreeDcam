/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package freed.viewer.gridview;

import android.Manifest.permission;
import android.annotation.TargetApi;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.provider.DocumentFile;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;

import com.troop.freedcam.R.dimen;
import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;
import com.troop.freedcam.R.menu;
import com.troop.freedcam.R.string;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import freed.ActivityAbstract.FormatTypes;
import freed.ActivityInterface;
import freed.ActivityInterface.I_OnActivityResultCallback;
import freed.utils.FreeDPool;
import freed.utils.Logger;
import freed.utils.StringUtils.FileEnding;
import freed.viewer.dngconvert.DngConvertingActivity;
import freed.viewer.dngconvert.DngConvertingFragment;
import freed.viewer.holder.FileHolder;
import freed.viewer.screenslide.ScreenSlideFragment;

/**
 * Created by troop on 11.12.2015.
 */
public class GridViewFragment extends BaseGridViewFragment implements I_OnActivityResultCallback
{
    private ImageAdapter mPagerAdapter;

    private final String TAG = GridViewFragment.class.getSimpleName();

    private Button deleteButton;
    private Button filetypeButton;
    private Button rawToDngButton;
    /**
     * the files that get shown by the gridview
     */
    private FormatTypes formatsToShow = FormatTypes.all;
    private FormatTypes lastFormat = FormatTypes.all;
    private RequestModes requestMode = RequestModes.none;

    /**
     * textview that show the cound of selected files 1/12
     */
    private TextView filesSelected;
    /**
     * count of selected files
     */
    private int filesSelectedCount;
    /**
     * rootdir is when all folders contained from DCIM on internal and external SD card are showed.
     * internalSD/DCIM/Camera
     * internalSD/DCIM/FreeDcam
     * extSD/DCIM/FreeDcam showed with sd icon
     */
    private boolean isRootDir = true;
    /**
     * the current state of the gridview if items are in selection mode or normal rdy to click
     */
    private ViewStates currentViewState = ViewStates.normal;
    private  int mImageThumbSize;
    private  ExecutorService executor;
    private ActivityInterface viewerActivityInterface;
    private ScreenSlideFragment.I_ThumbClick onGridItemClick;
    private FileHolder folderToShow;

    public int DEFAULT_ITEM_TO_SET = 0;

    public void SetPosition(int position)
    {
        gridView.smoothScrollToPosition(position);
    }

    public View GetGridItem(int position)
    {
        return gridView.getChildAt(position);
    }

    public enum RequestModes
    {
        none,
        delete,
        rawToDng,
    }

    public void SetOnGridItemClick(ScreenSlideFragment.I_ThumbClick onGridItemClick)
    {
        this.onGridItemClick = onGridItemClick;
    }

    public void NotifyDataSetChanged()
    {
        mPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        this.mImageThumbSize = getResources().getDimensionPixelSize(dimen.image_thumbnail_size);
        viewerActivityInterface = (ActivityInterface) getActivity();
        executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        deleteButton = (Button) view.findViewById(id.button_deltePics);
        deleteButton.setVisibility(View.GONE);
        deleteButton.setOnClickListener(onDeltedButtonClick);

        ImageButton gobackButton = (ImageButton) view.findViewById(id.button_goback);
        gobackButton.setOnClickListener(onGobBackClick);

        filetypeButton = (Button) view.findViewById(id.button_filetype);
        filetypeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileSelectionPopup(v);
            }
        });

        filesSelected = (TextView) view.findViewById(id.textView_filesSelected);

        rawToDngButton = (Button) view.findViewById(id.button_rawToDng);
        rawToDngButton.setVisibility(View.GONE);
        rawToDngButton.setOnClickListener(onRawToDngClick);

        if (VERSION.SDK_INT >= VERSION_CODES.M)
        {
            checkMarshmallowPermissions();
        }
        else
            firstload();

        return view;
    }

    @Override
    protected void inflate(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(layout.gridviewfragment, container, false);
    }

    @Override
    public void onDestroyView()
    {
        if (mPagerAdapter != null)
            mPagerAdapter.Destroy();
        super.onDestroyView();
    }

    private void firstload()
    {
        if (mPagerAdapter == null)
        {
            mPagerAdapter = new ImageAdapter();
            gridView.setAdapter(mPagerAdapter);
            setViewMode(ViewStates.normal);
            //if its a normal startup and files are not loaded
            if (viewerActivityInterface.getFiles() == null && viewerActivityInterface.getFiles().size() ==0)
                viewerActivityInterface.LoadDCIMDirs();
            else //we return from screenslide
                isRootDir = false;
            gridView.smoothScrollToPosition(DEFAULT_ITEM_TO_SET);
        }
    }

    @TargetApi(VERSION_CODES.M)
    private void checkMarshmallowPermissions() {
        if (getActivity().checkSelfPermission(permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                            permission.READ_EXTERNAL_STORAGE,

                    },
                    1);
        }
        else
            firstload();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            firstload();
        }
        else
            getActivity().finish();
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        switch (currentViewState)
        {
            case normal:
                //handel normal griditem click to open screenslide when its not a folder
                if (!viewerActivityInterface.getFiles().get(position).IsFolder())
                {
                    this.onGridItemClick.onThumbClick(position, view);
                }
                else //handel folder click
                {
                    //hold the current folder to show if a format is empty
                    folderToShow = viewerActivityInterface.getFiles().get(position).getParent();
                    viewerActivityInterface.LoadFolder(viewerActivityInterface.getFiles().get(position),formatsToShow);
                    isRootDir = false;
                    setViewMode(currentViewState);

                }
                break;
            case selection:
                if (viewerActivityInterface.getFiles().get(position).IsSelected()) {
                    viewerActivityInterface.getFiles().get(position).SetSelected(false);
                    filesSelectedCount--;
                } else {
                    viewerActivityInterface.getFiles().get(position).SetSelected(true);
                    filesSelectedCount++;
                }
                updateFilesSelected();
                ((GridImageView)view).SetViewState(currentViewState);
                break;
        }
    }

    private void showFileSelectionPopup(View v) {
        PopupMenu popup = new PopupMenu(getContext(), v);
        popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int i = item.getItemId();
                if (i == id.all)
                {
                    filetypeButton.setText(string.ALL);
                    formatsToShow = FormatTypes.all;
                }
                else if (i == id.raw)
                {
                    filetypeButton.setText("RAW");
                    formatsToShow = FormatTypes.raw;
                }
                else if (i == id.bayer)
                {
                    filetypeButton.setText("BAYER");
                    formatsToShow = FormatTypes.raw;
                }
                else if (i == id.dng)
                {
                    filetypeButton.setText("DNG");
                    formatsToShow = FormatTypes.dng;
                }
                else if (i == id.jps)
                {
                    filetypeButton.setText("JPS");
                    formatsToShow = FormatTypes.jps;
                }
                else if (i == id.jpg)
                {
                    filetypeButton.setText("JPG");
                    formatsToShow = FormatTypes.jpg;
                }
                else if (i == id.mp4)
                {
                    filetypeButton.setText("MP4");
                    formatsToShow = FormatTypes.mp4;
                }
                //if (savedInstanceFilePath != null)
                viewerActivityInterface.LoadFolder(folderToShow,formatsToShow);

                return false;

            }
        });
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(menu.filetypepopupmenu, popup.getMenu());
        popup.show();
    }

    private void setViewMode(ViewStates viewState)
    {
        currentViewState = viewState;
        mPagerAdapter.SetViewState(currentViewState);
        //mPagerAdapter.notifyDataSetChanged();
        if (isRootDir)
        {
            deleteButton.setVisibility(View.VISIBLE);
            rawToDngButton.setVisibility(View.GONE);
            filetypeButton.setVisibility(View.GONE);
            filesSelected.setVisibility(View.GONE);
        }
        else {
            switch (viewState)
            {
                case normal:
                    if (formatsToShow == FormatTypes.raw && lastFormat != FormatTypes.raw) {
                        formatsToShow = lastFormat;
                        viewerActivityInterface.LoadFolder(viewerActivityInterface.getFiles().get(0).getParent(),formatsToShow);
                    }
                    requestMode = RequestModes.none;
                    deleteButton.setVisibility(View.VISIBLE);
                    rawToDngButton.setVisibility(View.VISIBLE);
                    filetypeButton.setVisibility(View.VISIBLE);
                    filesSelected.setVisibility(View.GONE);
                    break;
                case selection:
                    filesSelectedCount = 0;
                    filesSelected.setVisibility(View.VISIBLE);
                    updateFilesSelected();
                    switch (requestMode) {
                        case none:
                            deleteButton.setVisibility(View.VISIBLE);
                            rawToDngButton.setVisibility(View.VISIBLE);
                            filetypeButton.setVisibility(View.VISIBLE);
                            break;
                        case delete:
                            deleteButton.setVisibility(View.VISIBLE);
                            rawToDngButton.setVisibility(View.GONE);
                            filetypeButton.setVisibility(View.GONE);
                            break;
                        case rawToDng:
                            lastFormat = formatsToShow;
                            formatsToShow = FormatTypes.raw;
                            viewerActivityInterface.LoadFolder(viewerActivityInterface.getFiles().get(0).getParent(),formatsToShow);
                            deleteButton.setVisibility(View.GONE);
                            rawToDngButton.setVisibility(View.VISIBLE);
                            filetypeButton.setVisibility(View.GONE);
                            break;
                    }
                    break;
            }
        }
    }

    private void updateFilesSelected()
    {
        filesSelected.setText(getString(string.files_selected) + filesSelectedCount);
    }

    private void deleteFiles()
    {
        FreeDPool.Execute(new Runnable()
        {
            @Override
            public void run()
            {
                int fileselected = filesSelectedCount;

                int filesdeletedCount = 0;
                List<FileHolder> to_del = new ArrayList<>();
                for (int i = 0; i < viewerActivityInterface.getFiles().size(); i++)
                {
                    if (viewerActivityInterface.getFiles().get(i).IsSelected())
                    {
                        to_del.add(viewerActivityInterface.getFiles().get(i));
                    }
                }
                viewerActivityInterface.DeleteFiles(to_del);
            }
        });
    }

    private final OnClickListener onGobBackClick = new OnClickListener() {
        @Override
        public void onClick(View view)
        {
            if (currentViewState == ViewStates.normal)
            {
                if (viewerActivityInterface.getFiles() != null && viewerActivityInterface.getFiles().size() > 0)
                {
                    File topPath = viewerActivityInterface.getFiles().get(0).getFile().getParentFile().getParentFile();
                    if (topPath.getName().equals("DCIM") && !isRootDir)
                    {
                        viewerActivityInterface.LoadDCIMDirs();
                        isRootDir = true;
                        setViewMode(currentViewState);
                    }
                    else if (isRootDir)
                    {
                        getActivity().finish();
                    }
                    else
                    {
                        isRootDir = false;
                        viewerActivityInterface.LoadFolder(viewerActivityInterface.getFiles().get(0),formatsToShow);
                        setViewMode(currentViewState);
                    }
                }
                else
                {
                    viewerActivityInterface.LoadDCIMDirs();
                    isRootDir = true;
                    setViewMode(currentViewState);
                }
            }
            else if (currentViewState == ViewStates.selection)
            {
                for (int i = 0; i< viewerActivityInterface.getFiles().size(); i++)
                {
                    FileHolder f = viewerActivityInterface.getFiles().get(i);
                    f.SetSelected(false);
                }
                setViewMode(ViewStates.normal);
            }
        }
    };




    @Override
    public void onActivityResultCallback(Uri uri) {

    }

    private final OnClickListener onRawToDngClick = new OnClickListener() {
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
                for (FileHolder f : viewerActivityInterface.getFiles()) {
                    if (f.IsSelected() &&
                            (f.getFile().getName().toLowerCase().endsWith(FileEnding.RAW) ||f.getFile().getName().toLowerCase().endsWith(FileEnding.BAYER))) {
                        ar.add(f.getFile().getAbsolutePath());
                    }

                }
                for (FileHolder f : viewerActivityInterface.getFiles()) {
                    f.SetSelected(false);
                }
                setViewMode(ViewStates.normal);
                Intent i = new Intent(getActivity(), DngConvertingActivity.class);
                String[] t = new String[ar.size()];
                ar.toArray(t);
                i.putExtra(DngConvertingFragment.EXTRA_FILESTOCONVERT, t);
                startActivity(i);
            }
        }

    };

    /*
    DELTE FILES STUFF
     */
    private final DialogInterface.OnClickListener dialogDeleteClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    deleteFiles();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    setViewMode(ViewStates.normal);
                    for (int i = 0; i< viewerActivityInterface.getFiles().size(); i++)
                    {
                        FileHolder f = viewerActivityInterface.getFiles().get(i);
                        f.SetSelected(false);
                    }
                    break;
            }
        }
    };

    private final OnClickListener onDeltedButtonClick = new OnClickListener() {
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
                //check if files are selceted
                boolean hasfilesSelected = false;
                for (FileHolder f : viewerActivityInterface.getFiles()) {
                    if (f.IsSelected()) {
                        hasfilesSelected = true;
                        break;
                    }
                }
                //if no files selected skip dialog
                if (!hasfilesSelected)
                    return;
                //else show dialog
                if (VERSION.SDK_INT <= VERSION_CODES.LOLLIPOP || !viewerActivityInterface.getFiles().get(0).isExternalSD())
                {
                    Builder builder = new Builder(getContext());
                    builder.setMessage(string.delete_files).setPositiveButton(string.yes, dialogDeleteClickListener)
                            .setNegativeButton(string.no, dialogDeleteClickListener).show();
                    setViewMode(ViewStates.normal);
                }
                else
                {
                    DocumentFile sdDir = viewerActivityInterface.getExternalSdDocumentFile();
                    if (sdDir == null) {
                        //ActivityInterface fragment_activityInterface = (ActivityInterface) getActivity();
                        viewerActivityInterface.ChooseSDCard(GridViewFragment.this);
                    }
                    else
                    {
                        Builder builder = new Builder(getContext());
                        builder.setMessage(string.delete_files).setPositiveButton(string.yes, dialogDeleteClickListener)
                                .setNegativeButton(string.no, dialogDeleteClickListener).show();
                    }
                }

            }
        }
    };

    class ImageAdapter extends BaseAdapter
    {
        private final String TAG = ImageAdapter.class.getSimpleName();

        public ImageAdapter() {

        }

        public void Destroy()
        {
            if (executor != null)
                executor.shutdown();
            while (!executor.isShutdown())
            {}
        }

        @Override
        public int getCount() {
            return viewerActivityInterface.getFiles().size();
        }

        @Override
        public Object getItem(int position) {
            return viewerActivityInterface.getFiles().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            GridImageView imageView;
            if (convertView == null) { // if it's not recycled, initialize some attributes
                imageView = new GridImageView(getContext(), executor, viewerActivityInterface.getBitmapHelper());
            } else {
                imageView = (GridImageView) convertView;
                imageView.SetThreadPoolAndBitmapHelper(executor, viewerActivityInterface.getBitmapHelper());
            }
            Logger.d(TAG, "filessize:" + viewerActivityInterface.getFiles().size() + " position:"+position);
            if (viewerActivityInterface.getFiles().size() <= position)
                position = viewerActivityInterface.getFiles().size() -1;
            if (imageView.getFileHolder() == null || !imageView.getFileHolder().equals(viewerActivityInterface.getFiles().get(position)) /*||imageView.viewstate != currentViewState*/)
            {
                imageView.SetEventListner(viewerActivityInterface.getFiles().get(position));
                imageView.SetViewState(currentViewState);
                imageView.loadFile(viewerActivityInterface.getFiles().get(position), mImageThumbSize);
            }
            return imageView;
        }



        public void SetViewState(ViewStates states)
        {
            currentViewState = states;
            for (int i = 0; i< viewerActivityInterface.getFiles().size(); i++)
            {
                FileHolder f = viewerActivityInterface.getFiles().get(i);
                f.SetViewState(states);
            }

        }
    }
}


