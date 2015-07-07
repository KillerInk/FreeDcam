package com.troop.freedcam.ui.menu.themes.classic;

import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.interfaces.I_CameraChangedListner;
import com.troop.freedcam.i_camera.interfaces.I_Module;
import com.troop.freedcam.i_camera.interfaces.I_error;
import com.troop.freedcam.sonyapi.CameraUiWrapperSony;
import com.troop.freedcam.ui.menu.themes.R;


/**
 * Created by Ingo on 15.02.2015.
 */
public class MessageHandler implements I_error, I_CameraChangedListner
{
    View activity;
    TextView textView;
    Handler handler;
    AbstractCameraUiWrapper cameraUiWrapper;

    public MessageHandler(View activity)
    {
        this.activity = activity;
        this.textView = (TextView)activity.findViewById(R.id.textView_UImessage);
        handler = new Handler();
        textView.setVisibility(View.GONE);
        textView.setText("");
    }

    public void SetCameraUiWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        cameraUiWrapper.SetCameraChangedListner(this);
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

    @Override
    public void OnError(final String error) {
        textView.post(new Runnable() {
            @Override
            public void run() {
                ShowMessage(error);
            }
        });
    }

    @Override
    public void onCameraOpen(String message) {
        try {
            if (cameraUiWrapper instanceof CameraUiWrapperSony)
            {
                ShowMessage("Searching RemoteDevice");
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onCameraOpenFinish(String message)
    {
        if (cameraUiWrapper instanceof CameraUiWrapperSony)
        {
            ShowMessage("Found RemoteDevice");
        }
    }

    @Override
    public void onCameraClose(String message) {

    }

    @Override
    public void onPreviewOpen(String message) {

    }

    @Override
    public void onPreviewClose(String message) {

    }

    @Override
    public void onCameraError(String error)
    {
        ShowMessage(error);
    }

    @Override
    public void onCameraStatusChanged(String status) {

    }

    @Override
    public void onModuleChanged(I_Module module) {

    }
}
