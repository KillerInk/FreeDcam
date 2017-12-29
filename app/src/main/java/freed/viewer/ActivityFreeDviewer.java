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

package freed.viewer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.troop.freedcam.R;

import java.util.List;

import freed.ActivityAbstract;
import freed.utils.FreeDPool;
import freed.utils.LocationManager;
import freed.utils.StorageFileManager;
import freed.viewer.gridview.GridViewFragment;
import freed.viewer.helper.BitmapHelper;
import freed.viewer.holder.FileHolder;
import freed.viewer.screenslide.ScreenSlideFragment;

/**
 * Created by troop on 11.12.2015.
 */
public class ActivityFreeDviewer extends ActivityAbstract
{
    private final String TAGGrid = GridViewFragment.class.getSimpleName();
    private final String TAGSlide = ScreenSlideFragment.class.getSimpleName();
    private GridViewFragment gridViewFragment;
    private ScreenSlideFragment screenSlideFragment;
    private FrameLayout gridholder;
    private FrameLayout slideholder;
    private AnimatorSet mCurrentAnimator;
    private int mShortAnimationDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initOnCreate() {
        super.initOnCreate();
        init();
    }

    @Override
    protected void setContentToView() {
        setContentView(R.layout.freedviewer_activity);
    }

    private void init()
    {

        bitmapHelper =new BitmapHelper(getApplicationContext(),getResources().getDimensionPixelSize(R.dimen.image_thumbnails_size),this);
        storageHandler = new StorageFileManager();
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                LoadDCIMDirs();
            }
        });

        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
        gridViewFragment = (GridViewFragment) getSupportFragmentManager().findFragmentById(R.id.freedviewer_gridview);
        gridViewFragment.SetOnGridItemClick(onGridItemClick);
        screenSlideFragment = (ScreenSlideFragment)getSupportFragmentManager().findFragmentById(R.id.freedviewer_screenslide_fragment);
        screenSlideFragment.setOnBackClickListner(onScreenSlideBackClick);
        slideholder =  findViewById(R.id.freedviewer_screenslideholder);
        gridholder = findViewById(R.id.freedviewer_gridviewholder);
        slideholder.setVisibility(View.GONE);
    }

    /**
     * Loads all files stored in DCIM/FreeDcam from internal and external SD
     * and notfiy gridview and screenslide that files got changed
     */
    @Override
    public void LoadFreeDcamDCIMDirsFiles() {
        super.LoadFreeDcamDCIMDirsFiles();
        gridViewFragment.NotifyDataSetChanged();
        screenSlideFragment.NotifyDATAhasChanged(files);
    }

    /**
     * Loads the files stored from that folder
     * and notfiy gridview and screenslide that files got changed
     * @param fileHolder the folder to lookup
     * @param types the file format to load
     */
    @Override
    public void LoadFolder(FileHolder fileHolder, ActivityAbstract.FormatTypes types) {
        super.LoadFolder(fileHolder, types);
        gridViewFragment.NotifyDataSetChanged();
        screenSlideFragment.NotifyDATAhasChanged(files);
    }

    @Override
    public LocationManager getLocationManager() {
        return null;
    }

    /**
     * Loads all Folders from DCIM dir from internal and external SD
     * and notfiy gridview and screenslide that files got changed
     */
    @Override
    public void LoadDCIMDirs()
    {
        super.LoadDCIMDirs();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (gridViewFragment != null)
                    gridViewFragment.NotifyDataSetChanged();
                if (screenSlideFragment != null)
                    screenSlideFragment.NotifyDATAhasChanged(files);
            }
        });

    }

    @Override
    public void DeleteFiles(final List<FileHolder> files) {
        super.DeleteFiles(files);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gridViewFragment.NotifyDataSetChanged();
                screenSlideFragment.NotifyDATAhasChanged(getFiles());
            }
        });
    }

    @Override
    public boolean DeleteFile(FileHolder file)
    {
        boolean del = super.DeleteFile(file);
        gridViewFragment.NotifyDataSetChanged();
        screenSlideFragment.NotifyDATAhasChanged(files);
        return del;
    }

    private final ScreenSlideFragment.ButtonClick onScreenSlideBackClick = new ScreenSlideFragment.ButtonClick() {
        @Override
        public void onButtonClick(int position, View view)
        {
            loadGridView(position,view);
        }
    };

    private final ScreenSlideFragment.ButtonClick onGridItemClick = new ScreenSlideFragment.ButtonClick() {
        @Override
        public void onButtonClick(int position, View view)
        {
            loadScreenSlide(position,view);
        }
    };

    private void loadGridView(int position, View view)
    {
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        View griditem = gridViewFragment.GetGridItem(position);
        if (griditem == null)
            griditem = gridViewFragment.GetGridItem(0);
        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        griditem.getGlobalVisibleRect(startBounds);
        gridholder.getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        final float startScaleFinal = startScale;

        // Animate the four positioning/sizing properties in parallel,
        // back to their original values.
        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator
                .ofFloat(slideholder, View.X, startBounds.left))
                .with(ObjectAnimator
                        .ofFloat(slideholder,
                                View.Y,startBounds.top))
                .with(ObjectAnimator
                        .ofFloat(slideholder,
                                View.SCALE_X, startScaleFinal))
                .with(ObjectAnimator
                        .ofFloat(slideholder,
                                View.SCALE_Y, startScaleFinal));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                //gridholder.setAlpha(1f);
                slideholder.setVisibility(View.GONE);
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                gridholder.setAlpha(1f);
                slideholder.setVisibility(View.GONE);
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;
    }

    private void loadScreenSlide(int position, View view)
    {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        screenSlideFragment.SetPostition(position);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        view.getGlobalVisibleRect(startBounds);
        gridholder.getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        //slideholder.setAlpha(0f);
        slideholder.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        slideholder.setPivotX(0f);
        slideholder.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(slideholder, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(slideholder, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(slideholder, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(slideholder,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

    }

    @Override
    public void WorkHasFinished(final FileHolder fileHolder)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fileHolder.UpdateImage();
            }
        });

    }

    @Override
    public void WorkHasFinished(FileHolder[] fileHolder) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == gridViewFragment.STACK_REQUEST || requestCode == gridViewFragment.DNGCONVERT_REQUEST)
        {
            FileHolder f = getFiles().get(0).getParent();
            LoadFolder(f, gridViewFragment.formatsToShow);
        }

    }
}
