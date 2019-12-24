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
import java.util.ArrayList;
import java.util.List;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract;
import freed.cam.apis.basecamera.parameters.modes.ToneMapChooser;
import freed.cam.apis.camera2.modules.helper.CaptureType;
import freed.cam.apis.camera2.modules.helper.ImageCaptureHolder;
import freed.cam.apis.camera2.modules.helper.MySocket;
import freed.cam.apis.camera2.modules.helper.StreamAbleCaptureHolder;
import freed.cam.apis.camera2.parameters.ae.AeManagerCamera2;
import freed.cam.ui.themesample.handler.UserMessageHandler;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CellStormModule extends PictureModuleApi2 {
    // THIS IS THE OLD VERSION WITHOUT GITHUB SUPPORT!

    private final String TAG = CellStormModule.class.getSimpleName();

    private boolean continueCapture = false;
    private int cropSize = 100;

    private MySocket my_socket;

    private boolean doStream = true;
    private List<File> fileList;


    public CellStormModule(CameraWrapperInterface cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler) {
        super(cameraUiWrapper, mBackgroundHandler, mainHandler);
        name = cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.module_cellstorm);
        Log.i(TAG, "This is cellSTORM1!");
    }



    @Override
    public void InitModule() {
        super.InitModule();
        fileList = new ArrayList<>();
        if (doStream) {
            if (cameraUiWrapper.getActivityInterface().getPermissionManager().hasWifiPermission(null)) {
                try {
                    // connect to server for streaming the bytes
                    connectServer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        else
            if (my_socket != null)
            {
                my_socket.closeConnection();
                my_socket = null;
            }

        // Set cropsize derived from settingsmanager
        String mCropsize = SettingsManager.get(SettingKeys.mCropsize).get();
        try{
            cropSize = Integer.parseInt(mCropsize);
        }
        catch(NumberFormatException e){
            cropSize = cropSize;
        }
        Log.d(TAG, "Cropsize " + cropSize);
    }

    @Override
    public void DestroyModule() {
        super.DestroyModule();
        if (my_socket != null) {
            my_socket.closeConnection();
            my_socket.destroy();
        }
        my_socket = null;
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
        //currentCaptureHolder.setCropSize(cropSize, cropSize);
    }

    @Override
    public void internalFireOnWorkDone(File file) {
        if (continueCapture) {
            captureStillPicture();
            if (file != null)
                fileList.add(file);
        }
        else {
            fireOnWorkFinish(fileList.toArray(new File[fileList.size()]));
            fileList.clear();
        }

    }


    public void connectServer() throws Exception {
        if (my_socket != null && my_socket.isConnected()) {
            Log.d(TAG, "################Socket is already connected");
            return;
        }
        String ip_port = SettingsManager.get(SettingKeys.IP_PORT).get();
        String splitIP_Port[] = ip_port.split(":");
        if (splitIP_Port == null || splitIP_Port.length !=2)
            throw  new Exception("Ip or port is empty");
        my_socket = new MySocket(splitIP_Port[0],Integer.parseInt(splitIP_Port[1]));
        my_socket.connect();

    }

    @Override
    protected void TakePicture() {
        isWorking = true;
        currentCaptureHolder = new StreamAbleCaptureHolder(cameraHolder.characteristics, CaptureType.Bayer16, cameraUiWrapper.getActivityInterface(),this,this, this,my_socket);
        ((StreamAbleCaptureHolder)currentCaptureHolder).setCropsize(cropSize);
        currentCaptureHolder.setFilePath(getFileString(), SettingsManager.getInstance().GetWriteExternal());
        currentCaptureHolder.setForceRawToDng(SettingsManager.get(SettingKeys.forceRawToDng).get());
        currentCaptureHolder.setToneMapProfile(((ToneMapChooser)cameraUiWrapper.getParameterHandler().get(SettingKeys.TONEMAP_SET)).getToneMap());
        currentCaptureHolder.setSupport12bitRaw(SettingsManager.get(SettingKeys.support12bitRaw).get());

        Log.d(TAG, "captureStillPicture ImgCount:"+ BurstCounter.getImageCaptured() +  " ImageCaptureHolder Path:" + currentCaptureHolder.getFilepath());

        String cmat = SettingsManager.get(SettingKeys.MATRIX_SET).get();
        if (cmat != null && !TextUtils.isEmpty(cmat) &&!cmat.equals("off")) {
            currentCaptureHolder.setCustomMatrix(SettingsManager.getInstance().getMatrixesMap().get(cmat));
        }

        if (jpegReader != null)
            jpegReader.setOnImageAvailableListener(currentCaptureHolder,mBackgroundHandler);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && rawReader != null) {
            rawReader.setOnImageAvailableListener(currentCaptureHolder,mBackgroundHandler);
        }

        //cameraUiWrapper.captureSessionHandler.StopRepeatingCaptureSession();
        //cameraUiWrapper.captureSessionHandler.CancelRepeatingCaptureSession();
        prepareCaptureBuilder(BurstCounter.getImageCaptured());
        changeCaptureState(ModuleHandlerAbstract.CaptureStates.image_capture_start);
        Log.d(TAG, "StartStillCapture");
        captureStillPicture();
    }

    @Override
    protected void captureStillPicture() {
        Log.d(TAG, "#################### captureStillPicture #################");
        prepareCaptureBuilder(BurstCounter.getImageCaptured());
        changeCaptureState(ModuleHandlerAbstract.CaptureStates.image_capture_start);
        Log.d(TAG, "StartStillCapture");
        cameraUiWrapper.captureSessionHandler.StopRepeatingCaptureSession();
        cameraUiWrapper.captureSessionHandler.StartImageCapture(currentCaptureHolder, mBackgroundHandler);
    }

    @Override
    protected void finishCapture() {
        isWorking = false;
        changeCaptureState(ModuleHandlerAbstract.CaptureStates.image_capture_stop);
        try
        {
            if (continueCapture)
                return;
            else if (cameraUiWrapper.captureSessionHandler.getPreviewParameter(CaptureRequest.CONTROL_AE_MODE) == CaptureRequest.CONTROL_AE_MODE_OFF &&
                    cameraUiWrapper.captureSessionHandler.getPreviewParameter(CaptureRequest.SENSOR_EXPOSURE_TIME)> AeManagerCamera2.MAX_PREVIEW_EXPOSURETIME) {
                cameraUiWrapper.captureSessionHandler.SetPreviewParameter(CaptureRequest.SENSOR_EXPOSURE_TIME, AeManagerCamera2.MAX_PREVIEW_EXPOSURETIME);
                cameraUiWrapper.captureSessionHandler.SetPreviewParameter(CaptureRequest.SENSOR_FRAME_DURATION, AeManagerCamera2.MAX_PREVIEW_EXPOSURETIME);
                Log.d(TAG, "CancelRepeatingCaptureSessoion set onSessionRdy");
                cameraUiWrapper.captureSessionHandler.CancelRepeatingCaptureSession();
                onSesssionRdy();
                ((StreamAbleCaptureHolder)currentCaptureHolder).stop();
                currentCaptureHolder = null;
            }
            else {
                onSesssionRdy();
                ((StreamAbleCaptureHolder)currentCaptureHolder).stop();
                currentCaptureHolder = null;
            }
        }
        catch (NullPointerException ex) {
            Log.WriteEx(ex);
        }
    }

}