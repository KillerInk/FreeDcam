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

package freed.cam.apis.camera2;

import android.os.Build;

import androidx.annotation.RequiresApi;

import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract;
import freed.cam.apis.camera2.modules.AeBracketApi2;
import freed.cam.apis.camera2.modules.AfBracketApi2;
import freed.cam.apis.camera2.modules.HeifRec;
import freed.cam.apis.camera2.modules.HevcRec;
import freed.cam.apis.camera2.modules.HuaweiAeBracketApi2;
import freed.cam.apis.camera2.modules.IntervalApi2;
import freed.cam.apis.camera2.modules.PictureModuleApi2;
import freed.cam.apis.camera2.modules.VideoModuleApi2;
import freed.settings.Frameworks;
import freed.settings.SettingKeys;

/**
 * Created by troop on 12.12.2014.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ModuleHandlerApi2 extends ModuleHandlerAbstract<Camera2>
{
    public  ModuleHandlerApi2 (Camera2 cameraUiWrapper)
    {
        super(cameraUiWrapper);
        initModules();
    }

    public void initModules()
    {
        PictureModuleApi2 pictureModuleApi2 = new PictureModuleApi2(cameraUiWrapper,mBackgroundHandler,mainHandler);
        moduleList.put(pictureModuleApi2.ModuleName(), pictureModuleApi2);
        IntervalApi2 intervalModule = new IntervalApi2(cameraUiWrapper,mBackgroundHandler,mainHandler);
        moduleList.put(intervalModule.ModuleName(), intervalModule);
        VideoModuleApi2 videoModuleApi2 = new VideoModuleApi2(cameraUiWrapper,mBackgroundHandler,mainHandler);
        moduleList.put(videoModuleApi2.ModuleName(), videoModuleApi2);
        if (settingsManager.getFrameWork() != Frameworks.HuaweiCamera2Ex && settingsManager.get(SettingKeys.M_ExposureTime).isSupported()){
            AeBracketApi2 aeBracketApi2 = new AeBracketApi2(cameraUiWrapper,mBackgroundHandler,mainHandler);
            moduleList.put(aeBracketApi2.ModuleName(),aeBracketApi2);
        }
        else if (settingsManager.getFrameWork() == Frameworks.HuaweiCamera2Ex)
        {
            AeBracketApi2 aeBracketApi2 = new HuaweiAeBracketApi2(cameraUiWrapper,mBackgroundHandler,mainHandler);
            moduleList.put(aeBracketApi2.ModuleName(),aeBracketApi2);
        }
        if (settingsManager.get(SettingKeys.M_Focus).isSupported() && settingsManager.get(SettingKeys.M_Focus).getValues().length > 0) {
            AfBracketApi2 afBracketApi2 = new AfBracketApi2(cameraUiWrapper, mBackgroundHandler, mainHandler);
            moduleList.put(afBracketApi2.ModuleName(), afBracketApi2);
        }
        HeifRec heifRec = new HeifRec(cameraUiWrapper,mBackgroundHandler,mainHandler);
        moduleList.put(heifRec.ModuleName(),heifRec);

        HevcRec hevcRec = new HevcRec(cameraUiWrapper,mBackgroundHandler,mainHandler);
        moduleList.put(hevcRec.ModuleName(),hevcRec);

    }

    @Override
    public String getCurrentModuleName() {
        return super.getCurrentModuleName();
    }
}
