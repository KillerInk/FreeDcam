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

package com.freedcam.ui.themesample.handler;

import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.freedcam.apis.basecamera.camera.AbstractCameraUiWrapper;
import com.freedcam.apis.basecamera.camera.interfaces.I_CameraChangedListner;
import com.freedcam.apis.basecamera.camera.interfaces.I_Module;
import com.troop.freedcam.R;
import com.troop.freedcam.R.id;


/**
 * Created by troop on 04.10.2015.
 */
public class UserMessageHandler implements I_CameraChangedListner
{
    private LinearLayout messageHolder;
    private TextView messageTextView;
    private AbstractCameraUiWrapper cameraUiWrapper;
    private Handler handler;

    public UserMessageHandler(View view)
    {
        messageHolder = (LinearLayout)view.findViewById(id.userMessageHolder);
        messageTextView = (TextView)view.findViewById(id.textView_usermessage);

        handler = new Handler();

    }

    public void SetCameraUiWrapper(AbstractCameraUiWrapper wrapper)
    {
        cameraUiWrapper =wrapper;
        cameraUiWrapper.SetCameraChangedListner(this);
    }

    private void SetUserMessage(String msg)
    {
        handler.removeCallbacks(hideTextView);
        messageHolder.setVisibility(View.VISIBLE);
        messageTextView.setText(/*messageTextView.getText() + "\n" + */msg);
        handler.postDelayed(hideTextView, 3000);
    }

    private Runnable hideTextView = new Runnable() {
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
