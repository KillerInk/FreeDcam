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
public class ScreenSlideActivity extends FragmentActivity implements I_Activity
{

    final static String TAG = ScreenSlideActivity.class.getSimpleName();
    int flags;
    public static final String EXTRA_IMAGE = "extra_image";
    public static final String IMAGE_PATH = "image_path";
    public static final String FileType = "filetype";
    int extra = 0;
    AppSettingsManager appSettingsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appSettingsManager = new AppSettingsManager(PreferenceManager.getDefaultSharedPreferences(this), this);
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
        if (!DeviceUtils.HAS_CONTEXT())
            DeviceUtils.SETCONTEXT(getApplicationContext());


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

    @Override
    public void SwitchCameraAPI(String Api) {

    }

    @Override
    public void SetTheme(String Theme) {

    }

    @Override
    public int[] GetScreenSize() {
        return new int[0];
    }

    @Override
    public void ShowHistogram(boolean enable) {

    }

    @Override
    public void loadImageViewerFragment(File file) {

    }

    @Override
    public void loadCameraUiFragment() {

    }

    @Override
    public void closeActivity() {

    }

    private I_Activity.I_OnActivityResultCallback resultCallback;

    @Override
    public void ChooseSDCard(I_Activity.I_OnActivityResultCallback callback)
    {
        this.resultCallback = callback;
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    private static final int READ_REQUEST_CODE = 42;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                final int takeFlags = data.getFlags()
                        & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                // Check for the freshest data.


                getContentResolver().takePersistableUriPermission(uri,takeFlags);
                AppSettingsManager.APPSETTINGSMANAGER.SetBaseFolder(uri.toString());
                if (resultCallback != null)
                {
                    resultCallback.onActivityResultCallback(uri);
                    this.resultCallback = null;
                }
            }
        }
    }
}
