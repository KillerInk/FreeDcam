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
Y = Yes , N = No
KK = Kitkat,L = Lollipop , M = Marshmallow

| Device                |Dng   | MF    | Shutter  |Iso  |
| --------------------- |:----:|:----:| :----:|:------:|
|Alcatel 985n           | Y | N| N | N |
|Alcatel Idol3/small    | Y | Y | Y  | Y  |
|Aquaris E5             | Y | Y | Y  | Y  |
|Blackberry Priv        | Y | Y | Y  | Y  |
|Elephone P9000         | Y | Y | Y  | Y  |
|FowardArt              | Y | Y | Y  | Y  |
|Gione E7               | Y | N| N | N|
|Htc Desire500          | Y | N| N | N|
|Htc M8/9               | Y | Y | Y  | N|
|Htc One A9             | Y | N| N| N|
|Htc One E8             | Y | N| N| N|
|Htc One SV             | Y | N| N| N|
|Htc One XL             | Y | N| N| N|
|Huawei GX8             | Y | Y| N| N|
|Huawei Honor 5X        | Y | Y| N| N|
|I Mobile IStylteQ6     | Y | Y| Y| Y|
|Jiayu S3               | Y | Y| Y| Y|
|Lenovo K4 Note         | Y | Y| Y| Y|
|Lenovo K50             | Y | Y| Y| Y|
|Lenovo K910            | Y | N| N| N|
|Lenovo K920            | Y | Y| Y| Y|
|Lenovo VibeP1          | Y | Y| Y| Y|
|Lenovo VibeShot Z90    | Y | Y| Y| Y|
|LG G2                  | Y | Y| N| N|
|LG G2pro               | Y | Y| Y| N|
|LG G3                  | Y |KK=Y,L=N,M=Y| N| N|
|LG G4                  | Y | Y| Y| Y|
|Meizu M2 Note          | Y | Y| Y| Y|
|Lenovo MX4/5           | Y | Y| Y| Y|
|Moto X 2015            | Y | N| N| N|
|Moto X Style Pure Play | Y | Y| Y| Y|
|OnePlus One            | Y | Y| Y| N|
|OnePlus Two            | Y | Y| Y| N|
|Retro                  | Y | N| N| N|
|Sony C5                | Y | Y| Y| Y|
|Sony M4                | Y | Y| Y| Y|
|Sony M5                | Y | Y| Y| Y|
|Sony XperiaL           | Y | N| N| N|
|THL5000                | Y | Y| Y| Y|
|Vivo Xplay             | Y | N| N| N|
|Xiaomi Mi3w            | Y | Y| Y| N|
|Xiaomi Mi4c            | Y | Y| Y| N|
|Xiaomi Mi4w            | Y | Y| Y| N|
|Xiaomi Mi3             | Y | Y| Y| N|
|Xiaomi Mi Note Pro     | Y | Y| Y| N|
|Xiaomi Redmi Note      | Y | Y| Y| N|
|Xiaomi Redmi Note 2    | Y | Y| Y| Y|
|Xiaomi Redmi Note 3 (Snap/MTK) | Y | Y| Y| Y|
|Yu Yureka              | Y | N| N| N|
|Zoppo 8Speed           | Y | Y| Y| Y|



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