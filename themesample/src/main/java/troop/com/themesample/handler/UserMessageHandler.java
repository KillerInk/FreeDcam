package troop.com.themesample.handler;

import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.interfaces.I_CameraChangedListner;
import com.troop.freedcam.i_camera.interfaces.I_Module;
import com.troop.freedcam.ui.AppSettingsManager;

import troop.com.themesample.R;


/**
 * Created by troop on 04.10.2015.
 */
public class UserMessageHandler implements I_CameraChangedListner
{
    LinearLayout messageHolder;
    TextView messageTextView;
    AbstractCameraUiWrapper cameraUiWrapper;
    Handler handler;

    public UserMessageHandler(View view)
    {
        this.messageHolder = (LinearLayout)view.findViewById(R.id.userMessageHolder);
        this.messageTextView = (TextView)view.findViewById(R.id.textView_usermessage);

        handler = new Handler();

    }

    public void SetCameraUiWrapper(AbstractCameraUiWrapper wrapper)
    {
        this.cameraUiWrapper =wrapper;
        cameraUiWrapper.SetCameraChangedListner(this);
    }

    public void SetUserMessage(String msg)
    {
        handler.removeCallbacks(hideTextView);
        messageHolder.setVisibility(View.VISIBLE);
        messageTextView.setText(/*messageTextView.getText() + "\n" + */msg);
        handler.postDelayed(hideTextView, 3000);
    }

    Runnable hideTextView = new Runnable() {
        @Override
        public void run()
        {
            messageTextView.setText("");
            messageHolder.setVisibility(View.GONE);
        }
    };

    @Override
    public void onCameraOpen(String message) {

    }

    @Override
    public void onCameraOpenFinish(String message) {

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
    public void onCameraError(final String error)
    {
        messageTextView.post(new Runnable() {
            @Override
            public void run() {
                SetUserMessage(error);
            }
        });
    }

    @Override
    public void onCameraStatusChanged(String status) {

    }

    @Override
    public void onModuleChanged(I_Module module) {

    }
}
