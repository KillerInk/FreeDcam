package com.troop.freecamv2.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;

import com.troop.freecam.R;

import com.troop.freecam.manager.MediaScannerManager;
import com.troop.freecamv2.camera.CameraUiWrapper;
import com.troop.freecamv2.camera.modules.I_WorkEvent;
import com.troop.freecamv2.ui.TextureView.ExtendedSurfaceView;
import com.troop.freecamv2.ui.menu.MenuHandler;
import com.troop.freecamv2.ui.switches.CameraSwitchHandler;
import com.troop.freecamv2.ui.switches.FlashSwitchHandler;
import com.troop.freecamv2.ui.switches.ModuleSwitchHandler;

import java.io.File;

/**
 * Created by troop on 18.08.2014.
 */
public class MainActivity_v2 extends MenuVisibilityActivity
{
    ExtendedSurfaceView cameraPreview;
    CameraUiWrapper cameraUiWrapper;
    AppSettingsManager appSettingsManager;
    MenuHandler menuHandler;
    ImageView shutterButton;
    CameraSwitchHandler cameraSwitchHandler;
    ModuleSwitchHandler moduleSwitchHandler;
    FlashSwitchHandler flashSwitchHandler;
    Activity activity;
    ImageView thumbView;
    File lastFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appSettingsManager = new AppSettingsManager(PreferenceManager.getDefaultSharedPreferences(this));
        cameraPreview = (ExtendedSurfaceView)findViewById(R.id.CameraPreview);
        cameraUiWrapper = new CameraUiWrapper(cameraPreview, appSettingsManager,null);
        cameraUiWrapper.moduleHandler.moduleEventHandler.AddWorkFinishedListner(cameraModuleHasWorkFinished);
        menuHandler = new MenuHandler(this, cameraUiWrapper, appSettingsManager);

        shutterButton = (ImageView)findViewById(R.id.shutter_imageview);
        shutterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraUiWrapper.DoWork();
            }
        });

        cameraSwitchHandler = new CameraSwitchHandler(this, cameraUiWrapper, appSettingsManager, cameraPreview);
        moduleSwitchHandler = new ModuleSwitchHandler(this, cameraUiWrapper, appSettingsManager);
        flashSwitchHandler = new FlashSwitchHandler(this, cameraUiWrapper, appSettingsManager);
        activity = this;
        thumbView = (ImageView)findViewById(R.id.imageView_Thumbnail);
        thumbView.setOnClickListener(onThumbViewClick);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private I_WorkEvent cameraModuleHasWorkFinished = new I_WorkEvent() {
        @Override
        public String WorkHasFinished(File filePath)
        {
            lastFile = filePath;
            MediaScannerManager.ScanMedia(activity, filePath);
            thumbView.setImageBitmap(loadThumbViewImage(filePath));
            return null;
        }
    };

    private View.OnClickListener onThumbViewClick = new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            if (lastFile != null)
            {
                Uri uri = Uri.fromFile(lastFile);
                Intent i=new Intent(Intent.ACTION_VIEW);
                if (lastFile.getAbsolutePath().endsWith("mp4"))
                    i.setDataAndType(uri, "video/*");
                else
                    i.setDataAndType(uri, "image/*");
                startActivity(i);
            }
        }
    };

    private Bitmap loadThumbViewImage(File file)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmaporg = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        options.inSampleSize = calculateInSampleSize(options, thumbView.getWidth(), thumbView.getHeight());
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
    {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

}
