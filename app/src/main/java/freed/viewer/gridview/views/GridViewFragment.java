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

package freed.viewer.gridview.views;

import android.app.AlertDialog;
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
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.PopupMenu;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.troop.freedcam.R;
import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;
import com.troop.freedcam.R.menu;
import com.troop.freedcam.R.string;
import com.troop.freedcam.databinding.FreedviewerGridviewfragmentBinding;

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

import freed.viewer.gridview.adapter.ImageAdapter;
import freed.viewer.gridview.enums.RequestModes;
import freed.viewer.gridview.enums.ViewStates;
import freed.viewer.gridview.modelview.GridViewFragmentModelView;
import freed.viewer.screenslide.ScreenSlideFragment;
import freed.viewer.stack.DngStackActivity;
import freed.viewer.stack.StackActivity;

/**
 * Created by troop on 11.12.2015.
 */
public class GridViewFragment extends Fragment implements I_OnActivityResultCallback ,AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener
{

    private FreedviewerGridviewfragmentBinding gridviewfragmentBinding;
    private GridViewFragmentModelView gridViewFragmentModelView;

    public final int STACK_REQUEST = 44;
    public final int DNGCONVERT_REQUEST = 45;
    private boolean firststart = false;

    private ImageAdapter mPagerAdapter;

    private final String TAG = GridViewFragment.class.getSimpleName();


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
    //private boolean isRootDir = true;

    private ActivityInterface viewerActivityInterface;
    private ScreenSlideFragment.ButtonClick onGridItemClick;
    //private BaseHolder folderToShow;

    public int DEFAULT_ITEM_TO_SET = 0;

    public void SetPosition(int position)
    {
        gridviewfragmentBinding.gridViewBase.smoothScrollToPosition(position);
    }

    public View GetGridItem(int position)
    {
        return gridviewfragmentBinding.gridViewBase.getChildAt(position);
    }



    public void SetOnGridItemClick(ScreenSlideFragment.ButtonClick onGridItemClick)
    {
        this.onGridItemClick = onGridItemClick;
    }

    public void NotifyDataSetChanged(List<BaseHolder> files)
    {
        mPagerAdapter.setFiles(files);
        if (!firststart) {
            if (files != null
                    && files.size() > 0
                    && files.get(0) instanceof UriHolder
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
        gridviewfragmentBinding = DataBindingUtil.inflate(inflater, layout.freedviewer_gridviewfragment, container, false);
        gridViewFragmentModelView = new ViewModelProvider(this).get(GridViewFragmentModelView.class);

        gridviewfragmentBinding.gridViewBase.setOnItemClickListener(this);
        gridviewfragmentBinding.gridViewBase.setOnItemLongClickListener(this);
        gridviewfragmentBinding.gridViewBase.smoothScrollToPosition(DEFAULT_ITEM_TO_SET);

        gridviewfragmentBinding.buttonGoback.setOnClickListener(gridViewFragmentModelView.onGobBackClick);

        gridviewfragmentBinding.buttonFiletype.setOnClickListener(v -> showFileSelectionPopup(v));

        gridviewfragmentBinding.buttonOptions.setOnClickListener(v -> {
            if (gridViewFragmentModelView.isRootDir())
                return;
            PopupMenu popup = new PopupMenu(getContext(), v);

            popup.getMenu().add(0,0,0, "Delete File");
            if (RenderScriptManager.isSupported())
               popup.getMenu().add(0,1,1, "StackJpeg");
            popup.getMenu().add(0,2,2, "Raw to Dng");
            popup.getMenu().add(0,3,3, "DngStack");
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId())
                {
                    case 0:
                        gridViewFragmentModelView.onDeltedButtonClick.onClick(null);
                        break;
                    case 1:
                        gridViewFragmentModelView.onStackClick.onClick(null);
                        break;
                    case 2:
                        gridViewFragmentModelView.onRawToDngClick.onClick(null);
                        break;
                    case 3:
                        gridViewFragmentModelView.onDngStackClick.onClick(null);
                }
                return false;
            });
            popup.show();
        });

        gridviewfragmentBinding.buttonDoAction.setVisibility(View.GONE);
        gridViewFragmentModelView.getIntentModel().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                Intent i = new Intent(getActivity(), gridViewFragmentModelView.getIntentModel().getIntentClass());
                ArrayList<String> ar = gridViewFragmentModelView.getIntentModel().getAr();
                String[] t = new String[ar.size()];
                ar.toArray(t);
                i.putExtra(DngConvertingFragment.EXTRA_FILESTOCONVERT, t);
                getActivity().startActivityForResult(i, STACK_REQUEST);
            }
        });

        gridViewFragmentModelView.getFinishActivityModel().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                getActivity().finish();
            }
        });

        gridViewFragmentModelView.getAlterDialogModel().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(R.string.delete_files).setPositiveButton(R.string.yes, dialogDeleteClickListener)
                        .setNegativeButton(R.string.no, dialogDeleteClickListener).show();
            }
        });
        firstload();

        return gridviewfragmentBinding.getRoot();
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
            gridViewFragmentModelView.getViewStateModel().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable sender, int propertyId) {
                    mPagerAdapter.SetViewState(gridViewFragmentModelView.getViewStateModel().getCurrentViewState());
                }
            });
            gridviewfragmentBinding.gridViewBase.setAdapter(mPagerAdapter);
            gridViewFragmentModelView.setViewMode(ViewStates.normal);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }



    private void showFileSelectionPopup(View v) {
        PopupMenu popup = new PopupMenu(getContext(), v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });

        popup.setOnMenuItemClickListener(gridViewFragmentModelView.popupMenuItemClickListner);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(menu.filetypepopupmenu, popup.getMenu());
        popup.show();
    }





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
            NotifyDataSetChanged(viewerActivityInterface.getFileListController().getFiles());
        }

    }

    /*
    DELTE FILES STUFF
     */
    private final DialogInterface.OnClickListener dialogDeleteClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    gridViewFragmentModelView.deleteFiles();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    gridViewFragmentModelView.setViewMode(ViewStates.normal);
                    break;
            }
        }
    };




}


