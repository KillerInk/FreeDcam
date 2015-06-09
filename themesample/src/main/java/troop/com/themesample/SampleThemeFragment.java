package troop.com.themesample;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.ui.AbstractFragment;

/**
 * Created by troop on 09.06.2015.
 */
public class SampleThemeFragment extends AbstractFragment
{
    AbstractCameraUiWrapper abstractCameraUiWrapper;
    View view;
    @Override
    public void SetCameraUIWrapper(AbstractCameraUiWrapper wrapper)
    {
        this.abstractCameraUiWrapper = wrapper;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.themesamplefragment, container, false);
        return view;
    }
}
