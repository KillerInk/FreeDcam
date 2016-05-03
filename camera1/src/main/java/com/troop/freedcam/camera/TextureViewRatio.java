package com.troop.freedcam.camera;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

import com.troop.filelogger.Logger;
import com.troop.freedcam.ui.I_AspectRatio;

/**
 * Created by troop on 27.08.2015.
 */
public class TextureViewRatio extends TextureView implements I_AspectRatio
{
    private int mRatioWidth = 0;
    private int mRatioHeight = 0;
    private static String TAG = TextureViewRatio.class.getSimpleName();

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
        Logger.d(TAG, "new size: " + width + "x" + height);
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
