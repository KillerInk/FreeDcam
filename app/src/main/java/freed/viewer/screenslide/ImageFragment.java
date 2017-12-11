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

package freed.viewer.screenslide;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.ortiz.touch.TouchImageView;
import com.troop.freedcam.R.dimen;
import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import java.lang.ref.WeakReference;

import freed.ActivityInterface;
import freed.image.ImageManager;
import freed.image.ImageTask;
import freed.utils.Log;
import freed.viewer.holder.FileHolder;
import freed.viewer.screenslide.ScreenSlideFragment.FragmentClickClistner;

/**
 * Created by troop on 21.08.2015.
 */
public class ImageFragment extends Fragment
{

    public interface I_WaitForWorkFinish
    {
        void onHistogramData(int[] histodata, int position);
    }

    public int getPosition;

    private final String TAG = ImageFragment.class.getSimpleName();
    private TouchImageView imageView;
    private FileHolder file;
    private FragmentClickClistner onClickListener;
    private ProgressBar progressBar;
    private int [] histogramData;
    private boolean isWorking;
    private I_WaitForWorkFinish waitForWorkFinish;
    private int position = -1;

    /**
     * Set the file to load by this fragment
     * @param filepath
     */
    public void SetFilePath(FileHolder filepath)
    {
        file = filepath;
    }

    public FileHolder getFile()
    {
        return file;
    }

    public boolean IsWorking()
    {
        return isWorking;
    }

    public void SetWaitForWorkFinishLisnter(I_WaitForWorkFinish workFinish, int position)
    {
        this.position = position;
        waitForWorkFinish = workFinish;
    }

    /**
     *
     * @return the File attached to this view
     */
    public FileHolder GetFilePath()
    {
        return file;
    }

    public int[] GetHistogramData()
    {
        return histogramData;
    }

    public void SetOnclickLisnter(FragmentClickClistner onClickListener)
    {
        this.onClickListener = onClickListener;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,savedInstanceState);

        int mImageThumbSize = getResources().getDimensionPixelSize(dimen.image_thumbnail_size);
        View view = inflater.inflate(layout.freedviewer_screenslide_imageframent, container, false);
        imageView = (TouchImageView) view.findViewById(id.imageView_PicView);

        progressBar = (ProgressBar) view.findViewById(id.progressBar_screenslideImageview);
        imageView.setOnClickListener(onImageClick);
        progressBar.setVisibility(View.VISIBLE);
        if (file != null) {
            ImageManager.putImageLoadTask(new BitmapLoader(file,this));
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        histogramData = null;
    }

    private final OnClickListener onImageClick = new OnClickListener() {
        @Override
        public void onClick(View v)
        {
            if (onClickListener != null)
                onClickListener.onFragmentClick(ImageFragment.this);
        }
    };

    private class BitmapLoader extends ImageTask
    {
        private WeakReference<ImageFragment> imageviewRef;
        private FileHolder file;

        public BitmapLoader(FileHolder file, ImageFragment imageFragment)
        {
            this.file = file;
            imageviewRef = new WeakReference<ImageFragment>(imageFragment);
        }

        @Override
        public boolean process() {
            if (getActivity() == null) {
                Log.e(TAG, "ImageLoaderTask: Activity is null");
                return false;
            }
            Log.d(TAG, "ImageLoaderTask: LoadImage:" + file.getFile().getName());
            final Bitmap response = ((ActivityInterface)getActivity()).getBitmapHelper().getBitmap(file,false);
            createHistogramm(response);
            if (waitForWorkFinish != null && position >-1)
                waitForWorkFinish.onHistogramData(histogramData, position);
            waitForWorkFinish = null;
            Log.d(TAG, "ImageLoaderTask: LoadImage Done:" + file.getFile().getName());
            if (imageviewRef != null && response != null) {
                final ImageFragment imageFragment = imageviewRef.get();
                if (imageFragment != null && imageFragment.getFile() == file)
                {
                    Log.d(TAG, "set bitmap to imageview");
                    imageView.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            imageView.setImageBitmap(response);
                        }
                    });

                }
                else
                    response.recycle();
            }
            else
            {
                if (response != null)
                    response.recycle();
            }
            return true;
        }
    }

    private void createHistogramm(Bitmap bitmap)
    {
        Log.d(TAG, "Histodata");
        if(bitmap == null || bitmap.isRecycled())
            return;
        int [] histo = new int [ 256 * 3 ];
        int w = bitmap.getWidth ();
        int h = bitmap.getHeight ();
        int [] pixels = new int [ w * h ];
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h);
        for ( int i = 0 ; i < w ; i+=4) {
            for ( int j = 0 ; j < h ; j+=4) {
                int index = j * w + i ;
                int r = Color.red ( pixels [ index ]);
                int g = Color.green ( pixels [ index ]);
                int b = Color.blue ( pixels [ index ]);
                histo [ r ]++;
                histo [ 256 + g ]++;
                histo [ 512 + b ]++;
            }
        }
        pixels = null;
        histogramData = histo;
    }

}
