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

import android.app.AlertDialog.Builder;
import android.app.RecoverableSecurityException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;
import com.troop.freedcam.R.menu;
import com.troop.freedcam.R.string;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import freed.ActivityAbstract;
import freed.ActivityInterface;
import freed.ActivityInterface.I_OnActivityResultCallback;
import freed.file.FileListController.FormatTypes;
import freed.file.holder.BaseHolder;
import freed.file.holder.FileHolder;
import freed.file.holder.UriHolder;
import freed.image.ImageManager;
import freed.renderscript.RenderScriptManager;
import freed.utils.FreeDPool;
import freed.utils.Log;
import freed.utils.StringUtils.FileEnding;
import freed.viewer.dngconvert.DngConvertingActivity;
import freed.viewer.dngconvert.DngConvertingFragment;
import freed.viewer.screenslide.ScreenSlideFragment;
import freed.viewer.stack.DngStackActivity;
import freed.viewer.stack.StackActivity;

/**
 * Created by troop on 11.12.2015.
 */
public class GridViewFragment extends Fragment implements I_OnActivityResultCallback ,AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener
{
    public final int STACK_REQUEST = 44;
    public final int DNGCONVERT_REQUEST = 45;

    protected GridView gridView;
    protected View view;
    protected boolean pos0ret;
    protected ViewStates currentViewState = ViewStates.normal;
    private boolean firststart = false;



    public enum ViewStates
    {
        normal,
        selection,
    }

    private ImageAdapter mPagerAdapter;

    private final String TAG = GridViewFragment.class.getSimpleName();

    private Button filetypeButton;
    private Button optionsButton;
    private Button doActionButton;
    /**
     * the files that get shown by the gridview
     */
    public FormatTypes formatsToShow = FormatTypes.all;
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

