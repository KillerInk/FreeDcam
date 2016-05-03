package troop.com.imageconverter;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by troop on 22.04.2016.
 */
public class TestActivity extends Activity
{
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testactivity);
        imageView = (ImageView)findViewById(R.id.test_activity_imageview);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ImageProcessorWrapper imageProcessorWrapper = new ImageProcessorWrapper();
        File f = new File("/sdcard/test.jpg");
        if (!f.exists())
            return;
        imageProcessorWrapper.loadFile(f.getAbsolutePath());
        imageView.setImageBitmap(imageProcessorWrapper.GetNativeBitmap());
        imageProcessorWrapper.ReleaseNative();
    }
}
