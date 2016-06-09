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

package com.freedviewer.gridview;

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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;

import com.freedcam.ui.I_Activity;
import com.freedcam.ui.I_Activity.I_OnActivityResultCallback;
import com.freedcam.ui.handler.MediaScannerManager;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.FileUtils;
import com.freedcam.utils.FreeDPool;
import com.freedcam.utils.Logger;
import com.freedcam.utils.StringUtils.FileEnding;
import com.freedviewer.dngconvert.DngConvertingActivity;
import com.freedviewer.dngconvert.DngConvertingFragment;
import com.freedviewer.helper.BitmapHelper;
import com.freedviewer.holder.FileHolder;
import com.freedviewer.screenslide.ScreenSlideActivity;
import com.troop.freedcam.R.dimen;
import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;
import com.troop.freedcam.R.menu;
import com.troop.freedcam.R.string;

import java.io.File;
import java.util.ArrayList;

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
    private final String savedInstanceString = "lastpath";
    private String savedInstanceFilePath;
    private FormatTypes formatsToShow = FormatTypes.all;
    private FormatTypes lastFormat = FormatTypes.all;
    private RequestModes requestMode = RequestModes.none;

    private TextView filesSelected;
    private int filesSelectedCount =0;
    private boolean isRootDir = true;
    private AppSettingsManager appSettingsManager;
    private BitmapHelper bitmapHelper;

    public void SetBitmapHelperAndAppSettings(BitmapHelper helper, AppSettingsManager appSettingsManager)
    {
        bitmapHelper =helper;
        this.appSettingsManager = appSettingsManager;
    }

    public enum FormatTypes
    {
        all,
        raw,
        dng,
        jpg,
        jps,
        mp4,
    }

    public enum RequestModes
    {
        none,
        delete,
        rawToDng,

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        deleteButton = (Button)view.findViewById(id.button_deltePics);
        deleteButton.setVisibility(View.GONE);
        deleteButton.setOnClickListener(onDeltedButtonClick);

        ImageButton gobackButton = (ImageButton) view.findViewById(id.button_goback);
        gobackButton.setOnClickListener(onGobBackClick);

        filetypeButton = (Button)view.findViewById(id.button_filetype);
        filetypeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileSelectionPopup(v);
            }
        });

        filesSelected = (TextView)view.findViewById(id.textView_filesSelected);

        rawToDngButton = (Button)view.findViewById(id.button_rawToDng);
        rawToDngButton.setVisibility(View.GONE);
        rawToDngButton.setOnClickListener(onRawToDngClick);

        if (VERSION.SDK_INT >= VERSION_CODES.M)
        {
            checkMarshmallowPermissions();
        }
        else
            load();

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

    private void load()
    {
        if (mPagerAdapter == null)
        {
            mPagerAdapter = new ImageAdapter(getContext(), getResources().getDimensionPixelSize(dimen.image_thumbnail_size), bitmapHelper);
            gridView.setAdapter(mPagerAdapter);
            setViewMode(ViewStates.normal);
        }
        if (savedInstanceFilePath == null)
            mPagerAdapter.loadDCIMFolders();
        else
            mPagerAdapter.loadFiles(new File(savedInstanceFilePath));

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
            load();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            load();
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
                if (!mPagerAdapter.GetFileHolder(position).IsFolder())
                {
                    Intent i = new Intent(getActivity(), ScreenSlideActivity.class);
                    i.putExtra(ScreenSlideActivity.EXTRA_IMAGE, position);
                    if (mPagerAdapter.getFiles() != null &&mPagerAdapter.getFiles().size() >0)
                    {
                        if (!mPagerAdapter.GetFileHolder(position).IsFolder())
                            i.putExtra(ScreenSlideActivity.IMAGE_PATH, mPagerAdapter.GetFileHolder(position).getFile().getParentFile().getAbsolutePath());
                        else
                            i.putExtra(ScreenSlideActivity.IMAGE_PATH,  mPagerAdapter.GetFileHolder(position).getFile().getAbsolutePath());
                    }
                    i.putExtra(ScreenSlideActivity.FileType, formatsToShow.name());
                    startActivity(i);
                }
                else
                {
                    savedInstanceFilePath =  mPagerAdapter.GetFileHolder(position).getFile().getAbsolutePath();
                    mPagerAdapter.loadFiles(mPagerAdapter.GetFileHolder(position).getFile());
                    isRootDir = false;
                    setViewMode(currentViewState);

                }
                break;
            case selection:
                if (mPagerAdapter.GetFileHolder(position).IsSelected()) {
                    mPagerAdapter.GetFileHolder(position).SetSelected(false);
                    filesSelectedCount--;
                } else {
                    mPagerAdapter.GetFileHolder(position).SetSelected(true);
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
                mPagerAdapter.SetFormatToShow(formatsToShow);
                //if (savedInstanceFilePath != null)
                mPagerAdapter.loadFiles(new File(savedInstanceFilePath));

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
        mPagerAdapter.notifyDataSetChanged();
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
                    if ((formatsToShow == FormatTypes.raw && lastFormat != FormatTypes.raw)) {
                        formatsToShow = lastFormat;
                        mPagerAdapter.SetFormatToShow(formatsToShow);
                        mPagerAdapter.loadFiles(new File(savedInstanceFilePath));
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
                            if (savedInstanceFilePath == null)
                                break;
                            File toload = new File(savedInstanceFilePath);
                            if (toload == null)
                                break;
                            lastFormat = formatsToShow;
                            formatsToShow = FormatTypes.raw;
                            mPagerAdapter.SetFormatToShow(formatsToShow);
                            mPagerAdapter.loadFiles(toload);
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
                for (int i = 0; i < mPagerAdapter.getFiles().size(); i++)
                {
                    if (mPagerAdapter.getFiles().get(i).IsSelected())
                    {
                        FileHolder f = mPagerAdapter.getFiles().get(i);
                        boolean del = bitmapHelper.DeleteFile(f,appSettingsManager, getContext());
                        MediaScannerManager.ScanMedia(getContext(), f.getFile());
                        Logger.d(TAG, "file: " + f.getFile().getName() + " deleted:" + del);
                        if (del) {
                            i--;
                            mPagerAdapter.delteFile(f);
                        }
                        filesdeletedCount++;
                    }
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        setViewMode(ViewStates.normal);
                        mPagerAdapter.notifyDataSetChanged();
                    }
                });

            }
        });
    }

    private OnClickListener onGobBackClick = new OnClickListener() {
        @Override
        public void onClick(View view)
        {
            if (currentViewState == ViewStates.normal)
            {
                if (mPagerAdapter.getFiles() != null && mPagerAdapter.getFiles().size() > 0)
                {
                    File topPath =  mPagerAdapter.GetFileHolder(0).getFile().getParentFile().getParentFile();
                    if (topPath.getName().equals("DCIM") && !isRootDir)
                    {
                        savedInstanceFilePath = null;
                        mPagerAdapter.loadDCIMFolders();
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
                        mPagerAdapter.loadFiles(mPagerAdapter.GetFileHolder(0).getFile());
                        savedInstanceFilePath =  mPagerAdapter.GetFileHolder(0).getFile().getAbsolutePath();
                        setViewMode(currentViewState);
                    }
                }
                else
                {
                    mPagerAdapter.loadDCIMFolders();
                    isRootDir = true;
                    setViewMode(currentViewState);
                }
            }
            else if (currentViewState == ViewStates.selection)
            {
                for (int i = 0; i< mPagerAdapter.getFiles().size(); i++)
                {
                    FileHolder f =  mPagerAdapter.GetFileHolder(i);
                    f.SetSelected(false);
                }
                setViewMode(ViewStates.normal);
            }
        }
    };




    @Override
    public void onActivityResultCallback(Uri uri) {

    }

    private OnClickListener onRawToDngClick = new OnClickListener() {
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
                for (FileHolder f : mPagerAdapter.getFiles()) {
                    if (f.IsSelected() &&
                            (f.getFile().getName().toLowerCase().endsWith(FileEnding.RAW) ||f.getFile().getName().toLowerCase().endsWith(FileEnding.BAYER))) {
                        ar.add(f.getFile().getAbsolutePath());
                    }

                }
                for (FileHolder f : mPagerAdapter.getFiles()) {
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
    private DialogInterface.OnClickListener dialogDeleteClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    deleteFiles();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    setViewMode(ViewStates.normal);
                    for (int i = 0; i< mPagerAdapter.getFiles().size(); i++)
                    {
                        FileHolder f =  mPagerAdapter.GetFileHolder(i);
                        f.SetSelected(false);
                    }
                    break;
            }
        }
    };

    private OnClickListener onDeltedButtonClick = new OnClickListener() {
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
                for (FileHolder f : mPagerAdapter.getFiles()) {
                    if (f.IsSelected()) {
                        hasfilesSelected = true;
                        break;
                    }
                }
                //if no files selected skip dialog
                if (!hasfilesSelected)
                    return;
                //else show dialog
                if (VERSION.SDK_INT <= VERSION_CODES.LOLLIPOP || !mPagerAdapter.getFiles().get(0).isExternalSD())
                {
                    Builder builder = new Builder(getContext());
                    builder.setMessage(string.delete_files).setPositiveButton(string.yes, dialogDeleteClickListener)
                            .setNegativeButton(string.no, dialogDeleteClickListener).show();
                    setViewMode(ViewStates.normal);
                }
                else
                {
                    DocumentFile sdDir = FileUtils.getExternalSdDocumentFile(appSettingsManager,getContext());
                    if (sdDir == null) {
                        I_Activity i_activity = (I_Activity) getActivity();
                        i_activity.ChooseSDCard(GridViewFragment.this);
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
}


