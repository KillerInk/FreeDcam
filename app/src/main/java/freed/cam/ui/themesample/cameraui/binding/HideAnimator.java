package freed.cam.ui.themesample.cameraui.binding;

import android.animation.Animator;
import android.view.View;
import android.widget.LinearLayout;

public class HideAnimator implements Animator.AnimatorListener {

    private View linearLayout;

    public HideAnimator(View linearLayout)
    {
        this.linearLayout = linearLayout;
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if (linearLayout != null) {
            linearLayout.setVisibility(View.GONE);
            linearLayout = null;
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
