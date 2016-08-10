/*
 *
 *     Copyright (C) 2015 George Kiarie
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

package freed.cam.apis.camera1.modules;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.Type.Builder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract.CaptureStates;
import freed.cam.apis.camera1.parameters.modes.StackModeParameter;
import freed.utils.FreeDPool;
import freed.utils.Logger;
import freed.utils.RenderScriptHandler;
import freed.utils.ScriptField_MinMaxPixel;

/**
 * Created by GeorgeKiarie on 13/04/2016.
 */
public class StackingModule extends PictureModule {
    final String TAG = StackingModule.class.getSimpleName();
    private boolean KeepStacking;
    private int FrameCount;
    private String SessionFolder="";
    private List<File> capturedPics;

    public StackingModule(CameraWrapperInterface cameraUiWrapper) {
        super(cameraUiWrapper);
        name = KEYS.MODULE_STACKING;
    }

    @Override
    public String ModuleName() {
        return name;
    }

    @Override
    public boolean DoWork() {
        Logger.d(TAG, "isWorking: " + isWorking + " KeepStacking: " + KeepStacking);
        if (!isWorking && !KeepStacking)
        {
            FrameCount = 0;
            SessionFolder = cameraUiWrapper.getActivityInterface().getStorageHandler().getNewSessionFolderPath(appSettingsManager.GetWriteExternal());
            Logger.d(TAG,"Start Stacking");
            KeepStacking = true;
            capturedPics = new ArrayList<>();
            initRsStuff();
            changeCaptureState(CaptureStates.continouse_capture_start);
            cameraUiWrapper.GetParameterHandler().SetPictureOrientation(cameraUiWrapper.getActivityInterface().getOrientation());
            String picFormat = cameraUiWrapper.GetParameterHandler().PictureFormat.GetValue();
            if (!picFormat.equals(KEYS.JPEG))
                cameraUiWrapper.GetParameterHandler().PictureFormat.SetValue(KEYS.JPEG,true);
            isWorking =true;
            cameraHolder.TakePicture(this);
            return true;
        }
        else if (KeepStacking)
        {
            Logger.d(TAG, "Stop Stacking");
            KeepStacking = false;
            if (isWorking)
                changeCaptureState(CaptureStates.cont_capture_stop_while_working);
            else
                changeCaptureState(CaptureStates.cont_capture_stop_while_notworking);
            return false;
        }
        return false;

    }

