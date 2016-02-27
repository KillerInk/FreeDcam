package troop.com.themesample.views.menu;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_Activity;
import com.troop.freedcam.ui.I_swipe;
import com.troop.freedcam.ui.SwipeMenuListner;

import troop.com.themesample.R;
import troop.com.themesample.views.uichilds.UiSettingsChild;

/**
 * Created by troop on 14.06.2015.
 */
public class MenuItem extends UiSettingsChild implements View.OnClickListener, I_swipe
{
    TextView description;

    LinearLayout toplayout;

    TextView headerText;
    SwipeMenuListner swipeMenuListner;

    SwipeMenuListner controlswipeListner;

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
        //toplayout.setOnClickListener(this);
        controlswipeListner = new SwipeMenuListner(this);
        toplayout.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return controlswipeListner.onTouchEvent(event);
            }
        });
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

    public void SetStuff(I_Activity i_activity, AppSettingsManager appSettingsManager, String settingvalue,SwipeMenuListner swipeMenuListner) {
        super.SetStuff(i_activity, appSettingsManager, settingvalue);
        this.swipeMenuListner = swipeMenuListner;
    }


    @Override
    public void doLeftToRightSwipe()
    {
        if (swipeMenuListner != null)
        swipeMenuListner.LeftToRightSwipe();
    }

    @Override
    public void doRightToLeftSwipe()
    {
        if (swipeMenuListner != null)
            swipeMenuListner.RightToLeftSwipe();
    }

    @Override
    public void doTopToBottomSwipe() {
        if (swipeMenuListner != null)
            swipeMenuListner.TopToBottomSwipe();
    }

    @Override
    public void doBottomToTopSwipe() {
        if (swipeMenuListner != null)
            swipeMenuListner.BottomToTopSwipe();
    }

    @Override
    public void onClick(int x, int y) {
        if (onItemClick != null)
            onItemClick.onMenuItemClick(this, false);
    }
}
