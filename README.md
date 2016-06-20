FreeDcam
========
<img src="/playstoreimages/freedcam.jpg" width="500" height="280">

##Build
To build use latest [Android Studio](http://developer.android.com/sdk/installing/studio.html)  


All needed libs are included

For ndk build set in your *local.properties*  
`ndk.dir=C\:\\Android\\android-ndk-r10b`  
Yes you need `\\` that for the folderpath


##Supported Apis:  
[android.hardware.camera](http://developer.android.com/reference/android/hardware/Camera.html)  


[android.hardware.camera2](http://developer.android.com/reference/android/hardware/camera2/package-summary.html)  


[Sony Camera Remote Api (PlayMemoriesMobile)](https://developer.sony.com/downloads/camera-file/sony-camera-remote-api-beta-sdk/)  



Projects used by FreeDcam
=========================

[MetadataExtractor](https://github.com/drewnoakes/metadata-extractor)  


[Libtiff](http://www.remotesensing.org/libtiff/)


Special Thanks @ [Dave Coffin](https://www.cybercom.net/~dcoffin/dcraw/) for dcraw and the bayer extracting to 16bit  


[Libraw](https://github.com/LibRaw/LibRaw)


[TouchImageview](https://github.com/MikeOrtiz/TouchImageView)



Camera Ui Icons
=========================
<img src="/playstoreimages/cameraui.png" width="500" height="280">


<table>
    <tr>
    <td>Modes:</td>
    <td>Manuals:</td>
    </tr>
    <tr>
        </td>
        <td>
            <table bgcolor="#6f6f6f">
                <tr>
                    <td><img src="/app/src/main/res/drawable-hdpi/quck_set_wb.png" width="50" height="50"></td>
                    <td>Whitebalance Mode</td>
                </tr>
                <tr>
                    <td><img src="/app/src/main/res/drawable-hdpi/manual_iso.png" width="50" height="50"></td>
                    <td>Iso Mode</td>
                </tr>
                <tr>
                    <td><img src="/app/src/main/res/drawable-hdpi/quck_set_flash.png" width="50" height="50"></td>
                    <td>Flash Mode</td>
                </tr>
                <tr>
                    <td><img src="/app/src/main/res/drawable-hdpi/manual_focus.png" width="50" height="50"></td>
                    <td>Focus Mode</td>
                </tr>
                <tr>
                    <td><img src="/app/src/main/res/drawable-hdpi/quck_set_ae.png" width="50" height="50"></td>
                    <td>Exposure Mode Mode</td>
                </tr>
                <tr>
                    <td><img src="/app/src/main/res/drawable-hdpi/ae_priority.png" width="50" height="50"></td>
                    <td>Ae Priority Mode</td>
                </tr>
                <tr>
                    <td><img src="/app/src/main/res/drawable-hdpi/quck_set_night.png" width="50" height="50"></td>
                    <td>Night Mode</td>
                </tr>
                <tr>
                    <td><img src="/app/src/main/res/drawable-hdpi/quck_set_contin.png" width="50" height="50"></td>
                    <td>Continouse Capture Mode</td>
                </tr>
                <tr>
                    <td><img src="/app/src/main/res/drawable-hdpi/quck_set_hdr.png" width="50" height="50"></td>
                    <td>HDR Mode</td>
                </tr>
                <tr>
                    <td><img src="/app/src/main/res/drawable-hdpi/quck_set_format2.png" width="50" height="50"></td>
                    <td>Picture Format Mode</td>
                </tr>
                <tr>
                    <td><img src="/app/src/main/res/drawable-hdpi/quck_set_exit.png" width="50" height="50"></td>
                    <td>Exit App</td>
                </tr>
                <tr>
                    <td><img src="/app/src/main/res/drawable-hdpi/quck_set_mode.png" width="50" height="50"></td>
                    <td>Switch Mode/Module</td>
                </tr>
                <tr>
                    <td><img src="/app/src/main/res/drawable-hdpi/quck_set_zebra.png" width="50" height="50"></td>
                    <td>FocusPeak/ZebraPattern</td>
                </tr>
                <tr>
                    <td><img src="/app/src/main/res/drawable-hdpi/quck_set_cswitch.png" width="50" height="50"></td>
                    <td>Switch Camera</td>
                </tr>
            </table>
        </td>
        
        <td>
            <table  bgcolor="#6f6f6f">
                <tr>
                    <td><img src="/app/src/main/res/drawable-hdpi/manual_focus.png" width="50" height="50"></td>
                    <td>Manual Focus</td>
                </tr>
                <tr>
                    <td><img src="/app/src/main/res/drawable-hdpi/manual_iso.png" width="50" height="50"></td>
                    <td>Manual Iso</td>
                </tr>
                <tr>
                    <td><img src="/app/src/main/res/drawable-hdpi/manual_shutter.png" width="50" height="50"></td>
                    <td>Shutter/Exposure Time</td>
                </tr>
                <tr>
                    <td><img src="/app/src/main/res/drawable-hdpi/manual_fnum.png" width="50" height="50"></td>
                    <td>Manual Aperture/F~Number</td>
                </tr>
                <tr>
                    <td><img src="/app/src/main/res/drawable-hdpi/manual_exposure.png" width="50" height="50"></td>
                    <td>Manual Exposure</td>
                </tr>
                <tr>
                    <td><img src="/app/src/main/res/drawable-hdpi/brightness.png" width="50" height="50"></td>
                    <td>Manual Brightness</td>
                </tr>
                <tr>
                    <td><img src="/app/src/main/res/drawable-hdpi/manual_burst.png" width="50" height="50"></td>
                    <td>Burst Count</td>
                </tr>
                <tr>
                    <td><img src="/app/src/main/res/drawable-hdpi/manual_wb.png" width="50" height="50"></td>
                    <td>Manual WhiteBalance Correction</td>
                </tr>
                <tr>
                    <td><img src="/app/src/main/res/drawable-hdpi/manual_contrast.png" width="50" height="50"></td>
                    <td>Contrast</td>
                </tr>
                <tr>
                    <td><img src="/app/src/main/res/drawable-hdpi/manual_saturation.png" width="50" height="50"></td>
                    <td>Saturation</td>
                </tr>
                <tr>
                    <td><img src="/app/src/main/res/drawable-hdpi/manual_sharpness.png" width="50" height="50"></td>
                    <td>Sharpness</td>
                </tr>
                <tr>
                    <td><img src="/app/src/main/res/drawable-hdpi/manual_shift.png" width="50" height="50"></td>
                    <td>Program Shift</td>
                </tr>
                <tr>
                    <td ><img src="/app/src/main/res/drawable-hdpi/manual_zoom.png" width="50" height="50"></td>
                    <td>Zoom</td>
                </tr>
                <tr>
                    <td><img src="/app/src/main/res/drawable-hdpi/manual_convergence.png" width="50" height="50"></td>
                    <td>3D deepth on on o3d</td>
                </tr>
            </table>
        </td>
    
    </tr>

</table>

Camera1 Dng Supported Devices
=============================

KK = Kitkat,L = Lollipop , M = Marshmallow

| Device                |Dng   | MF    | Shutter  |Iso  |
| --------------------- |:----:|:----:| :----:|:------:|
|Alcatel 985n           | ![check](/playstoreimages/check.png) | ![cross](/playstoreimages/cross.png)| ![cross](/playstoreimages/cross.png) | ![cross](/playstoreimages/cross.png) |
|Alcatel Idol3/small    | ![check](/playstoreimages/check.png) | ![check](/playstoreimages/check.png) | ![check](/playstoreimages/check.png)  | ![check](/playstoreimages/check.png)  |
|Aquaris E5             | ![check](/playstoreimages/check.png) | ![check](/playstoreimages/check.png) | ![check](/playstoreimages/check.png)  | ![check](/playstoreimages/check.png)  |
|Blackberry Priv        | ![check](/playstoreimages/check.png) | ![check](/playstoreimages/check.png) | ![check](/playstoreimages/check.png)  | ![check](/playstoreimages/check.png)  |
|Elephone P9000         | ![check](/playstoreimages/check.png) | ![check](/playstoreimages/check.png) | ![check](/playstoreimages/check.png)  | ![check](/playstoreimages/check.png)  |
|FowardArt              | ![check](/playstoreimages/check.png) | ![check](/playstoreimages/check.png) | ![check](/playstoreimages/check.png)  | ![check](/playstoreimages/check.png)  |
|Gione E7               | ![check](/playstoreimages/check.png) | ![cross](/playstoreimages/cross.png)| ![cross](/playstoreimages/cross.png) | ![cross](/playstoreimages/cross.png)|
|Htc Desire500          | ![check](/playstoreimages/check.png) | ![cross](/playstoreimages/cross.png)| ![cross](/playstoreimages/cross.png) | ![cross](/playstoreimages/cross.png)|
|Htc M8/9               | ![check](/playstoreimages/check.png) | ![check](/playstoreimages/check.png) | ![check](/playstoreimages/check.png)  | ![cross](/playstoreimages/cross.png)|
|Htc One A9             | ![check](/playstoreimages/check.png) | ![cross](/playstoreimages/cross.png)| ![cross](/playstoreimages/cross.png)| ![cross](/playstoreimages/cross.png)|
|Htc One E8             | ![check](/playstoreimages/check.png) | ![cross](/playstoreimages/cross.png)| ![cross](/playstoreimages/cross.png)| ![cross](/playstoreimages/cross.png)|
|Htc One SV             | ![check](/playstoreimages/check.png) | ![cross](/playstoreimages/cross.png)| ![cross](/playstoreimages/cross.png)| ![cross](/playstoreimages/cross.png)|
|Htc One XL             | ![check](/playstoreimages/check.png) | ![cross](/playstoreimages/cross.png)| ![cross](/playstoreimages/cross.png)| ![cross](/playstoreimages/cross.png)|
|Huawei GX8             | ![check](/playstoreimages/check.png) | ![check](/playstoreimages/check.png)| ![cross](/playstoreimages/cross.png)| ![cross](/playstoreimages/cross.png)|
|Huawei Honor 5X        | ![check](/playstoreimages/check.png) | ![check](/playstoreimages/check.png)| ![cross](/playstoreimages/cross.png)| ![cross](/playstoreimages/cross.png)|
|I Mobile IStylteQ6     | ![check](/playstoreimages/check.png) | ![check](/playstoreimages/check.png)| ![check](/playstoreimages/check.png)| ![check](/playstoreimages/check.png)|
|Jiayu S3               | ![check](/playstoreimages/check.png) | ![check](/playstoreimages/check.png)| ![check](/playstoreimages/check.png)| ![check](/playstoreimages/check.png)|
|Lenovo K4 Note         | ![check](/playstoreimages/check.png) | ![check](/playstoreimages/check.png)| ![check](/playstoreimages/check.png)| ![check](/playstoreimages/check.png)|
|Lenovo K50             | ![check](/playstoreimages/check.png) | ![check](/playstoreimages/check.png)| ![check](/playstoreimages/check.png)| ![check](/playstoreimages/check.png)|
|Lenovo K910            | ![check](/playstoreimages/check.png) | ![cross](/playstoreimages/cross.png)| ![cross](/playstoreimages/cross.png)| ![cross](/playstoreimages/cross.png)|
|Lenovo K920            | ![check](/playstoreimages/check.png) | ![check](/playstoreimages/check.png)| ![check](/playstoreimages/check.png)| ![check](/playstoreimages/check.png)|
|Lenovo VibeP1          | ![check](/playstoreimages/check.png) | ![check](/playstoreimages/check.png)| ![check](/playstoreimages/check.png)| ![check](/playstoreimages/check.png)|
|Lenovo VibeShot Z90    | ![check](/playstoreimages/check.png) | ![check](/playstoreimages/check.png)| ![check](/playstoreimages/check.png)| ![check](/playstoreimages/check.png)|
|LG G2                  | ![check](/playstoreimages/check.png) | ![check](/playstoreimages/check.png)| ![cross](/playstoreimages/cross.png)| ![cross](/playstoreimages/cross.png)|
|LG G2pro               | ![check](/playstoreimages/check.png) | ![check](/playstoreimages/check.png)| ![check](/playstoreimages/check.png)| ![cross](/playstoreimages/cross.png)|
|LG G3                  | ![check](/playstoreimages/check.png) |KK=![check](/playstoreimages/check.png),L=![cross](/playstoreimages/cross.png),M=![check](/playstoreimages/check.png)| ![cross](/playstoreimages/cross.png)| ![cross](/playstoreimages/cross.png)|
|LG G4                  | ![check](/playstoreimages/check.png) | ![check](/playstoreimages/check.png)| ![check](/playstoreimages/check.png)| ![check](/playstoreimages/check.png)|
|Meizu M2 Note          | ![check](/playstoreimages/check.png) | ![check](/playstoreimages/check.png)| ![check](/playstoreimages/check.png)| ![check](/playstoreimages/check.png)|
|Lenovo MX4/5           | ![check](/playstoreimages/check.png) | ![check](/playstoreimages/check.png)| ![check](/playstoreimages/check.png)| ![check](/playstoreimages/check.png)|
|Moto X 2015            | ![check](/playstoreimages/check.png) | ![cross](/playstoreimages/cross.png)| ![cross](/playstoreimages/cross.png)| ![cross](/playstoreimages/cross.png)|
|Moto X Style Pure Play | ![check](/playstoreimages/check.png) | ![check](/playstoreimages/check.png)| ![check](/playstoreimages/check.png)| ![check](/playstoreimages/check.png)|
|OnePlus One            | ![check](/playstoreimages/check.png) | ![check](/playstoreimages/check.png)| ![check](/playstoreimages/check.png)| ![cross](/playstoreimages/cross.png)|
|OnePlus Two            | ![check](/playstoreimages/check.png) | ![check](/playstoreimages/check.png)| ![check](/playstoreimages/check.png)| ![cross](/playstoreimages/cross.png)|
|Retro                  | ![check](/playstoreimages/check.png) | ![cross](/playstoreimages/cross.png)| ![cross](/playstoreimages/cross.png)| ![cross](/playstoreimages/cross.png)|
|Sony C5                | ![check](/playstoreimages/check.png) | ![check](/playstoreimages/check.png)| ![check](/playstoreimages/check.png)| ![check](/playstoreimages/check.png)|
|Sony M4                | ![check](/playstoreimages/check.png) | ![check](/playstoreimages/check.png)| ![check](/playstoreimages/check.png)| ![check](/playstoreimages/check.png)|
|Sony M5                | ![check](/playstoreimages/check.png) | ![check](/playstoreimages/check.png)| ![check](/playstoreimages/check.png)| ![check](/playstoreimages/check.png)|
|Sony XperiaL           | ![check](/playstoreimages/check.png) | ![cross](/playstoreimages/cross.png)| ![cross](/playstoreimages/cross.png)| ![cross](/playstoreimages/cross.png)|
|THL5000                | ![check](/playstoreimages/check.png) | ![check](/playstoreimages/check.png)| ![check](/playstoreimages/check.png)| ![check](/playstoreimages/check.png)|
|Vivo Xplay             | ![check](/playstoreimages/check.png) | ![cross](/playstoreimages/cross.png)| ![cross](/playstoreimages/cross.png)| ![cross](/playstoreimages/cross.png)|
|Xiaomi Mi3w            | ![check](/playstoreimages/check.png) | ![check](/playstoreimages/check.png)| ![check](/playstoreimages/check.png)| ![cross](/playstoreimages/cross.png)|
|Xiaomi Mi4c            | ![check](/playstoreimages/check.png) | ![check](/playstoreimages/check.png)| ![check](/playstoreimages/check.png)| ![cross](/playstoreimages/cross.png)|
|Xiaomi Mi4w            | ![check](/playstoreimages/check.png) | ![check](/playstoreimages/check.png)| ![check](/playstoreimages/check.png)| ![cross](/playstoreimages/cross.png)|
|Xiaomi Mi3             | ![check](/playstoreimages/check.png) | ![check](/playstoreimages/check.png)| ![check](/playstoreimages/check.png)| ![cross](/playstoreimages/cross.png)|
|Xiaomi Mi Note Pro     | ![check](/playstoreimages/check.png) | ![check](/playstoreimages/check.png)| ![check](/playstoreimages/check.png)| ![cross](/playstoreimages/cross.png)|
|Xiaomi Redmi Note      | ![check](/playstoreimages/check.png) | ![check](/playstoreimages/check.png)| ![check](/playstoreimages/check.png)| ![cross](/playstoreimages/cross.png)|
|Xiaomi Redmi Note 2    | ![check](/playstoreimages/check.png) | ![check](/playstoreimages/check.png)| ![check](/playstoreimages/check.png)| ![check](/playstoreimages/check.png)|
|Xiaomi Redmi Note 3 (Snap/MTK) | ![check](/playstoreimages/check.png) | ![check](/playstoreimages/check.png)| ![check](/playstoreimages/check.png)| ![check](/playstoreimages/check.png)|
|Yu Yureka              | ![check](/playstoreimages/check.png) | ![cross](/playstoreimages/cross.png)| ![cross](/playstoreimages/cross.png)| ![cross](/playstoreimages/cross.png)|
|Zoppo 8Speed           | ![check](/playstoreimages/check.png) | ![check](/playstoreimages/check.png)| ![check](/playstoreimages/check.png)| ![check](/playstoreimages/check.png)|



License
=======
This program is free software; you can redistribute it and/or modify  
it under the terms of the GNU General Public License as published by  
the Free Software Foundation; either version 2 of the License, or  
(at your option) any later version.  

This program is distributed in the hope that it will be useful,  
but WITHOUT ANY WARRANTY; without even the implied warranty of  
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  
GNU General Public License for more details.  

You should have received a copy of the GNU General Public License along  
with this program; if not, write to the Free Software Foundation, Inc.,  
51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.  