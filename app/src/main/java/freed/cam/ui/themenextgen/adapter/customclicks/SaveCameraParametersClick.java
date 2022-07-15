package freed.cam.ui.themenextgen.adapter.customclicks;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.media.MediaRecorder;
import android.os.Build;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import camera2_hidden_keys.ReflectionHelper;
import freed.cam.apis.CameraApiManager;
import freed.cam.apis.camera1.CameraHolder;
import freed.cam.ui.themenextgen.view.button.NextGenSettingButton;
import freed.settings.SettingsManager;
import freed.utils.Log;
import freed.utils.MediaScannerManager;

public class SaveCameraParametersClick implements NextGenSettingButton.NextGenSettingButtonClick{

    private final CameraApiManager cameraApiManager;
    private final SettingsManager settingsManager;
    private final Context context;

    public SaveCameraParametersClick(CameraApiManager apiManager, SettingsManager settingsManager, Context context)
    {
        this.cameraApiManager = apiManager;
        this.settingsManager = settingsManager;
        this.context = context;
    }

    @Override
    public void onSettingButtonClick() {
        saveCamParameters();
    }
    private void saveCamParameters()
    {
        String[] paras = null;
        CameraHolder holder = (CameraHolder) cameraApiManager.getCamera().getCameraHolder();

        paras = holder.GetCamera().getParameters().flatten().split(";");

        Arrays.sort(paras);

        FileOutputStream outputStream;
        File freedcamdir = new File(settingsManager.getAppDataFolder().getAbsolutePath());
        if (!freedcamdir.exists())
            freedcamdir.mkdirs();
        File file = new File(freedcamdir.getAbsolutePath()+"/"+ Build.MODEL + "_CameraParameters.txt");
        try {
            //file.mkdirs();
            file.createNewFile();
        } catch (IOException e) {
            Log.WriteEx(e);
        }

        try {
            outputStream = new FileOutputStream(file);
            outputStream.write((Build.MODEL + "\r\n").getBytes());
            outputStream.write((System.getProperty("os.version") + "\r\n").getBytes());
            for (String s : paras)
            {
                outputStream.write((s+"\r\n").getBytes());
            }

            ReflectionHelper reflectionHelper = new ReflectionHelper();

            reflectionHelper.dumpClass(Camera.class,outputStream,0);
            reflectionHelper.dumpClass(MediaRecorder.class,outputStream,0);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                reflectionHelper.dumpClass(CameraDevice.class,outputStream,0);
                reflectionHelper.dumpClass(CameraCharacteristics.class,outputStream,0);
                reflectionHelper.dumpClass(CaptureRequest.class,outputStream,0);
                reflectionHelper.dumpClass(CaptureResult.class,outputStream,0);
                reflectionHelper.dumpClass(CameraManager.class,outputStream,0);
            }

            outputStream.close();
        } catch (Exception e) {
            Log.WriteEx(e);
        }
        MediaScannerManager.ScanMedia(context,file);
    }
}
