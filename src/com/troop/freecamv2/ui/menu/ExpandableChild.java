package com.troop.freecamv2.ui.menu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.troop.freecam.R;
import com.troop.freecamv2.camera.parameters.modes.I_ModeParameter;

/**
 * Created by troop on 18.08.2014.
 */
public class ExpandableChild extends LinearLayout
{
    private String Name;
    private String Value;
    private I_ModeParameter parameterHolder;
    Context context;
    TextView nameTextView;
    TextView valueTextView;

    public ExpandableChild(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ExpandableChild(Context context) {
        super(context);
        init(context);
    }

    public ExpandableChild(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context)
    {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.expandable_childs, this);
        nameTextView = (TextView)findViewById(R.id.tvChild);
        valueTextView = (TextView)findViewById(R.id.tvChildValue);
    }

    public String getName() {
        return Name;
    }
    public void setName(String Name) {
        this.Name = Name;
    }
    public String Value() {
        return Value;
    }
    public void setValue(String value) {
        this.Value = value;
    }

    public I_ModeParameter getParameterHolder(){ return parameterHolder;}
    public void setParameterHolder( I_ModeParameter parameterHolder)
    {
        this.parameterHolder = parameterHolder;
        nameTextView.setText(Name);
        valueTextView.setText(parameterHolder.GetValue());
    }
}
