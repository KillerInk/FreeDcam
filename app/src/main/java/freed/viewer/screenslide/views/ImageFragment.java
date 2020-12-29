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


import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.ortiz.touch.TouchImageView;
import com.troop.freedcam.R.dimen;
import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;
import com.troop.freedcam.databinding.FreedviewerScreenslideImageframentBinding;

import java.lang.ref.WeakReference;

import freed.ActivityInterface;
import freed.file.holder.BaseHolder;
import freed.image.ImageManager;
import freed.image.ImageTask;
import freed.utils.Log;
import freed.viewer.screenslide.models.ImageFragmentModel;
import freed.viewer.screenslide.views.ScreenSlideFragment.FragmentClickClistner;

/**
 * Created by troop on 21.08.2015.
 */
public class ImageFragment extends Fragment
{

    public int getPosition;

    private final String TAG = ImageFragment.class.getSimpleName();
    private FragmentClickClistner onClickListener;

    private FreedviewerScreenslideImageframentBinding imageframentBinding;
    private ImageFragmentModel imageFragmentModel;


    public void setImageFragmentModel(ImageFragmentModel imageFragmentModel)
    {
        this.imageFragmentModel = imageFragmentModel;
        bind();
    }

    public ImageFragmentModel getImageFragmentModel() {
        return imageFragmentModel;
    }


    public int[] GetHistogramData()
    {
        return imageFragmentModel.getHistodata();
    }

    public void SetOnclickLisnter(FragmentClickClistner onClickListener)
    {
        this.onClickListener = onClickListener;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,savedInstanceState);
        imageframentBinding =  DataBindingUtil.inflate(inflater, layout.freedviewer_screenslide_imageframent, container, false);
        int mImageThumbSize = getResources().getDimensionPixelSize(dimen.image_thumbnail_size);

        imageframentBinding.imageViewPicView.setOnClickListener(onImageClick);
        imageframentBinding.progressBarScreenslideImageview.setVisibility(View.VISIBLE);
        bind();
        return imageframentBinding.getRoot();
    }

    private void bind()
    {
        if (imageframentBinding !=null && imageFragmentModel != null)
            imageframentBinding.setImageFragmentModel(imageFragmentModel);
    }

    @Override
    public synchronized void onDestroyView() {
        super.onDestroyView();
        if (imageFragmentModel != null)
            imageFragmentModel.setHistodata(null);
    }

    private final OnClickListener onImageClick = new OnClickListener() {
        @Override
        public void onClick(View v)
        {
            if (onClickListener != null)
                onClickListener.onFragmentClick(ImageFragment.this);
        }
    };

}
