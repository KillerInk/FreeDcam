package com.troop.freecam.controls.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.troop.freecam.R;


/**
 * Created by troop on 03.01.14.
 */
public class MenuItemControl extends LinearLayout
{
    TextView textView;
    Button button;
    String text;
    String buttonText;
    Button.OnClickListener clickListener;
    Context context;

    public MenuItemControl(Context context) {
        super(context);
    }

    public MenuItemControl(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    public MenuItemControl(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    /*public MenuItemControl(CameraManager camMan, MainActivity activity, String text, String buttonText, View.OnClickListener listner) {
        super(camMan, activity);
        this.text = text;
        this.buttonText = buttonText;
        this.clickListener = listner;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.menuitemfragment, container, false);
        init();
        return view;
    }*/

    private void init(Context context, AttributeSet attrs)
    {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.MenuItemControl,
                0, 0);

        inflater.inflate(R.layout.menuitemfragment, this);
        button = (Button)findViewById(R.id.buttonMenu);
        buttonText = (String)a.getText(R.styleable.MenuItemControl_ButtonText);
        button.setText(buttonText);
        //button.setOnClickListener(clickListener);
        textView = (TextView)findViewById(R.id.textViewMenu);
        text = (String) a.getText(R.styleable.MenuItemControl_TextViewText);
        textView.setText(text);
        a.recycle();
    }

    public void SetText(String text)
    {
        textView.setText(text);
    }
    public void SetButtonText(String text)
    {
        button.setText(text);
    }

    public void SetOnClickListner(Button.OnClickListener onClickListener)
    {
        button.setOnClickListener(onClickListener);
    }
}
