package troop.com.imageconverter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.Type;
import android.widget.ImageView;

import com.troop.filelogger.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by troop on 22.08.2015.
 */
public class RenderScriptArgbActivity extends Activity
{
    final String TAG = RenderScriptArgbActivity.class.getSimpleName();
    RenderScript mRS;
    private Allocation mInputAllocation;
    private Allocation mOutputAllocation;
    ImageView imageView;
    Bitmap orginal;
    Bitmap drawBitmap;
    int w,h;
    ScriptC_focuspeak_argb focuspeak_argb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.renderscript_argb);
        this.imageView = (ImageView)findViewById(R.id.imageView_render);
        mRS = RenderScript.create(this.getApplicationContext());
        orginal = BitmapFactory.decodeResource(getResources(), R.drawable.orginalframe);
        w = orginal.getWidth();
        h = orginal.getHeight();
        initRenderScript();
        mInputAllocation.copyFrom(orginal);
        focuspeak_argb.set_gCurrentFrame(mInputAllocation);
        focuspeak_argb.forEach_peak(mOutputAllocation);
        mOutputAllocation.copyTo(drawBitmap);
        imageView.setImageBitmap(drawBitmap);
        saveBitmap(drawBitmap, "testargb.jpg");

    }

    private void initRenderScript()
    {
        drawBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Type.Builder tbIn = new Type.Builder(mRS, Element.RGBA_8888(mRS));
        tbIn.setX(w);
        tbIn.setY(h);

        Type.Builder tbOut = new Type.Builder(mRS, Element.RGBA_8888(mRS));
        tbOut.setX(w);
        tbOut.setY(h);

        mInputAllocation = Allocation.createTyped(mRS, tbIn.create(), Allocation.MipmapControl.MIPMAP_NONE,  Allocation.USAGE_SCRIPT & Allocation.USAGE_SHARED);
        mOutputAllocation = Allocation.createTyped(mRS, tbOut.create(), Allocation.MipmapControl.MIPMAP_NONE,  Allocation.USAGE_SCRIPT & Allocation.USAGE_SHARED);
        focuspeak_argb = new ScriptC_focuspeak_argb(mRS);
        //mScriptFocusPeak = new ScriptC_focus_peak(mRS);
    }

    private void saveBitmap(Bitmap bimap, String filename)
    {
        String path = Environment.getExternalStorageDirectory().toString();
        OutputStream fOut = null;
        File file = new File(path, filename); // the File to save to
        try {
            fOut = new FileOutputStream(file);
            bimap.compress(Bitmap.CompressFormat.JPEG, 85, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
            fOut.flush();
            fOut.close(); // do not forget to close the stream
        } catch (FileNotFoundException e) {
            Logger.e(TAG, e.getMessage());
        } catch (IOException e) {
            Logger.e(TAG, e.getMessage());
        }
        finally {
            try {
                fOut.close();
            } catch (IOException e) {
                Logger.e(TAG, e.getMessage());
            }

        }
    }
}
