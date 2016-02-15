package troop.com.themesample.subfragments;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import troop.com.imageviewer.gridviewfragments.GridViewFragment;

/**
 * Created by troop on 15.02.2016.
 */
public class VideoProfileEditorActivity extends FragmentActivity
{
    final String TAG = VideoProfileEditorActivity.class.getSimpleName();

    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getSupportFragmentManager().findFragmentByTag(TAG) == null) {
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(android.R.id.content, new VideoProfileEditorFragment(), TAG);
            ft.commit();
        }
    }
}
