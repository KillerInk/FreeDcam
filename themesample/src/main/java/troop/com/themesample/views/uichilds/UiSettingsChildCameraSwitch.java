package troop.com.themesample.views.uichilds;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.troop.freedcam.camera.ExtendedSurfaceView;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.sonyapi.CameraUiWrapperSony;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_Activity;

/**
 * Created by troop on 13.06.2015.
 */
public class UiSettingsChildCameraSwitch extends UiSettingsChild
{
    AbstractCameraUiWrapper cameraUiWrapper;
    int currentCamera = 0;
    public UiSettingsChildCameraSwitch(Context context) {
        super(context);
    }

    public UiSettingsChildCameraSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(Context context) {
        super.init(context);

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCamera();
            }
        });
        valueText.setText(getCamera(currentCamera));
    }

    @Override
    public void SetStuff(I_Activity i_activity, AppSettingsManager appSettingsManager, String settingvalue) {
        super.SetStuff(i_activity, appSettingsManager, settingvalue);
        currentCamera = appSettingsManager.GetCurrentCamera();
    }

    public void SetCameraUiWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        if (cameraUiWrapper instanceof CameraUiWrapperSony)
        {
            this.setVisibility(View.GONE);
        }
        else {
            this.setVisibility(View.VISIBLE);
        }
    }

    private void switchCamera()
    {
        int maxcams = cameraUiWrapper.cameraHolder.CameraCout();
        if (currentCamera++ >= maxcams - 1)
            currentCamera = 0;

        appSettingsManager.SetCurrentCamera(currentCamera);
        Log.d(TAG, "Stop Preview and Camera");
        if (i_activity.GetSurfaceView() != null &&  i_activity.GetSurfaceView() instanceof ExtendedSurfaceView)
        {
            ((ExtendedSurfaceView)i_activity.GetSurfaceView()).SwitchViewMode();
        }
        i_activity.SwitchCameraAPI(appSettingsManager.getCamApi());
        valueText.setText(getCamera(currentCamera));
    }

    private String getCamera(int i)
    {
        if (i == 0)
            return "Front";
        else if (i == 1)
            return "Back";
        else
            return "3D";
    }

    @Override
    public String[] GetValues() {
        return null;
    }


}