    private ActivityInterface viewerActivityInterface;
    private ScreenSlideFragment.ButtonClick onGridItemClick;
    private BaseHolder folderToShow;

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
        stack,
        dngstack,
    }

    public void SetOnGridItemClick(ScreenSlideFragment.ButtonClick onGridItemClick)
    {
        this.onGridItemClick = onGridItemClick;
    }

    public void NotifyDataSetChanged()
    {
        mPagerAdapter.notifyDataSetChanged();
        if (!firststart) {
            if (viewerActivityInterface.getFileListController() != null
                    && viewerActivityInterface.getFileListController().getFiles() != null
                    && viewerActivityInterface.getFileListController().getFiles().size() > 0
                    && viewerActivityInterface.getFileListController().getFiles().get(0) instanceof UriHolder
                    ) {

                isRootDir = false;
                Log.d(TAG, "NotifyDataSetChanged rootdir:" +isRootDir);
                setViewMode(ViewStates.normal);
            }
            firststart = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        viewerActivityInterface = (ActivityInterface) getActivity();

        inflate(inflater, container);
        gridView = view.findViewById(id.gridView_base);
        gridView.setOnItemClickListener(this);
        gridView.setOnItemLongClickListener(this);
        gridView.smoothScrollToPosition(DEFAULT_ITEM_TO_SET);

        ImageButton gobackButton = view.findViewById(id.button_goback);
        gobackButton.setOnClickListener(onGobBackClick);

        filetypeButton = view.findViewById(id.button_filetype);
        filetypeButton.setOnClickListener(v -> showFileSelectionPopup(v));

        filesSelected = view.findViewById(id.textView_filesSelected);

        optionsButton = view.findViewById(id.button_options);
        optionsButton.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(getContext(), v);

            popup.getMenu().add(0,0,0, "Delete File");
            if (!isRootDir && RenderScriptManager.isSupported())
               popup.getMenu().add(0,1,1, "StackJpeg");
            if (!isRootDir)
               popup.getMenu().add(0,2,2, "Raw to Dng");
            if (!isRootDir)
                popup.getMenu().add(0,3,3, "DngStack");
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId())
                {
                    case 0:
                        onDeltedButtonClick.onClick(null);
                        break;
                    case 1:
                        onStackClick.onClick(null);
                        break;
                    case 2:
                        onRawToDngClick.onClick(null);
                        break;
                    case 3:
                        onDngStackClick.onClick(null);
                }
                return false;
            });
            popup.show();
        });

        doActionButton = view.findViewById(id.button_DoAction);
        doActionButton.setVisibility(View.GONE);
        firstload();

        return view;
    }


    protected void inflate(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(layout.freedviewer_gridviewfragment, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        ImageManager.cancelImageLoadTasks();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
    }

    private void firstload()
    {
        if (mPagerAdapter == null)
        {
            mPagerAdapter = new ImageAdapter(viewerActivityInterface);
            gridView.setAdapter(mPagerAdapter);
            setViewMode(ViewStates.normal);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        switch (currentViewState)
        {
            case normal:
                //handel normal griditem click to open screenslide when its not a folder
                if (!viewerActivityInterface.getFileListController().getFiles().get(position).IsFolder())
                {
                    this.onGridItemClick.onButtonClick(position, view);
                }
                else //handel folder click
                {
                    //hold the current folder to show if a format is empty
                    folderToShow = viewerActivityInterface.getFileListController().getFiles().get(position);
                    viewerActivityInterface.getFileListController().LoadFolder(viewerActivityInterface.getFileListController().getFiles().get(position),formatsToShow);
                    isRootDir = false;
                    setViewMode(currentViewState);

                }
                break;
            case selection:
                if (viewerActivityInterface.getFileListController().getFiles().get(position).IsSelected()) {
                    viewerActivityInterface.getFileListController().getFiles().get(position).SetSelected(false);
                    filesSelectedCount--;
                } else {
                    viewerActivityInterface.getFileListController().getFiles().get(position).SetSelected(true);
                    filesSelectedCount++;
                }
                updateFilesSelected();
                ((GridImageView)view).SetViewState(currentViewState);
                break;
        }
    }

    private void showFileSelectionPopup(View v) {
        PopupMenu popup = new PopupMenu(getContext(), v);
        popup.setOnMenuItemClickListener(item -> {
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
            viewerActivityInterface.getFileListController().LoadFolder(folderToShow,formatsToShow);

            return false;

        });
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(menu.filetypepopupmenu, popup.getMenu());
        popup.show();
    }

    private void setViewMode(ViewStates viewState)
    {
        Log.d(TAG,"setViewMode:  isRootDir" + isRootDir);
        currentViewState = viewState;
        mPagerAdapter.SetViewState(currentViewState);
        if (isRootDir)
        {
            filetypeButton.setVisibility(View.GONE);
            filesSelected.setVisibility(View.GONE);
        }
        else
        {
            switch (viewState)
            {
                case normal:
                    if (formatsToShow == FormatTypes.raw && lastFormat != FormatTypes.raw) {
                        formatsToShow = lastFormat;
                        viewerActivityInterface.getFileListController().LoadFolder(folderToShow,formatsToShow);
                    }
                    requestMode = RequestModes.none;
                    filetypeButton.setVisibility(View.VISIBLE);
                    filesSelected.setVisibility(View.GONE);
                    optionsButton.setVisibility(View.VISIBLE);
                    doActionButton.setVisibility(View.GONE);
                    break;
                case selection:
                    filesSelectedCount = 0;
                    filesSelected.setVisibility(View.VISIBLE);
                    updateFilesSelected();
                    switch (requestMode) {
                        case none:
                            filetypeButton.setVisibility(View.VISIBLE);
                            optionsButton.setVisibility(View.VISIBLE);
                            doActionButton.setVisibility(View.GONE);
                            doActionButton.setOnClickListener(null);
                            break;
                        case delete:
                            filetypeButton.setVisibility(View.GONE);
                            optionsButton.setVisibility(View.GONE);
                            doActionButton.setText("Delete");
                            doActionButton.setOnClickListener(onDeltedButtonClick);
                            doActionButton.setVisibility(View.VISIBLE);
                            break;
                        case rawToDng:
                            lastFormat = formatsToShow;
                            formatsToShow = FormatTypes.raw;
                            viewerActivityInterface.getFileListController().LoadFolder(folderToShow,formatsToShow);
                            optionsButton.setVisibility(View.GONE);
                            optionsButton.setVisibility(View.GONE);
                            filetypeButton.setVisibility(View.GONE);
                            doActionButton.setText("RawToDng");
                            doActionButton.setOnClickListener(onRawToDngClick);
                            doActionButton.setVisibility(View.VISIBLE);
                            break;
                        case stack:
                            lastFormat = formatsToShow;
                            formatsToShow = FormatTypes.jpg;
                            viewerActivityInterface.getFileListController().LoadFolder(folderToShow,formatsToShow);
                            optionsButton.setVisibility(View.GONE);
                            filetypeButton.setVisibility(View.GONE);
                            doActionButton.setText("Stack");
                            doActionButton.setOnClickListener(onStackClick);
                            doActionButton.setVisibility(View.VISIBLE);
                            break;
                        case dngstack:
                            lastFormat = formatsToShow;
                            formatsToShow = FormatTypes.dng;
                            viewerActivityInterface.getFileListController().LoadFolder(folderToShow,formatsToShow);
                            optionsButton.setVisibility(View.GONE);
                            filetypeButton.setVisibility(View.GONE);
                            doActionButton.setText("DngStack");
                            doActionButton.setOnClickListener(onDngStackClick);
                            doActionButton.setVisibility(View.VISIBLE);
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

    private List<UriHolder> urisToDelte = new ArrayList<>();

    private void deleteFiles()
    {
        ImageManager.cancelImageLoadTasks();
        FreeDPool.Execute(() -> {
            int fileselected = filesSelectedCount;

            int filesdeletedCount = 0;
            List<BaseHolder> to_del = new ArrayList<>();

            urisToDelte.clear();
            for (int i = 0; i < viewerActivityInterface.getFileListController().getFiles().size(); i++)
            {
                try {
                    if (viewerActivityInterface.getFileListController().getFiles().get(i).IsSelected())
                    {
                        viewerActivityInterface.getFileListController().DeleteFile(viewerActivityInterface.getFileListController().getFiles().get(i));
                        GridViewFragment.this.gridView.post(() ->{NotifyDataSetChanged();});
                    }
                }
                catch(SecurityException ex){
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        if (ex instanceof RecoverableSecurityException)
                        {
                            RecoverableSecurityException rex = (RecoverableSecurityException)ex;
                            try {
                                UriHolder uriHolder = (UriHolder) viewerActivityInterface.getFileListController().getFiles().get(i);
                                urisToDelte.add(uriHolder);
                                startIntentSenderForResult(rex.getUserAction().getActionIntent().getIntentSender(), ActivityAbstract.DELETE_REQUEST_CODE,null,0,0,0,null);
                            } catch (IntentSender.SendIntentException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });
    }



    private final OnClickListener onGobBackClick = new OnClickListener() {
        @Override
        public void onClick(View view)
        {
            if (currentViewState == ViewStates.normal)
            {
                if (viewerActivityInterface.getFileListController().getFiles() != null && viewerActivityInterface.getFileListController().getFiles().size() > 0
                && viewerActivityInterface.getFileListController().getFiles().get(0) instanceof FileHolder)
                {
                    FileHolder fileHolder = (FileHolder) viewerActivityInterface.getFileListController().getFiles().get(0);
                    File topPath = fileHolder.getFile().getParentFile().getParentFile();
                    if (topPath.getName().equals("DCIM") && !isRootDir)
                    {
                        viewerActivityInterface.getFileListController().loadDefaultFiles();
                        isRootDir = true;
                        Log.d(TAG, "onGobBackClick dcim folder rootdir:" +isRootDir);
                        setViewMode(currentViewState);
                    }
                    else if (isRootDir)
                    {
                        getActivity().finish();
                    }
                    else
                    {
                        isRootDir = false;
                        Log.d(TAG, "onGobBackClick load folder rootdir:" +isRootDir);
                        viewerActivityInterface.getFileListController().loadDefaultFiles();
                        //viewerActivityInterface.LoadFolder(viewerActivityInterface.getFiles().get(0),formatsToShow);
                        setViewMode(currentViewState);
                    }
                }
                else if (viewerActivityInterface.getFileListController().getFiles().size() > 0 && viewerActivityInterface.getFileListController().getFiles().get(0) instanceof UriHolder)
                    if (viewerActivityInterface.getFileListController().getFiles().get(0).IsFolder())
                        getActivity().finish();
                    else
                        viewerActivityInterface.getFileListController().loadDefaultFiles();
                else
                {
                    viewerActivityInterface.getFileListController().loadDefaultFiles();
                    //viewerActivityInterface.LoadDCIMDirs();
                    Log.d(TAG, "onGobBackClick dcim folder rootdir:" +isRootDir);
                    isRootDir = true;

                    setViewMode(currentViewState);
                    if (viewerActivityInterface.getFileListController().getFiles().size() == 0)
                        getActivity().finish();
                }
            }
            else if (currentViewState == ViewStates.selection)
            {
                for (int i = 0; i< viewerActivityInterface.getFileListController().getFiles().size(); i++)
                {
                    BaseHolder f = viewerActivityInterface.getFileListController().getFiles().get(i);
                    f.SetSelected(false);
                }
                setViewMode(ViewStates.normal);
            }
        }
    };




    @Override
    public void onActivityResultCallback(Uri uri) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == STACK_REQUEST || requestCode == DNGCONVERT_REQUEST)
            viewerActivityInterface.getFileListController().LoadFolder(folderToShow,formatsToShow);
        if (requestCode == ActivityAbstract.DELETE_REQUEST_CODE)
        {
            UriHolder uriHolder = urisToDelte.get(0);
            urisToDelte.remove(0);
            viewerActivityInterface.getFileListController().DeleteFile(uriHolder);
            NotifyDataSetChanged();
        }

    }

    private final OnClickListener onStackClick = new OnClickListener() {
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
                for (BaseHolder f : viewerActivityInterface.getFileListController().getFiles()) {
                    if (f.IsSelected() && f.getName().toLowerCase().endsWith(FileEnding.JPG))
                    {
                        if (f instanceof FileHolder)
                            ar.add(((FileHolder)f).getFile().getAbsolutePath());
                        else if (f instanceof UriHolder)
                            ar.add(((UriHolder)f).getMediaStoreUri().toString());
                    }

                }
                for (BaseHolder f : viewerActivityInterface.getFileListController().getFiles()) {
                    f.SetSelected(false);
                }
                setViewMode(ViewStates.normal);
                Intent i = new Intent(getActivity(), StackActivity.class);
                String[] t = new String[ar.size()];
                ar.toArray(t);
                i.putExtra(DngConvertingFragment.EXTRA_FILESTOCONVERT, t);
                getActivity().startActivityForResult(i, STACK_REQUEST);
            }
            else
            {
                requestMode = RequestModes.none;
                setViewMode(ViewStates.normal);
            }
        }
    };

    private final OnClickListener onDngStackClick = new OnClickListener() {
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
                for (BaseHolder f : viewerActivityInterface.getFileListController().getFiles()) {
                    if (f.IsSelected() && f.getName().toLowerCase().endsWith(FileEnding.DNG))
                    {
                        if (f instanceof FileHolder)
                            ar.add(((FileHolder)f).getFile().getAbsolutePath());
                        else if (f instanceof UriHolder)
                            ar.add(((UriHolder)f).getMediaStoreUri().toString());
                    }

                }
                for (BaseHolder f : viewerActivityInterface.getFileListController().getFiles()) {
                    f.SetSelected(false);
                }
                setViewMode(ViewStates.normal);
                Intent i = new Intent(getActivity(), DngStackActivity.class);
                String[] t = new String[ar.size()];
                ar.toArray(t);
                i.putExtra(DngConvertingFragment.EXTRA_FILESTOCONVERT, t);
                getActivity().startActivityForResult(i, STACK_REQUEST);
            }
            else
            {
                requestMode = RequestModes.none;
                setViewMode(ViewStates.normal);
            }
        }
    };

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
                for (BaseHolder f : viewerActivityInterface.getFileListController().getFiles()) {
                    if (f.IsSelected() &&
                            (f.getName().toLowerCase().endsWith(FileEnding.RAW) ||f.getName().toLowerCase().endsWith(FileEnding.BAYER))) {
                        if (f instanceof FileHolder)
                            ar.add(((FileHolder) f).getFile().getAbsolutePath());
                        else if (f instanceof UriHolder)
                            ar.add(((UriHolder) f).getMediaStoreUri().toString());
                    }

                }
                for (BaseHolder f : viewerActivityInterface.getFileListController().getFiles()) {
                    f.SetSelected(false);
                }
                setViewMode(ViewStates.normal);
                Intent i = new Intent(getActivity(), DngConvertingActivity.class);
                String[] t = new String[ar.size()];
                ar.toArray(t);
                i.putExtra(DngConvertingFragment.EXTRA_FILESTOCONVERT, t);
                getActivity().startActivityForResult(i, DNGCONVERT_REQUEST);
            }
            else
            {
                requestMode = RequestModes.none;
                setViewMode(ViewStates.normal);
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
                    for (int i = 0; i< viewerActivityInterface.getFileListController().getFiles().size(); i++)
                    {
                        BaseHolder f = viewerActivityInterface.getFileListController().getFiles().get(i);
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
                //check if files are selected
                boolean hasfilesSelected = false;
                for (BaseHolder f : viewerActivityInterface.getFileListController().getFiles()) {
                    if (f.IsSelected()) {
                        hasfilesSelected = true;
                        break;
                    }
                }
                //if no files selected skip dialog
                if (!hasfilesSelected)
                    return;
                //else show dialog
                Builder builder = new Builder(getContext());
                builder.setMessage(string.delete_files).setPositiveButton(string.yes, dialogDeleteClickListener)
                        .setNegativeButton(string.no, dialogDeleteClickListener).show();
                setViewMode(ViewStates.normal);

               /* if (VERSION.SDK_INT <= VERSION_CODES.LOLLIPOP || !(viewerActivityInterface.getFileListController().getFiles().get(0) instanceof FileHolder))
                {

                }
                else
                {
                    DocumentFile sdDir = viewerActivityInterface.getFileListController().getExternalSdDocumentFile(getContext());
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
                }*/

            }
            else
            {
                requestMode = RequestModes.none;
                setViewMode(ViewStates.normal);
            }
        }
    };


}


