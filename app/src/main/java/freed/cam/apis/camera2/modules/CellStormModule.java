package freed.cam.apis.camera2.modules;

import android.hardware.Camera;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import com.troop.freedcam.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract;
import freed.cam.apis.basecamera.parameters.modes.ToneMapChooser;
import freed.cam.apis.camera2.modules.helper.CaptureType;
import freed.cam.apis.camera2.modules.helper.ImageCaptureHolder;
import freed.cam.apis.camera2.modules.helper.StreamAbleCaptureHolder;
import freed.cam.apis.camera2.parameters.ae.AeManagerCamera2;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CellStormModule extends PictureModuleApi2 {
    // THIS IS THE OLD VERSION WITHOUT GITHUB SUPPORT!

    private final String TAG = CellStormModule.class.getSimpleName();

    private boolean continueCapture = false;
    private int cropSize = 100;

    String my_server_ip ="192.168.2.100"; // "172.26.19.190"; //// "172.26.19.190";// "192.168.43.86";//"192.168.2.100";//"172.26.19.190";//"192.168.43.86"; //
    int my_portnumber = 4444;
    Socket mysocket;
    PrintWriter myprintwriter;
    private boolean hasConnection = false;

    public CellStormModule(CameraWrapperInterface cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler) {
        super(cameraUiWrapper, mBackgroundHandler, mainHandler);
        name = cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.module_cellstorm);
        Log.i(TAG, "This is cellSTORM1!");
    }



    @Override
    public void InitModule() {
        super.InitModule();
        if (cameraUiWrapper.getActivityInterface().getPermissionManager().hasWifiPermission(null)) {
            try {
                // connect to server for streaming the bytes
                connectServer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Set cropsize derived from settingsmanager
        String mCropsize = SettingsManager.get(SettingKeys.mCropsize).get();
        try{
            cropSize = Integer.parseInt(mCropsize);
        }
        catch(NumberFormatException e){
            cropSize = cropSize;
        }

        Log.i(TAG, "Values set:" + my_server_ip + ":" + String.valueOf(my_portnumber) + "- cropsize: " + String.valueOf(cropSize));

    }

    @Override
    public void DestroyModule() {
        super.DestroyModule();
        try {
            mysocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        hasConnection = false;
    }

    @Override
    public String LongName() {
        return "CellStorm";
    }

    @Override
    public String ShortName() {
        return "Cell";
    }

    @Override
    public void DoWork() {
        Log.i(TAG, "This is cellSTORM!" + continueCapture);
        if (continueCapture) {
            continueCapture = false;
            mBackgroundHandler.post(()->{
                Log.d(TAG, "cancel capture");
                cameraUiWrapper.captureSessionHandler.cancelCapture();
                finishCapture();
                changeCaptureState(ModuleHandlerAbstract.CaptureStates.image_capture_stop);
            });
        }
        else {
            continueCapture = true;
            mBackgroundHandler.post(()->TakePicture());
        }
    }

    @Override
    protected void prepareCaptureBuilder(int captureNum) {
        currentCaptureHolder.setCropSize(cropSize, cropSize);
    }

    @Override
    public void internalFireOnWorkDone(File file) {
        if (continueCapture)
            captureStillPicture();

    }


    public void connectServer() throws Exception {
        if (hasConnection)
            return;
        String ip_port = SettingsManager.get(SettingKeys.IP_PORT).get();
        String splitIP_Port[] = ip_port.split(":");
        if (splitIP_Port == null || splitIP_Port.length !=2)
            throw  new Exception("Ip or port is empty");
        my_server_ip = splitIP_Port[0];
        my_portnumber = Integer.parseInt(splitIP_Port[1]);
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

            mysocket = new Socket(my_server_ip, my_portnumber);
            hasConnection = true;

        } catch (IOException e) {
            hasConnection = false;
            e.printStackTrace();
            Log.e(TAG, String.valueOf(e));
        }
    }

    @Override
    protected void TakePicture() {
        isWorking = true;
        currentCaptureHolder = new StreamAbleCaptureHolder(cameraHolder.characteristics, CaptureType.Bayer16, cameraUiWrapper.getActivityInterface(),this,this, this,mysocket);
        currentCaptureHolder.setCropSize(cropSize,cropSize);
        currentCaptureHolder.setFilePath(getFileString(), SettingsManager.getInstance().GetWriteExternal());
        currentCaptureHolder.setForceRawToDng(SettingsManager.get(SettingKeys.forceRawToDng).get());
        currentCaptureHolder.setToneMapProfile(((ToneMapChooser)cameraUiWrapper.getParameterHandler().get(SettingKeys.TONEMAP_SET)).getToneMap());
        currentCaptureHolder.setSupport12bitRaw(SettingsManager.get(SettingKeys.support12bitRaw).get());
        //currentCaptureHolder.setCropSize(cropSize, cropSize);

        //currentCaptureHolder.setOutputStream(myOutputStream);

        Log.d(TAG, "captureStillPicture ImgCount:"+ BurstCounter.getImageCaptured() +  " ImageCaptureHolder Path:" + currentCaptureHolder.getFilepath());

        String cmat = SettingsManager.get(SettingKeys.MATRIX_SET).get();
        if (cmat != null && !TextUtils.isEmpty(cmat) &&!cmat.equals("off")) {
            currentCaptureHolder.setCustomMatrix(SettingsManager.getInstance().getMatrixesMap().get(cmat));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            rawReader.setOnImageAvailableListener(currentCaptureHolder,mBackgroundHandler);
        }

        //cameraUiWrapper.captureSessionHandler.StopRepeatingCaptureSession();
        //cameraUiWrapper.captureSessionHandler.CancelRepeatingCaptureSession();
        prepareCaptureBuilder(BurstCounter.getImageCaptured());
        changeCaptureState(ModuleHandlerAbstract.CaptureStates.image_capture_start);
        Log.d(TAG, "StartStillCapture");
        super.TakePicture();
    }

    @Override
    protected void captureStillPicture() {
        Log.d(TAG, "#################### captureStillPicture #################");
        prepareCaptureBuilder(BurstCounter.getImageCaptured());
        changeCaptureState(ModuleHandlerAbstract.CaptureStates.image_capture_start);
        Log.d(TAG, "StartStillCapture");
        cameraUiWrapper.captureSessionHandler.StartImageCapture(currentCaptureHolder, mBackgroundHandler);
    }


    @Override
    protected void finishCapture() {
        isWorking = false;
        changeCaptureState(ModuleHandlerAbstract.CaptureStates.image_capture_stop);
        try
        {
            if (continueCapture)
                captureStillPicture();
            else if (cameraUiWrapper.captureSessionHandler.getPreviewParameter(CaptureRequest.CONTROL_AE_MODE) == CaptureRequest.CONTROL_AE_MODE_OFF &&
                    cameraUiWrapper.captureSessionHandler.getPreviewParameter(CaptureRequest.SENSOR_EXPOSURE_TIME)> AeManagerCamera2.MAX_PREVIEW_EXPOSURETIME) {
                cameraUiWrapper.captureSessionHandler.SetPreviewParameter(CaptureRequest.SENSOR_EXPOSURE_TIME, AeManagerCamera2.MAX_PREVIEW_EXPOSURETIME);
                cameraUiWrapper.captureSessionHandler.SetPreviewParameter(CaptureRequest.SENSOR_FRAME_DURATION, AeManagerCamera2.MAX_PREVIEW_EXPOSURETIME);
                Log.d(TAG, "CancelRepeatingCaptureSessoion set onSessionRdy");
                cameraUiWrapper.captureSessionHandler.CancelRepeatingCaptureSession();
                onSesssionRdy();
                ((StreamAbleCaptureHolder)currentCaptureHolder).stop();
            }
            else {
                onSesssionRdy();
            }
        }
        catch (NullPointerException ex) {
            Log.WriteEx(ex);
        }
    }
}