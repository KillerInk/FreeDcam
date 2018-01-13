package freed.cam.apis.camera1.renderscript;


import java.lang.ref.WeakReference;

import freed.cam.apis.camera1.I_AspectRatio;

public class SetOutputAlphaRunner implements Runnable {

    WeakReference<I_AspectRatio> weakReference;
    private int alpha = 0;

    public SetOutputAlphaRunner(I_AspectRatio output)
    {
        this.weakReference = new WeakReference<I_AspectRatio>(output);
    }

    public void setAlpha(int alpha)
    {
        this.alpha = alpha;
    }

    @Override
    public void run() {
        I_AspectRatio ratio = weakReference.get();
        if (ratio != null)
            ratio.setAlpha(alpha);
    }
}
