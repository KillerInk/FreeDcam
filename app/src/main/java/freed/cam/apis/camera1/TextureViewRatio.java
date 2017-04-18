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

package freed.cam.apis.camera1;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

import freed.utils.Log;

/**
 * Created by troop on 27.08.2015.
 */
public class TextureViewRatio extends TextureView implements I_AspectRatio
{
    private int mRatioWidth;
    private int mRatioHeight;
    private final String TAG = TextureViewRatio.class.getSimpleName();

    public TextureViewRatio(Context context) {
        super(context);
    }

    public TextureViewRatio(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextureViewRatio(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    public void setAspectRatio(int width, int height) {
        if (width < 0 || height < 0)
        {
            throw new IllegalArgumentException("Size cannot be negative.");
        }
        mRatioWidth = width;
        mRatioHeight = height;
        Log.d(this.TAG, "new size: " + width + "x" + height);
        requestLayout();
    }

    @Override
    protected void onMeasure ( int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(width, height);
        } else {
            if (width < height * mRatioWidth / mRatioHeight) {
                setMeasuredDimension(width, width * mRatioHeight / mRatioWidth);
            } else {
                setMeasuredDimension(height * mRatioWidth / mRatioHeight, height);
            }
        }
    }


}
