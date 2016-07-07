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

package freed.dng;


import freed.cam.apis.basecamera.parameters.modes.MatrixChooserParameter;
import freed.utils.DeviceUtils.Devices;

/**
 * Created by troop on 11.05.2015.
 */
public class DngSupportedDevices
{
    public DngProfile getProfile(Devices device, int filesize, MatrixChooserParameter matrixChooser)
    {
        switch (filesize) {

            case 2658304: //g3 front mipi
                return new DngProfile(64, 1212, 1096, DngProfile.Mipi, DngProfile.BGGR, 2424,
                        new CustomMatrix(Matrixes.CC_A_FRONT,
                        Matrixes.CC_D65_FRONT,
                        Matrixes.neutral_light_front,
                        Matrixes.G4_foward_matrix1,
                        Matrixes.G4_foward_matrix2,
                        Matrixes.G4_reduction_matrix1,
                        Matrixes.G4_reduction_matrix2,
                        Matrixes.G4_noise_3x1_matrix));
            case 2842624://g3 front qcom
                //TODO somethings wrong with it;
                return new DngProfile(64, 1296, 1096, DngProfile.Qcom, DngProfile.BGGR, 0,
                        new CustomMatrix(Matrixes.CC_A_FRONT,
                        Matrixes.CC_D65_FRONT,
                        Matrixes.neutral_light_front,
                        Matrixes.G4_foward_matrix1,
                        Matrixes.G4_foward_matrix2,
                        Matrixes.G4_reduction_matrix1,
                        Matrixes.G4_reduction_matrix2,
                        Matrixes.G4_noise_3x1_matrix));
            case 2969600:
                switch (device) {
                    case XiaomiMI3W:
                        return new DngProfile(64,1976,1200,DngProfile.Mipi16,DngProfile.RGGB,0, matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                    default:
                        return new DngProfile(64, 1236, 1200, DngProfile.Mipi, DngProfile.BGGR, 2472, matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));//g2 mipi front
                }
            case 3170304://Xiaomi_mi3 front Qcom
                return new DngProfile(0, 1976, 1200, DngProfile.Qcom, DngProfile.RGGB, 0, matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
            case 3763584: //I_Mobile_I_StyleQ6
                return new DngProfile(0, 1584, 1184, DngProfile.Plain, DngProfile.GRBG, 0,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
            case 5364240: //testing matrix DEVICE????
                return new DngProfile(0, 2688, 1520, DngProfile.Mipi, DngProfile.GRBG, DngProfile.HTCM8_rowSize,
                        matrixChooser.GetCustomMatrix(MatrixChooserParameter.OmniVision));
            case 6299648:
                return new DngProfile(16,2592,1944,DngProfile.Mipi,DngProfile.BGGR,0, matrixChooser.GetCustomMatrix(MatrixChooserParameter.OmniVision));
            case 6721536:
                switch (device) {
                    case XiaomiMI_Note_Pro:
                        return new DngProfile(64,2592,1296,DngProfile.Qcom,DngProfile.BGGR,0, matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                    case ZTE_ADV:
                        return new DngProfile(64,2592,1296,DngProfile.Qcom,DngProfile.BGGR,0, matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                    case LenovoK910:
                        return new DngProfile(64, 2592, 1296, DngProfile.Qcom, DngProfile.BGGR, 0, matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                    default:
                        return new DngProfile(64, 2592, 1296, DngProfile.Qcom, DngProfile.BGGR, 0,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                }
            case 6746112:// Htc One SV
                return new DngProfile(64,2592,1944,DngProfile.Qcom,DngProfile.GRBG,0, matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
            case 9631728: //I_Mobile_I_StyleQ6
            return new DngProfile(0, 2532, 1902, DngProfile.Plain, DngProfile.GRBG, 0,
                    matrixChooser.GetCustomMatrix(MatrixChooserParameter.OmniVision));
            case 9830400: //NGM Forward Art
                return new DngProfile(16, 2560, 1920, DngProfile.Plain, DngProfile.BGGR, 0,
                        matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
            case 9990144://e7 front mipi
                return new DngProfile(16, 3264, 2448, DngProfile.Mipi, DngProfile.BGGR, 4080,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
            case 10653696://e7 front qcom
                //TODO somethings wrong with it;
                return new DngProfile(16, 3264, 2448, DngProfile.Qcom, DngProfile.BGGR, 0,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
            case 10782464: //mytouch 4g slide / desire 500 not sure about black level 64 has green cast
                return new DngProfile(0, 3282, 2448, DngProfile.Qcom, DngProfile.GRBG, 0,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
            case 10788864: //xperia L
                return new DngProfile(64, 3282, 2448, DngProfile.Qcom, DngProfile.BGGR, DngProfile.XperiaL_rowSize,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));

            case 16224256://MIPI g2
                switch (device) {
                    case LG_G2:
                        return new DngProfile(64, 4208, 3082, DngProfile.Mipi, DngProfile.BGGR, DngProfile.ROWSIZE,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                    case LG_G3:
                        return new DngProfile(64, 4208, 3082, DngProfile.Mipi, DngProfile.BGGR, DngProfile.ROWSIZE,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                    default:
                        return new DngProfile(64, 4208, 3082, DngProfile.Mipi, DngProfile.BGGR, DngProfile.ROWSIZE,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                }
            case 16424960:
                switch (device) {
                    case Vivo_Xplay3s:
                        return new DngProfile(64, 4212, 3120, DngProfile.Mipi, DngProfile.BGGR, DngProfile.ROWSIZE,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                    case Aquaris_E5:
                    case Xiaomi_RedmiNote:
                        return new DngProfile(64, 4208, 3120, DngProfile.Mipi, DngProfile.RGGB, DngProfile.ROWSIZE,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                    case Lenovo_VibeP1: //Says GRBG unsure if correct to be confirmed
                        return new DngProfile(64, 4208, 3120, DngProfile.Mipi, DngProfile.GRBG, DngProfile.ROWSIZE,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                    case XiaomiMI3W:
                        return new DngProfile(64, 4208, 3120, DngProfile.Mipi, DngProfile.RGGB, DngProfile.ROWSIZE,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                    case XiaomiMI4W:
                        return new DngProfile(64, 4208, 3120, DngProfile.Mipi, DngProfile.RGGB, DngProfile.ROWSIZE,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                    case Alcatel_Idol3:
                        return new DngProfile(64, 4208, 3120, DngProfile.Mipi, DngProfile.RGGB, 0,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                    case Alcatel_Idol3_small:
                        return new DngProfile(64, 4208, 3120, DngProfile.Mipi, DngProfile.BGGR, DngProfile.ROWSIZE,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                    case OnePlusOne:
                        return new DngProfile(64, 4212, 3120, DngProfile.Mipi, DngProfile.RGGB, DngProfile.ROWSIZE,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                    case SonyM4_QC:
                        return new DngProfile(64, 4208, 3120, DngProfile.Mipi, DngProfile.RGGB, DngProfile.ROWSIZE,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                    case ZTE_ADV:
                       // return new Nexus6Profile(64, 4208, 3120, Mipi, BGGR, ROWSIZE);
                        return new DngProfile(64, 4208, 3120, DngProfile.Mipi, DngProfile.BGGR, DngProfile.ROWSIZE,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                    case ZTEADVIMX214:
                        return new DngProfile(64, 4208, 3120, DngProfile.Mipi, DngProfile.RGGB, DngProfile.ROWSIZE,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                    case LenovoK910:
                        return new DngProfile(64, 4208, 3120, DngProfile.Mipi, DngProfile.BGGR, DngProfile.ROWSIZE,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                    case LG_G2:
                        return new DngProfile(64, 4212, 3120, DngProfile.Mipi, DngProfile.BGGR, DngProfile.ROWSIZE,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                    case LG_G3:
                        return new DngProfile(64, 4208, 3120, DngProfile.Mipi, DngProfile.BGGR, DngProfile.ROWSIZE,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                    case Yu_Yureka:
                        return new DngProfile(64, 4208, 3120, DngProfile.Mipi, DngProfile.BGGR, DngProfile.ROWSIZE,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                    case HTC_OneA9:
                        return new DngProfile(64, 4208, 3120, DngProfile.Mipi, DngProfile.RGGB, DngProfile.ROWSIZE,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                    case Huawei_GX8:
                        return new DngProfile(64, 4208, 3120, DngProfile.Mipi, DngProfile.RGGB, DngProfile.ROWSIZE,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                    case Huawei_HONOR5x:
                        return new DngProfile(64, 4208, 3120, DngProfile.Mipi, DngProfile.RGGB, DngProfile.ROWSIZE,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                    default:
                        return new DngProfile(64, 4212, 3082, DngProfile.Mipi, DngProfile.BGGR, DngProfile.ROWSIZE,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                }
            case 16473600: //oneplus
                return new DngProfile(16,4224,3120,DngProfile.Mipi,DngProfile.BGGR,5280, matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
            case 16510976://mi 4c
                return new DngProfile(64,4208,3120,DngProfile.Mipi16,DngProfile.BGGR,0,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
            case 16560128:
                switch (device) {
                    case XiaomiMI_Note_Pro:
                        return new DngProfile(64, 4208, 3120, DngProfile.Mipi16, DngProfile.RGGB, 0,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                    case HTC_OneE8:
                        return new DngProfile(16,4224,3136,DngProfile.Mipi16,DngProfile.BGGR,0, matrixChooser.GetCustomMatrix(MatrixChooserParameter.OmniVision));
                    default:
                        return new DngProfile(64, 4212, 3120, DngProfile.Mipi, DngProfile.RGGB, 0,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                }
            case 17326080://qcom g3
                return new DngProfile(64, 4164, 3120, DngProfile.Qcom, DngProfile.BGGR, DngProfile.ROWSIZE,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
            case 17522688:
                switch (device) {
                    case Vivo_Xplay3s:
                        return new DngProfile(64, 4208, 3120, DngProfile.Qcom, DngProfile.BGGR, DngProfile.ROWSIZE,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                    case Xiaomi_RedmiNote:
                        return new DngProfile(64, 4212, 3082, DngProfile.Qcom, DngProfile.RGGB, DngProfile.ROWSIZE,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                    case XiaomiMI3W:
                        return new DngProfile(0, 4212, 3120, DngProfile.Qcom, DngProfile.RGGB, DngProfile.ROWSIZE,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                    case XiaomiMI4W:
                        return new DngProfile(0, 4212, 3120, DngProfile.Qcom, DngProfile.RGGB, DngProfile.ROWSIZE,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                    case Alcatel_Idol3:
                        return new DngProfile(64, 4208, 3120, DngProfile.Qcom, DngProfile.RGGB, 0,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                    case OnePlusOne:
                        return new DngProfile(64, 4212, 3082, DngProfile.Qcom, DngProfile.RGGB, DngProfile.ROWSIZE, matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                    case ZTE_ADV:
                        return new DngProfile(64, 4212, 3120, DngProfile.Qcom, DngProfile.BGGR, DngProfile.ROWSIZE, matrixChooser.GetCustomMatrix(MatrixChooserParameter.G4));
                    case ZTEADVIMX214:
                        return new DngProfile(64, 4212, 3120, DngProfile.Qcom, DngProfile.RGGB, DngProfile.ROWSIZE, matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                    case LenovoK910:
                        return new DngProfile(64, 4212, 3120, DngProfile.Qcom, DngProfile.BGGR, DngProfile.ROWSIZE, matrixChooser.GetCustomMatrix(MatrixChooserParameter.G4));
                    case Huawei_HONOR5x:
                    case Huawei_GX8:
                        return new DngProfile(64, 4212, 3120, DngProfile.Qcom, DngProfile.RGGB, DngProfile.ROWSIZE, matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                    case LG_G3:
                        return new DngProfile(64, 4212, 3082, DngProfile.Qcom, DngProfile.BGGR, DngProfile.ROWSIZE, matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                    case Yu_Yureka:
                        return new DngProfile(0, 4212, 3082, DngProfile.Qcom, DngProfile.BGGR, DngProfile.ROWSIZE, matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                    default:
                        return new DngProfile(64, 4208, 3120, DngProfile.Qcom, DngProfile.BGGR, DngProfile.ROWSIZE, matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                }
            case 17612800:
                switch (device) {
                    case XiaomiMI_Note_Pro:
                        return new DngProfile(64, 4212, 3120, DngProfile.Qcom, DngProfile.RGGB, 0,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                    default:
                        return new DngProfile(64, 4212, 3120, DngProfile.Qcom, DngProfile.RGGB, 0,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                }
            case 19906560://e7mipi
            return new DngProfile(16, 4608, 3456, DngProfile.Mipi, DngProfile.BGGR, 0,
                    matrixChooser.GetCustomMatrix(MatrixChooserParameter.OmniVision));
            case 19992576:  //lenovo k920
                return new DngProfile(64, 5328,3000, DngProfile.Mipi, DngProfile.GBRG, 0,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
            case 19976192: //g4 bayer mipi camera1
                return new DngProfile(64, 5312,2988,DngProfile.Mipi16, DngProfile.BGGR,0,
                        matrixChooser.GetCustomMatrix(MatrixChooserParameter.G4));
            case 20041728: // DEVICE?
                return new DngProfile(64, 5344,3000,DngProfile.Mipi16, DngProfile.RGGB,0,
                        matrixChooser.GetCustomMatrix(MatrixChooserParameter.G4));
            case 20389888: //xiaomi redmi note3 / pro
                return new DngProfile(64, 4632, 3480, DngProfile.Mipi16, DngProfile.GRBG, 0,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
            case 21233664: //e7qcom
                return new DngProfile(16, 4608, 3456, DngProfile.Qcom, DngProfile.BGGR, 0,
                        matrixChooser.GetCustomMatrix(MatrixChooserParameter.OmniVision));
            case 25677824://m9 mipi
                return new DngProfile(64, 5388, 3752, DngProfile.Mipi16, DngProfile.GRBG, 0,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
            case 26023936: //THL 5000 MTK, Redmi note2
                switch (device)
                {
                    case THL5000_MTK:
                        return new DngProfile(64, 4192, 3104, DngProfile.Plain, DngProfile.RGGB, 0,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                    case Xiaomi_RedmiNote2_MTK:
                        return new DngProfile(64, 4192, 3104, DngProfile.Plain, DngProfile.GBRG, 0,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                    case Xiaomi_Redmi_Note3:
                        return new DngProfile(64, 4192, 3104, DngProfile.Plain, DngProfile.GBRG, 0,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                    case Lenovo_K50_MTK:
                        return new DngProfile(16, 4192, 3104, DngProfile.Plain, DngProfile.BGGR, 0,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                    case Lenovo_K4Note_MTK:
                        return new DngProfile(16, 4192, 3104, DngProfile.Plain, DngProfile.GBRG, 0,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                    default:
                        return new DngProfile(64, 4192, 3104, DngProfile.Plain, DngProfile.RGGB, 0,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
                }
            case 26257920://SOny C5 probable imx 214 rggb
                switch (device)
                {
                    case SonyC5_MTK:
                        return new DngProfile(64, 4206, 3120, DngProfile.Plain,DngProfile.RGGB, 0, matrixChooser.GetCustomMatrix(MatrixChooserParameter.IMX214));
                    case Jiayu_S3:
                        return new DngProfile(64, 4208, 3120, DngProfile.Plain, DngProfile.RGGB, 0, matrixChooser.GetCustomMatrix(MatrixChooserParameter.IMX214));
                }
            case 15967488: // 8MP ALLview MTK Device
                return new DngProfile(64, 3264, 2446, DngProfile.Plain, DngProfile.BGGR, 0,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
            case 26357760: //oneplus
                return new DngProfile(16,4224,3120,DngProfile.Plain,DngProfile.BGGR,0, matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
            case 27127808: //HTC M9 QCom
                return new DngProfile(64, 5388, 3752, DngProfile.Qcom, DngProfile.GRBG, 0,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
            case 41312256: // Meizu MX4/5
                return new DngProfile(64, 5248, 3936, DngProfile.Plain, DngProfile.BGGR, 0,matrixChooser.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
            case 42923008://Moto_X_Style_Pure_Play
                return new DngProfile(64, 5344, 4016, DngProfile.Plain, DngProfile.RGGB, 0, matrixChooser.GetCustomMatrix(MatrixChooserParameter.IMX230));
            case 26935296://Moto_X_Style_Pure_Play
                return new DngProfile(64,5344,4016,DngProfile.Mipi16,DngProfile.RGGB, 0, matrixChooser.GetCustomMatrix(MatrixChooserParameter.IMX230));
        }
        if (device == Devices.LG_G4)
            return new DngProfile(64, 5312,2988,DngProfile.Mipi, DngProfile.BGGR,0, matrixChooser.GetCustomMatrix(MatrixChooserParameter.G4));
        if (device == Devices.Htc_M8)
        {
            if (filesize < 6000000 && filesize > 5382641)
                return new DngProfile(0, 2688, 1520, DngProfile.Qcom, DngProfile.GRBG, 0, matrixChooser.GetCustomMatrix(MatrixChooserParameter.OmniVision));
            else if (filesize <= 5382641 && filesize > 5000000)//M8 mipi
                return new DngProfile(0, 2688, 1520, DngProfile.Mipi16, DngProfile.GRBG, DngProfile.HTCM8_rowSize,matrixChooser.GetCustomMatrix(MatrixChooserParameter.OmniVision));
            return null;
        }
        return null;

    }

    public DngProfile GetEmptyProfile()
    {
        return new DngProfile(0,0,0,0,"bggr",0,
            new CustomMatrix(Matrixes.Nex6CCM1,
            Matrixes.Nex6CCM2,
            Matrixes.Nex6NM,
            Matrixes.Nexus6_foward_matrix1,
            Matrixes.Nexus6_foward_matrix2,
            Matrixes.Nexus6_reduction_matrix1,
            Matrixes.Nexus6_reduction_matrix2,
            Matrixes.Nexus6_noise_3x1_matrix));
    }
}
