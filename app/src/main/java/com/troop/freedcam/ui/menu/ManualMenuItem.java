package com.troop.freedcam.ui.menu;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.troop.freedcam.R;
import com.troop.freedcam.i_camera.parameters.AbstractManualParameter;
import com.troop.freedcam.i_camera.parameters.I_ManualParameter;
import com.troop.freedcam.sonyapi.parameters.manual.BaseManualParameterSony;

/**
 * Created by troop on 01.09.2014.
 */
public class ManualMenuItem extends LinearLayout implements View.OnClickListener, AbstractManualParameter.I_ManualParameterEvent, I_ManualParameter
{

    final static String TAG = ManualMenuItem.class.getSimpleName();
    Context context;
    public TextView textViewName;
    public TextView textViewValue;
    private LinearLayout toggleButton;
    public final String name;
    ManualMenuHandler manualMenuHandler;
    public AbstractManualParameter manualParameter;
    boolean isChecked = false;
    boolean isVisibile = false;
    boolean isSetSupported = false;
    int btncolor;
    int txtcolor;
    String[] stringValues;

    public ManualMenuItem(Context context, String name, ManualMenuHandler manualMenuHandler) {
        super(context);
        this.context =context;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.manual_menu_item, this);
        this.textViewName = (TextView)findViewById(R.id.manual_item_Text);
        this.textViewValue = (TextView)findViewById(R.id.manual_item_valueText);
        this.name = name;
        this.manualMenuHandler = manualMenuHandler;
        txtcolor = textViewName.getCurrentTextColor();

        textViewName.setText(name);
        writeLog("created");

        //set int to textviews always as string or you will get and res not found ex!!

        toggleButton = (LinearLayout)findViewById(R.id.manual_item);
        Drawable background = toggleButton.getBackground();
        if (background instanceof ColorDrawable)
            btncolor = ((ColorDrawable) background).getColor();

    }



    public void SetAbstractManualParameter(AbstractManualParameter parameter)
    {
        this.manualParameter = parameter;
        manualParameter.addEventListner(this);
        if (manualParameter.IsSupported())
        {
            writeLog("is supported");
            String txt = manualParameter.GetStringValue();
            if (txt != null && !txt.equals("")) {
                setTextToTextView(txt);
                writeLog("GetStringValue: set text to " + txt);
            }
            else
            {
                setTextToTextView(parameter.GetValue() + "");
                writeLog("loading int value: " + textViewValue.getText());
            }
            toggleButton.setOnClickListener(this);
            isSetSupported = true;
            onIsSupportedChanged(true);
        }
        else
        {
            onIsSupportedChanged(false);
            writeLog("is not supported");
        }
    }

    @Override
    public void onClick(View v)
    {
        if (isSetSupported)
        {
            writeLog("onclick");
            manualMenuHandler.DisableOtherItems(name);
        }
    }

    public void DisableItem()
    {
        isChecked = false;
    }

    public void EnableItem() {isChecked = true;}



    //**
    // AbstractManualParameter.I_ManualParameterEvent
    // AbstractManualParameter.I_ManualParameterEvent
    @Override
    public void onIsSupportedChanged(boolean supported)
    {
        writeLog("on is supported changed " + supported);
        if (supported && !isVisibile)
        {
            manualMenuHandler.manualMenu.addView(this);
            isVisibile =true;
        }
        if (!supported && isVisibile) {
            manualMenuHandler.manualMenu.removeView(this);
            isVisibile =false;
        }

    }

    @Override
    public void onIsSetSupportedChanged(boolean supported)
    {
        writeLog("on is SET supported changed " + supported);
        if (supported)
        {
            toggleButton.setBackgroundColor(btncolor);
            textViewName.setTextColor(txtcolor);
            textViewValue.setTextColor(txtcolor);
            toggleButton.setClickable(true);
            toggleButton.setOnClickListener(this);
            isSetSupported = true;
        }
        else
        {
            toggleButton.setBackgroundColor(txtcolor);
            textViewName.setTextColor(btncolor);
            textViewValue.setTextColor(btncolor);
            toggleButton.setOnClickListener(null);
            toggleButton.setClickable(false);
            isSetSupported = false;
        }
        String txt = manualParameter.GetStringValue();
        if (txt != null && !txt.equals("")) {
            setTextToTextView(txt);
            writeLog("GetStringValue: set text to " + txt);
        }

    }

    @Override
    public void onMaxValueChanged(int max) {

    }

    @Override
    public void onMinValueChanged(int min) {

    }

    @Override
    public void onCurrentValueChanged(int current)
    {
        writeLog("onCurrentValueChanged" + current);
        final String txt = getStringValue(current);
        writeLog("onCurrentValueChanged" + txt);
        if (txt != null && !txt.equals("") && !txt.equals("null")) {
            setTextToTextView(txt);
        }
        else
            setTextToTextView(current + "");

    }

    @Override
    public void onValuesChanged(String[] values) {
        stringValues = values;
    }

    //
    // AbstractManualParameter.I_ManualParameterEvent
    // AbstractManualParameter.I_ManualParameterEvent
    //**

    public String getStringValue(int pos)
    {
        if(stringValues == null)
            stringValues = manualParameter.getStringValues();
        if (stringValues != null && stringValues.length > 0)
        {
            return stringValues[pos];
        }

        return null;
    }

    @Override
    public boolean IsSupported() {
        return manualParameter.IsSupported();
    }

    @Override
    public int GetMaxValue() {
        return manualParameter.GetMaxValue();
    }

    @Override
    public int GetMinValue() {
        return manualParameter.GetMinValue();
    }

    @Override
    public int GetValue() {
        return manualParameter.GetValue();
    }

    @Override
    public String GetStringValue()
    {
        return manualParameter.GetStringValue();
    }

    @Override
    public String[] getStringValues()
    {
        if (stringValues != null || stringValues.length > 0 )
        {
            writeLog("have values returned:  " + stringValues);
            return stringValues;
        }
        stringValues = manualParameter.getStringValues();
        writeLog("had no values loaded it:  " + stringValues);
        if (stringValues != null || stringValues.length > 0 )
            return stringValues;
        writeLog("String values are null ");
        return null;
    }

    @Override
    public void SetValue(int valueToSet)
    {
        writeLog("set value to: " + valueToSet);
        manualParameter.SetValue(valueToSet);
        onCurrentValueChanged(valueToSet);
    }

    @Override
    public void RestartPreview()
    {
        manualParameter.RestartPreview();
    }

    private void setTextToTextView(final String txt)
    {
        writeLog("set text value: " +txt);
        textViewValue.post(new Runnable() {
            @Override
            public void run() {
                textViewValue.setText(txt);
            }
        });
    }

    private void writeLog(String txt)
    {
        Log.d(TAG, name + ": " + txt);
    }
}
