package freed.cam.apis.camera2.modules.capture;

import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureResult;
import android.location.Location;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.util.Log;
import android.util.Size;

import androidx.annotation.RequiresApi;

import freed.ActivityInterface;
import freed.cam.apis.basecamera.modules.ModuleInterface;
import freed.cam.apis.camera2.modules.helper.CaptureType;
import freed.dng.CustomMatrix;
import freed.dng.ToneMapProfile;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public abstract class StillImageCapture extends AbstractImageCapture {

    private final String TAG = StillImageCapture.class.getSimpleName();

    protected CustomMatrix customMatrix;
    protected ToneMapProfile toneMapProfile;
    protected int orientation = 0;
    protected Location location;
    protected boolean externalSD =false;
    protected CameraCharacteristics characteristics;
    protected CaptureType captureType;

    public String getFilepath() {
        return filepath;
    }

    protected String filepath;

    protected boolean forceRawToDng = false;
    protected boolean support12bitRaw = false;

    protected ActivityInterface activityInterface;
    protected ModuleInterface moduleInterface;
    protected final String file_ending;


    public StillImageCapture(Size size, int format, boolean setToPreview,ActivityInterface activityInterface,ModuleInterface moduleInterface, String file_ending, int max_images) {
        super(size, format, setToPreview,max_images);
        this.activityInterface = activityInterface;
        this.moduleInterface = moduleInterface;
        this.file_ending = file_ending;
    }

    public void setCustomMatrix(CustomMatrix custmMat)
    {
        this.customMatrix = custmMat;
    }

    public void setToneMapProfile(ToneMapProfile toneMapProfile)
    {
        this.toneMapProfile = toneMapProfile;
    }

    public void setOrientation(int or)
    {
        this.orientation = or;
    }

    public void setFilePath(String path, boolean extSD)
    {
        this.filepath = path;
        this.externalSD = extSD;
    }

    public void setLocation(Location location)
    {
        this.location = location;
    }

    public void setForceRawToDng(boolean force)
    {
        this.forceRawToDng = force;
    }

    public void setSupport12bitRaw(boolean support12bitRaw)
    {
        this.support12bitRaw =support12bitRaw;
    }

    public void setCharacteristics(CameraCharacteristics characteristics)
    {
        this.characteristics = characteristics;
    }

    public void setCaptureType(CaptureType captureType)
    {
        this.captureType = captureType;
    }
}
