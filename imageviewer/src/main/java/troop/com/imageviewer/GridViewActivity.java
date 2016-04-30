package troop.com.imageviewer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.WindowManager;

import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.FreeDPool;
import com.troop.freedcam.ui.I_Activity;

import java.io.File;

import troop.com.imageviewer.gridviewfragments.GridViewFragment;

/**
 * Created by troop on 11.12.2015.
 */
public class GridViewActivity extends AbstractFragmentActivity
{
    final String TAG = GridViewActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportFragmentManager().findFragmentByTag(TAG) == null) {
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(android.R.id.content, new GridViewFragment(), TAG);
            ft.commit();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    public int getMuliplier() {
        return 2;
    }
}
