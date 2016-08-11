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

package freed.cam.apis.camera1.parameters;

import android.hardware.Camera.Parameters;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera1.CameraHolder;
import freed.cam.apis.camera1.CameraHolder.Frameworks;
import freed.cam.apis.camera1.parameters.device.AbstractDevice;
import freed.cam.apis.camera1.parameters.device.BaseMTKDevice;
import freed.cam.apis.camera1.parameters.device.BaseQcomDevice;
import freed.cam.apis.camera1.parameters.device.Xiaomi_Redmi_Note3_QC_MTK;
import freed.cam.apis.camera1.parameters.device.mtk.Alcatel_985n;
import freed.cam.apis.camera1.parameters.device.mtk.Elephone_P9000;
import freed.cam.apis.camera1.parameters.device.mtk.ForwardArt_MTK;
import freed.cam.apis.camera1.parameters.device.mtk.I_Mobile_IStyleQ6;
import freed.cam.apis.camera1.parameters.device.mtk.Jiayu_S3;
import freed.cam.apis.camera1.parameters.device.mtk.Lenovo_K4Note_MTK;
import freed.cam.apis.camera1.parameters.device.mtk.Lenovo_K50_MTK;
import freed.cam.apis.camera1.parameters.device.mtk.Lumigon_T3;
import freed.cam.apis.camera1.parameters.device.mtk.Meizu_M2_Note_MTK;
import freed.cam.apis.camera1.parameters.device.mtk.Meizu_MX4_5_MTK;
import freed.cam.apis.camera1.parameters.device.mtk.Mlais_M52_Red_Note;
import freed.cam.apis.camera1.parameters.device.mtk.Prestigio_Multipad_Color;
import freed.cam.apis.camera1.parameters.device.mtk.Retro_MTK;
import freed.cam.apis.camera1.parameters.device.mtk.Rome_X;
import freed.cam.apis.camera1.parameters.device.mtk.Sony_C5;
import freed.cam.apis.camera1.parameters.device.mtk.Sony_M5_MTK;
import freed.cam.apis.camera1.parameters.device.mtk.THL5000_MTK;
import freed.cam.apis.camera1.parameters.device.mtk.Xiaomi_Redmi_Note2_MTK;
import freed.cam.apis.camera1.parameters.device.mtk.Zoppo_8speed;
import freed.cam.apis.camera1.parameters.device.qcom.Alcatel_Idol3;
import freed.cam.apis.camera1.parameters.device.qcom.Alcatel_Idol3_small;
import freed.cam.apis.camera1.parameters.device.qcom.Aquaris_E5;
import freed.cam.apis.camera1.parameters.device.qcom.Blackberry_Priv;
import freed.cam.apis.camera1.parameters.device.qcom.GioneE7;
import freed.cam.apis.camera1.parameters.device.qcom.HTC_Desire500;
import freed.cam.apis.camera1.parameters.device.qcom.HTC_M8;
import freed.cam.apis.camera1.parameters.device.qcom.HTC_M9;
import freed.cam.apis.camera1.parameters.device.qcom.HTC_One_A9;
import freed.cam.apis.camera1.parameters.device.qcom.HTC_One_E8;
import freed.cam.apis.camera1.parameters.device.qcom.HTC_One_SV;
import freed.cam.apis.camera1.parameters.device.qcom.HTC_One_XL;
import freed.cam.apis.camera1.parameters.device.qcom.Huawei_GX8;
import freed.cam.apis.camera1.parameters.device.qcom.Huawei_Honor5x;
import freed.cam.apis.camera1.parameters.device.qcom.LG_G2;
import freed.cam.apis.camera1.parameters.device.qcom.LG_G2pro;
import freed.cam.apis.camera1.parameters.device.qcom.LG_G3;
import freed.cam.apis.camera1.parameters.device.qcom.LG_G4;
import freed.cam.apis.camera1.parameters.device.qcom.Lenovo_K910;
import freed.cam.apis.camera1.parameters.device.qcom.Lenovo_K920;
import freed.cam.apis.camera1.parameters.device.qcom.Lenovo_VibeP1;
import freed.cam.apis.camera1.parameters.device.qcom.Lenovo_VibeShot_Z90;
import freed.cam.apis.camera1.parameters.device.qcom.Mi_Max;
import freed.cam.apis.camera1.parameters.device.qcom.Moto_X2k14;
import freed.cam.apis.camera1.parameters.device.qcom.Moto_X_Style_Pure_Play;
import freed.cam.apis.camera1.parameters.device.qcom.OnePlusOne;
import freed.cam.apis.camera1.parameters.device.qcom.OnePlusTwo;
import freed.cam.apis.camera1.parameters.device.qcom.Sony_M4;
import freed.cam.apis.camera1.parameters.device.qcom.Sony_XperiaL;
import freed.cam.apis.camera1.parameters.device.qcom.Vivo_Xplay3s;
import freed.cam.apis.camera1.parameters.device.qcom.WileyFox_Swift;
import freed.cam.apis.camera1.parameters.device.qcom.Xiaomi_Mi3_4;
import freed.cam.apis.camera1.parameters.device.qcom.Xiaomi_Mi_Note_Pro;
import freed.cam.apis.camera1.parameters.device.qcom.Xiaomi_Redmi2;
import freed.cam.apis.camera1.parameters.device.qcom.Xiaomi_Redmi3;
import freed.cam.apis.camera1.parameters.device.qcom.Xiaomi_Redmi_Note;
import freed.cam.apis.camera1.parameters.device.qcom.Yu_Yureka;
import freed.cam.apis.camera1.parameters.device.qcom.ZTE_ADV;
import freed.cam.apis.camera1.parameters.device.qcom.ZTE_ADV_IMX214;
import freed.cam.apis.camera1.parameters.device.qcom.ZTE_ADV_IMX234;
import freed.utils.Logger;

