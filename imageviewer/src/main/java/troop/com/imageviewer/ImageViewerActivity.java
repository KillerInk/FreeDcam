package troop.com.imageviewer;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by troop on 23.07.2015.
 */
public class ImageViewerActivity extends FragmentActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View appViewGroup = (ViewGroup) inflater.inflate(R.layout.imageviewer_activity, null);
        setContentView(R.layout.imageviewer_activity);

        ImageViewerFragment fragment = new ImageViewerFragment();
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(troop.com.imageviewer.R.id.imageviewer_holder, fragment, "Imageviewer");
        transaction.commit();
    }
}
