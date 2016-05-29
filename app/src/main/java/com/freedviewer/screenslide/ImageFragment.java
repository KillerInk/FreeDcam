package com.freedviewer.screenslide;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.freedcam.utils.FreeDPool;
import com.freedcam.utils.Logger;
import com.freedviewer.helper.BitmapHelper;
import com.freedviewer.holder.FileHolder;
import com.ortiz.touch.TouchImageView;
import com.troop.freedcam.R;

import java.io.File;

/**
 * Created by troop on 21.08.2015.
 */
public class ImageFragment extends Fragment
{

    public interface I_WaitForWorkFinish
    {
        void HistograRdyToSet(int[] histodata, int position);
    }

    private final String TAG = ImageFragment.class.getSimpleName();
    private TouchImageView imageView;
    private FileHolder file;
    private int mImageThumbSize = 0;
    private ScreenSlideFragment.FragmentClickClistner onClickListener;
    private ProgressBar progressBar;
    private int [] histogramData;
    private boolean isWorking = false;
    private I_WaitForWorkFinish waitForWorkFinish;
    private int position = -1;
    private BitmapHelper bitmapHelper;

    public void SetBitmapHelper(BitmapHelper bitmapHelper)
    {
        this.bitmapHelper =bitmapHelper;
    }

    /**
     * Set the file to load by this fragment
     * @param filepath
     */
    public void SetFilePath(FileHolder filepath)
    {
        this.file = filepath;
        if (imageView != null) {
            FreeDPool.Execute(new Runnable() {
                @Override
                public void run() {
                    loadImage();
                }
            });

        }
    }

    public boolean IsWorking()
    {
        return isWorking;
    }

    public void SetWaitForWorkFinishLisnter(I_WaitForWorkFinish workFinish, int position)
    {
        this.position = position;
        this.waitForWorkFinish = workFinish;
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
        return  histogramData;
    }

    public void SetOnclickLisnter(ScreenSlideFragment.FragmentClickClistner onClickListener)
    {
        this.onClickListener = onClickListener;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,savedInstanceState);

        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        View view = inflater.inflate(R.layout.imageframent, container, false);
        this.imageView = (TouchImageView) view.findViewById(R.id.imageView_PicView);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar_screenslideImageview);
        imageView.setOnClickListener(onImageClick);
        progressBar.setVisibility(View.VISIBLE);
        if (file != null) {
            FreeDPool.Execute(new Runnable() {
                @Override
                public void run() {
                    loadImage();
                }
            });
        }


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private View.OnClickListener onImageClick = new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            if (ImageFragment.this.onClickListener != null)
                ImageFragment.this.onClickListener.onClick(ImageFragment.this);
        }
    };

    private void loadImage()
    {
        Logger.d(TAG,"loadImage()"+ file.getFile().getName());
        isWorking = true;
        final Bitmap response = getBitmap();
        imageView.post(new Runnable() {
            @Override
            public void run()
            {
                progressBar.setVisibility(View.GONE);
                imageView.setImageBitmap(response);
            }
        });
        if (waitForWorkFinish != null && position >-1)
            waitForWorkFinish.HistograRdyToSet(histogramData,position);
        waitForWorkFinish = null;
        isWorking = false;
    }

    private Bitmap getBitmap()
    {
        Bitmap response =null;
        try {
            response = bitmapHelper.getBitmap(file.getFile(),false,mImageThumbSize,mImageThumbSize);
            createHistogramm(response);
        }
        catch (IllegalArgumentException ex)
        {
            Logger.exception(ex);
        }
        return response;
    }

    private void createHistogramm(Bitmap bitmap)
    {
        if(bitmap == null || bitmap.isRecycled())
            return;
        int [] histo = new int [ 256 * 3 ];
        int w = bitmap.getWidth ();
        int h = bitmap.getHeight ();
        int [] pixels = new int [ w * h ];
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h);
        for ( int i = 0 ; i < w ; i ++) {
            for ( int j = 0 ; j < h ; j ++) {
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
