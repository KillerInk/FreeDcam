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

package freed.cam.apis.camera2.modules;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.IntervalModule;
import freed.cam.apis.basecamera.modules.ModuleAbstract;

/**
 * Created by troop on 26.02.2016.
 */
public class IntervalApi2 extends IntervalModule implements I_PreviewWrapper
{
    private final PictureModuleApi2 picModule;
    public IntervalApi2(ModuleAbstract picModule, CameraWrapperInterface cameraUiWrapper) {
        super(picModule,cameraUiWrapper);
        this.picModule = (PictureModuleApi2)picModule;
    }


    @Override
    public void startPreview() {
        picModule.startPreview();
    }

    @Override
    public void stopPreview() {
        picModule.stopPreview();
    }

    @Override
    public void InitModule()
    {
        picModule.InitModule();
    }

    @Override
    public void DestroyModule() {
        picModule.DestroyModule();
    }
}
