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

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.fragment.app.Fragment;

import com.troop.freedcam.BR;
import com.troop.freedcam.R;
import com.troop.freedcam.R.layout;
import com.troop.freedcam.R.menu;
import com.troop.freedcam.databinding.FreedviewerGridviewfragmentBinding;

import java.util.ArrayList;

import freed.ActivityAbstract;
import freed.ActivityInterface;
import freed.ActivityInterface.I_OnActivityResultCallback;
import freed.image.ImageManager;
import freed.renderscript.RenderScriptManager;
import freed.utils.Log;
import freed.viewer.dngconvert.DngConvertingFragment;

import freed.viewer.gridview.adapter.ImageAdapter;
import freed.viewer.gridview.enums.ViewStates;
import freed.viewer.gridview.modelview.GridViewFragmentModelView;
import freed.viewer.screenslide.views.ScreenSlideFragment;

/**
 * Created by troop on 11.12.2015.
 */
public class GridViewFragment extends Fragment implements I_OnActivityResultCallback
{

    private FreedviewerGridviewfragmentBinding gridviewfragmentBinding;
    private GridViewFragmentModelView gridViewFragmentModelView;

    public final int STACK_REQUEST = 44;
    public final int DNGCONVERT_REQUEST = 45;

    private ImageAdapter mPagerAdapter;

    private final String TAG = GridViewFragment.class.getSimpleName();

    private ActivityInterface viewerActivityInterface;
    private ScreenSlideFragment.ButtonClick onGridItemClick;

    public int DEFAULT_ITEM_TO_SET = 0;

    public View GetGridItem(int position)
    {
        return gridviewfragmentBinding.gridViewBase.getChildAt(position);
    }

    public void setGridViewFragmentModelView(GridViewFragmentModelView gridViewFragmentModelView)
    {
        Log.d(TAG,"setGridViewFragmentModelView");
        this.gridViewFragmentModelView = gridViewFragmentModelView;
        bindGridModelView();
    }

    public void SetOnGridItemClick(ScreenSlideFragment.ButtonClick onGridItemClick)
    {
        this.onGridItemClick = onGridItemClick;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        viewerActivityInterface = (ActivityInterface) getActivity();
        gridviewfragmentBinding = DataBindingUtil.inflate(inflater, layout.freedviewer_gridviewfragment, container, false);
        bindGridModelView();
        return gridviewfragmentBinding.getRoot();
    }

    private void bindGridModelView() {
        if (gridviewfragmentBinding == null || gridViewFragmentModelView == null)
            return;
        gridviewfragmentBinding.setGridfragmentmodel(gridViewFragmentModelView);
        gridviewfragmentBinding.gridViewBase.setOnItemClickListener(gridViewFragmentModelView.onItemClickListener);
        gridviewfragmentBinding.gridViewBase.smoothScrollToPosition(DEFAULT_ITEM_TO_SET);
        gridViewFragmentModelView.setButtonClick(onGridItemClick);

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
                Builder builder = new Builder(getContext());
                builder.setMessage(R.string.delete_files).setPositiveButton(R.string.yes, dialogDeleteClickListener)
                        .setNegativeButton(R.string.no, dialogDeleteClickListener).show();
            }
        });

        gridViewFragmentModelView.getIntentSenderModel().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                try {
                    startIntentSenderForResult(gridViewFragmentModelView.getIntentSenderModel().getIntentSender(), ActivityAbstract.DELETE_REQUEST_CODE,null,0,0,0,null);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });
        firstload();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(gridViewFragmentModelView.getFilesHolderModel().getFiles() != null)
            gridViewFragmentModelView.getFilesHolderModel().setFiles(gridViewFragmentModelView.getFilesHolderModel().getFiles());
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
            mPagerAdapter = new ImageAdapter();
            gridViewFragmentModelView.getViewStateModel().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable sender, int propertyId) {
                    mPagerAdapter.SetViewState(gridViewFragmentModelView.getViewStateModel().getCurrentViewState());
                }
            });
            gridViewFragmentModelView.getFilesHolderModel().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable sender, int propertyId) {
                    if (propertyId == BR.files) {
                        getActivity().runOnUiThread(() -> mPagerAdapter.setGridImageViewModels(gridViewFragmentModelView.getFilesHolderModel().getGridImageViewModels()));
                    }
                }
            });
            gridviewfragmentBinding.gridViewBase.setAdapter(mPagerAdapter);
            gridViewFragmentModelView.setViewMode(ViewStates.normal);
        }
    }


    @Override
    public void onActivityResultCallback(Uri uri) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == STACK_REQUEST || requestCode == DNGCONVERT_REQUEST)
            gridViewFragmentModelView.refreshCurrentFolder();
        if (requestCode == ActivityAbstract.DELETE_REQUEST_CODE)
        {
            gridViewFragmentModelView.deleteNextFile();
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


