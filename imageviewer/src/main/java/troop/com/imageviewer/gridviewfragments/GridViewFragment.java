package troop.com.imageviewer.gridviewfragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.provider.DocumentFile;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.troop.filelogger.Logger;
import com.troop.freedcam.manager.MediaScannerManager;
import com.troop.freedcam.ui.I_Activity;
import com.troop.freedcam.utils.FileUtils;
import com.troop.freedcam.utils.StringUtils;


import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import troop.com.imageviewer.BitmapHelper;
import troop.com.imageviewer.DngConvertingActivity;
import troop.com.imageviewer.DngConvertingFragment;
import troop.com.imageviewer.R;
import troop.com.imageviewer.ScreenSlideActivity;
import troop.com.imageviewer.gridimageviews.GridImageView;
import troop.com.imageviewer.holder.FileHolder;

/**
 * Created by troop on 11.12.2015.
 */
public class GridViewFragment extends BaseGridViewFragment implements I_Activity.I_OnActivityResultCallback
{
    private ImageAdapter mPagerAdapter;
    //private List<FileHolder> files;

    final String TAG = GridViewFragment.class.getSimpleName();

    private Button deleteButton;
    private ImageButton gobackButton;
    private Button filetypeButton;
    private Button rawToDngButton;
    final String savedInstanceString = "lastpath";
    private String savedInstanceFilePath;
    private FormatTypes formatsToShow = FormatTypes.all;
    private FormatTypes lastFormat = FormatTypes.all;
    private boolean pos0ret = false;
    private boolean PERMSISSIONGRANTED = false;
    private RequestModes requestMode = RequestModes.none;

    private TextView filesSelected;
    private int filesSelectedCount =0;



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
        deleteButton = (Button)view.findViewById(R.id.button_deltePics);
        deleteButton.setVisibility(View.GONE);
        deleteButton.setOnClickListener(onDeltedButtonClick);

        gobackButton = (ImageButton)view.findViewById(R.id.button_goback);
        gobackButton.setOnClickListener(onGobBackClick);

