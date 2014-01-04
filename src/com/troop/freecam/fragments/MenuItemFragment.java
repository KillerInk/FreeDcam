package com.troop.freecam.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.troop.freecam.camera.CameraManager;
import com.troop.freecam.MainActivity;
import com.troop.freecam.R;

/**
 * Created by troop on 03.01.14.
 */
public class MenuItemFragment extends BaseFragment
{
    TextView textView;
    Button button;
    String text;
    String buttonText;
    View.OnClickListener clickListener;

    public MenuItemFragment(CameraManager camMan, MainActivity activity, String text, String buttonText, View.OnClickListener listner) {
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
    }

    private void init()
    {
        button = (Button)view.findViewById(R.id.buttonMenu);
        button.setText(buttonText);
        button.setOnClickListener(clickListener);
        textView = (TextView)view.findViewById(R.id.textViewMenu);
        textView.setText(text);
    }

    public void SetText(String text)
    {
        textView.setText(text);
    }
    public void SetButtonText(String text)
    {
        button.setText(text);
    }

    public void SetOnClickListner(View.OnClickListener onClickListener)
    {
        button.setOnClickListener(onClickListener);
    }
}
