package troop.com.imageviewer;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by troop on 22.12.2015.
 */
public class DngConvertingActivity extends FragmentActivity
{
    final String TAG = DngConvertingActivity.class.getSimpleName();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportFragmentManager().findFragmentByTag(TAG) == null) {
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(android.R.id.content, new DngConvertingFragment(), TAG);
            ft.commit();
        }

    }
}