/**
 * Created by GeorgeKiarie on 6/2/2016.
 */
public class DeviceSelector {


    public DeviceSelector()
    {
    }


    public AbstractDevice getDevice(CameraWrapperInterface cameraUiWrapper, Parameters cameraParameters)
    {
        Logger.d(DeviceSelector.class.getSimpleName(), "getDevice " + cameraUiWrapper.GetAppSettingsManager().getDevice());
        switch (cameraUiWrapper.GetAppSettingsManager().getDevice())
        {
           case UNKNOWN:
               return getDefault(cameraUiWrapper,cameraParameters);
            case Alcatel_985n:
                return new Alcatel_985n(cameraParameters, cameraUiWrapper);

            case Blackberry_Priv:
                return new Blackberry_Priv(cameraParameters,cameraUiWrapper);
            case Aquaris_E5:
                return new Aquaris_E5(cameraParameters,cameraUiWrapper);

            case Alcatel_Idol3:
               return new Alcatel_Idol3(cameraParameters,cameraUiWrapper);
                
            case Alcatel_Idol3_small:
               return new Alcatel_Idol3_small(cameraParameters,cameraUiWrapper);

            case Asus_Zenfon2:
                return getDefault(cameraUiWrapper,cameraParameters);
            case Elephone_P9000:
                return new Elephone_P9000(cameraParameters,cameraUiWrapper);

            case GioneE7:
               return new GioneE7(cameraParameters,cameraUiWrapper);
                
            case ForwardArt_MTK:
               return new ForwardArt_MTK(cameraParameters,cameraUiWrapper);

            case Htc_Evo3d:
                return getDefault(cameraUiWrapper,cameraParameters);
            case Htc_M8:
               return new HTC_M8(cameraParameters,cameraUiWrapper);
                
            case Htc_M9:
               return new HTC_M9(cameraParameters,cameraUiWrapper);

            case Htc_M10:
                return getDefault(cameraUiWrapper,cameraParameters);
            case Htc_One_Sv:
               return new HTC_One_SV(cameraParameters,cameraUiWrapper);
                
            case Htc_One_Xl:
               return new HTC_One_XL(cameraParameters,cameraUiWrapper);
                
            case HTC_OneA9:
               return new HTC_One_A9(cameraParameters,cameraUiWrapper);
                
            case HTC_OneE8:
               return new HTC_One_E8(cameraParameters,cameraUiWrapper);
                
            case HTC_Desire500:
               return new HTC_Desire500(cameraParameters,cameraUiWrapper);
                
            case Huawei_GX8:
               return new Huawei_GX8(cameraParameters,cameraUiWrapper);
                
            case Huawei_HONOR5x:
               return new Huawei_Honor5x(cameraParameters,cameraUiWrapper);

            case huawei_honor6:
                return getDefault(cameraUiWrapper,cameraParameters);
            case I_Mobile_I_StyleQ6:
               return new I_Mobile_IStyleQ6(cameraParameters,cameraUiWrapper);
                
            case Jiayu_S3:
               return new Jiayu_S3(cameraParameters,cameraUiWrapper);
                
            case LenovoK910:
               return new Lenovo_K910(cameraParameters,cameraUiWrapper);
                
            case LenovoK920:
               return new Lenovo_K920(cameraParameters,cameraUiWrapper);
                
            case Lenovo_K4Note_MTK:
               return new Lenovo_K4Note_MTK(cameraParameters,cameraUiWrapper);
                
            case Lenovo_K50_MTK:
               return new Lenovo_K50_MTK(cameraParameters,cameraUiWrapper);
                
            case Lenovo_VibeP1:
               return new Lenovo_VibeP1(cameraParameters,cameraUiWrapper);

            case Lenovo_VibeShot_Z90:
                return new Lenovo_VibeShot_Z90(cameraParameters,cameraUiWrapper);
            case LG_G2:
               return new LG_G2(cameraParameters,cameraUiWrapper);

            case LG_G2pro:
                return new LG_G2pro(cameraParameters,cameraUiWrapper);
            case LG_G3:
               return new LG_G3(cameraParameters,cameraUiWrapper);
                
            case LG_G4:
               return new LG_G4(cameraParameters,cameraUiWrapper);
            case Lumigon_T3:
                return new Lumigon_T3(cameraParameters,cameraUiWrapper);
                
            case MeizuMX4_MTK:
            case MeizuMX5_MTK:
               return new Meizu_MX4_5_MTK(cameraParameters,cameraUiWrapper);
                
            case Meizu_m2Note_MTK:
               return new Meizu_M2_Note_MTK(cameraParameters,cameraUiWrapper);

            case Mlais_M52_Red_Note_MTK:
                return new Mlais_M52_Red_Note(cameraParameters,cameraUiWrapper);

            case Moto_X2k14:
                return new Moto_X2k14(cameraParameters,cameraUiWrapper);
            case Moto_X_Style_Pure_Play:
               return new Moto_X_Style_Pure_Play(cameraParameters,cameraUiWrapper);

            case MotoG3:
                return getDefault(cameraUiWrapper,cameraParameters);
            case MotoG_Turbo:
                return getDefault(cameraUiWrapper,cameraParameters);
            case Nexus4:
                return getDefault(cameraUiWrapper,cameraParameters);
            case OnePlusOne:
               return new OnePlusOne(cameraParameters,cameraUiWrapper);
                
            case OnePlusTwo:
               return new OnePlusTwo(cameraParameters,cameraUiWrapper);

            case p8:
                return getDefault(cameraUiWrapper,cameraParameters);
            case p8lite:
                return getDefault(cameraUiWrapper,cameraParameters);
            case Prestigio_Multipad_Color:
                return new Prestigio_Multipad_Color(cameraParameters,cameraUiWrapper);
            case Xiaomi_RedmiNote:
               return new Xiaomi_Redmi_Note(cameraParameters,cameraUiWrapper);

            case Xiaomi_Redmi2:
                return new Xiaomi_Redmi2(cameraParameters,cameraUiWrapper);
            case Xiaomi_RedmiNote2_MTK:
               return new Xiaomi_Redmi_Note2_MTK(cameraParameters,cameraUiWrapper);
                
            case Retro_MTK:
               return new Retro_MTK(cameraParameters,cameraUiWrapper);

            case Samsung_S6_edge:
                return getDefault(cameraUiWrapper,cameraParameters);
            case Samsung_S6_edge_plus:
                return getDefault(cameraUiWrapper,cameraParameters);
            case SonyADV:
                return getDefault(cameraUiWrapper,cameraParameters);
            case SonyM5_MTK:
               return new Sony_M5_MTK(cameraParameters,cameraUiWrapper);
                
            case SonyM4_QC:
               return new Sony_M4(cameraParameters,cameraUiWrapper);
                
            case SonyC5_MTK:
               return new Sony_C5(cameraParameters,cameraUiWrapper);
                
            case Sony_XperiaL:
               return new Sony_XperiaL(cameraParameters,cameraUiWrapper);
                
            case THL5000_MTK:
               return new THL5000_MTK(cameraParameters,cameraUiWrapper);

            case Umi_Rome_X:
                return new Rome_X(cameraParameters,cameraUiWrapper);

            case Vivo_Xplay3s:
               return new Vivo_Xplay3s(cameraParameters,cameraUiWrapper);

            case Wileyfox_Swift:
                return new WileyFox_Swift(cameraParameters,cameraUiWrapper);
            case Mi_Max:
                return new Mi_Max(cameraParameters,cameraUiWrapper);

            case XiaomiMI3W:
               return new Xiaomi_Mi3_4(cameraParameters,cameraUiWrapper);
                
            case XiaomiMI4W:
                return new Xiaomi_Mi3_4(cameraParameters,cameraUiWrapper);
                
            case XiaomiMI4C:
                return new Xiaomi_Mi3_4(cameraParameters,cameraUiWrapper);
            case XiaomiMI5:
                return getDefault(cameraUiWrapper,cameraParameters);
            case XiaomiMI_Note_Pro:
               return new Xiaomi_Mi_Note_Pro(cameraParameters,cameraUiWrapper);
                
            case Xiaomi_Redmi_Note3:
               return new Xiaomi_Redmi_Note3_QC_MTK(cameraParameters,cameraUiWrapper);

            case Xiaomi_Redmi3:
                return new Xiaomi_Redmi3(cameraParameters,cameraUiWrapper);
            case Yu_Yureka:
               return new Yu_Yureka(cameraParameters,cameraUiWrapper);
                
            case ZTE_ADV:
               return new ZTE_ADV(cameraParameters,cameraUiWrapper);
                
            case ZTEADVIMX214:
               return new ZTE_ADV_IMX214(cameraParameters,cameraUiWrapper);
                
            case ZTEADV234:
               return new ZTE_ADV_IMX234(cameraParameters,cameraUiWrapper);
            case Zoppo_8speed:
                return new Zoppo_8speed(cameraParameters,cameraUiWrapper);
            default:
                return getDefault(cameraUiWrapper,cameraParameters);
        }
    }

    private AbstractDevice getDefault(CameraWrapperInterface cameraUiWrapper, Parameters parameters)
    {
        if (((CameraHolder)cameraUiWrapper.GetCameraHolder()).DeviceFrameWork == Frameworks.MTK)
        {
            Logger.d(DeviceSelector.class.getSimpleName(), "USE DEFAULT MTK DEVICE");
            return new BaseMTKDevice(parameters, cameraUiWrapper);
        }
        else {
            Logger.d(DeviceSelector.class.getSimpleName(), "USE DEFAULT QCOM DEVICE");
            return new BaseQcomDevice(parameters, cameraUiWrapper);
        }
    }
}
