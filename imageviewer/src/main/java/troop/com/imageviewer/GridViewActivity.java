package troop.com.imageviewer;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by troop on 11.12.2015.
 */
public class GridViewActivity extends FragmentActivity
{
    final String TAG = GridViewActivity.class.getSimpleName();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportFragmentManager().findFragmentByTag(TAG) == null) {
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(android.R.id.content, new GridViewFragment(), TAG);
            ft.commit();
        }
    }
}
