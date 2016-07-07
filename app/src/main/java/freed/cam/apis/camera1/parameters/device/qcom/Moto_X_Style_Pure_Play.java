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

package freed.cam.apis.camera1.parameters.device.qcom;

import android.hardware.Camera.Parameters;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.manual.AbstractManualParameter;
import freed.cam.apis.basecamera.parameters.modes.MatrixChooserParameter;
import freed.cam.apis.camera1.parameters.manual.BaseManualParameter;
import freed.cam.apis.camera1.parameters.manual.qcom.SkintoneManualPrameter;
import freed.dng.DngProfile;

/**
 * Created by troop on 01.06.2016.
 */
public class Moto_X_Style_Pure_Play extends Alcatel_Idol3 {


    public Moto_X_Style_Pure_Play(Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(parameters, cameraUiWrapper);
    }

    @Override
    public DngProfile getDngProfile(int filesize)
    {
        switch (filesize)
        {
            case 42923008://Moto_X_Style_Pure_Play
                return new DngProfile(64, 5344, 4016, DngProfile.Plain, DngProfile.RGGB, 0, matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.IMX230));
            case 26935296:
                return new DngProfile(64,5344,4016,DngProfile.Mipi16,DngProfile.RGGB, 0, matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.IMX230));
        }
        return null;
    }

    @Override
    public AbstractManualParameter getSkintoneParameter() {
        AbstractManualParameter Skintone = new SkintoneManualPrameter(parameters, cameraUiWrapper);
        parametersHandler.PictureFormat.addEventListner(((BaseManualParameter)Skintone).GetPicFormatListner());
        cameraUiWrapper.GetModuleHandler().addListner(((BaseManualParameter) Skintone).GetModuleListner());
        return Skintone;
    }
}
