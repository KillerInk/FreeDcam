package troop.com.imageconverter;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

/**
 * Created by troop on 22.04.2016.
 */
public class TestActivity extends Activity
{
    ImageView imageView;

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
        imageProcessorWrapper.loadFile(ImageProcessorWrapper.ARGB, "/storage/emulated/0/test.jpg");
        imageView.setImageBitmap(imageProcessorWrapper.GetNativeBitmap());
        imageProcessorWrapper.ReleaseNative();
    }
}
