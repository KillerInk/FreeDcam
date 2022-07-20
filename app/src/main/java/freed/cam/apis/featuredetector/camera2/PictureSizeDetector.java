package freed.cam.apis.featuredetector.camera2;

import android.graphics.ImageFormat;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.util.Size;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import camera2_hidden_keys.xiaomi.CameraCharacteristicsXiaomi;
import freed.cam.apis.featuredetector.Camera2Util;
import freed.settings.Frameworks;
import freed.settings.SettingKeys;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class PictureSizeDetector extends BaseParameter2Detector {

    @Override
    protected void findAndFillSettings(CameraCharacteristics cameraCharacteristics) {
        detectPictureSizes(cameraCharacteristics);
    }


    private void detectPictureSizes(CameraCharacteristics characteristics)
    {
        List<Size> outputSizes = new ArrayList<>();
        StreamConfigurationMap smap =  characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        outputSizes.addAll(Arrays.asList(smap.getOutputSizes(ImageFormat.JPEG)));


        Size[] highsize = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            highsize = smap.getHighResolutionOutputSizes(ImageFormat.JPEG);
            if (highsize != null)
                outputSizes.addAll(Arrays.asList(highsize));
        }
        if (settingsManager.getFrameWork() == Frameworks.Xiaomi)
        {
            int[] highres = characteristics.get(CameraCharacteristicsXiaomi.availableSuperResolutionStreamConfigurations);
            if(highres != null)
                outputSizes.addAll(Arrays.asList(Camera2Util.getOutputSizeForImageFormat(highres,ImageFormat.YUV_420_888)));
        }
        String[] ar = new String[outputSizes.size()];
        Size[] sizes = new Size[outputSizes.size()];
        outputSizes.toArray(sizes);

        java.util.Arrays.sort(sizes,new SizeComparer());

        int i = 0;
        for (Size s : sizes)
        {
            ar[i++] = s.getWidth()+"x"+s.getHeight();
        }



        settingsManager.get(SettingKeys.PICTURE_SIZE).setIsSupported(true);
        settingsManager.get(SettingKeys.PICTURE_SIZE).set(ar[0]);
        settingsManager.get(SettingKeys.PICTURE_SIZE).setValues(ar);
    }

    public static class SizeComparer implements Comparator<Size> {

        @Override
        public int compare(Size o1, Size o2) {
            return (o2.getHeight() * o2.getWidth()) - (o1.getHeight()* o1.getWidth());
        }
    }
}
