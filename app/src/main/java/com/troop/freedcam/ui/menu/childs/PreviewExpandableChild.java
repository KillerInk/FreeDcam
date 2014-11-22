package com.troop.freedcam.ui.menu.childs;

import android.content.Context;
import android.util.AttributeSet;

import com.troop.freedcam.ui.TextureView.I_PreviewSizeEvent;
import com.troop.freedcam.ui.menu.childs.ExpandableChild;

/**
 * Created by troop on 06.09.2014.
 */
public class PreviewExpandableChild extends ExpandableChild
{

    private I_PreviewSizeEvent previewSizeEvent;

    public PreviewExpandableChild(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PreviewExpandableChild(Context context) {
        super(context);
    }

    public PreviewExpandableChild(Context context, I_PreviewSizeEvent previewSizeEvent) {
        super(context);
        this.previewSizeEvent = previewSizeEvent;
    }

    public PreviewExpandableChild(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setValue(String value) {
        super.setValue(value);
        if (previewSizeEvent != null)
        {
            String[] widthHeight = value.split("x");
            int w = Integer.parseInt(widthHeight[0]);
            int h = Integer.parseInt(widthHeight[1]);
            previewSizeEvent.OnPreviewSizeChanged(w, h);
        }
    }
}
