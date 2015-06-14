package troop.com.themesample.views.menu;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.troop.freedcam.i_camera.modules.I_ModuleEvent;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.i_camera.parameters.I_ParametersLoaded;

import troop.com.themesample.R;
import troop.com.themesample.views.UiSettingsChild;

/**
 * Created by troop on 14.06.2015.
 */
public class MenuItem extends UiSettingsChild
{
    TextView description;

    public MenuItem(Context context) {
        super(context);
    }

    public MenuItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        //get custom attributs
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.MenuItem,
                0, 0
        );
        //try to set the attributs
        try
        {

            description.setText(a.getText(R.styleable.MenuItem_Description));
        }
        finally {
            a.recycle();
        }
        Log.d(TAG, "Ctor done");
    }

    @Override
    protected void init(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflateTheme(inflater);
        headerText = (TextView)findViewById(R.id.textview_menuitem_header);
        valueText = (TextView)findViewById(R.id.textview_menuitem_header_value);
        description = (TextView)findViewById(R.id.textview_menuitem_description);
    }

    @Override
    protected void inflateTheme(LayoutInflater inflater) {
        inflater.inflate(R.layout.menu_item, this);
    }

    public void onValueChanged(String val)
    {
        Log.d(TAG, "Set Value to:" + val);
        valueText.setText(val);
    }

}
