package troop.com.themesample.views.menu;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import troop.com.themesample.R;
import troop.com.themesample.views.uichilds.UiSettingsChild;

/**
 * Created by troop on 14.06.2015.
 */
public class MenuItem extends UiSettingsChild implements View.OnClickListener
{
    TextView description;

    LinearLayout toplayout;

    TextView headerText;

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
        TypedArray b = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.UiSettingsChild,
                0, 0
        );
        //try to set the attributs
        try
        {

            headerText.setText(b.getText(R.styleable.UiSettingsChild_HeaderText));

            description.setText(a.getText(R.styleable.MenuItem_Description));
        }
        finally {
            a.recycle();
        }
        sendLog("Ctor done");
    }

    @Override
    protected void init(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflateTheme(inflater);
        headerText = (TextView)findViewById(R.id.textview_menuitem_header);
        valueText = (TextView)findViewById(R.id.textview_menuitem_header_value);
        description = (TextView)findViewById(R.id.textview_menuitem_description);
        toplayout = (LinearLayout)findViewById(R.id.menu_item_toplayout);
        toplayout.setOnClickListener(this);
    }

    @Override
    protected void inflateTheme(LayoutInflater inflater) {
        inflater.inflate(R.layout.menu_item, this);
    }

    public void onValueChanged(String val)
    {
        sendLog("Set Value to:" + val);
        valueText.setText(val);
    }

    @Override
    public void onClick(View v) {
        if (onItemClick != null)
            onItemClick.onMenuItemClick(this, false);
    }


}
