/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package freed.cam.ui.themesample.handler;

import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.troop.freedcam.R.id;

import freed.cam.apis.basecamera.CameraStateEvents;
import freed.cam.apis.basecamera.CameraWrapperInterface;


/**
 * Created by troop on 04.10.2015.
 */
public class UserMessageHandler implements CameraStateEvents
{
    private final LinearLayout messageHolder;
    private final TextView messageTextView;
    private final Handler handler;

    public UserMessageHandler(View view)
    {
        messageHolder = (LinearLayout)view.findViewById(id.userMessageHolder);
        messageTextView = (TextView)view.findViewById(id.textView_usermessage);

        handler = new Handler();

    }

    public void SetCameraUiWrapper(CameraWrapperInterface wrapper)
    {
        CameraWrapperInterface cameraUiWrapper = wrapper;
        cameraUiWrapper.setCameraStateChangedListner(this);
    }

    private void SetUserMessage(String msg)
    {
        handler.removeCallbacks(hideTextView);
        messageHolder.setVisibility(View.VISIBLE);
        messageTextView.setText(/*messageTextView.getText() + "\n" + */msg);
        handler.postDelayed(hideTextView, 3000);
    }

    private final Runnable hideTextView = new Runnable() {
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
}
