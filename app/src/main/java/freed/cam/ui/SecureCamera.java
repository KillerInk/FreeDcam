package freed.cam.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.view.Window;
import android.view.WindowManager;

/**
 * Workaround helper class for secure-lockscreen double-onResume() bug:
 * <p>
 * If started from the secure-lockscreen, the activity may be quickly started,
 * resumed, paused, stopped, and then started and resumed again. This is
 * problematic for launch time from the secure-lockscreen because we typically open the
 * camera in onResume() and close it in onPause(). These camera operations take
 * a long time to complete. To workaround it, this class filters out
 * high-frequency onResume()->onPause() sequences if the current intent
 * indicates that we have started from the secure-lockscreen.
 * </p>
 * <p>
 * Sequences of onResume() followed quickly by onPause(), when the activity is
 * started from a secure-lockscreen will result in a quick no-op.<br>
 * </p>
 */
public class SecureCamera {

    // The intent extra for camera from secure lock screen. True if the gallery
    // should only show newly captured pictures. sSecureAlbumId does not
    // increment. This is used when switching between camera, camcorder, and
    // panorama. If the extra is not set, it is in the normal camera mode.
    public static final String SECURE_CAMERA_EXTRA = "secure_camera";

    public interface SecureCameraActivity {
        void onResumeTasks();
        void onPauseTasks();
    }

    /**
     * The amount of time to wait before running onResumeTasks when started from
     * the lockscreen.
     */
    private static final long ON_RESUME_DELAY_MILLIS = 200;

    /**
     * A reference to the main handler on which to run lifecycle methods.
     */
    private Handler mMainHandler;

    private boolean mPaused;

    private boolean fIsSecureCamera;

    /**
     * True if the last call to onResume() resulted in a delayed call to
     * mOnResumeTasks which was then canceled due to an immediate onPause().
     * This allows optimizing the common case in which the subsequent
     * call to onResume() should execute onResumeTasks() immediately.
     */
    private boolean mCanceledResumeTasks = false;

    /**
     * A runnable for deferring tasks to be performed in onResume() if starting
     * from the lockscreen.
     */
    private final Runnable mOnResumeTasks = new Runnable() {
        @Override
        public void run() {
            if (mPaused) {
                mActivity.onResumeTasks();
                mPaused = false;
                mCanceledResumeTasks = false;
            }
        }
    };

    private final SecureCameraActivity mActivity;

    public SecureCamera(SecureCameraActivity activity) {
        mActivity = activity;
    }

    public void onCreate() {
        mMainHandler = new Handler(getActivity().getMainLooper());
        mPaused = true;
        checkSecure();
        if (fIsSecureCamera) {
            // Change the window flags so that secure camera can show when locked
            Window win = getActivity().getWindow();
            WindowManager.LayoutParams params = win.getAttributes();
            params.flags |= WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
            params.flags |= WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;
            params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
            //PowerManager pm = ((PowerManager) getActivity().getSystemService(Context.POWER_SERVICE));
            //mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, );
            //mWakeLock.acquire();
            win.setAttributes(params);
        }
    }

    public void onResume() {
        mMainHandler.removeCallbacks(mOnResumeTasks);
        if (fIsSecureCamera && !mCanceledResumeTasks) {
            mMainHandler.postDelayed(mOnResumeTasks, ON_RESUME_DELAY_MILLIS);
        } else {
            if (mPaused) {
                mActivity.onResumeTasks();
                mPaused = false;
                mCanceledResumeTasks = false;
            }
        }
    }

    public void onPause() {
        mMainHandler.removeCallbacks(mOnResumeTasks);
        if (!mPaused) {
            mActivity.onPauseTasks();
            mPaused = true;
        } else {
            mCanceledResumeTasks = true;
        }
    }

    private void checkSecure() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            fIsSecureCamera = false;
            return;
        }
        Intent intent = getActivity().getIntent();
        String action = intent.getAction();
        fIsSecureCamera = MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA_SECURE.equals(action)
                || MediaStore.ACTION_IMAGE_CAPTURE_SECURE.equals(action)
                || intent.getBooleanExtra(SECURE_CAMERA_EXTRA, false);
    }

    private Activity getActivity() {
        return (Activity)mActivity;
    }
}
