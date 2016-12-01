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
import freed.cam.apis.camera1.parameters.device.mediatek.ALCATEL.Alcatel_985n;
import freed.cam.apis.camera1.parameters.device.mediatek.ALCATEL.THL5000_MTK;
import freed.cam.apis.camera1.parameters.device.mediatek.ELEPHONE.Elephone_P9000;
import freed.cam.apis.camera1.parameters.device.mediatek.ForwardArt_MTK;
import freed.cam.apis.camera1.parameters.device.mediatek.I_Mobile_IStyleQ6;
import freed.cam.apis.camera1.parameters.device.mediatek.InFocusM808;
import freed.cam.apis.camera1.parameters.device.mediatek.JIAYU.Jiayu_S3;
import freed.cam.apis.camera1.parameters.device.mediatek.LENOVO.Lenovo_K4Note_MTK;
import freed.cam.apis.camera1.parameters.device.mediatek.LENOVO.Lenovo_K50_MTK;
import freed.cam.apis.camera1.parameters.device.mediatek.LUMIGON.Lumigon_T3;
import freed.cam.apis.camera1.parameters.device.mediatek.MEIZU.MeizuM1Metal;
import freed.cam.apis.camera1.parameters.device.mediatek.MEIZU.Meizu_M2_Note_MTK;
import freed.cam.apis.camera1.parameters.device.mediatek.MEIZU.Meizu_MX4_5_MTK;
import freed.cam.apis.camera1.parameters.device.mediatek.Mlais_M52_Red_Note;
import freed.cam.apis.camera1.parameters.device.mediatek.MyPhoneInfinity2S;
import freed.cam.apis.camera1.parameters.device.mediatek.PRESTIGIO.Prestigio_Multipad_Color;
import freed.cam.apis.camera1.parameters.device.mediatek.Retro_MTK;
import freed.cam.apis.camera1.parameters.device.mediatek.Rome_X;
import freed.cam.apis.camera1.parameters.device.mediatek.SONY.Sony_C4;
import freed.cam.apis.camera1.parameters.device.mediatek.SONY.Sony_C5;
import freed.cam.apis.camera1.parameters.device.mediatek.SONY.Sony_M5_MTK;
import freed.cam.apis.camera1.parameters.device.mediatek.VERNEE.VERNEE_APOLLO_Lite;
import freed.cam.apis.camera1.parameters.device.mediatek.XIAOMI.Xiaomi_Redmi_Note2_MTK;
import freed.cam.apis.camera1.parameters.device.mediatek.XOLO.Xolo_Omega5;
import freed.cam.apis.camera1.parameters.device.mediatek.ZOPPO.Zoppo_8speed;
import freed.cam.apis.camera1.parameters.device.qualcomm.ALCATEL.Alcatel_Idol3;
import freed.cam.apis.camera1.parameters.device.qualcomm.ALCATEL.Alcatel_Idol3_small;
import freed.cam.apis.camera1.parameters.device.qualcomm.AQUARIS.Aquaris_E5;
import freed.cam.apis.camera1.parameters.device.qualcomm.AQUARIS.Aquaris_M5;
import freed.cam.apis.camera1.parameters.device.qualcomm.ASUS.Asus_Zenfone_Go;
import freed.cam.apis.camera1.parameters.device.qualcomm.GIONEE.GioneE7;
import freed.cam.apis.camera1.parameters.device.qualcomm.HTC.HTC_Desire500;
import freed.cam.apis.camera1.parameters.device.qualcomm.HTC.HTC_M8;
import freed.cam.apis.camera1.parameters.device.qualcomm.HTC.HTC_M9;
import freed.cam.apis.camera1.parameters.device.qualcomm.HTC.HTC_One_A9;
import freed.cam.apis.camera1.parameters.device.qualcomm.HTC.HTC_One_E8;
import freed.cam.apis.camera1.parameters.device.qualcomm.HTC.HTC_One_SV;
import freed.cam.apis.camera1.parameters.device.qualcomm.HTC.HTC_One_XL;
import freed.cam.apis.camera1.parameters.device.qualcomm.HUAWEI.Huawei_GX8;
import freed.cam.apis.camera1.parameters.device.qualcomm.HUAWEI.Huawei_Honor5x;
import freed.cam.apis.camera1.parameters.device.qualcomm.LEECO.LeEco_Cool1;
import freed.cam.apis.camera1.parameters.device.qualcomm.LENOVO.Lenovo_K910;
import freed.cam.apis.camera1.parameters.device.qualcomm.LENOVO.Lenovo_K920;
import freed.cam.apis.camera1.parameters.device.qualcomm.LENOVO.Lenovo_VibeP1;
import freed.cam.apis.camera1.parameters.device.qualcomm.LENOVO.Lenovo_VibeShot_Z90;
import freed.cam.apis.camera1.parameters.device.qualcomm.LENOVO.Lenovo_Vibe_X3;
import freed.cam.apis.camera1.parameters.device.qualcomm.LENOVO.MotoG3;
import freed.cam.apis.camera1.parameters.device.qualcomm.LENOVO.MotoG_Turbo;
import freed.cam.apis.camera1.parameters.device.qualcomm.LENOVO.Moto_X2k14;
import freed.cam.apis.camera1.parameters.device.qualcomm.LENOVO.Moto_X_Style_Pure_Play;
import freed.cam.apis.camera1.parameters.device.qualcomm.LG.LG_G2;
import freed.cam.apis.camera1.parameters.device.qualcomm.LG.LG_G2pro;
import freed.cam.apis.camera1.parameters.device.qualcomm.LG.LG_G3;
import freed.cam.apis.camera1.parameters.device.qualcomm.LG.LG_G4;
import freed.cam.apis.camera1.parameters.device.qualcomm.LG.LG_L5;
import freed.cam.apis.camera1.parameters.device.qualcomm.Mi_Max;
import freed.cam.apis.camera1.parameters.device.qualcomm.NEXUS.Nexus6p_5x;
import freed.cam.apis.camera1.parameters.device.qualcomm.OPPO.OnePlusOne;
import freed.cam.apis.camera1.parameters.device.qualcomm.OPPO.OnePlusTwo;
import freed.cam.apis.camera1.parameters.device.qualcomm.RIM.Blackberry_Priv;
import freed.cam.apis.camera1.parameters.device.qualcomm.SONY.Sony_M4;
import freed.cam.apis.camera1.parameters.device.qualcomm.SONY.Sony_XperiaL;
import freed.cam.apis.camera1.parameters.device.qualcomm.SONY.Sony_Z5C;
import freed.cam.apis.camera1.parameters.device.qualcomm.VIVO.Vivo_V3;
import freed.cam.apis.camera1.parameters.device.qualcomm.VIVO.Vivo_Xplay3s;
import freed.cam.apis.camera1.parameters.device.qualcomm.WIKO.Wikio_Stairway;
import freed.cam.apis.camera1.parameters.device.qualcomm.WileyFox_Swift;
import freed.cam.apis.camera1.parameters.device.qualcomm.XIAOMI.Xiaomi_Mi3_4;
import freed.cam.apis.camera1.parameters.device.qualcomm.XIAOMI.Xiaomi_Mi5;
import freed.cam.apis.camera1.parameters.device.qualcomm.XIAOMI.Xiaomi_Mi_Note_Pro;
import freed.cam.apis.camera1.parameters.device.qualcomm.XIAOMI.Xiaomi_Redmi2;
import freed.cam.apis.camera1.parameters.device.qualcomm.XIAOMI.Xiaomi_Redmi3;
import freed.cam.apis.camera1.parameters.device.qualcomm.XIAOMI.Xiaomi_Redmi3s;
import freed.cam.apis.camera1.parameters.device.qualcomm.XIAOMI.Xiaomi_Redmi_Note;
import freed.cam.apis.camera1.parameters.device.qualcomm.YU.Yu_Yuphoria;
import freed.cam.apis.camera1.parameters.device.qualcomm.YU.Yu_Yureka;
import freed.cam.apis.camera1.parameters.device.qualcomm.ZTE.ZTE_ADV;
import freed.cam.apis.camera1.parameters.device.qualcomm.ZTE.ZTE_ADV_IMX214;
import freed.cam.apis.camera1.parameters.device.qualcomm.ZTE.ZTE_ADV_IMX234;
import freed.cam.apis.camera1.parameters.device.qualcomm.ZTE.ZTE_Z11;
import freed.cam.apis.camera1.parameters.device.qualcomm.ZTE.ZTE_Z5SMINI;
import android.util.Log;

