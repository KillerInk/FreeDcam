package troop.com.themesample.subfragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;

import troop.com.themesample.R;
import troop.com.themesample.views.uichilds.SimpleValueChild;

/**
 * Created by troop on 16.06.2015.
 */
public class HorizontalValuesFragment extends Fragment implements Interfaces.I_CloseNotice, AbstractModeParameter.I_ModeParameterEvent
{
    View view;
    LinearLayout valuesHolder;
    String[] values;
    Interfaces.I_CloseNotice rdytoclose;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,null);
        this.view = inflater.inflate(R.layout.horizontal_values_fragment, container, false);
        valuesHolder = (LinearLayout) view.findViewById(R.id.horizontal_values_holder);
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        setValueToView();
    }

    private void setValueToView() {
        int i = 0;
        LinearLayout linearLayout = getNewLayout();
        if (values == null)
            return;
        for (String s : values)
        {
            if (i == 3 || i == 6 || i == 9)
                linearLayout = getNewLayout();
            SimpleValueChild child = new SimpleValueChild(view.getContext());
            child.SetString(s, this);
            linearLayout.addView(child);
            i++;
        }
    }

    private LinearLayout getNewLayout()
    {
        final LinearLayout linearLayout = new LinearLayout(view.getContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(params);
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        valuesHolder.addView(linearLayout);
        return linearLayout;
    }


    public void SetStringValues(String[] values, Interfaces.I_CloseNotice rdytoclose)
    {
        this.values = values;
        this.rdytoclose = rdytoclose;
    }

    public void ListenToParameter(AbstractModeParameter parameter)
    {
        parameter.addEventListner(this);
    }

    /*
    this gets attached to the Simplevalue childes and returns the value from the clicked SimpleValueChild
     */
    @Override
    public void onClose(String value)
    {
        if (rdytoclose != null)
            rdytoclose.onClose(value);
    }

    @Override
    public void onValueChanged(String val) {

    }

    @Override
    public void onIsSupportedChanged(boolean isSupported) {

    }

    @Override
    public void onIsSetSupportedChanged(boolean isSupported) {

    }

    @Override
    public void onValuesChanged(String[] values) {
        this.values = values;
        setValueToView();
    }

    @Override
    public void onVisibilityChanged(boolean visible) {

    }
}
