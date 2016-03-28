package troop.com.imageviewer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.WindowManager;

import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_Activity;
import com.troop.freedcam.utils.DeviceUtils;

import java.io.File;

import troop.com.imageviewer.gridviewfragments.GridViewFragment;

/**
 * Created by troop on 21.08.2015.
 */
public class ScreenSlideActivity extends AbstractFragmentActivity
{
    final static String TAG = ScreenSlideActivity.class.getSimpleName();
    public static final String EXTRA_IMAGE = "extra_image";
    public static final String IMAGE_PATH = "image_path";
    public static final String FileType = "filetype";
    int extra = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportFragmentManager().findFragmentByTag(TAG) == null) {
            ScreenSlideFragment fragment = new ScreenSlideFragment();
            final int extraCurrentItem = getIntent().getIntExtra(EXTRA_IMAGE, -1);
            final String path = getIntent().getStringExtra(IMAGE_PATH);
            if (extraCurrentItem != -1) {
                this.extra = extraCurrentItem;
            }
            if (path != null && !path.equals(""))
                fragment.FilePathToLoad = path;
            fragment.defitem = extra;
            fragment.filestoshow = GridViewFragment.FormatTypes.valueOf(getIntent().getStringExtra(FileType));
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(android.R.id.content, fragment, TAG);
            ft.commit();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
