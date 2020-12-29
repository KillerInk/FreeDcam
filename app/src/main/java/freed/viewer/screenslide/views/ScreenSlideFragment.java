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

package freed.viewer.screenslide.views;


import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.RecoverableSecurityException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.troop.freedcam.BR;
import com.troop.freedcam.R;
import com.troop.freedcam.R.dimen;
import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;
import com.troop.freedcam.databinding.FreedviewerScreenslideFragmentBinding;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import freed.ActivityAbstract;
import freed.ActivityInterface;
import freed.ActivityInterface.I_OnActivityResultCallback;
import freed.file.FileListController;
import freed.file.holder.BaseHolder;
import freed.file.holder.FileHolder;
import freed.file.holder.UriHolder;
import freed.image.ImageManager;
import freed.image.ImageTask;
import freed.settings.SettingsManager;
import freed.utils.Log;
import freed.utils.StringUtils.FileEnding;
import freed.viewer.screenslide.adapter.ScreenSlidePagerAdapter;
import freed.viewer.screenslide.models.ImageFragmentModel;
import freed.viewer.screenslide.modelview.ScreenSlideFragmentModelView;


/**
 * Created by troop on 18.09.2015.
 */
public class ScreenSlideFragment extends Fragment implements ViewPager.OnPageChangeListener, I_OnActivityResultCallback
{
    public final String TAG = ScreenSlideFragment.class.getSimpleName();
    public interface ButtonClick
    {
        void onButtonClick(int position, View view);
    }

    public interface FragmentClickClistner
    {
        void onFragmentClick(Fragment fragment);
    }

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private ScreenSlidePagerAdapter mPagerAdapter;
    private ScreenSlideFragmentModelView screenSlideFragmentModelView;


    public int defitem = -1;
    public FileListController.FormatTypes filestoshow = FileListController.FormatTypes.all;
    private ButtonClick backClickListner;
    //hold the showed folder_to_show



    private ActivityInterface activityInterface;

    private boolean showExifInfo = false;
    private int position;

