/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package freed.viewer.stack;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.Type;
import android.support.v4.provider.DocumentFile;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.ortiz.touch.TouchImageView;
import com.troop.freedcam.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import freed.ActivityAbstract;
import freed.cam.ui.handler.MediaScannerManager;
import freed.utils.FreeDPool;
import freed.utils.LocationHandler;
import freed.utils.Log;
import freed.utils.RenderScriptHandler;
import freed.utils.StorageFileHandler;
import freed.utils.StringUtils;
import freed.viewer.dngconvert.DngConvertingFragment;
import freed.viewer.holder.FileHolder;


/**
 * Created by troop on 06.07.2016.
 */
public class StackActivity extends ActivityAbstract
{
    private String[] filesToStack = null;
    private RenderScriptHandler renderScriptHandler;
    private int stackMode = 0;
    private TouchImageView imageView;
    private TextView stackcounter;
    private Button closeButton;

    public static String AVARAGE = "avarage";
    public static String AVARAGE1x2 = "avarage1x2";
    public static String AVARAGE1x3 = "avarage1x3";
    public static String AVARAGE3x3 = "avarage3x3";
    public static String LIGHTEN = "lighten";
    public static String LIGHTEN_V = "lighten_v";
    public static String MEDIAN = "median";
    public static String EXPOSURE = "exposure";