    @Override
    public void onPictureTaken(final byte[] data, Camera camera) {
        Logger.d(TAG, "Take Picture Callback");
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                File f = new File(cameraUiWrapper.getActivityInterface().getStorageHandler().getNewFilePath(appSettingsManager.GetWriteExternal(), ".jpg"));
                processData(data, f);
            }
        });
    }

    @Override
    public String ShortName() {
        return "Stack";
    }

    @Override
    public String LongName() {
        return "Stacking";
    }

    @Override
    public boolean IsWorking() {
        return isWorking;
    }

    @Override
    public void InitModule()
    {
        super.InitModule();
    }

    @Override
    public void DestroyModule(){

    }

    private void initRsStuff()
    {
        RenderScriptHandler rsh = cameraUiWrapper.getRenderScriptHandler();
        int mWidth = Integer.parseInt(cameraUiWrapper.GetParameterHandler().PictureSize.GetValue().split("x")[0]);
        int mHeight = Integer.parseInt(cameraUiWrapper.GetParameterHandler().PictureSize.GetValue().split("x")[1]);
        Builder tbIn2 = new Builder(rsh.GetRS(), Element.RGBA_8888(rsh.GetRS()));
        tbIn2.setX(mWidth);
        tbIn2.setY(mHeight);
        rsh.SetAllocsTypeBuilder(tbIn2,tbIn2,Allocation.USAGE_SCRIPT,Allocation.USAGE_SCRIPT);

        rsh.imagestack.set_Width(mWidth);
        rsh.imagestack.set_Height(mHeight);
        rsh.imagestack.set_yuvinput(false);
        if (cameraUiWrapper.GetParameterHandler().imageStackMode.GetValue().equals(StackModeParameter.MEDIAN))
        {
            ScriptField_MinMaxPixel medianMinMax = new ScriptField_MinMaxPixel(rsh.GetRS(), mWidth * mHeight);
            rsh.imagestack.bind_medianMinMaxPixel(medianMinMax);
        }

        rsh.imagestack.set_gCurrentFrame(rsh.GetIn());
        rsh.imagestack.set_gLastFrame(rsh.GetOut());
    }


    private void processData(byte[] data, File file)
    {
        cameraHolder.StartPreview();
        Logger.d(TAG, "start preview");
        Logger.d(TAG,"The Data Is " + data.length + " bytes Long" + " and the path is " + file.getAbsolutePath());
        //create file to save
        File f = new File(SessionFolder +cameraUiWrapper.getActivityInterface().getStorageHandler().getNewFileDatedName(".jpg"));
        //save file
        saveBytesToFile(data,f);
        //add file for later stack
        capturedPics.add(f);
        //Add file to media storage that its visible by mtp
        scanAndFinishFile(f);

        isWorking = false;
        //notice ui/shutterbutton about the current workstate
        changeCaptureState(CaptureStates.continouse_capture_work_stop);
        cameraHolder.SendUIMessage("Captured Picture: " + FrameCount++);
        //Take next picture for later stacking aslong keepstacking is true
        if (KeepStacking)
        {
            changeCaptureState(CaptureStates.continouse_capture_work_start);
            Logger.d(TAG, "keepstacking take next pic");
            isWorking = true;
            cameraHolder.TakePicture(this);
        }
        else //keepstacking is false, lets start mergin all pics
        {
            Logger.d(TAG, "End of Stacking create bitmap and compress");
            FrameCount = 0;
            isWorking = true;
            changeCaptureState(CaptureStates.continouse_capture_work_start);

            for (File s : capturedPics)
            {
                cameraHolder.SendUIMessage("Stacked: " + FrameCount++ + "/"+ capturedPics.size());
                stackImage(s);
            }
            changeCaptureState(CaptureStates.continouse_capture_work_stop);
            int mWidth = Integer.parseInt(cameraUiWrapper.GetParameterHandler().PictureSize.GetValue().split("x")[0]);
            int mHeight = Integer.parseInt(cameraUiWrapper.GetParameterHandler().PictureSize.GetValue().split("x")[1]);
            Bitmap outputBitmap = Bitmap.createBitmap(mWidth, mHeight, Config.ARGB_8888);
            cameraUiWrapper.getRenderScriptHandler().GetOut().copyTo(outputBitmap);
            File stackedImg = new File(SessionFolder + cameraUiWrapper.getActivityInterface().getStorageHandler().getNewFileDatedName("_Stack.jpg"));
            SaveBitmapToFile(outputBitmap,stackedImg);
            isWorking = false;
            changeCaptureState(CaptureStates.continouse_capture_stop);
            scanAndFinishFile(stackedImg);
        }
    }

    private void stackImage(File file)
    {
        RenderScriptHandler rsh = cameraUiWrapper.getRenderScriptHandler();
        rsh.GetIn().copyFrom(BitmapFactory.decodeFile(file.getAbsolutePath()));
        Logger.d(TAG, "Copied data to inputalloc");

        Logger.d(TAG, "setted inputalloc to RS");
        if (cameraUiWrapper.GetParameterHandler().imageStackMode.GetValue().equals(StackModeParameter.AVARAGE))
            rsh.imagestack.forEach_stackimage_avarage(rsh.GetOut());
        else if (cameraUiWrapper.GetParameterHandler().imageStackMode.GetValue().equals(StackModeParameter.AVARAGE1x2))
            rsh.imagestack.forEach_stackimage_avarage1x2(rsh.GetOut());
        else if (cameraUiWrapper.GetParameterHandler().imageStackMode.GetValue().equals(StackModeParameter.AVARAGE1x3))
            rsh.imagestack.forEach_stackimage_avarage1x3(rsh.GetOut());
        else if (cameraUiWrapper.GetParameterHandler().imageStackMode.GetValue().equals(StackModeParameter.AVARAGE3x3))
            rsh.imagestack.forEach_stackimage_avarage3x3(rsh.GetOut());
        else if(cameraUiWrapper.GetParameterHandler().imageStackMode.GetValue().equals(StackModeParameter.LIGHTEN))
            rsh.imagestack.forEach_stackimage_lighten(rsh.GetOut());
        else if(cameraUiWrapper.GetParameterHandler().imageStackMode.GetValue().equals(StackModeParameter.LIGHTEN_V))
            rsh.imagestack.forEach_stackimage_lightenV(rsh.GetOut());
        else if (cameraUiWrapper.GetParameterHandler().imageStackMode.GetValue().equals(StackModeParameter.MEDIAN))
        {
            rsh.imagestack.forEach_stackimage_median(rsh.GetOut());
        }
        else if (cameraUiWrapper.GetParameterHandler().imageStackMode.GetValue().equals(StackModeParameter.EXPOSURE))
        {
            rsh.imagestack.forEach_stackimage_exposure(rsh.GetOut());
        }
    }


}
