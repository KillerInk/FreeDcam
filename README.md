FreeDcam
========
[Build](#build)  
[Supported Apis](#supported-apis)    
[Projects used by FreeDcam](#projects-used-by-freedcam)  
[Q&A](#qa)  
[Camera Ui Icon](#camera-ui-icons)  
[Camera1 Dng Supported Devices](#camera1-dng-supported-devices)  
[How to create a Custom Matrix](#how-to-create-a-custom-matrix)  
[License](#license) 

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

Q&A
===
**Q:**  I'm able to choose camera2 api, but many things are missing. Why?﻿  
**A:** because its not fully supported by your device. such devices are called legacy devices. they support just basic image capture and recording features.﻿

**Q** Why can't i open raw/bayer files?  
**A** Because its the pure data. the decoder cant read it because it does not know how to open it.  
To tell the decoder how the data is stored, the dng container is needed.
In that case send us the raw/bayer file and it will work soon.


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

[true]: /playstoreimages/check.png
[false]: /playstoreimages/cross.png

| Device                |Dng   | MF    | Shutter  |Iso  |
| --------------------- |:----:|:----:| :----:|:------:|
|Alcatel 985n           | ![true] | ![false]| ![false] | ![false] |
|Alcatel Idol3/small    | ![true] | ![true] | ![true]  | ![true]  |
|Aquaris E5             | ![true] | ![true] | ![true]  | ![true]  |
|Blackberry Priv        | ![true] | ![true] | ![true]  | ![true]  |
|Elephone P9000         | ![true] | ![true] | ![true]  | ![true]  |
|FowardArt              | ![true] | ![true] | ![true]  | ![true]  |
|Gione E7               | ![true] | ![false]| ![false] | ![false]|
|Htc Desire500          | ![true] | ![false]| ![false] | ![false]|
|Htc M8/9               | ![true] | ![true] | ![true]  | ![false]|
|Htc One A9             | ![true] | ![false]| ![false]| ![false]|
|Htc One E8             | ![true] | ![false]| ![false]| ![false]|
|Htc One SV             | ![true] | ![false]| ![false]| ![false]|
|Htc One XL             | ![true] | ![false]| ![false]| ![false]|
|Huawei GX8             | ![true] | ![true]| ![false]| ![false]|
|Huawei Honor 5X        | ![true] | ![true]| ![false]| ![false]|
|I Mobile IStylteQ6     | ![true] | ![true]| ![true]| ![true]|
|Jiayu S3               | ![true] | ![true]| ![true]| ![true]|
|Lenovo K4 Note         | ![true] | ![true]| ![true]| ![true]|
|Lenovo K50             | ![true] | ![true]| ![true]| ![true]|
|Lenovo K910            | ![true] | ![false]| ![false]| ![false]|
|Lenovo K920            | ![true] | ![true]| ![true]| ![true]|
|Lenovo VibeP1          | ![true] | ![true]| ![true]| ![true]|
|Lenovo VibeShot Z90    | ![true] | ![true]| ![true]| ![true]|
|LG G2                  | ![true] | ![true]| ![false]| ![false]|
|LG G2pro               | ![true] | ![true]| ![true]| ![false]|
|LG G3                  | ![true] |KK=![true],L=![false],M=![true]| ![false]| ![false]|
|LG G4                  | ![true] | ![true]| ![true]| ![true]|
|Meizu M2 Note          | ![true] | ![true]| ![true]| ![true]|
|Meizu MX4/5            | ![true] | ![true]| ![true]| ![true]|
|Mlais M52 Red Note     | ![true] | ![true]| ![true]| ![true]|
|Moto X 2015            | ![true] | ![false]| ![false]| ![false]|
|Moto X Style Pure Play | ![true] | ![true]| ![true]| ![true]|
|OnePlus One            | ![true] | ![true]| ![true]| ![false]|
|OnePlus Two            | ![true] | ![true]| ![true]| ![false]|
|Retro                  | ![true] | ![false]| ![false]| ![false]|
|Sony C5                | ![true] | ![true]| ![true]| ![true]|
|Sony M4                | ![true] | ![true]| ![true]| ![true]|
|Sony M5                | ![true] | ![true]| ![true]| ![true]|
|Sony XperiaL           | ![true] | ![false]| ![false]| ![false]|
|THL5000                | ![true] | ![true]| ![true]| ![true]|
|Vivo Xplay             | ![true] | ![false]| ![false]| ![false]|
|Xiaomi Mi3w            | ![true] | ![true]| ![true]| ![false]|
|Xiaomi Mi4c            | ![true] | ![true]| ![true]| ![false]|
|Xiaomi Mi4w            | ![true] | ![true]| ![true]| ![false]|
|Xiaomi Mi3             | ![true] | ![true]| ![true]| ![false]|
|Xiaomi Mi Note Pro     | ![true] | ![true]| ![true]| ![false]|
|Xiaomi Redmi Note      | ![true] | ![true]| ![true]| ![false]|
|Xiaomi Redmi Note 2    | ![true] | ![true]| ![true]| ![true]|
|Xiaomi Redmi Note 3 (Snap/MTK) | ![true] | ![true]| ![true]| ![true]|
|Yu Yureka              | ![true] | ![false]| ![false]| ![false]|
|Zoppo 8Speed           | ![true] | ![true]| ![true]| ![true]|



How to create a Custom Matrix
=============================
First you need a *Colorchecker Passport* without it, its useless to continue

![colorchecker](/playstoreimages/cc_passport.jpg)

Set it somewhere in the Scene and capture a Dng from it.  
When done you need [Adobes Dng Profile Editor](https://www.adobe.com/support/downloads/product.jsp?product=195&platform=Windows)  
Load there the Dng and choose Chart tab and place the dots to the edges of the chart.  
Then click *Create Color Table*. Now you can tune the matrixes. When done save the dcp profile into camera raw appData folder.  
C:\Users\ *UserName*\AppData\Roaming\Adobe\CameraRaw\CameraProfiles.  
Then open the Dng into CameraRaw and go to tab *Camera Calibration*.  
Select there the created dcp profile and click on *Save Image...* and save the Dng with the choosen profile, do not open the image!  

Open the saved Dng now into exiftools or a simliar tool, wich can read metadata, and copy out the matrixes into a txt file in the bottom order.

```
0.9581,0.0274,-0.1154,-0.3463,1.2258,0.1311,-0.0565,0.2411,0.2431 colormatrix1 is needed
0.6291,0.018,-0.0758,-0.3463,1.2258,0.1311,-0.0887,0.3788,0.3819  colormatrix2 is needed
0.581421,1,0.565397                                               neutral matrix is needed
0.6328,0.0469,0.2813,0.1641,0.7578,0.0781,-0.0469,-0.6406,1.5078  forwardmatrix1 is optional
0.7578,0.0859,0.1172,0.2734,0.8281,-0.1016,0.0156,-0.2813,1.0859  forwardmatrix2 is optional
                                                                  reductionmatrix1 is optional
                                                                  reductionmatrix2 is optional
                                                                  noise reduction is optional
```
                                                                                                                                


Copy that file now on your phones internalSD/DCIM/FreeDcam/config/matrix and you can select it inside Freedcam.  
Its gets then applied to each new dng



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