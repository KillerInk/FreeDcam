package troop.com.themesample.subfragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import troop.com.themesample.R;
import troop.com.themesample.views.SimpleValueChild;
import troop.com.themesample.views.UiSettingsChild;

/**
 * Created by troop on 16.06.2015.
 */
public class HorizontalValuesFragment extends Fragment implements Interfaces.I_CloseNotice
{
    View view;
    LinearLayout valuesHolder;
    String[] values;
    Interfaces.I_CloseNotice rdytoclose;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.horizontal_values_fragment, container, false);
        valuesHolder = (LinearLayout) view.findViewById(R.id.horizontal_values_holder);
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        for (String s : values)
        {
            SimpleValueChild child = new SimpleValueChild(view.getContext());
            child.SetString(s, this);
            valuesHolder.addView(child);
        }
    }


    public void SetStringValues(String[] values, Interfaces.I_CloseNotice rdytoclose)
    {
        this.values = values;
        this.rdytoclose = rdytoclose;
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
}