/**
 * Created by GeorgeKiarie on 6/2/2016.
 */
public class DeviceSelector {


    public DeviceSelector()
    {
    }


    public AbstractDevice getDevice(CameraWrapperInterface cameraUiWrapper, Parameters cameraParameters)
    {
        Log.d(DeviceSelector.class.getSimpleName(), "getDevice " + cameraUiWrapper.GetAppSettingsManager().getDevice());
        switch (cameraUiWrapper.GetAppSettingsManager().getDevice())
        {
           case UNKNOWN:
               return getDefault(cameraUiWrapper,cameraParameters);
            case Alcatel_985n:
                return new Alcatel_985n(cameraParameters, cameraUiWrapper);

            case Asus_Zenfone_Go:
                return new Asus_Zenfone_Go(cameraParameters,cameraUiWrapper);
            case Blackberry_Priv:
                return new Blackberry_Priv(cameraParameters,cameraUiWrapper);
            case Aquaris_E5:
                return new Aquaris_E5(cameraParameters,cameraUiWrapper);

            case Aquaris_M5:
                return new Aquaris_M5(cameraParameters,cameraUiWrapper);

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

            case InFocus_M808:
                return new InFocusM808(cameraParameters,cameraUiWrapper);
            case Jiayu_S3:
               return new Jiayu_S3(cameraParameters,cameraUiWrapper);

            case LeEco_Cool1:
                return new LeEco_Cool1(cameraParameters,cameraUiWrapper);

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

            case Lenovo_Vibe_X3:
                return new Lenovo_Vibe_X3(cameraParameters,cameraUiWrapper);

            case LG_G2:
               return new LG_G2(cameraParameters,cameraUiWrapper);

            case LG_G2pro:
                return new LG_G2pro(cameraParameters,cameraUiWrapper);
            case LG_G3:
               return new LG_G3(cameraParameters,cameraUiWrapper);
                
            case LG_G4:
               return new LG_G4(cameraParameters,cameraUiWrapper);

            case LG_L5:
                return new LG_L5(cameraParameters,cameraUiWrapper);
            case Lumigon_T3:
                return new Lumigon_T3(cameraParameters,cameraUiWrapper);

            case MeizuM1Metal_MTK:
                return new MeizuM1Metal(cameraParameters,cameraUiWrapper);

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
                return new MotoG3(cameraParameters,cameraUiWrapper);

            case MotoG_Turbo:
                return new MotoG_Turbo(cameraParameters,cameraUiWrapper);

            case MyPhone_Infinity2S:
                return new MyPhoneInfinity2S(cameraParameters,cameraUiWrapper);

            case Nexus4:
                return getDefault(cameraUiWrapper,cameraParameters);

            case Nexus6:
                return getDefault(cameraUiWrapper,cameraParameters);

            case Nexus5x:
            case Nexus6p:
                return new Nexus6p_5x(cameraParameters,cameraUiWrapper);

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

            case SonyC4_MTK:
               return new Sony_C4(cameraParameters,cameraUiWrapper);

            case SonyC5_MTK:
               return new Sony_C5(cameraParameters,cameraUiWrapper);

            case Sony_Z5C:
                return new Sony_Z5C(cameraParameters,cameraUiWrapper);
                
            case Sony_XperiaL:
               return new Sony_XperiaL(cameraParameters,cameraUiWrapper);
                
            case THL5000_MTK:
               return new THL5000_MTK(cameraParameters,cameraUiWrapper);

            case Umi_Rome_X:
                return new Rome_X(cameraParameters,cameraUiWrapper);

            case Vivo_Xplay3s:
               return new Vivo_Xplay3s(cameraParameters,cameraUiWrapper);

            case Vivo_V3:
                return new Vivo_V3(cameraParameters,cameraUiWrapper);

            case Wiko_Stairway:
                return new Wikio_Stairway(cameraParameters,cameraUiWrapper);

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
                return new Xiaomi_Mi5(cameraParameters,cameraUiWrapper);

            case XiaomiMI_Note_Pro:
               return new Xiaomi_Mi_Note_Pro(cameraParameters,cameraUiWrapper);
                
            case Xiaomi_Redmi_Note3:
               return new Xiaomi_Redmi_Note3_QC_MTK(cameraParameters,cameraUiWrapper);

            case Xiaomi_Redmi3:
                return new Xiaomi_Redmi3(cameraParameters,cameraUiWrapper);

            case Xiaomi_Redmi3S:
                return new Xiaomi_Redmi3s(cameraParameters,cameraUiWrapper);

            case Xolo_Omega5:
                return new Xolo_Omega5(cameraParameters,cameraUiWrapper);

            case Yu_Yuphoria:
                return new Yu_Yuphoria(cameraParameters,cameraUiWrapper);

            case Yu_Yureka:
               return new Yu_Yureka(cameraParameters,cameraUiWrapper);

            case VERNEE_APOLLO_Lite:
                return new VERNEE_APOLLO_Lite(cameraParameters,cameraUiWrapper);
                
            case ZTE_ADV:
               return new ZTE_ADV(cameraParameters,cameraUiWrapper);
                
            case ZTEADVIMX214:
               return new ZTE_ADV_IMX214(cameraParameters,cameraUiWrapper);
                
            case ZTEADV234:
               return new ZTE_ADV_IMX234(cameraParameters,cameraUiWrapper);

            case ZTE_Z11:
                return new ZTE_Z11(cameraParameters,cameraUiWrapper);

            case ZTE_Z9:
                return getDefault(cameraUiWrapper,cameraParameters);

            case ZTE_Z5SMINI:
                return new ZTE_Z5SMINI(cameraParameters,cameraUiWrapper);

            case ZTE_MyPrague:
                return getDefault(cameraUiWrapper,cameraParameters);

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
            Log.d(DeviceSelector.class.getSimpleName(), "USE DEFAULT MTK DEVICE");
            return new BaseMTKDevice(parameters, cameraUiWrapper);
        }
        else {
            Log.d(DeviceSelector.class.getSimpleName(), "USE DEFAULT QCOM DEVICE");
            return new BaseQcomDevice(parameters, cameraUiWrapper);
        }
    }
}
