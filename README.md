FreeDcam
========
* [Build](#build)
* [Supported Apis](#supported-apis)
* [Projects used by FreeDcam](#projects-used-by-freedcam)
* [Q&A](#qa)
* [Hidden Oem Settings](#Hidden-Oem-Settings)
* [Camera Ui Icon](#camera-ui-icons)
* [Camera1 Dng Supported Devices](#camera1-dng-supported-devices)
* [How to create a Custom Matrix](#how-to-create-a-custom-matrix)
* [How to create a ToneMapProfile](#how-to-create-a-tonemapprofile)
* [MSM Camera Blobs Logging for Devs](#msm-camera-blobs-logging-for-devs)
* [License](#license)

<img src="/playstoreimages/freedcam.jpg" width="500" height="280">

Build
=====
To build use latest [Android Studio](http://developer.android.com/sdk/installing/studio.html)  
Use NDK 12b!

All needed libs are included

For ndk build set in your *local.properties*  
`ndk.dir=C\:\\Android\\android-ndk-r10b`  
Yes you need `\\` that for the folderpath


Supported Apis:
===========
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
* **Q:**  I'm able to choose camera2 api, but many things are missing. Why?﻿  
**A:** because its not fully supported by your device. such devices are called legacy devices. they support just basic image capture and recording features.﻿

* **Q** Why can't i open raw/bayer files?  
**A** Because its the pure data. the decoder cant read it because it does not know how to open it.  
To tell the decoder how the data is stored, the dng container is needed.  
In that case send us the raw/bayer file and it will work soon.

* **Q** Why my Device dont show an highspeed video profile, i know it support it  
**A** Its because the Oem's did not add it as MediaProfile and freedcam support it only for most snapdragon socs
- In that case open the VideoProfileEditor inside FreeDcamSetting.
- As sample for 720p highspeed select first the 720p profile.
- Rename it to 720Hfr or what ever you want.
- Change Framerate to 120
- Select Highspeed
- Save the Profile and close the VideoProfileEditor.

* **Q** Why cant i decompile apps build with nougat sdk
**A** They changed the dex header version. Use a hexeditor to change the version from 37 to 35 and dex2jar works


Hidden Oem Settings
=========================
Huawei Code:
```
*#*#2846579#*#*
```

Camera Ui Icons
=========================
<img src="/playstoreimages/cameraui.png" width="500" height="280">


<table>
    <tr>
    <td>Modes:</td>
    <td>Manuals:</td>
    </tr>
    <tr>
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

List is not up to date

KK = Kitkat,L = Lollipop , M = Marshmallow

[true]: /playstoreimages/check.png
[false]: /playstoreimages/cross.png

| Device                |Dng   | MF    | Shutter  |Iso  |
| --------------------- |:----:|:----:| :----:|:------:|
|Alcatel 985n           | ![true] | ![false]| ![false] | ![false] |
|Alcatel Idol3/small    | ![true] | ![true] | ![true]  | ![true]  |
|Aquaris E5             | ![true] | ![true] | ![true]  | ![true]  |
|Aquaris M5             | ![true] | ![true] | ![true]  | ![true]  |
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
|Lumigon T3             | ![true] | ![true]| ![true]| ![true]|
|LG G2                  | ![true] | ![true]| ![false]| ![false]|
|LG G2pro               | ![true] | ![true]| ![true]| ![false]|
|LG G3                  | ![true] |KK=![true],L=![false],M=![true]| ![false]| ![false]|
|LG G4                  | ![true] | ![true]| ![true]| ![true]|
|Meizu M1 Metal         | ![true] | ![true]| ![true]| ![true]|
|Meizu M2 Note          | ![true] | ![true]| ![true]| ![true]|
|Meizu MX4/5            | ![true] | ![true]| ![true]| ![true]|
|Mlais M52 Red Note     | ![true] | ![true]| ![true]| ![true]|
|Moto X 2015            | ![true] | ![false]| ![false]| ![false]|
|Moto X Style Pure Play | ![true] | ![true]| ![true]| ![true]|
|MyPhone Infinity 2S    | ![true] | ![true]| ![true]| ![true]|
|OnePlus One            | ![true] | ![true]| ![true]| ![false]|
|OnePlus Two            | ![true] | ![true]| ![true]| ![false]|
|Prestigio Multipad Color | ![true] | ![true]| ![true]| ![true]|
|Retro                  | ![true] | ![false]| ![false]| ![false]|
|Sony C4                | ![true] | ![true]| ![true]| ![true]|
|Sony C5                | ![true] | ![true]| ![true]| ![true]|
|Sony M4                | ![true] | ![true]| ![true]| ![true]|
|Sony M5                | ![true] | ![true]| ![true]| ![true]|
|Sony XperiaL           | ![true] | ![false]| ![false]| ![false]|
|THL5000                | ![true] | ![true]| ![true]| ![true]|
|Umi Rome X             | ![true] | ![true]| ![true]| ![true]|
|Vivo Xplay             | ![true] | ![false]| ![false]| ![false]|
|WileyFox Swift         | ![true] | ![true]| ![true]| ![false]|
|Xiaomi Mi3             | ![true] | ![true]| ![true]| ![false]|
|Xiaomi Mi3w            | ![true] | ![true]| ![true]| ![false]|
|Xiaomi Mi4c            | ![true] | ![true]| ![true]| ![false]|
|Xiaomi Mi4w            | ![true] | ![true]| ![true]| ![false]|
|Xiaomi Mi 5            | ![false]broken raw stream | ![true]| ![true]| ![false]|
|Xiaomi Mi Max          | ![true] | ![true]| ![true]| ![false]|
|Xiaomi Mi Note Pro     | ![true] | ![true]| ![true]| ![false]|
|Xiaomi Redmi Note      | ![true] | ![true]| ![true]| ![false]|
|Xiaomi Redmi Note 2    | ![true] | ![true]| ![true]| ![true]|
|Xiaomi Redmi Note 3 (Snap/MTK) | ![true] | ![true]| ![true]| ![true]|
|Xolo Omega5            | ![true] | ![true]| ![true]| ![true]|
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
<matrixes>
    <matrix name="G4">
        <color1>1.15625, -0.2890625, -0.3203125, -0.53125, 1.5625f, 0.0625, -0.078125, 0.28125, 0.5625</color1>
        <color2>0.5859375, 0.0546875, -0.125, -0.6484375, 1.5546875, 0.0546875, -0.2421875, 0.5625, 0.390625</color2>
        <neutral>0.53125, 1, 0.640625</neutral>
        <forward1>0.820300, -0.218800, 0.359400, 0.343800, 0.570300,0.093800, 0.015600, -0.726600, 1.539100</forward1>
        <forward2>0.679700, -0.078100, 0.359400, 0.210900, 0.703100,0.085900, -0.046900, -0.828100, 1.695300</forward2>
        <reduction1>0.9921875, 0, 0, 0, 1, 0, 0, 0, 1.015625</reduction1>
        <reduction2>0.9921875, 0, 0, 0, 1, 0, 0, 0, 1.015625</reduction2>
        <noise>0.8853462669953089, 0,  0.8853462669953089f, 0, 0.8853462669953089f,0</noise>
    </matrix> 
</matrixes>
```

How to create a ToneMapProfile
=============================
Can get used to apply custom profiles direct to a dng.  
data can get created with various tools.  
like from a dcp with dcamprof or dcptools.  
or extracted from a existing dng with exiftools.

tonemapprofiles.xml
```
<tonemapprofiles>

<!-- thats the first profile -->
    <tonemapprofile name="linear">
        <tonecurve>0,0,0.25,0.25,0.5,0.5,0.75,0.75,1,1</tonecurve> use "," to split. ignores whitespace and line breaks,
    
    <baselineexposure>0.35</baselineexposure> set to avoid the hidden exposure
    <huesatmapdims>90 25 1</huesatmapdims> should only contain whitespaces for splitting no line breaks!
    <huesatmapdata1>0 0 .... 1 1</huesatmapdata1> should only contain whitespaces for splitting no line breaks!
    </tonemapprofile>


    <!-- next profile -->
    <tonemapprofile name="srgb">
        <tonecurve>0,0,......,1,1</tonecurve>
    </tonemapprofile>
    
    <tonemapprofile name="iso100">
        <baselineexposure>-0.35</baselineexposure>
    </tonemapprofile>
</tonemapprofiles>

```

Copy that file now on your phones internalSD/DCIM/FreeDcam/config/matrixes.xml and you can select it inside Freedcam.  
Its gets then applied to each new dng

MSM Camera Blobs Logging for DEVS
========================
Create File in /data/misc/camera/camera_dbg.txt  
Valid values:  
none    - no logging  
error   - error message logging only, default  
high    - log high priority messages and up  
warn    - log warnings and higher  
low     - verbose logging  
debug   - debug logging level  

```
 cam_dbglevel=debug
 mct_dbglevel=debug
 sensor_dbglevel=debug
 iface_dbglevel=debug
 isp_dbglevel=debug
 stats_dbglevel=debug
 pproc_dbglevel=debug
 imglib_dbglevel=debug
 cpp_dbglevel=debug
 hal_dbglevel=debug
 jpeg_dbglevel=debug
 c2d_dbglevel=debug
 ```
 
 Permission #chmod 770   
 persist.camera.global.debug 4  
 persist.camera.debug.logfile 1

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