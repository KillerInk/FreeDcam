package freed.cam.apis.camera1.cameraholder;

import android.hardware.Camera;

import com.sonyericsson.cameraextension.CameraExtension;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.FocusEvents;
import freed.cam.apis.camera1.CameraHolder;
import freed.settings.Frameworks;
import freed.utils.Log;

/**
 * Created by troop on 20.03.2017.
 */

public class CameraHolderSony extends CameraHolder {
    private final String TAG = CameraHolderSony.class.getSimpleName();
    private CameraExtension sonyCameraExtension;

    public CameraHolderSony(CameraWrapperInterface cameraUiWrapper, Frameworks frameworks) {
        super(cameraUiWrapper, frameworks);
    }

    /**
     * Opens the Camera
     * @param camera the camera to open
     * @return false if camera open fails, return true when open
     */
    @Override
    public boolean OpenCamera(int camera)
    {
        try
        {
            Log.d(TAG, "open camera");
            mCamera = Camera.open(camera);
            isRdy = true;


        } catch (Exception ex) {
            isRdy = false;
            Log.WriteEx(ex);
        }

        try
        {
            Log.d(TAG, "open SonyCameraExtension");
            sonyCameraExtension.open(mCamera,camera);
        } catch (Exception ex) {
            isRdy = false;
            Log.WriteEx(ex);
        }
        cameraUiWrapper.fireCameraOpen();
        return isRdy;
    }

    @Override
    public void CloseCamera()
    {
        Log.d(TAG, "Try to close Camera");
        try
        {
            mCamera.release();
            sonyCameraExtension.release();
            Log.d(TAG, "Camera Released");
        }
        catch (Exception ex)
        {
            Log.WriteEx(ex);
        }
        finally {
            mCamera = null;
            isRdy = false;
            Log.d(TAG, "Camera closed");
        }
        isRdy = false;
        cameraUiWrapper.fireCameraClose();
    }

    @Override
    public Camera.Parameters GetCameraParameters()
    {
        Camera.Parameters parameters = mCamera.getParameters();
        sonyCameraExtension.fetchParameters(parameters);
        return parameters;
    }

    @Override
    public void StartFocus(final FocusEvents autoFocusCallback) {
        sonyCameraExtension.startAutoFocus(new CameraExtension.AutoFocusCallback() {
            @Override
            public void onAutoFocus(CameraExtension.AutoFocusResult autoFocusResult) {
                if (autoFocusResult.isFocused())
                    sonyCameraExtension.stopAutoFocus();
                autoFocusCallback.onFocusEvent(autoFocusResult.isFocused());
            }
        },true,true,true);
    }

    @Override
    public void CancelFocus() {
        sonyCameraExtension.stopAutoFocus();
    }
}
