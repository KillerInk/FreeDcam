package freed.cam.apis.camera2.parameters.modes;

import android.graphics.Rect;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.MeteringRectangle;
import android.os.Build;

import androidx.annotation.RequiresApi;

import freed.cam.apis.camera2.Camera2;
import freed.settings.SettingKeys;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MeteringMode extends BaseModeApi2 {
    public MeteringMode(Camera2 cameraUiWrapper, SettingKeys.Key settingMode) {
        super(cameraUiWrapper, settingMode);
    }

    public MeteringMode(Camera2 cameraUiWrapper, SettingKeys.Key key, CaptureRequest.Key<Integer> parameterKey) {
        super(cameraUiWrapper, key, parameterKey);
    }

    @Override
    protected void setValue(String valueToSet, boolean setToCamera) {
         settingMode.set(valueToSet);
        Rect s = cameraUiWrapper.getCameraHolder().characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
         if (valueToSet.equals("Frame Average"))
         {
             MeteringRectangle meteringRectangle = new MeteringRectangle(s,MeteringRectangle.METERING_WEIGHT_MAX);
             MeteringRectangle[] rectangles = new MeteringRectangle[]{meteringRectangle};
             captureSessionHandler.setMeteringArea(rectangles);
         }
         else if (valueToSet.contains("Spot"))
         {
             int h_w = s.width()/8;
             int h_h = s.height()/8;
             MeteringRectangle meteringRectangle = new MeteringRectangle(s.centerX()-h_w,s.centerY()- h_h,h_w*2, h_h*2,MeteringRectangle.METERING_WEIGHT_MAX);
             MeteringRectangle[] rectangles = new MeteringRectangle[]{meteringRectangle};
             captureSessionHandler.setMeteringArea(rectangles);
         }
         fireStringValueChanged(valueToSet);
    }
}
