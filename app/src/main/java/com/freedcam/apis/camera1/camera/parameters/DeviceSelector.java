package com.freedcam.apis.camera1.camera.parameters;

import android.hardware.Camera;
import android.os.Handler;

import com.freedcam.apis.camera1.camera.CameraHolderApi1;
import com.freedcam.apis.camera1.camera.CameraHolderApi1.Frameworks;
import com.freedcam.apis.camera1.camera.CameraUiWrapper;
import com.freedcam.apis.camera1.camera.parameters.device.AbstractDevice;
import com.freedcam.apis.camera1.camera.parameters.device.BaseMTKDevice;
import com.freedcam.apis.camera1.camera.parameters.device.BaseQcomDevice;
import com.freedcam.apis.camera1.camera.parameters.device.Xiaomi_Redmi_Note3_QC_MTK;
import com.freedcam.apis.camera1.camera.parameters.device.mtk.Alcatel_985n;
import com.freedcam.apis.camera1.camera.parameters.device.mtk.ForwardArt_MTK;
import com.freedcam.apis.camera1.camera.parameters.device.mtk.I_Mobile_IStyleQ6;
import com.freedcam.apis.camera1.camera.parameters.device.mtk.Jiayu_S3;
import com.freedcam.apis.camera1.camera.parameters.device.mtk.Lenovo_K4Note_MTK;
import com.freedcam.apis.camera1.camera.parameters.device.mtk.Lenovo_K50_MTK;
import com.freedcam.apis.camera1.camera.parameters.device.mtk.Meizu_M2_Note_MTK;
import com.freedcam.apis.camera1.camera.parameters.device.mtk.Meizu_MX4_5_MTK;
import com.freedcam.apis.camera1.camera.parameters.device.mtk.Retro_MTK;
import com.freedcam.apis.camera1.camera.parameters.device.mtk.Sony_C5;
import com.freedcam.apis.camera1.camera.parameters.device.mtk.Sony_M5_MTK;
import com.freedcam.apis.camera1.camera.parameters.device.mtk.THL5000_MTK;
import com.freedcam.apis.camera1.camera.parameters.device.mtk.Xiaomi_Redmi_Note2_MTK;
import com.freedcam.apis.camera1.camera.parameters.device.qcom.Alcatel_Idol3;
import com.freedcam.apis.camera1.camera.parameters.device.qcom.Alcatel_Idol3_small;
import com.freedcam.apis.camera1.camera.parameters.device.qcom.Aquaris_E5;
import com.freedcam.apis.camera1.camera.parameters.device.qcom.GioneE7;
import com.freedcam.apis.camera1.camera.parameters.device.qcom.HTC_Desire500;
import com.freedcam.apis.camera1.camera.parameters.device.qcom.HTC_M8;
import com.freedcam.apis.camera1.camera.parameters.device.qcom.HTC_M9;
import com.freedcam.apis.camera1.camera.parameters.device.qcom.HTC_One_A9;
import com.freedcam.apis.camera1.camera.parameters.device.qcom.HTC_One_E8;
import com.freedcam.apis.camera1.camera.parameters.device.qcom.HTC_One_SV;
import com.freedcam.apis.camera1.camera.parameters.device.qcom.HTC_One_XL;
import com.freedcam.apis.camera1.camera.parameters.device.qcom.Huawei_GX8;
import com.freedcam.apis.camera1.camera.parameters.device.qcom.Huawei_Honor5x;
import com.freedcam.apis.camera1.camera.parameters.device.qcom.LG_G2;
import com.freedcam.apis.camera1.camera.parameters.device.qcom.LG_G3;
import com.freedcam.apis.camera1.camera.parameters.device.qcom.LG_G4;
import com.freedcam.apis.camera1.camera.parameters.device.qcom.Lenovo_K910;
import com.freedcam.apis.camera1.camera.parameters.device.qcom.Lenovo_K920;
import com.freedcam.apis.camera1.camera.parameters.device.qcom.Lenovo_VibeP1;
import com.freedcam.apis.camera1.camera.parameters.device.qcom.Moto_MSM8982_8994;
import com.freedcam.apis.camera1.camera.parameters.device.qcom.OnePlusOne;
import com.freedcam.apis.camera1.camera.parameters.device.qcom.OnePlusTwo;
import com.freedcam.apis.camera1.camera.parameters.device.qcom.Sony_M4;
import com.freedcam.apis.camera1.camera.parameters.device.qcom.Sony_XperiaL;
import com.freedcam.apis.camera1.camera.parameters.device.qcom.Vivo_Xplay3s;
import com.freedcam.apis.camera1.camera.parameters.device.qcom.Xiaomi_Mi3W;
import com.freedcam.apis.camera1.camera.parameters.device.qcom.Xiaomi_Mi4W;
import com.freedcam.apis.camera1.camera.parameters.device.qcom.Xiaomi_Mi4c;
import com.freedcam.apis.camera1.camera.parameters.device.qcom.Xiaomi_Mi_Note_Pro;
import com.freedcam.apis.camera1.camera.parameters.device.qcom.Xiaomi_Redmi_Note;
import com.freedcam.apis.camera1.camera.parameters.device.qcom.Yu_Yureka;
import com.freedcam.apis.camera1.camera.parameters.device.qcom.ZTE_ADV;
import com.freedcam.apis.camera1.camera.parameters.device.qcom.ZTE_ADV_IMX214;
import com.freedcam.apis.camera1.camera.parameters.device.qcom.ZTE_ADV_IMX234;
import com.freedcam.utils.DeviceUtils;
import com.freedcam.utils.Logger;

