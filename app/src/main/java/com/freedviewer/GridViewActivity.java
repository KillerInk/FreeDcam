package com.freedviewer;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.AbstractFragmentActivity;
import com.freedviewer.gridview.GridViewFragment;

/**
 * Created by troop on 11.12.2015.
 */
public class GridViewActivity extends AbstractFragmentActivity
{
    private final String TAG = GridViewActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createHandlers();
        if (getSupportFragmentManager().findFragmentByTag(TAG) == null)
        {
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            GridViewFragment fragment = new GridViewFragment();
            fragment.SetBitmapHelperAndAppSettings(bitmapHelper,appSettingsManager);
            ft.add(android.R.id.content, fragment, TAG);
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
