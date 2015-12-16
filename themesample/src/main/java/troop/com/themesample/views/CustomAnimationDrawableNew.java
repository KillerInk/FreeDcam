package troop.com.themesample.views;

import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.SystemClock;

/**
 * Created by GeorgeKiarie on 12/15/2015.
 */
public class CustomAnimationDrawableNew extends AnimationDrawable {

    // We need to keep our own frame count because mCurFrame in AnimationDrawable is private
    private int mCurFrameHack = -1;

    // This is the Runnable that we will call when the animation finishes
    private Runnable mCallback = null;

    public CustomAnimationDrawableNew(AnimationDrawable aniDrawable) {
        for(int i=0;i<aniDrawable.getNumberOfFrames();i++){
            this.addFrame(aniDrawable.getFrame(i), aniDrawable.getDuration(i));
        }
    }

    /*
    * We override the run method in order to increment the frame count the same
    * way the AnimationDrawable class does. Also, when we are on the last frame we
    * schedule our callback to be called after the duration of the last frame.
    */
    @Override
    public void run() {
        super.run();

        mCurFrameHack += 1;
        if (mCurFrameHack == (getNumberOfFrames() - 1) && mCallback != null) {
            scheduleSelf(mCallback, SystemClock.uptimeMillis() + getDuration(mCurFrameHack));
            stop();
        }
    }

    /*
    * We override this method simply to reset the frame count just as is done in
    * AnimationDrawable.
    */
    @Override
    public void unscheduleSelf(Runnable what) {
        super.unscheduleSelf(what);
        mCurFrameHack = -1;
    }

    public void setOnFinishCallback(Runnable callback) {
        mCallback = callback;
    }

    public Runnable getOnFinishCallback() {
        return mCallback;
    }


}