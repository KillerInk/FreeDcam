package troop.com.themesample;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.ui.AbstractFragment;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_Activity;

import troop.com.themesample.subfragments.CameraUiFragment;
import troop.com.themesample.subfragments.SettingsMenuFragment;

/**
 * Created by troop on 09.06.2015.
 */
public class SampleThemeFragment extends AbstractFragment
{
    final String TAG = SampleThemeFragment.class.getSimpleName();

    View view;
    I_Activity i_activity;
    AppSettingsManager appSettingsManager;
    CameraUiFragment cameraUiFragment;

    FrameLayout fragmentHolder;
    boolean settingsOpen= false;

    public SampleThemeFragment()
    {


    }

    @Override
    public void SetStuff(AppSettingsManager appSettingsManager, I_Activity i_activity) {
        this.i_activity = i_activity;
        this.appSettingsManager = appSettingsManager;


    }

    @Override
    public void SetCameraUIWrapper(AbstractCameraUiWrapper wrapper)
    {
        this.wrapper = wrapper;
        if (cameraUiFragment != null)
            cameraUiFragment.SetCameraUIWrapper(wrapper);



    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, null);
        this.view = inflater.inflate(R.layout.samplethemefragment, container, false);
        if (cameraUiFragment == null)
        {
            cameraUiFragment = new CameraUiFragment();
            cameraUiFragment.SetStuff(appSettingsManager, i_activity);
            cameraUiFragment.SetCameraUIWrapper(wrapper);
        }


        android.support.v4.app.FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentholder, cameraUiFragment);
        transaction.commitAllowingStateLoss();
        return view;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }



}
