package com.troop.freecam.surfaces;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceView;

import com.troop.freecam.interfaces.PreviewSizeChangedInterface;

import java.util.List;

/**
 * Created by troop on 01.12.13.
 */
public class BasePreview extends SurfaceView implements PreviewSizeChangedInterface
{

    protected boolean hasReal3d = false;
    protected Context context;

    public BasePreview(Context context)
    {
        super(context);
        this.context = context;
    }

    public BasePreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public BasePreview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    protected void isReald3d()
    {
        try {

            Class c = Class.forName("com.lge.real3d.Real3D");

            final String LGE_3D_DISPLAY = "lge.hardware.real3d.barrier.landscape";

            if(context.getPackageManager().hasSystemFeature(LGE_3D_DISPLAY))

                hasReal3d = true;

        } catch (ClassNotFoundException e) {

            hasReal3d = false;

        }

    }

    public void setPreviewSize(Camera.Size size)
    {
        {
            double targetRatio = (double) size.width / size.height;
        }
    }



    @Override
    public void onPreviewsizeHasChanged(int w, int h) {

    }
}
