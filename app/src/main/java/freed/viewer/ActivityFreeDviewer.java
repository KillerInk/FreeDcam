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

import androidx.databinding.Observable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.troop.freedcam.BR;
import com.troop.freedcam.R;

import java.util.List;

import freed.ActivityAbstract;
import freed.file.FileListController;
import freed.file.holder.BaseHolder;
import freed.utils.FreeDPool;
import freed.utils.LocationManager;
import freed.utils.Log;
import freed.utils.PermissionManager;
import freed.viewer.gridview.modelview.GridViewFragmentModelView;
import freed.viewer.gridview.views.GridViewFragment;
import freed.viewer.helper.BitmapHelper;
import freed.viewer.screenslide.modelview.ScreenSlideFragmentModelView;
import freed.viewer.screenslide.views.ScreenSlideFragment;

/**
 * Created by troop on 11.12.2015.
 */
public class ActivityFreeDviewer extends ActivityAbstract
{
    private final String TAG = ActivityFreeDviewer.class.getSimpleName();
    private GridViewFragment gridViewFragment;
    private ScreenSlideFragment screenSlideFragment;
    private FrameLayout gridholder;
    private FrameLayout slideholder;
    private AnimatorSet mCurrentAnimator;
    private int mShortAnimationDuration;
    private GridViewFragmentModelView gridViewFragmentModelView;
    private ScreenSlideFragmentModelView screenSlideFragmentModelView;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        init();
    }


    @Override
    protected void setContentToView() {
        Log.d(TAG, "Set Content to view");
        setContentView(R.layout.freedviewer_activity);
    }

    private void init()
    {
        Log.d(TAG,"init");
        gridViewFragmentModelView =  new ViewModelProvider(this).get(GridViewFragmentModelView.class);
        screenSlideFragmentModelView = new ViewModelProvider(this).get(ScreenSlideFragmentModelView.class);
        bitmapHelper =new BitmapHelper(getApplicationContext(),getResources().getDimensionPixelSize(R.dimen.image_thumbnails_size));
        fileListController = new FileListController(getApplicationContext());
        gridViewFragmentModelView.setFileListController(fileListController);
        gridViewFragmentModelView.setBitmapHelper(bitmapHelper);
        screenSlideFragmentModelView.setFileListController(fileListController);
        screenSlideFragmentModelView.setBitmapHelper(bitmapHelper);
        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
        gridViewFragment = new GridViewFragment();
        gridViewFragment.setGridViewFragmentModelView(gridViewFragmentModelView);
        gridViewFragment.SetOnGridItemClick(onGridItemClick);



        screenSlideFragment = new ScreenSlideFragment();
        screenSlideFragment.setOnBackClickListner(onScreenSlideBackClick);
        screenSlideFragment.setScreenSlideFragmentModelView(screenSlideFragmentModelView);
        slideholder =  findViewById(R.id.freedviewer_screenslideholder);
        gridholder = findViewById(R.id.freedviewer_gridviewholder);
        slideholder.setVisibility(View.GONE);
        replaceCameraFragment(gridViewFragment,"Gridview", R.id.freedviewer_gridviewholder);
        replaceCameraFragment(screenSlideFragment,"Gridview", R.id.freedviewer_screenslideholder);


        gridViewFragmentModelView.getFilesHolderModel().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (BR.formatType == propertyId)
                {
                    screenSlideFragmentModelView.getFilesHolderModel().setFormatTypes(gridViewFragmentModelView.getFilesHolderModel().getFormatType());
                }
            }
        });

        FreeDPool.Execute(() -> fileListController.loadDefaultFiles());
    }

    private void replaceCameraFragment(Fragment fragment, String id, int layout)
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //transaction.setCustomAnimations(R.anim.left_to_right_enter, R.anim.left_to_right_exit);
        transaction.replace(layout, fragment, id);
        transaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getPermissionManager().isPermissionGranted(PermissionManager.Permissions.SdCard) && (fileListController.getFiles() == null || fileListController.getFiles().size() == 0))
            FreeDPool.Execute(() -> fileListController.loadDefaultFiles());
    }

    @Override
    public LocationManager getLocationManager() {
        return null;
    }

    private final ScreenSlideFragment.ButtonClick onScreenSlideBackClick = this::loadGridView;

    private final ScreenSlideFragment.ButtonClick onGridItemClick = this::loadScreenSlide;

    private void loadGridView(int position, View view)
    {
        //replaceCameraFragment(gridViewFragment,"Gridview");
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
        if (griditem != null)
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
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == gridViewFragment.STACK_REQUEST || requestCode == gridViewFragment.DNGCONVERT_REQUEST)
        {
            List<BaseHolder> files = fileListController.getFiles();
            if(files.size() > 0 && files.get(0).IsFolder()) {

                BaseHolder f =  files.get(0);
                fileListController.LoadFolder(f, gridViewFragmentModelView.formatsToShow);
            }
            else
                fileListController.loadDefaultFiles();
        }
       /* else if (requestCode == ActivityAbstract.DELETE_REQUEST_CODE)
        {
            screenSlideFragmentModelView.getFilesHolderModel().notifyChange();
            gridViewFragmentModelView.getFilesHolderModel().notifyChange();
        }*/

    }

}
