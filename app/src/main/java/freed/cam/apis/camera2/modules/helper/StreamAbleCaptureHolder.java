package freed.cam.apis.camera2.modules.helper;

import android.hardware.camera2.CameraCharacteristics;
import android.media.Image;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

import freed.ActivityInterface;
import freed.cam.apis.basecamera.modules.ModuleInterface;
import freed.cam.apis.basecamera.modules.WorkFinishEvents;

public class StreamAbleCaptureHolder extends ImageCaptureHolder {

    //the connection to the server
    private Socket socket;
    //used to send data to the target
    private BufferedOutputStream bufferedOutputStream;

    // Settings for cropping the image
    private int mCropsize = 100;
    private int x_crop_pos = 0;
    private int y_crop_pos = 0;

    public void setCropsize(int myCropsize){
        this.mCropsize = myCropsize;
    }

    public StreamAbleCaptureHolder(CameraCharacteristics characteristicss, CaptureType captureType, ActivityInterface activitiy, ModuleInterface imageSaver, WorkFinishEvents finish, RdyToSaveImg rdyToSaveImg, Socket socket) {
        super(characteristicss, captureType, activitiy, imageSaver, finish, rdyToSaveImg);
        this.socket =socket;
        try {
            this.bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void saveImage(Image image, String f) {
        //ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        //byte[] bytes = new byte[buffer.remaining()];
        //buffer.get(bytes);
        byte[] bytes = cropByteArray(x_crop_pos, y_crop_pos, mCropsize, mCropsize, image);
        image.close();
        try {
            //sending plain bayer bytearray with simple start end of file
            //bufferedOutputStream.write("START".getBytes());
            bufferedOutputStream.write(bytes);
            //bufferedOutputStream.write("END".getBytes());
            bufferedOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        workerfinish.internalFireOnWorkDone(null);
    }

    /**
     *
     * @param x_crop_pos start crop position in the pixel area
     * @param y_crop_pos start crop position int the pixel area
     * @param buf_width length of the buffer in pixel size
     * @param buf_height length of the buffer in pixel size
     * @param image to get cropped
     * @return
     */
    private byte[] cropByteArray(int x_crop_pos ,int y_crop_pos, int buf_width, int buf_height, Image image)
    {
        byte bytes[] = new byte[buf_width*buf_height*2];
        int bytepos = 0;
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        int rowsize = buf_width*2;
        for (int y = y_crop_pos; y < y_crop_pos+buf_height; y++)
        {
            for (int x = x_crop_pos; x < x_crop_pos+buf_width; x++)
            {
                try {
                    // low-byte
                    bytes[bytepos] = buffer.get((y * rowsize + x)*2);
                    // high-byte
                    bytes[bytepos+1] = buffer.get((y * rowsize + x)*2 + 1);
                    bytepos = bytepos+2;
                }
                catch(ArrayIndexOutOfBoundsException e){
                    Log.e("BUFFER ERROR", String.valueOf(x)+'/'+String.valueOf(y)+'/'+String.valueOf(bytepos));
                }
            }
        }




        return bytes;
    }
}
