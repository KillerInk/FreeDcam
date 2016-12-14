package freed.utils;

import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by GeorgeKiarie on 12/10/2016.
 */

public class LayoutUtils {



    int Preview_Width;
    int Preview_Height;

    double AR;
    int shift ;


    public LayoutUtils()
    {}

    public int getPicture_Height() {
        return Preview_Height;
    }
    public int getPicture_Width() {
        return Preview_Width;
    }

    public double getAR() {
        return AR;
    }

    public void setAR(double AR) {
        this.AR = AR;
    }

    public void setPicture_Height(int picture_Height) {
        Preview_Height = picture_Height;
    }
    public void setPicture_Width(int picture_Height) {
        Preview_Width = picture_Height;
    }



    public   void setMargins (View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    public void setDimension (View view, int width, int height) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.width= width;
            p.height = height;

            view.requestLayout();
        }
    }

    public void setFULL (View view) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.width= ViewGroup.MarginLayoutParams.FILL_PARENT;
            p.height = ViewGroup.MarginLayoutParams.FILL_PARENT;

            view.requestLayout();
        }
    }



    public int dp2px(int dp,Resources x)
    {
        //Resources r = x;
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, x.getDisplayMetrics());

        return (int)px;
    }

    public int getShift() {
        return shift;
    }

    public void setShift(int shift) {
        this.shift = shift;
    }
}
