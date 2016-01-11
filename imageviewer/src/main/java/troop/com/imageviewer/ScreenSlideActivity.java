package troop.com.imageviewer;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.troop.freedcam.utils.DeviceUtils;

import troop.com.imageviewer.gridviewfragments.GridViewFragment;

/**
 * Created by troop on 21.08.2015.
 */
public class ScreenSlideActivity extends FragmentActivity {

    final static String TAG = ScreenSlideActivity.class.getSimpleName();
    int flags;
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

        flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        HIDENAVBAR();
        if (DeviceUtils.contex == null)
            DeviceUtils.contex = getApplicationContext();


    }

    public void HIDENAVBAR()
    {
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        else
        {
            //HIDE nav and action bar
            final View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(flags);
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if (visibility > 0) {
                        if (Build.VERSION.SDK_INT >= 16)
                            getWindow().getDecorView().setSystemUiVisibility(flags);
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        HIDENAVBAR();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)
            HIDENAVBAR();
    }
}