        filetypeButton = (Button)view.findViewById(R.id.button_filetype);
        filetypeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v);
            }
        });

        filesSelected = (TextView)view.findViewById(R.id.textView_filesSelected);

        rawToDngButton = (Button)view.findViewById(R.id.button_rawToDng);
        rawToDngButton.setVisibility(View.GONE);
        rawToDngButton.setOnClickListener(onRawToDngClick);


        return view;
    }


    @Override
    protected void inflate(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.gridviewfragment, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null)
        {
            savedInstanceFilePath = (String) savedInstanceState.get(savedInstanceString);

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(savedInstanceString, savedInstanceFilePath);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(savedInstanceState != null){
            savedInstanceFilePath = (String) savedInstanceState.get(savedInstanceString);
        }
        mPagerAdapter = new ImageAdapter(getContext(), getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size));
        gridView.setAdapter(mPagerAdapter);
        setViewMode(ViewStates.normal);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkMarshmallowPermissions();
        }
        else
            load();
    }

    DialogInterface.OnClickListener dialogDeleteClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final File folder = mPagerAdapter.getFiles().get(0).getFile().getParentFile();
                            for (int i = 0; i< mPagerAdapter.getFiles().size(); i++)
                            {
                                if (mPagerAdapter.getFiles().get(i).IsSelected())
                                {
                                    BitmapHelper.DeleteFile(mPagerAdapter.getFiles().get(i));
                                }
                            }
                            gridView.post(new Runnable() {
                                @Override
                                public void run() {
                                    MediaScannerManager.ScanMedia(getContext(),folder);
                                    mPagerAdapter.notifyDataSetChanged();
                                }
                            });

                        }
                    }).start();

                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };





    private void load()
    {
        if (savedInstanceFilePath == null)
            mPagerAdapter.loadDCIMFolders();
        else
            mPagerAdapter.loadFiles(new File(savedInstanceFilePath));

    }

    private void checkMarshmallowPermissions() {
        if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,

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
                if (!mPagerAdapter.GetFileHolder(position).IsFolder()) {
                    final Intent i = new Intent(getActivity(), ScreenSlideActivity.class);
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

                }
                break;
            case selection:
            {
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
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
    {
        return  super.onItemLongClick(parent,view,position,id);
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this.getContext(), v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int i = item.getItemId();
                if (i == R.id.all)
                {
                    filetypeButton.setText(R.string.ALL);
                    formatsToShow = FormatTypes.all;
                }
                else if (i == R.id.raw)
                {
                    filetypeButton.setText("RAW");
                    formatsToShow = FormatTypes.raw;
                }
                else if (i == R.id.bayer)
                {
                    filetypeButton.setText("BAYER");
                    formatsToShow = FormatTypes.raw;
                }
                else if (i == R.id.dng)
                {
                    filetypeButton.setText("DNG");
                    formatsToShow = FormatTypes.dng;
                }
                else if (i == R.id.jps)
                {
                    filetypeButton.setText("JPS");
                    formatsToShow = FormatTypes.jps;
                }
                else if (i == R.id.jpg)
                {
                    filetypeButton.setText("JPG");
                    formatsToShow = FormatTypes.jpg;
                }
                else if (i == R.id.mp4)
                {
                    filetypeButton.setText("MP4");
                    formatsToShow = FormatTypes.mp4;
                }
                mPagerAdapter.SetFormatToShow(formatsToShow);
                if (savedInstanceFilePath != null)
                    mPagerAdapter.loadFiles(new File(savedInstanceFilePath));

                return false;

            }
        });
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.filetypepopupmenu, popup.getMenu());
        popup.show();
    }

    private View.OnClickListener onGobBackClick = new View.OnClickListener() {
        @Override
        public void onClick(View view)
        {
            if (currentViewState == ViewStates.normal) {
                if (mPagerAdapter.getFiles() != null && mPagerAdapter.getFiles().size() > 0) {
                    String topPath =  mPagerAdapter.GetFileHolder(0).getFile().getParentFile().getParentFile().getAbsolutePath() + "/";
                    String inter = StringUtils.GetInternalSDCARD() + StringUtils.DCIMFolder;
                    String external = StringUtils.GetExternalSDCARD() + StringUtils.DCIMFolder;

                    if ((inter.contains(topPath) && topPath.length() < inter.length() || topPath.equals(inter))
                            || (external.contains(topPath) && topPath.length() < external.length() || topPath.equals(external)))
                    {
                        if(topPath.equals(inter) || topPath.equals(external)) {
                            savedInstanceFilePath = null;
                            mPagerAdapter.loadDCIMFolders();
                        }
                        else
                            getActivity().finish();
                    }
                    else {
                        mPagerAdapter.loadFiles(mPagerAdapter.GetFileHolder(0).getFile());
                        savedInstanceFilePath =  mPagerAdapter.GetFileHolder(0).getFile().getAbsolutePath();
                    }
                }
                else
                    mPagerAdapter.loadDCIMFolders();
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

    private View.OnClickListener onDeltedButtonClick = new View.OnClickListener() {
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
                if (!StringUtils.IS_L_OR_BIG() || StringUtils.WRITE_NOT_EX_AND_L_ORBigger())
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage(R.string.delete_files).setPositiveButton(R.string.yes, dialogDeleteClickListener)
                            .setNegativeButton(R.string.no, dialogDeleteClickListener).show();
                    setViewMode(ViewStates.normal);
                }
                else
                {
                    DocumentFile sdDir = FileUtils.getExternalSdDocumentFile();
                    if (sdDir == null) {
                        I_Activity i_activity = (I_Activity) getActivity();
                        i_activity.ChooseSDCard(GridViewFragment.this);
                    }
                    else
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage(R.string.delete_files).setPositiveButton(R.string.yes, dialogDeleteClickListener)
                                .setNegativeButton(R.string.no, dialogDeleteClickListener).show();
                        setViewMode(ViewStates.normal);
                    }
                }


            }
        }
    };


    @Override
    public void onActivityResultCallback(Uri uri) {

    }

    private View.OnClickListener onRawToDngClick = new View.OnClickListener() {
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
                ArrayList<String> ar = new ArrayList<String>();
                for (FileHolder f : mPagerAdapter.getFiles()) {
                    if (f.IsSelected() &&
                       (f.getFile().getAbsolutePath().endsWith(StringUtils.FileEnding.RAW) ||f.getFile().getAbsolutePath().endsWith(StringUtils.FileEnding.BAYER))) {
                        ar.add(f.getFile().getAbsolutePath());
                    }

                }
                for (FileHolder f : mPagerAdapter.getFiles()) {
                    f.SetSelected(false);
                }
                setViewMode(ViewStates.normal);
                final Intent i = new Intent(getActivity(), DngConvertingActivity.class);
                String[] t = new String[ar.size()];
                ar.toArray(t);
                i.putExtra(DngConvertingFragment.EXTRA_FILESTOCONVERT, t);
                startActivity(i);
            }
        }

    };


    private void setViewMode(ViewStates viewState)
    {
        this.currentViewState = viewState;
        mPagerAdapter.SetViewState(currentViewState);
        //mPagerAdapter.notifyDataSetChanged();
        switch (viewState)
        {
            case normal:
            {
                if ((formatsToShow == FormatTypes.raw && lastFormat != FormatTypes.raw))
                {
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
            }
            case selection:
            {
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
                        mPagerAdapter.loadFiles(new File(savedInstanceFilePath));
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
        filesSelected.setText(getString(R.string.files_selected) + filesSelectedCount);
    }


}


