package freed.cam.apis.camera2.modules;

import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Rational;

import com.huawei.camera2ex.CameraCharacteristicsEx;
import com.huawei.camera2ex.CaptureRequestEx;
import com.troop.freedcam.R;

import java.io.File;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.utils.Log;

/**
 * Created by troop on 12.06.2017.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class HuaweiAeBracketApi2 extends AeBracketApi2 {

    private final String TAG = HuaweiAeBracketApi2.class.getSimpleName();

    public HuaweiAeBracketApi2(CameraWrapperInterface cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler) {
        super(cameraUiWrapper, mBackgroundHandler, mainHandler);
    }

    @Override
    protected void onStartTakePicture() {
        //for dng capture double files are needed cause we save jpeg and dng
        if (mrawImageReader != null)
            savedFiles = new File[Integer.parseInt(parameterHandler.Burst.GetStringValue())*2];
        else
            savedFiles = new File[Integer.parseInt(parameterHandler.Burst.GetStringValue())];
        currentFileCount = 0;
        int isorange[] = cameraHolder.characteristics.get(CameraCharacteristicsEx.HUAWEI_SENSOR_ISO_RANGE);
        maxiso = isorange[1];
        currentExposureTime = cameraHolder.currentExposureTime;
        currentiso = cameraHolder.currentIso;
        exposureTimeStep = currentExposureTime/2;

    }


    @Override
    protected void prepareCaptureBuilder(int captureNum) {
        long expotimeToSet = currentExposureTime;

        if (currentiso >= maxiso)
            currentiso = maxiso;
        if (currentiso == 0)
            currentiso = 100;
        Log.d(TAG, "set iso to :" + currentiso);
        cameraHolder.captureSessionHandler.SetCaptureParameter(CaptureRequestEx.HUAWEI_PROFESSIONAL_MODE,CaptureRequestEx.HUAWEI_PROFESSIONAL_MODE_ENABLED);
        cameraHolder.captureSessionHandler.SetCaptureParameter(CaptureRequestEx.HUAWEI_SENSOR_ISO_VALUE, currentiso);
        if (0 == captureNum)
            expotimeToSet = currentExposureTime - exposureTimeStep;
        else if (1== captureNum)
            expotimeToSet = currentExposureTime;
        else if (2 == captureNum)
            expotimeToSet = currentExposureTime + exposureTimeStep;
        Log.d(TAG,"Set shutter to:" + expotimeToSet);
        int msexpo = (int)(expotimeToSet)/1000; //ns to ms
        cameraHolder.captureSessionHandler.SetCaptureParameter(CaptureRequestEx.HUAWEI_SENSOR_EXPOSURE_TIME,msexpo);
        Rational exporat;
        if (msexpo > 1000000)
        {
            exporat = new Rational(msexpo/1000000, 1);
        }
        else
            exporat = new Rational(1,(int)(0.5D + 1.0E9F / msexpo));
        cameraHolder.captureSessionHandler.SetCaptureParameter(CaptureRequestEx.HUAWEI_PROF_EXPOSURE_TIME, exporat);
        cameraHolder.captureSessionHandler.SetPreviewParameter(CaptureRequestEx.HUAWEI_PROFESSIONAL_MODE,CaptureRequestEx.HUAWEI_PROFESSIONAL_MODE_ENABLED);
        cameraHolder.captureSessionHandler.SetPreviewParameter(CaptureRequestEx.HUAWEI_SENSOR_EXPOSURE_TIME,msexpo);
        cameraHolder.captureSessionHandler.SetPreviewParameter(CaptureRequestEx.HUAWEI_PROF_EXPOSURE_TIME, exporat);
        cameraHolder.captureSessionHandler.SetPreviewParameter(CaptureRequestEx.HUAWEI_SENSOR_ISO_VALUE, currentiso);
        //take Max latency frames to make sure full capture has correct values set. only full devices with latency 0 applys changes direct
        for (int i = 0; i < cameraHolder.characteristics.get(CameraCharacteristics.SYNC_MAX_LATENCY);i++)
            cameraHolder.captureSessionHandler.capture();
    }


    @Override
    protected void finishCapture() {
        super.finishCapture();
        if (imagecount == 3) {
            fireOnWorkFinish(savedFiles);
        }
    }
}
