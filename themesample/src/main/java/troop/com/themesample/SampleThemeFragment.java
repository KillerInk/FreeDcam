package troop.com.themesample;

import android.animation.Animator;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.parameters.I_ParametersLoaded;
import com.troop.freedcam.ui.AbstractFragment;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_Activity;

import troop.com.themesample.handler.SampleInfoOverlayHandler;
import troop.com.themesample.subfragments.CameraUiFragment;
import troop.com.themesample.subfragments.SettingsMenuFragment;
import troop.com.themesample.views.ThumbView;
import troop.com.themesample.views.UiSettingsChild;
import troop.com.themesample.views.UiSettingsChildCameraSwitch;
import troop.com.themesample.views.UiSettingsChildExit;
import troop.com.themesample.views.UiSettingsChildModeSwitch;

/**
 * Created by troop on 09.06.2015.
 */
public class SampleThemeFragment extends AbstractFragment
{
    final String TAG = SampleThemeFragment.class.getSimpleName();
    AbstractCameraUiWrapper abstractCameraUiWrapper;

    View view;
    I_Activity i_activity;
    AppSettingsManager appSettingsManager;
    CameraUiFragment cameraUiFragment;
    SettingsMenuFragment settingsMenuFragment;
    FrameLayout fragmentHolder;
    boolean settingsOpen= false;

    public SampleThemeFragment()
    {
        cameraUiFragment = new CameraUiFragment();
        settingsMenuFragment =  new SettingsMenuFragment();
    }

    @Override
    public void SetStuff(AppSettingsManager appSettingsManager, I_Activity i_activity) {
        this.i_activity = i_activity;
        this.appSettingsManager = appSettingsManager;
        cameraUiFragment.SetStuff(appSettingsManager,i_activity, onSettingsClick);
        settingsMenuFragment.SetStuff(appSettingsManager,i_activity,onSettingsClick);
    }

    @Override
    public void SetCameraUIWrapper(AbstractCameraUiWrapper wrapper)
    {
        this.abstractCameraUiWrapper = wrapper;
        cameraUiFragment.SetCameraUIWrapper(wrapper);
        settingsMenuFragment.SetCameraUIWrapper(wrapper);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        this.view = inflater.inflate(R.layout.samplethemefragment, container, false);
        android.support.v4.app.FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragmentholder, cameraUiFragment);
        transaction.commit();
        return view;
    }

    @Override
    public void onDestroyView()
    {

        super.onDestroyView();
        this.settingsMenuFragment = null;
        this.cameraUiFragment = null;

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    View.OnClickListener onSettingsClick = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (!settingsOpen)
                replaceCameraUIWithSettings();
            else
                replaceSettingsWithCameraUI();
        }
    };

    private void replaceCameraUIWithSettings()
    {
        settingsOpen = true;
        android.support.v4.app.FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit);
        transaction.replace(R.id.fragmentholder, settingsMenuFragment);
        transaction.commit();
    }

    private void replaceSettingsWithCameraUI()
    {
        settingsOpen = false;
        android.support.v4.app.FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit);
        transaction.replace(R.id.fragmentholder, cameraUiFragment);
        transaction.commit();
    }

}
