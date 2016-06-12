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

package com.freedcam.ui.themesample.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.freedcam.apis.basecamera.interfaces.CameraWrapperInterface;
import com.freedcam.apis.basecamera.modules.I_WorkEvent;
import com.freedcam.utils.FreeDPool;
import com.freedcam.utils.Logger;
import com.freedviewer.helper.BitmapHelper;
import com.freedviewer.helper.BitmapHelper.FileEvent;
import com.freedviewer.screenslide.ScreenSlideFragment.I_ThumbClick;
import com.troop.freedcam.R.dimen;
import com.troop.freedcam.R.drawable;

import java.io.File;

/**
 * Created by troop on 13.06.2015.
 */
public class ThumbView extends ImageView implements I_WorkEvent, OnClickListener, FileEvent
{
    private final  String TAG = ThumbView.class.getSimpleName();
    private Bitmap mask;
    private I_ThumbClick click;
    private int mImageThumbSize = 0;
    private Context context;
    private BitmapHelper bitmapHelper;

    public ThumbView(Context context) {
        super(context);
        setOnClickListener(this);
        setBackgroundDrawable(context.getResources().getDrawable(drawable.thumbnail));
        this.context = context;
    }

    public ThumbView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setOnClickListener(this);
        setBackgroundDrawable(context.getResources().getDrawable(drawable.thumbnail));
        this.context = context;
    }


    public void INIT(CameraWrapperInterface cameraUiWrapper, BitmapHelper bitmapHelper)
    {
        this.bitmapHelper = bitmapHelper;
        bitmapHelper.AddFileListner(this);
        try {
            mask = BitmapFactory.decodeResource(getContext().getResources(), drawable.maskthumb);
            mImageThumbSize = context.getResources().getDimensionPixelSize(dimen.image_thumbnails_size);


            //WorkHasFinished(bitmapHelper.getFiles().get(0).getFile());
        }
        catch (NullPointerException | IndexOutOfBoundsException ex)
        {Logger.exception(ex);}


    }

    @Override
    public void WorkHasFinished(final File filePath)
    {
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                Logger.d(TAG, "Load Thumb " + filePath.getName());
                try {
                    showThumb(filePath);
                } catch (NullPointerException ex) {
                    Logger.exception(ex);
                }
            }
        });
    }

    private void showThumb(final File filePath)
    {
        final Bitmap bitmap = bitmapHelper.getBitmap(filePath, true, mImageThumbSize, mImageThumbSize);
        final Bitmap drawMap = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Config.ARGB_8888);
        Canvas drawc = new Canvas(drawMap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        if (bitmap != null && !bitmap.isRecycled())
            drawc.drawBitmap(bitmap, 0, 0, null);
        drawc.drawBitmap(mask, 0, 0, paint);
        paint.setXfermode(null);
        post(new Runnable() {
            @Override
            public void run() {
                setImageBitmap(drawMap);
            }
        });
    }

    public void SetOnThumbClickListener(I_ThumbClick click)
    {
        this.click = click;
    }

    @Override
    public void onClick(View v)
    {
        if (click != null)
            click.onThumbClick();


    }

    @Override
    public void onFileDeleted(File file) {
        WorkHasFinished(bitmapHelper.getFiles().get(0).getFile());
    }

    @Override
    public void onFileAdded(File file) {
        showThumb(file);
    }
}
