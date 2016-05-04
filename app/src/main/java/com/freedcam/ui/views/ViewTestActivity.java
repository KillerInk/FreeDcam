package com.freedcam.ui.views;

import android.app.Activity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import com.troop.freedcam.R;

/**
 * Created by troop on 07.12.2015.
 */
public class ViewTestActivity extends Activity
{
    private RotatingSeekbar seekbar;
    private TextView textView;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewtest);
        textView =(TextView)findViewById(R.id.textView);
        seekbar = (RotatingSeekbar)findViewById(R.id.view);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textView.setText(seekbar.GetCurrentString());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
