package freed.cam.ui.themesample.cameraui.binding;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.View;

import androidx.databinding.BindingAdapter;

import freed.cam.apis.basecamera.parameters.AbstractParameter;

public class CustomBinding {
    @BindingAdapter("setViewState")
    public static void setViewState(View view, AbstractParameter.ViewState value)
    {
        if (view == null || value == null)
            return;
        switch (value)
        {
            case Enabled:
                if (view.getVisibility() == View.GONE)
                    view.setVisibility(View.VISIBLE);
                if (view.getBackground() != null)
                    view.getBackground().setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC_ATOP);
                break;
            case Disabled:
                if (view.getVisibility() == View.GONE)
                    view.setVisibility(View.VISIBLE);
                if (view.getBackground() != null)
                    view.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
                break;
            case Visible:
                view.setVisibility(View.VISIBLE);
                //view.animate().setListener(null).scaleY(1f).setDuration(300);
                break;
            case Hidden:
                //view.animate().setListener(new HideAnimator(view)).scaleY(0f).setDuration(300);
                view.setVisibility(View.GONE);
                break;
        }
    }
}
