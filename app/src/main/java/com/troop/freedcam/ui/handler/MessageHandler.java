package com.troop.freedcam.ui.handler;

import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.troop.androiddng.MainActivity;
import com.troop.freedcam.R;
import com.troop.freedcam.ui.MainActivity_v2;

import org.w3c.dom.Text;

/**
 * Created by Ingo on 15.02.2015.
 */
public class MessageHandler
{
    MainActivity_v2 activity;
    TextView textView;
    Handler handler;
    public MessageHandler(MainActivity_v2 activity)
    {
        this.activity = activity;
        this.textView = (TextView)activity.findViewById(R.id.textView_UImessage);
        handler = new Handler();
        textView.setVisibility(View.GONE);
        textView.setText("");
    }

    public void ShowMessage(String msg)
    {
        String mes = textView.getText() + "\n" + msg;
        textView.setText(mes);
        textView.setVisibility(View.VISIBLE);
        handler.removeCallbacks(hideTextView);
        handler.postDelayed(hideTextView, 1500);
    }

    Runnable hideTextView = new Runnable() {
        @Override
        public void run()
        {
            textView.setText("");
            textView.setVisibility(View.GONE);
        }
    };

    public void close()
    {
        textView.setText("");

        handler.removeCallbacks(hideTextView);

    }
}
