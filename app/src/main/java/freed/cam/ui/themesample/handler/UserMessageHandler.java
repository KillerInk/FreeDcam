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

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import freed.cam.apis.basecamera.CameraStateEvents;
import freed.utils.Log;


/**
 * Created by troop on 04.10.2015.
 */
public class UserMessageHandler extends Handler implements CameraStateEvents , Runnable
{
    private static WeakReference<LinearLayout> messageHolderRef;
    private static WeakReference<TextView> messageTextViewRef;

    private static UserMessageHandler handler = new UserMessageHandler(Looper.getMainLooper());
    private static WeakReference<Context> contextref;

    private UserMessageHandler(Looper looper)
    {
        super(looper);
    }

    public static void setContext(Context contextt)
    {
        if (contextt == null)
            contextref = null;
        else
            contextref = new WeakReference<Context>(contextt);
    }

    public static void setMessageTextView(TextView messageTextView1, LinearLayout messageHolder1)
    {
        if (messageHolder1 == null)
            messageHolderRef = null;
        else
            messageHolderRef = new WeakReference<LinearLayout>(messageHolder1);
        if (messageTextView1 == null)
            messageTextViewRef = null;
        else
            messageTextViewRef = new WeakReference<TextView>(messageTextView1);
    }

    public static void sendMSG(String msg,boolean asToast)
    {
        if (asToast)
            handler.obtainMessage(MSG_SEND_MESSAGE_TO_USER,1,0, msg).sendToTarget();
        else
            handler.obtainMessage(MSG_SEND_MESSAGE_TO_USER,0,0, msg).sendToTarget();
    }

    public final static int MSG_SEND_MESSAGE_TO_USER = 0;
    @Override
    public void handleMessage(Message msg) {
        switch (msg.what)
        {
            case MSG_SEND_MESSAGE_TO_USER:
                try {
                    if (msg.arg1 == 1)
                        setUserMessage((String)msg.obj,true);
                    else
                        setUserMessage((String)msg.obj,false);
                }
                catch (NullPointerException ex)
                {
                    Log.WriteEx(ex);
                }
                break;
            default:
                super.handleMessage(msg);
        }

    }


    private void setUserMessage(String msg,boolean asToast)
    {
        if (asToast) {
            Context context = contextref.get();
            if (context != null)
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        }
        else {
            LinearLayout messageHolder = messageHolderRef.get();
            TextView messageTextView = messageTextViewRef.get();
            if (messageHolder != null) {
                handler.removeCallbacks(this);
                messageHolder.setVisibility(View.VISIBLE);
                if (messageTextView != null)
                    messageTextView.setText(msg);
                handler.postDelayed(this, 3000);
            }
        }
    }

    @Override
    public void onCameraOpen() {

    }

    @Override
    public void onCameraOpenFinish() {

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
        sendMSG(error,true);
    }



    @Override
    public void run()
    {
        LinearLayout messageHolder = messageHolderRef.get();
        TextView messageTextView = messageTextViewRef.get();
        if (messageHolder != null && messageTextView !=null) {
            messageTextView.setText("");
            messageHolder.setVisibility(View.GONE);
        }
    }
}
