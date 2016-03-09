package troop.com.imageviewer;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.defcomk.jni.libraw.RawUtils;
import com.ortiz.touch.TouchImageView;
import com.troop.filelogger.Logger;

import java.io.File;

/**
 * Created by troop on 21.08.2015.
 */
public class ImageFragment extends Fragment  {
    final String TAG = ImageFragment.class.getSimpleName();
    TouchImageView imageView;
    private File file;
    ProgressBar spinner;
    ViewPager pager;
    Button playVideo;
    CacheHelper cacheHelper;
    int mImageThumbSize = 0;


    public ScreenSlideFragment activity;

    private final int animationTime = 500;

    public void SetFilePath(File filepath, CacheHelper cacheHelper)
    {
        this.file = filepath;
        this.cacheHelper = cacheHelper;
    }

    public File GetFilePath()
    {
        return file;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.imageframent, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.imageView = (TouchImageView)view.findViewById(R.id.imageView_PicView);
        this.spinner = (ProgressBar)view.findViewById(R.id.progressBar);
        if(savedInstanceState != null && file == null)
        {
            file = new File((String) savedInstanceState.get(ScreenSlideFragment.SAVESTATE_FILEPATH));
        }
        pager = (ViewPager)view.findViewById(R.id.pager);

        imageView.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public void onLongPress(MotionEvent e) {
                    Log.d("ImageFragment", "onLongPress");
                        activity.showHideBars();
                }
            });

                } else {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });
        this.playVideo = (Button)view.findViewById(R.id.button_playvideo);
        playVideo.setVisibility(View.GONE);

    }
        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        if (file != null && file.getAbsolutePath() != null)
            outState.putString(ScreenSlideFragment.SAVESTATE_FILEPATH, file.getAbsolutePath());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (file != null) {
            spinner.post(new Runnable() {
                @Override
                public void run() {
                    fadeout();
                    spinner.setVisibility(View.VISIBLE);
                }
            });
            new Thread(new Runnable() {
                @Override
                public void run() {
                    loadImage();
                }
            }).start();
        }
            Logger.exception(e);
            Logger.exception(e);
            Logger.exception(ex);
    }

    private void loadImage()
    {
        final Bitmap response = getBitmap();
        imageView.post(new Runnable() {
            @Override
            public void run() {
                fadein();
                imageView.setImageBitmap(response);
                //activity.myHistogram.setBitmap(response, false);
            }
        });
    }

    private Bitmap getBitmap()
    {
        Bitmap response;
        if (cacheHelper == null)
            cacheHelper = new CacheHelper(getContext());
        try {
            response = BitmapHelper.getBitmap(file,false,cacheHelper,mImageThumbSize,mImageThumbSize);
        }

            }
        catch (IllegalArgumentException ex)
        {
            response = null;
            filename.post(new Runnable() {
                @Override
                public void run() {
                        activity.filename.setText(R.string.failed_to_load + file.getName());

                }
            });
        }
        if (response == null)
            workDone.onWorkDone(false, file);
        else
            workDone.onWorkDone(true, file);
        return response;
    }

    private void fadeout()
    {
        imageView.animate().alpha(0f).setDuration(animationTime).setListener(null);
        spinner.animate().alpha(1f).setDuration(animationTime).setListener(null);
    }

    private void fadein()
    {
        spinner.animate().alpha(0f).setDuration(animationTime).setListener(null);
        imageView.animate().alpha(1f).setDuration(animationTime).setListener(null);
    }

    interface WorkeDoneInterface
    {
        void onWorkDone(boolean success, File file);
    }



    WorkeDoneInterface workDone = new WorkeDoneInterface() {
        @Override
        public void onWorkDone(final boolean success, final File file)
        {
            playVideo.post(new Runnable() {
                @Override
                public void run() {
                    if (success)
                    {
                        if (file.getAbsolutePath().endsWith(".mp4")) {
                            playVideo.setVisibility(View.VISIBLE);
                            playVideo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Uri uri = Uri.fromFile(file);
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setDataAndType(uri, "video/*");
                                    startActivity(i);
                                }
                            });
                        }
                        else if (!file.getAbsolutePath().endsWith(".mp4")) {
                            playVideo.setVisibility(View.GONE);
                        }
                    }
                    else
                    {
                        playVideo.setVisibility(View.GONE);
                    }
                }
            });

        }
    };

            Logger.d("Main", "Filesize: " + data.length + " File:" + file.getAbsolutePath());
            Logger.exception(e);
            Logger.exception(e);


}
