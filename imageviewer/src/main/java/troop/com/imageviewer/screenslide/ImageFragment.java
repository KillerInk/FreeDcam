package troop.com.imageviewer.screenslide;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.ortiz.touch.TouchImageView;
import com.troop.filelogger.Logger;
import com.troop.freedcam.ui.FreeDPool;

import java.io.File;

import troop.com.imageviewer.BitmapHelper;
import troop.com.imageviewer.R;
import troop.com.imageviewer.holder.FileHolder;

/**
 * Created by troop on 21.08.2015.
 */
public class ImageFragment extends Fragment
{
    private final String TAG = ImageFragment.class.getSimpleName();
    private TouchImageView imageView;
    private FileHolder file;
    private int mImageThumbSize = 0;
    private ScreenSlideFragment.FragmentClickClistner onClickListener;
    private ProgressBar progressBar;

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

    public FileHolder GetFilePath()
    {
        return file;
    }

    public void SetOnclickLisnter(ScreenSlideFragment.FragmentClickClistner onClickListener)
    {
        this.onClickListener = onClickListener;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        return inflater.inflate(R.layout.imageframent, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.imageView = (TouchImageView)view.findViewById(R.id.imageView_PicView);

        if(savedInstanceState != null && file == null)
        {
            file = new FileHolder(new File((String) savedInstanceState.get(ScreenSlideFragment.SAVESTATE_FILEPATH)),false);
        }

        progressBar = (ProgressBar)view.findViewById(R.id.progressBar_screenslideImageview);
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
        Logger.d(TAG,"onViewCreated");
    }



    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        if (file != null && file.getFile() != null && file.getFile().getAbsolutePath() != null)
            outState.putString(ScreenSlideFragment.SAVESTATE_FILEPATH, file.getFile().getAbsolutePath());
        super.onSaveInstanceState(outState);
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
        final Bitmap response = getBitmap();
        imageView.post(new Runnable() {
            @Override
            public void run()
            {
                progressBar.setVisibility(View.GONE);
                imageView.setImageBitmap(response);
            }
        });
    }

    private Bitmap getBitmap()
    {
        Bitmap response =null;
        try {
            response = BitmapHelper.getBitmap(file.getFile(),false,mImageThumbSize,mImageThumbSize);
        }
        catch (IllegalArgumentException ex)
        {
            Logger.exception(ex);
        }
        return response;
    }

    public void setTag(int tag) {
        int tag1 = tag;
    }
}
