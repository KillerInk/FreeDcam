package troop.com.imageconverter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by troop on 13.08.2015.
 */
public class SurfaceNativeDrawActivity extends Activity implements SurfaceHolder.Callback
{
    static
    {
        System.loadLibrary("surfacenativedraw");
    }

    private static native void drawFromNative(int data[], Surface surface,int w,int h);

    SurfaceView surfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        setContentView(R.layout.surface_native_draw_activity);
        surfaceView = (SurfaceView)findViewById(R.id.surfaceView_draw);
        surfaceView.getHolder().addCallback(this);
        surfaceView.getHolder().setFormat(PixelFormat.RGBA_8888);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        BitmapFactory.Options o = new BitmapFactory.Options();
        //o.inSampleSize = 2;
        final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test, o);

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int[] pixels = new int[w* h];
        bitmap.getPixels(pixels,0,w,0,0, w, h);
        drawFromNative(pixels, surfaceView.getHolder().getSurface(), bitmap.getWidth(),bitmap.getHeight());

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