    private FreedviewerScreenslideFragmentBinding screenslideFragmentBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,savedInstanceState);
        screenslideFragmentBinding =  DataBindingUtil.inflate(inflater, layout.freedviewer_screenslide_fragment, container, false);

        int mImageThumbSize = getResources().getDimensionPixelSize(dimen.image_thumbnail_size);
        activityInterface = (ActivityInterface) getActivity();

        // Instantiate a ViewPager and a PagerAdapter.




        bind();
        return screenslideFragmentBinding.getRoot();
    }

    public void setScreenSlideFragmentModelView(ScreenSlideFragmentModelView screenSlideFragmentModelView)
    {
        this.screenSlideFragmentModelView = screenSlideFragmentModelView;
        bind();
    }

    private void bind()
    {
        if (screenslideFragmentBinding != null && screenSlideFragmentModelView != null)
        {
            screenslideFragmentBinding.setScreenslideFragmentModel(screenSlideFragmentModelView);

            mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager(),screenslideFragmentBinding.pager,screenSlideFragmentModelView.getFragmentclickListner());
            screenslideFragmentBinding.pager.setAdapter(mPagerAdapter);
            screenslideFragmentBinding.pager.setOffscreenPageLimit(2);
            screenslideFragmentBinding.pager.addOnPageChangeListener(this);
            screenslideFragmentBinding.pager.setCurrentItem(position, false);

            screenSlideFragmentModelView.getFilesHolderModel().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable sender, int propertyId) {
                    if (propertyId == BR.files)
                        mPagerAdapter.setImageFragmentModels(screenSlideFragmentModelView.getFilesHolderModel().getImageFragmentModels());
                }
            });

            screenslideFragmentBinding.buttonCloseView.setOnClickListener(v -> {
                if (backClickListner != null) {
                    backClickListner.onButtonClick(screenslideFragmentBinding.pager.getCurrentItem(), screenslideFragmentBinding.getRoot());
                    screenslideFragmentBinding.pager.setCurrentItem(0);
                } else
                    getActivity().finish();
            });

            screenslideFragmentBinding.buttonPlay.setOnClickListener(onplayClick);
            screenSlideFragmentModelView.getPlayButton().setVisibility(false);
            screenSlideFragmentModelView.getDeleteButton().setVisibility(false);
            screenslideFragmentBinding.buttonDelete.setOnClickListener(onDeleteButtonClick);
        }
    }

    private final DialogInterface.OnClickListener onDeleteAlertButtonClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    try {
                        screenSlideFragmentModelView.getFilesHolderModel().getFileListController().DeleteFile(screenSlideFragmentModelView.getFolder_to_show());
                    }
                    catch (SecurityException ex)
                    {
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            if (ex instanceof RecoverableSecurityException)
                            {
                                RecoverableSecurityException rex = (RecoverableSecurityException)ex;
                                try {
                                    startIntentSenderForResult(rex.getUserAction().getActionIntent().getIntentSender(), ActivityAbstract.DELETE_REQUEST_CODE,null,0,0,0,null);
                                } catch (IntentSender.SendIntentException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };

    private View.OnClickListener onplayClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (screenSlideFragmentModelView.getFolder_to_show() == null)
                return;
            if (!screenSlideFragmentModelView.getFolder_to_show().getName().endsWith(FileEnding.RAW) || !screenSlideFragmentModelView.getFolder_to_show().getName().endsWith(FileEnding.BAYER)) {
                Uri uri = null;
                if (screenSlideFragmentModelView.getFolder_to_show() instanceof FileHolder) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        uri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".provider", ((FileHolder) screenSlideFragmentModelView.getFolder_to_show()).getFile());
                    else
                        uri = Uri.fromFile(((FileHolder) screenSlideFragmentModelView.getFolder_to_show()).getFile());
                }
                else if (screenSlideFragmentModelView.getFolder_to_show() instanceof UriHolder)
                    uri = ((UriHolder)screenSlideFragmentModelView.getFolder_to_show()).getMediaStoreUri();

                Intent i;
                if (screenSlideFragmentModelView.getFolder_to_show().getName().endsWith(FileEnding.MP4))
                {
                    i = new Intent(Intent.ACTION_VIEW);
                    i.setDataAndType(uri, "video/*");
                }
                else {
                    i = new Intent(Intent.ACTION_EDIT);
                    i.setDataAndType(uri, "image/*");
                }
                i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                Intent chooser = Intent.createChooser(i, "Choose App");
                chooser.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                //startActivity(i);
                if (i.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(chooser);
                }

            }
        }
    };

    private View.OnClickListener onDeleteButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP || Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !SettingsManager.getInstance().GetWriteExternal()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Delete File?").setPositiveButton("Yes", onDeleteAlertButtonClick)
                        .setNegativeButton("No", onDeleteAlertButtonClick).show();
            } else {
                DocumentFile sdDir = FileListController.getExternalSdDocumentFile(getContext());
                if (sdDir == null) {

                    activityInterface.ChooseSDCard(ScreenSlideFragment.this);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Delete File?").setPositiveButton("Yes", onDeleteAlertButtonClick)
                            .setNegativeButton("No", onDeleteAlertButtonClick).show();
                }
            }


        }
    };


    public void SetPostition(int position)
    {
        this.position = position;
        if (screenslideFragmentBinding.pager != null)
            screenslideFragmentBinding.pager.setCurrentItem(position, false);
    }


    public ScreenSlideFragment()
    {

    }

    public void setOnBackClickListner(ButtonClick thumbClick)
    {
        backClickListner = thumbClick;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
    {
        screenSlideFragmentModelView.updateUi(mPagerAdapter.getCurrentFile());
        ImageFragment fragment = (ImageFragment) mPagerAdapter.getRegisteredFragment(position);
        screenslideFragmentBinding.setImagefragment(mPagerAdapter.getCurrentImageFragmentModel());

    }

    @Override
    public void onPageSelected(int position)
    {


    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onActivityResultCallback(Uri uri) {

    }

}