    private Allocation maxValues;
    private Allocation minValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stack_activity);
        Spinner stackvaluesButton = (Spinner)findViewById(R.id.freedviewer_stack_stackvalues_button);
        imageView = (TouchImageView)findViewById(R.id.freedviewer_stack_imageview);
        String[] items =  new String[] {AVARAGE, AVARAGE1x2, AVARAGE1x3, AVARAGE3x3, LIGHTEN, LIGHTEN_V, MEDIAN,EXPOSURE};
        ArrayAdapter<String> stackadapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, items);
        stackvaluesButton.setAdapter(stackadapter);
        filesToStack = getIntent().getStringArrayExtra(DngConvertingFragment.EXTRA_FILESTOCONVERT);
        renderScriptHandler = new RenderScriptHandler(getContext());
        storageHandler = new StorageFileHandler(this);

        stackvaluesButton.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                stackMode = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button buttonStartStack = (Button)findViewById(R.id.button_stackPics);
        buttonStartStack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processStack();
            }
        });

        closeButton = (Button)findViewById(R.id.button_stack_close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });
        stackcounter = (TextView)findViewById(R.id.textView_stack_count);
        updateCounter(0);
    }

    private void processStack()
    {
        stackcounter.setText("0/"+ filesToStack.length);
        closeButton.setVisibility(View.GONE);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filesToStack[0],options);
        final int mWidth = options.outWidth;
        final int mHeight = options.outHeight;
        Type.Builder tbIn2 = new Type.Builder(renderScriptHandler.GetRS(), Element.RGBA_8888(renderScriptHandler.GetRS()));
        tbIn2.setX(mWidth);
        tbIn2.setY(mHeight);
        renderScriptHandler.SetAllocsTypeBuilder(tbIn2,tbIn2, Allocation.USAGE_SCRIPT,Allocation.USAGE_SCRIPT);

        final Bitmap outputBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);

        renderScriptHandler.freedcamScript.set_Width(mWidth);
        renderScriptHandler.freedcamScript.set_Height(mHeight);
        renderScriptHandler.freedcamScript.set_yuvinput(false);
        renderScriptHandler.freedcamScript.set_gCurrentFrame(renderScriptHandler.GetIn());
        renderScriptHandler.freedcamScript.set_gLastFrame(renderScriptHandler.GetOut());
        if (stackMode ==  6)
        {
            minValues = Allocation.createTyped(renderScriptHandler.GetRS(), tbIn2.create(), Allocation.MipmapControl.MIPMAP_NONE,  Allocation.USAGE_SCRIPT);
            maxValues = Allocation.createTyped(renderScriptHandler.GetRS(), tbIn2.create(), Allocation.MipmapControl.MIPMAP_NONE,  Allocation.USAGE_SCRIPT);
            renderScriptHandler.freedcamScript.set_medianStackMAX(maxValues);
            renderScriptHandler.freedcamScript.set_medianStackMIN(minValues);

        }
        FreeDPool.Execute(new Runnable()
        {
            @Override
            public void run()
            {
                int count = 0;
                for (String f : filesToStack)
                {
                    updateCounter(count++);
                    BitmapFactory.decodeFile(f,options);
                    if(mWidth != options.outWidth || mHeight != options.outHeight)
                        return;
                    renderScriptHandler.GetIn().copyFrom(BitmapFactory.decodeFile(f));
                    switch (stackMode)
                    {
                        case 0: //AVARAGE
                            renderScriptHandler.freedcamScript.forEach_stackimage_avarage(renderScriptHandler.GetOut());
                            break;
                        case 1: //AVARAGE1x2
                            renderScriptHandler.freedcamScript.forEach_stackimage_avarage1x2(renderScriptHandler.GetOut());
                            break;
                        case 2: //AVARAGE1x3
                            renderScriptHandler.freedcamScript.forEach_stackimage_avarage1x3(renderScriptHandler.GetOut());
                            break;
                        case 3: // AVARAGE3x3
                            renderScriptHandler.freedcamScript.forEach_stackimage_avarage3x3(renderScriptHandler.GetOut());
                            break;
                        case 4: // LIGHTEN
                            renderScriptHandler.freedcamScript.forEach_stackimage_lighten(renderScriptHandler.GetOut());
                            break;
                        case 5: // LIGHTEN_V
                            renderScriptHandler.freedcamScript.forEach_stackimage_lightenV(renderScriptHandler.GetOut());
                            break;
                        case 6: //MEDIAN
                            renderScriptHandler.freedcamScript.forEach_stackimage_median(renderScriptHandler.GetOut());
                            break;
                        case 7:
                            renderScriptHandler.freedcamScript.forEach_stackimage_exposure(renderScriptHandler.GetOut());
                            break;
                    }
                    renderScriptHandler.GetOut().copyTo(outputBitmap);
                    setBitmapToImageView(outputBitmap);
                }
                if (stackMode ==  6)
                {
                    renderScriptHandler.freedcamScript.forEach_process_median(renderScriptHandler.GetOut());
                    renderScriptHandler.GetOut().copyTo(outputBitmap);
                    setBitmapToImageView(outputBitmap);
                    renderScriptHandler.freedcamScript.set_medianStackMAX(null);
                    renderScriptHandler.freedcamScript.set_medianStackMIN(null);
                    minValues.destroy();
                    maxValues.destroy();
                }
                File file = new File(filesToStack[0]);
                String parent = file.getParent();
                saveBitmapToFile(outputBitmap,new File(parent+"/" + getStorageHandler().getNewFileDatedName("_Stack.jpg")));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeButton.setVisibility(View.VISIBLE);
                    }
                });
            }
        }
        );

    }

    private void setBitmapToImageView(final Bitmap bitmap)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap,bitmap.getWidth()/4,bitmap.getHeight()/4,false));
            }
        });
    }

    private void saveBitmapToFile(Bitmap bitmap, File file)
    {
        OutputStream outStream = null;
        String intsd = StringUtils.GetInternalSDCARD();

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP || file.getAbsolutePath().contains(intsd) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            try {
                outStream= new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                outStream.close();
            } catch (IOException e) {
                Log.WriteEx(e);
            }
        }
        else
        {
            DocumentFile df =  getFreeDcamDocumentFolder();

            DocumentFile wr = df.createFile("image/*", file.getName());
            try {
                outStream = getContentResolver().openOutputStream(wr.getUri());
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                outStream.close();
            } catch (IOException e) {
                Log.WriteEx(e);
            }
        }
        MediaScannerManager.ScanMedia(getContext(), file);
    }

    @Override
    public void WorkHasFinished(FileHolder fileHolder) {

    }

    @Override
    public void WorkHasFinished(FileHolder[] fileHolder) {

    }

    @Override
    public LocationHandler getLocationHandler() {
        return null;
    }

    private void updateCounter(final int count)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                stackcounter.setText(count+"/"+(filesToStack.length-1));
            }
        });

    }
}
