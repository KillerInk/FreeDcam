package freed.cam.apis.camera2.modules;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;
import android.os.Handler;


import androidx.annotation.RequiresApi;

import com.huawei.camera2ex.CameraCharacteristicsEx;
import com.huawei.camera2ex.CaptureRequestEx;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.settings.SettingKeys;
import freed.utils.Log;

/**
 * Created by troop on 12.06.2017.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class HuaweiAeBracketApi2 extends AeBracketApi2 {

    private final String TAG = HuaweiAeBracketApi2.class.getSimpleName();
    private int isoauto = 0;
    private int shutterauto = 0;

    public HuaweiAeBracketApi2(CameraWrapperInterface cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler) {
        super(cameraUiWrapper, mBackgroundHandler, mainHandler);
    }

    @Override
    protected void onStartTakePicture() {
        isoauto = cameraUiWrapper.parametersHandler.get(SettingKeys.M_ManualIso).GetValue();
        shutterauto = cameraUiWrapper.parametersHandler.get(SettingKeys.M_ExposureTime).GetValue();
        int isorange[] = cameraHolder.characteristics.get(CameraCharacteristicsEx.HUAWEI_SENSOR_ISO_RANGE);
        maxiso = isorange[isorange.length-1];
        currentExposureTime = cameraUiWrapper.cameraBackroundValuesChangedListner.currentExposureTime;
        currentiso = cameraUiWrapper.cameraBackroundValuesChangedListner.currentIso;
        exposureTimeStep = currentExposureTime/2;
        Log.d(TAG, "MaxIso:" +maxiso + " currentExposureTime:" + currentExposureTime +" currentiso:" + currentiso + " exposureStep:" +exposureTimeStep);
    }


    @Override
    protected void prepareCaptureBuilder(int captureNum) {
        long expotimeToSet = 0;

        if (currentiso >= maxiso)
            currentiso = maxiso;
        if (currentiso == 0)
            currentiso = 100;
        Log.d(TAG, "captureImage:" +captureNum+ " set iso to :" + currentiso);

        if (0 == captureNum)
            expotimeToSet = currentExposureTime - exposureTimeStep;
        else if (1== captureNum)
            expotimeToSet = currentExposureTime;
        else if (2 == captureNum)
            expotimeToSet = currentExposureTime + exposureTimeStep;
        Log.d(TAG,"Set shutter to:" + expotimeToSet);
        int msexpo = (int)(expotimeToSet)/1000; //ns to ms

       /* Rational exporat;
        if (msexpo > 1000000)
        {
            exporat = new Rational(msexpo/1000000, 1);
        }
        else
            exporat = new Rational(1,(int)(0.5D + 1.0E9F / msexpo));*/
        cameraUiWrapper.captureSessionHandler.SetCaptureParameter(CaptureRequestEx.HUAWEI_SENSOR_EXPOSURE_TIME,msexpo);
        //cameraUiWrapper.captureSessionHandler.SetCaptureParameter(CaptureRequestEx.HUAWEI_PROF_EXPOSURE_TIME, exporat);
        cameraUiWrapper.captureSessionHandler.SetCaptureParameter(CaptureRequestEx.HUAWEI_PROFESSIONAL_MODE,CaptureRequestEx.HUAWEI_PROFESSIONAL_MODE_ENABLED);
        cameraUiWrapper.captureSessionHandler.SetCaptureParameter(CaptureRequestEx.HUAWEI_SENSOR_ISO_VALUE, currentiso);
        cameraUiWrapper.captureSessionHandler.SetPreviewParameter(CaptureRequestEx.HUAWEI_PROFESSIONAL_MODE,CaptureRequestEx.HUAWEI_PROFESSIONAL_MODE_ENABLED);
        cameraUiWrapper.captureSessionHandler.SetPreviewParameter(CaptureRequestEx.HUAWEI_SENSOR_EXPOSURE_TIME,msexpo);
        //cameraUiWrapper.captureSessionHandler.SetPreviewParameter(CaptureRequestEx.HUAWEI_PROF_EXPOSURE_TIME, exporat);
        cameraUiWrapper.captureSessionHandler.SetPreviewParameter(CaptureRequestEx.HUAWEI_SENSOR_ISO_VALUE, currentiso);
        //take Max latency frames to make sure full capture has correct values set. only full devices with latency 0 applys changes direct
        int fps = cameraHolder.characteristics.get(CameraCharacteristics.SYNC_MAX_LATENCY);
        for (int i = 0; i < fps;i++)
            cameraUiWrapper.captureSessionHandler.capture();

    }


    @Override
    protected void finishCapture() {
        super.finishCapture();
        cameraUiWrapper.parametersHandler.get(SettingKeys.M_ManualIso).SetValue(isoauto,true);
        cameraUiWrapper.parametersHandler.get(SettingKeys.M_ExposureTime).SetValue(shutterauto,true);
    }

}
