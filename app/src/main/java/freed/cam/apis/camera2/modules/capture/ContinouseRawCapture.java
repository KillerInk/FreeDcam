package freed.cam.apis.camera2.modules.capture;

import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.SystemClock;
import android.util.Size;
import androidx.annotation.RequiresApi;

import com.troop.halidelib.RawStack;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;
import freed.FreedApplication;
import freed.cam.ActivityFreeDcamMain;
import freed.cam.apis.basecamera.modules.ModuleInterface;
import freed.cam.ui.themesample.handler.UserMessageHandler;
import freed.dng.DngProfile;
import freed.image.EmptyTask;
import freed.image.ImageManager;
import freed.image.ImageSaveTask;
import freed.image.ImageTask;
import freed.utils.Log;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ContinouseRawCapture extends RawImageCapture {


    private StackRunner stackRunner;
    private final String TAG = ContinouseRawCapture.class.getSimpleName();
    private LinkedBlockingQueue<Image> imageBlockingQueue;
    private UserMessageHandler userMessageHandler;
    private ImageManager imageManager;
    public ContinouseRawCapture(Size size, int format, boolean setToPreview, ModuleInterface moduleInterface, String file_ending,int max_images) {
        super(size, format, setToPreview,moduleInterface,file_ending,max_images);
        imageBlockingQueue = new LinkedBlockingQueue<>(max_images);
        userMessageHandler = ActivityFreeDcamMain.userMessageHandler();
        imageManager = FreedApplication.imageManager();
    }

    @Override
    public void onImageAvailable(ImageReader reader) {

        synchronized (imageBlockingQueue)
        {
            if(imageBlockingQueue.remainingCapacity() == 1) {
                try {
                    Log.d(TAG, "wait for queue free");
                    imageBlockingQueue.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            Log.d(TAG, "add new img to queue");
            imageBlockingQueue.put(reader.acquireLatestImage());
        } catch (InterruptedException | NullPointerException e) {
            e.printStackTrace();
        }
       synchronized (this)
        {
            createTask();
            this.notifyAll();
        }
    }

    @Override
    protected void createTask() {
        task = new EmptyTask();
    }

    public void startStack(int burstcount, int upshift)
    {
        Log.d(TAG, "start stack");
        if (stackRunner != null)
            stackRunner.stop();
        stackRunner = null;
        stackRunner = new StackRunner(burstcount,upshift);
        new Thread(stackRunner).start();
    }

    public void startStackAvarage(int burstcount, int upshift)
    {
        Log.d(TAG, "start stack");
        if (stackRunner != null)
            stackRunner.stop();
        stackRunner = null;
        stackRunner = new StackAvarageRunner(burstcount,upshift);
        new Thread(stackRunner).start();
    }

    public void startStackALL(int bust, int upshift)
    {
        StackAllRunner stackAllRunner = new StackAllRunner(bust,upshift);
        new Thread(stackAllRunner).start();
    }

    public void stopStack()
    {
        stackRunner.stop();
    }

    private class StackRunner implements Runnable
    {
        protected final int burst;
        boolean run = false;
        long rawsize = 0;
        protected final int upshift;
        protected RawStack rawStack;
        int w;
        int h;
        public StackRunner(int burst, int upshift)
        {
            run = true;
            this.burst = burst;
            this.upshift = upshift;
        }

        public void stop(){ run = false; }

        @Override
        public void run() {
            Log.d(TAG, "start stack");
            rawStack = new RawStack();
            rawStack.setShift(upshift);
            Image image = null;
            int stackCoutn = 0;

                try {
                    image = imageBlockingQueue.take();
                } catch (InterruptedException e) {
                    Log.WriteEx(e);
                }
                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                w = image.getWidth();
                h = image.getHeight();
                rawStack.setFirstFrame(buffer, w, h);
                image.close();
            synchronized (imageBlockingQueue) {
                imageBlockingQueue.notifyAll();
            }
            stackCoutn++;
            while (run && stackCoutn < burst) {

                try {
                        image = imageBlockingQueue.take();
                        Log.d(TAG, "take result");
                } catch (InterruptedException e) {
                    Log.WriteEx(e);
                }

                    buffer = image.getPlanes()[0].getBuffer();
                    Log.d(TAG, "stackframes");
                    rawStack.stackNextFrame(buffer);
                    image.close();
                    buffer.clear();
                synchronized (imageBlockingQueue) {
                    imageBlockingQueue.notifyAll();
                }
                stackCoutn++;
                userMessageHandler.sendMSG("Stacked:" +stackCoutn,false);
                Log.d(TAG, "stackframes done " + stackCoutn);
            }
            String f = getFilepath() + "_hdr_frames" + burst + file_ending;
            byte[] bytes = new byte[w*h*16/8];
            rawStack.getOutputBuffer(bytes);
            ImageTask task;
            task = process_rawWithDngConverter(bytes, DngProfile.Pure16bitTo16bit, new File(f), result, characteristics, w,h,moduleInterface,customMatrix,orientation,externalSD,toneMapProfile);
            ImageSaveTask itask = (ImageSaveTask)task;
            if (upshift > 0) {
                int bl = itask.getDngProfile().getBlacklvl();
                itask.getDngProfile().setBlackLevel(bl << upshift);
                int wl = itask.getDngProfile().getWhitelvl();
                itask.getDngProfile().setWhiteLevel(wl << upshift);
            }
            if (itask.getDngProfile().getRawType() != DngProfile.QuadBayerTo16bit)
                itask.getDngProfile().setRawType(DngProfile.Pure16bitTo16bit);

            if (task != null) {
                imageManager.putImageSaveTask(task);
                Log.d(TAG, "Put task to Queue");
            }
            rawStack.clear();
            rawStack = null;
            ContinouseRawCapture.this.stackRunner = null;
        }
    }

    private class StackAvarageRunner extends StackRunner
    {
        public StackAvarageRunner(int burst, int upshift)
        {
            super(burst,upshift);
            run = true;
        }

        public void stop(){ run = false; }

        @Override
        public void run() {
            Log.d(TAG, "start stack");
            rawStack = new RawStack();
            rawStack.setShift(upshift);
            Image image = null;
            int stackCoutn = 0;

                try {
                    image = imageBlockingQueue.take();
                } catch (InterruptedException e) {
                    Log.WriteEx(e);
                }
                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                w = image.getWidth();
                h = image.getHeight();
                rawStack.setFirstFrame(buffer, w, h);
                image.close();
            synchronized (imageBlockingQueue) {
                imageBlockingQueue.notifyAll();
            }
            stackCoutn++;
            while (run && stackCoutn < burst) {

                try {
                        image = imageBlockingQueue.take();
                        Log.d(TAG, "take result");
                } catch (InterruptedException e) {
                    Log.WriteEx(e);
                }

                    buffer = image.getPlanes()[0].getBuffer();
                    Log.d(TAG, "stackframes");
                    rawStack.stackNextFrameAvarage(buffer);
                    image.close();
                    buffer.clear();
                synchronized (imageBlockingQueue) {
                    imageBlockingQueue.notifyAll();
                }
                stackCoutn++;
                userMessageHandler.sendMSG("Stacked:" +stackCoutn,false);
                Log.d(TAG, "stackframes done " + stackCoutn);
            }
            String f = getFilepath() + "_average_frames" + burst + file_ending;
            byte[] bytes = new byte[w*h*16/8];
            rawStack.getOutputBuffer(bytes);
            ImageTask task;
            task = process_rawWithDngConverter(bytes, DngProfile.Pure16bitTo16bit, new File(f), result, characteristics, w,h,moduleInterface,customMatrix,orientation,externalSD,toneMapProfile);
            ImageSaveTask itask = (ImageSaveTask)task;
            if (upshift > 0) {
                int bl = itask.getDngProfile().getBlacklvl();
                itask.getDngProfile().setBlackLevel(bl << upshift);
                int wl = itask.getDngProfile().getWhitelvl();
                itask.getDngProfile().setWhiteLevel(wl << upshift);
            }
            if (itask.getDngProfile().getRawType() != DngProfile.QuadBayerTo16bit)
                itask.getDngProfile().setRawType(DngProfile.Pure16bitTo16bit);

            if (task != null) {
                imageManager.putImageSaveTask(task);
                Log.d(TAG, "Put task to Queue");
            }
            rawStack.clear();
            rawStack = null;
            ContinouseRawCapture.this.stackRunner = null;
        }
    }



    private class StackAllRunner implements Runnable
    {

        private int burst;
        private int upshift;

        public StackAllRunner(int burst, int upshift)
        {
            this.upshift = upshift;
            this.burst = burst;
        }

        @Override
        public void run() {
            //List<ByteBuffer> bufferList = new ArrayList<>();
            //List<Image> images = new ArrayList<>();
            RawStack rawStack = new RawStack();
            rawStack.setShift(upshift);
            Log.d(TAG,"queue size :" +imageBlockingQueue.size() + "/" + (imageBlockingQueue.size()-imageBlockingQueue.remainingCapacity()));
            Image img = null;
            int count = 0;
            int w = 0;
            int h = 0;

            ByteBuffer buffer;
            if (imageBlockingQueue.peek() != null) {
                try {
                    img = imageBlockingQueue.take();
                    w = img.getWidth();
                    h = img.getHeight();
                    buffer = img.getPlanes()[0].getBuffer();
                    rawStack.setFirstFrame(buffer,img.getWidth(),img.getHeight(),burst);
                    img.close();
                    count++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            while (imageBlockingQueue.peek() != null)
            {
                try {
                    img = imageBlockingQueue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                buffer = img.getPlanes()[0].getBuffer();
                rawStack.setNextFrame(buffer);
                img.close();
                count++;
                //images.add(img);
                //bufferList.add(img.getPlanes()[0].getBuffer());
            }
            long starTime = SystemClock.uptimeMillis();
            byte[] bytes = new byte[w*h*16/8];
            rawStack.stackAll(bytes);
            long endTime = SystemClock.uptimeMillis();
            Log.d(TAG, "Stacked " + count +"/"+burst +" stacktime: " + (endTime -starTime) +"ms");

            String f = getFilepath() + "_hdr_frames" + burst + file_ending;
            ImageTask task;
            task = process_rawWithDngConverter(bytes, 6, new File(f), result, characteristics, w,h,moduleInterface,customMatrix,orientation,externalSD,toneMapProfile);

            ImageSaveTask itask = (ImageSaveTask)task;
            if (upshift > 0) {
                int bl = itask.getDngProfile().getBlacklvl();
                itask.getDngProfile().setBlackLevel(bl << upshift);
                int wl = itask.getDngProfile().getWhitelvl();
                itask.getDngProfile().setWhiteLevel(wl << upshift);
            }
            if (itask.getDngProfile().getRawType() != DngProfile.QuadBayerTo16bit)
                itask.getDngProfile().setRawType(DngProfile.Pure16bitTo16bit);

            if (task != null) {
                imageManager.putImageSaveTask(task);
                Log.d(TAG, "Put task to Queue");
            }
            //rawStack.clear();
        }
    }
}
