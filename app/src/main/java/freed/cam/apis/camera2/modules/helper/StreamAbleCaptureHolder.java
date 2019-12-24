package freed.cam.apis.camera2.modules.helper;

import android.graphics.ImageFormat;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;


import androidx.annotation.RequiresApi;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import freed.ActivityInterface;
import freed.cam.apis.basecamera.modules.ModuleInterface;
import freed.cam.apis.basecamera.modules.WorkFinishEvents;
import freed.cam.ui.themesample.handler.UserMessageHandler;
import freed.dng.DngProfile;
import freed.dng.opcode.OpCodeCreator;
import freed.image.ImageManager;
import freed.image.ImageSaveTask;
import freed.jni.OpCode;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class StreamAbleCaptureHolder extends ImageCaptureHolder {

    private static final String TAG = StreamAbleCaptureHolder.class.getSimpleName();
    //the connection to the server
    private MySocket socket;
    //used to send data to the target
    private final BlockingQueue<Image> imageBlockingQueue;
    private FileStreamRunner fileStreamRunner;
    private final byte FRAME_START_FLAG = (byte)0xff;

    // Settings for cropping the image
    private int mCropsize = 100;
    //get currently ignored and use the center of the image
    private int x_crop_pos = 0;
    //get currently ignored and use the center of the image
    private int y_crop_pos = 0;
    private int capturecount = 0;

    public void stop()
    {
        if (fileStreamRunner != null)
            fileStreamRunner.stop();

    }

    public void setCropsize(int myCropsize){
        this.mCropsize = myCropsize;
    }

    public StreamAbleCaptureHolder(CameraCharacteristics characteristicss, CaptureType captureType, ActivityInterface activitiy, ModuleInterface imageSaver, WorkFinishEvents finish, RdyToSaveImg rdyToSaveImg, MySocket socket) {
        super(characteristicss, captureType, activitiy, imageSaver, finish, rdyToSaveImg);
        this.socket =socket;

        imageBlockingQueue = new LinkedBlockingQueue<>(4);
        if (socket != null && socket.isConnected()) {
            fileStreamRunner = new FileStreamRunner();
            Thread thread = new Thread(fileStreamRunner);
            thread.start();

        }
        else
            UserMessageHandler.sendMSG("Not Connected to Server ",false);
    }

    @Override
    protected void saveImage(Image image, String f) {
        Log.d(TAG, "add image to Queue left:" + imageBlockingQueue.remainingCapacity());
        if (socket != null) {
            try {
                imageBlockingQueue.put(image);
            } catch (InterruptedException e) {
                Log.WriteEx(e);
            }
        }
        else
            saveDng(image,f);
    }


    private void saveDng(Image image, String f) {
        f = f + "_c"+capturecount++ + ".dng";
        Log.d(TAG,"Save Image " + f);

        ImageSaveTask saveTask = new ImageSaveTask(activityInterface,moduleInterface);
        byte[] bytes = cropByteArray(x_crop_pos, y_crop_pos, mCropsize, mCropsize, image);
        saveTask.setBytesTosave(bytes,ImageSaveTask.RAW_SENSOR);
        saveTask.setForceRawToDng(true);
        saveTask.setOpCode(null);
        try {
            saveTask.setFocal(captureResult.get(CaptureResult.LENS_FOCAL_LENGTH));
        } catch (NullPointerException e) {
            Log.WriteEx(e);
        }
        try {
            saveTask.setFnum(captureResult.get(CaptureResult.LENS_APERTURE));
        } catch (NullPointerException e) {
            Log.WriteEx(e);
        }
        try {
            saveTask.setIso(captureResult.get(CaptureResult.SENSOR_SENSITIVITY));
        } catch (NullPointerException e) {
            Log.WriteEx(e);
            saveTask.setIso(100);
        }
        try {
            double mExposuretime = captureResult.get(CaptureResult.SENSOR_EXPOSURE_TIME).doubleValue() / 1000000000;
            saveTask.setExposureTime((float) mExposuretime);
        } catch (NullPointerException e) {
            Log.WriteEx(e);
            saveTask.setExposureTime(0);
        }
        try {
            saveTask.setExposureIndex(captureResult.get(CaptureResult.CONTROL_AE_EXPOSURE_COMPENSATION) * characteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_STEP).floatValue());
        } catch (NullPointerException e) {
            Log.WriteEx(e);
            saveTask.setExposureIndex(0);
        }

        try {
            float greensplit = captureResult.get(CaptureResult.SENSOR_GREEN_SPLIT);
            int fgreen = (int)(greensplit * 5000) -5000;
            Log.d(TAG,"GreenSplit:" + fgreen);
            saveTask.setBayerGreenSplit(fgreen);
        }
        catch (NullPointerException ex)
        {
            Log.WriteEx(ex);
        }



        DngProfile prof = getDngProfile(DngProfile.Pure16bit_To_12bit, mCropsize,mCropsize);
        /*int ar[] = {0,0,mCropsize,mCropsize};
        prof.setActiveArea(ar);*/
        image.close();
        saveTask.setDngProfile(prof);
        saveTask.setFilePath(new File(f), false);
        if (saveTask != null) {
            ImageManager.putImageSaveTask(saveTask);
            Log.d(TAG, "Put task to Queue");
        }
    }

    /**
     *
     * @param x_crop_pos start crop position in the pixel area
     * @param y_crop_pos start crop position in the pixel area
     * @param buf_width length of the buffer in pixel size
     * @param buf_height length of the buffer in pixel size
     * @param image to get cropped
     * @return
     */
    private byte[] cropByteArray(int x_crop_pos ,int y_crop_pos, int buf_width, int buf_height, Image image)
    {
        int x_center = image.getWidth() / 2 ;
        int y_center = image.getHeight() / 2 ;
        int x_offset = x_center - (buf_width/2);
        int x_end = (x_offset +buf_width);
        int y_offset =  y_center - (buf_height/2);
        int y_end = (y_offset +buf_height);

        byte bytes[] = new byte[buf_height*buf_width *2];
        int rowsize = image.getWidth()*2;
        int bytepos = 0;
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        Log.d(TAG, "y length " + (y_end - y_offset) + " x length " + (x_end - x_offset)+ "/" + (y_end*2-y_offset*2) + " rowsize in bytes " + rowsize);
        int nextXstart= 0;
        for (int y = y_offset; y < y_end; y++) {
            nextXstart = y * rowsize;
            //Log.d(TAG, "###########new Row " + (y-y_offset)+"/"+ (y_end-y_offset) + " bytepos " + bytepos + "  buf byte pos " + nextXstart);
            for (int x = x_offset*2; x < x_end*2; x++) {
                //Log.d(TAG, " buf " + (y*rowsize+ rowsize)+x);
                bytes[bytepos++] = buffer.get(nextXstart+x);


            }
        }
        if (buffer != null) {
            buffer.clear();
            buffer = null;
        }
        return bytes;
    }

    private class FileStreamRunner implements Runnable
    {
        boolean run = false;
        private int count = 0;
        public FileStreamRunner()
        {
            run = true;
            count = 0;
        }

        public void stop(){ run = false; }

        @Override
        public void run() {
            if (!socket.isConnected())
                return;
            Image image = null;
            while (run) {
                try {
                    image = imageBlockingQueue.take();
                } catch (InterruptedException e) {
                    Log.WriteEx(e);
                }

                Log.d(TAG,"Send img " + count++);
                byte[] bytes = cropByteArray(x_crop_pos, y_crop_pos, mCropsize, mCropsize, image);
                image.close();
                image = null;
                try {
                    //sending plain bayer bytearray with simple start end of file
                    socket.write(FRAME_START_FLAG);
                    Log.d(TAG, "Send data : " + bytes.length);
                    socket.write(bytes);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                moduleInterface.internalFireOnWorkDone(null);
            }
        }
    }
}