/**
 * Created by GeorgeKiarie on 6/2/2016.
 */
public class DeviceSelector {


    public DeviceSelector()
    {
    }


    public AbstractDevice getDevice(CameraUiWrapper cameraUiWrapper, Handler uiHandler, Camera.Parameters cameraParameters,CameraHolderApi1 cameraHolder)
    {
        Logger.d(DeviceSelector.class.getSimpleName(), "getDevice " + DeviceUtils.DEVICE());
        switch (DeviceUtils.DEVICE())
        {
            case Alcatel_985n:
                return new Alcatel_985n(uiHandler,cameraParameters,cameraUiWrapper);
//                case p8:
//                    
//                case p8lite:
//                    
//                case honor6:
//                    
            case Aquaris_E5:
                return new Aquaris_E5(uiHandler,cameraParameters,cameraUiWrapper);
            
            
            case Alcatel_Idol3:
               return new Alcatel_Idol3(uiHandler,cameraParameters,cameraUiWrapper);
                
            case Alcatel_Idol3_small:
               return new Alcatel_Idol3_small(uiHandler,cameraParameters,cameraUiWrapper);
                
            case GioneE7:
               return new GioneE7(uiHandler,cameraParameters,cameraUiWrapper);
                
            case ForwardArt_MTK:
               return new ForwardArt_MTK(uiHandler,cameraParameters,cameraUiWrapper);
                
            case Htc_M8:
               return new HTC_M8(uiHandler,cameraParameters,cameraUiWrapper);
                
            case Htc_M9:
               return new HTC_M9(uiHandler, cameraParameters,cameraUiWrapper);
                
            case Htc_One_Sv:
               return new HTC_One_SV(uiHandler,cameraParameters,cameraUiWrapper);
                
            case Htc_One_Xl:
               return new HTC_One_XL(uiHandler,cameraParameters,cameraUiWrapper);
                
            case HTC_OneA9:
               return new HTC_One_A9(uiHandler,cameraParameters,cameraUiWrapper);
                
            case HTC_OneE8:
               return new HTC_One_E8(uiHandler,cameraParameters,cameraUiWrapper);
                
            case HTC_Desire500:
               return new HTC_Desire500(uiHandler,cameraParameters,cameraUiWrapper);
                
            case Huawei_GX8:
               return new Huawei_GX8(uiHandler,cameraParameters,cameraUiWrapper);
                
            case Huawei_HONOR5x:
               return new Huawei_Honor5x(uiHandler,cameraParameters,cameraUiWrapper);
                
            case I_Mobile_I_StyleQ6:
               return new I_Mobile_IStyleQ6(uiHandler,cameraParameters,cameraUiWrapper);
                
            case Jiayu_S3:
               return new Jiayu_S3(uiHandler,cameraParameters,cameraUiWrapper);
                
            case LenovoK910:
               return new Lenovo_K910(uiHandler,cameraParameters,cameraUiWrapper);
                
            case LenovoK920:
               return new Lenovo_K920(uiHandler,cameraParameters,cameraUiWrapper);
                
            case Lenovo_K4Note_MTK:
               return new Lenovo_K4Note_MTK(uiHandler, cameraParameters,cameraUiWrapper);
                
            case Lenovo_K50_MTK:
               return new Lenovo_K50_MTK(uiHandler,cameraParameters,cameraUiWrapper);
                
            case Lenovo_VibeP1:
               return new Lenovo_VibeP1(uiHandler,cameraParameters,cameraUiWrapper);
                
            case LG_G2:
               return new LG_G2(uiHandler,cameraParameters,cameraUiWrapper);
                
//                case LG_G2pro:
//                    
            case LG_G3:
               return new LG_G3(uiHandler,cameraParameters,cameraUiWrapper);
                
            case LG_G4:
               return new LG_G4(uiHandler,cameraParameters,cameraUiWrapper);
                
            case MeizuMX4_MTK:
            case MeizuMX5_MTK:
               return new Meizu_MX4_5_MTK(uiHandler,cameraParameters,cameraUiWrapper);
                
            case Meizu_m2Note_MTK:
               return new Meizu_M2_Note_MTK(uiHandler,cameraParameters,cameraUiWrapper);
                
//                case Moto_MSM8974:
//                    
            case Moto_MSM8982_8994:
               return new Moto_MSM8982_8994(uiHandler,cameraParameters,cameraUiWrapper);
                
//                case MotoG3:
//                    
//                case MotoG_Turbo:
//                    
//                case Nexus4:
//                    
            case OnePlusOne:
               return new OnePlusOne(uiHandler,cameraParameters,cameraUiWrapper);
                
            case OnePlusTwo:
               return new OnePlusTwo(uiHandler,cameraParameters,cameraUiWrapper);
                
            case Xiaomi_RedmiNote:
               return new Xiaomi_Redmi_Note(uiHandler,cameraParameters,cameraUiWrapper);
                
            case Xiaomi_RedmiNote2_MTK:
               return new Xiaomi_Redmi_Note2_MTK(uiHandler,cameraParameters,cameraUiWrapper);
                
            case Retro_MTK:
               return new Retro_MTK(uiHandler,cameraParameters,cameraUiWrapper);
                
//                case Samsung_S6_edge:
//                    
//                case Samsung_S6_edge_plus:
//                    
//                case SonyADV:
//                    
            case SonyM5_MTK:
               return new Sony_M5_MTK(uiHandler,cameraParameters,cameraUiWrapper);
                
            case SonyM4_QC:
               return new Sony_M4(uiHandler,cameraParameters,cameraUiWrapper);
                
            case SonyC5_MTK:
               return new Sony_C5(uiHandler,cameraParameters,cameraUiWrapper);
                
            case Sony_XperiaL:
               return new Sony_XperiaL(uiHandler,cameraParameters,cameraUiWrapper);
                
            case THL5000_MTK:
               return new THL5000_MTK(uiHandler,cameraParameters,cameraUiWrapper);
                
            case Vivo_Xplay3s:
               return new Vivo_Xplay3s(uiHandler,cameraParameters,cameraUiWrapper);
                
            case XiaomiMI3W:
               return new Xiaomi_Mi3W(uiHandler,cameraParameters,cameraUiWrapper);
                
            case XiaomiMI4W:
               return new Xiaomi_Mi4W(uiHandler,cameraParameters,cameraUiWrapper);
                
            case XiaomiMI4C:
               return new Xiaomi_Mi4c(uiHandler,cameraParameters,cameraUiWrapper);
                
//                case XiaomiMI5:
//                    
            case XiaomiMI_Note_Pro:
               return new Xiaomi_Mi_Note_Pro(uiHandler,cameraParameters,cameraUiWrapper);
                
            case Xiaomi_Redmi_Note3:
               return new Xiaomi_Redmi_Note3_QC_MTK(uiHandler,cameraParameters,cameraUiWrapper);
                
            case Yu_Yureka:
               return new Yu_Yureka(uiHandler,cameraParameters,cameraUiWrapper);
                
            case ZTE_ADV:
               return new ZTE_ADV(uiHandler,cameraParameters,cameraUiWrapper);
                
            case ZTEADVIMX214:
               return new ZTE_ADV_IMX214(uiHandler,cameraParameters,cameraUiWrapper);
                
            case ZTEADV234:
               return new ZTE_ADV_IMX234(uiHandler,cameraParameters,cameraUiWrapper);
            default:
                if (cameraHolder.DeviceFrameWork == Frameworks.MTK)
                {
                    Logger.d(DeviceSelector.class.getSimpleName(), "USE DEFAULT MTK DEVICE");
                    return new BaseMTKDevice(uiHandler, cameraParameters, cameraUiWrapper);
                }
                else {
                    Logger.d(DeviceSelector.class.getSimpleName(), "USE DEFAULT QCOM DEVICE");
                    return new BaseQcomDevice(uiHandler, cameraParameters, cameraUiWrapper);
                }
        }
        
    }
}
