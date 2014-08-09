package com.troop.freecam.camera;

import android.annotation.SuppressLint;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.defcomk.jni.libraw.RawUtils;
import com.troop.freecam.interfaces.IShutterSpeedCallback;
import com.troop.freecam.interfaces.SavePictureCallback;
import com.troop.freecam.manager.SettingsManager;
import com.troop.freecam.manager.SoundPlayer;
import com.troop.freecam.manager.parameters.ParametersManager;
import com.troop.freecam.surfaces.CamPreview;
import com.troop.freecam.utils.DeviceUtils;
import com.troop.freecam.utils.SavePicture;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by troop on 18.10.13.
 */
public class PictureCam extends BaseCamera implements Camera.ShutterCallback, Camera.PictureCallback, SavePictureCallback
{


    //protected MediaScannerManager scanManager;
	
    public SoundPlayer soundPlayer;
    protected CamPreview context;
    protected SavePicture savePicture;
    public boolean crop = false;
    public ParametersManager parametersManager;
    public SavePictureCallback onsavePicture;
    public boolean IsWorking = false;
    //CameraManager cameraManager = new CameraManager(context, null, Settings);
    protected IShutterSpeedCallback shutterSpeedCallback;
    private byte[] rawByteStream;
   // ByteBuffer imx_135 = ByteBuffer.allocate(16424960);
    public void setOnShutterSpeed(IShutterSpeedCallback shutterSpeedCallback){this.shutterSpeedCallback = shutterSpeedCallback; }

	private String sdcardFilepath = Environment.getExternalStorageDirectory().getPath() + "/";
    private String outputDirectoryPath = sdcardFilepath + "raw_output/";
    private String tiffFilePath = outputDirectoryPath + "raw.tiff";
    private String xtiffFilePath = outputDirectoryPath + "raxw.tiff";


    final String TAG = "freecam.PictureCam";
    private void writeDebug(String s)
    {
        Log.d(TAG, s);
    }

    byte[] rawbuffer;




    public PictureCam(CamPreview context,SettingsManager preferences)
    {
        super(preferences);
        this.context = context;
        //this.scanManager = new MediaScannerManager(context.getContext());
        soundPlayer = new SoundPlayer(context.getContext());
        savePicture = new SavePicture(context.getContext(), preferences);
        savePicture.onSavePicture = this;
    }

    //private static final int CAMERA_MSG_RAW_IMAGE = 0x080;
    //private native final void _addCallbackBuffer(
            //byte[] callbackBuffer, int msgType);

    public void TakePicture(boolean crop)
    {
        IsWorking = true;
        this.crop = crop;
        //Camera.Size size = mCamera.getParameters().getPictureSize();
        //rawbuffer = new byte[size.width * size.height * 8];
        Log.d(TAG, "Start Taking Picture");
        try
        {
            soundPlayer.PlayShutter();
            mCamera.takePicture(null,null,this);
            Log.d(TAG, "Picture Taking is Started");

        }
        catch (Exception ex)
        {
            writeDebug("Take Picture Failed");
            ex.printStackTrace();
        }
    }
    

    
    @SuppressLint("SimpleDateFormat")
	public static File getOutputMediaFile(int i)
    {
        Log.d("Generating", "Filename");
       File file = new File(Environment.getExternalStorageDirectory() + "/DCIM/FreeCam/");
        Date date = new Date();
        String s = (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(date);
        String s1 = (new StringBuilder(String.valueOf(file.getPath()))).append(File.separator).append("IMG_").append(s).toString();

        if (i == 3)
            return new File((new StringBuilder(String.valueOf(s1))).append(".tiff").toString());
        if (i == 2)
        	return new File((new StringBuilder(String.valueOf(s1))).append(".jpg").toString());
        if (i == 1)
        	return new File((new StringBuilder(String.valueOf(s1))).append(".jps").toString());
        if (i == 4)
        	return new File((new StringBuilder(String.valueOf(s1))).append(".raw").toString());
        else
        	return null;
    }



    /** Handles data for raw picture */
    public Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            writeDebug("onPictureTaken - raw");
            //if (data != null)
                //saveRawData(data);
        }
    };

    public byte[] RawStream()
    {
        return rawByteStream;
    }
    
    private class RawDecodeX extends AsyncTask<Void, Void, Void>
    {

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			int black = 0;
    		if(DeviceUtils.isZTEADV()||DeviceUtils.isLGADV())
    			black = 43;   
			File tiff = getOutputMediaFile(3);
			
			RawUtils.unpackRawByte(String.valueOf(tiff), rawByteStream,black,2.0f,3.83f,0.10f,100.00f);
			
			
			return null;
		}
    	
    }


    @Override
    public void onPictureTaken(byte[] data, Camera camera)
    {
    	//File tiff = getOutputMediaFile(3);
        File file;
        boolean isRaw = false;

        String tmp = Settings.pictureFormat.Get();
        if (tmp.equals("jps"))
    	    file = getOutputMediaFile(1);
        else if (tmp.equals("raw")) {
            file = getOutputMediaFile(4);
            isRaw = true;
        }
        else
            file = getOutputMediaFile(2);

        writeDebug("OnPictureTaken callback recieved");
        boolean is3d = false;
        
        if (Settings.Cameras.GetCamera().equals(SettingsManager.Preferences.MODE_3D))
        {
            is3d = true;
        }
        writeDebug("start saving to sd");
        try {
        	
        	
        	if (!isRaw)
        	{
        		savePicture.SaveToSD(data, crop, mCamera.getParameters().getPictureSize(), is3d, file);
                writeDebug("save successed");
                
        	}
        	else
        	{
        		int black = 0;
        		if(DeviceUtils.isZTEADV()||DeviceUtils.isLGADV())
        			black = 43;
            	long aa = System.currentTimeMillis();
            	
            	RawDecodeX RDCD = new RawDecodeX();
            	Void params = null;
				RDCD.execute(params);
        		savePicture.SaveToSD(data, crop, mCamera.getParameters().getPictureSize(), is3d, file);
                
                long ab = System.currentTimeMillis();            
                long contime = ab - aa;
                
                Log.d(" Raw Save Time",String.valueOf(contime));
        	}
            

        }
        catch (Exception ex)
        {
            Log.e(TAG, "saving to sd failed");
            ex.printStackTrace();
        }

        try {
            writeDebug("try to start preview");

            mCamera.startPreview();
            if (DeviceUtils.isEvo3d())
                parametersManager.LensShade.set(Settings.LensShade.get());
        }
        catch (Exception ex)
        {
            Log.e(TAG, "preview start failed");
            ex.printStackTrace();
        }

        IsWorking = false;
        data = null;

    }

    @Override
    public void onShutter()
    {
        soundPlayer.PlayShutter();
    }

    @Override
    public void onPictureSaved(File file)
    {
       /* ExifManager m = new ExifManager();
        try {
            m.LoadExifFrom(file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        onShutterSpeed(m.getExposureTime());*/
    }

    private void saveRawData(byte[] data)
    {
        File file = SavePicture.getFilePath("raw", Environment.getExternalStorageDirectory());
        FileOutputStream outStream = null;
        try {
        outStream = new FileOutputStream(file);

        outStream.write(data);
        outStream.flush();
        outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void onShutterSpeed(String speed)
    {
        if (shutterSpeedCallback != null)
            shutterSpeedCallback.ShutterSpeedRecieved(speed);
    }
}

