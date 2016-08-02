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

package freed.cam.ui.themesample.cameraui;

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

import com.troop.freedcam.R.drawable;

import java.io.File;

import freed.ActivityAbstract;
import freed.viewer.screenslide.ScreenSlideFragment.I_ThumbClick;

/**
 * Created by troop on 13.06.2015.
 */
public class ThumbView extends ImageView implements OnClickListener, ActivityAbstract.FileEvent
{
    private final  String TAG = ThumbView.class.getSimpleName();
    private Bitmap mask;
    private I_ThumbClick click;

    public ThumbView(Context context) {
        super(context);
        setOnClickListener(this);
        setBackgroundResource(drawable.thumbnail);
        init();
    }

    public ThumbView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setOnClickListener(this);
        setBackgroundResource(drawable.thumbnail);
        init();
    }

    private void init()
    {
        mask = BitmapFactory.decodeResource(getContext().getResources(), drawable.maskthumb);
    }


    public void showThumb(Bitmap bitmap)
    {
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
            click.onThumbClick(0, v);
    }

    @Override
    public void onFileDeleted(File file)
    {
        //showThumb(newBitmapToShow);
    }

    @Override
    public void onFileAdded(File file) {
        //showThumb(bitmap);
    }
}
