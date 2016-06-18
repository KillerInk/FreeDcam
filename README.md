FreeDCam
========
to build use latest android studio  
http://developer.android.com/sdk/installing/studio.html

all needed libs are included

for ndk build set in your local.properties
ndk.dir=C\:\\Android\\android-ndk-r10b
yes you need \\ that for a folderpath


freedcam supports now 3 different apis:
android.hardware.camera  
http://developer.android.com/reference/android/hardware/Camera.html

android.hardware.camera2  
http://developer.android.com/reference/android/hardware/camera2/package-summary.html

sony camera remote api (PlayMemories)  
https://developer.sony.com/downloads/camera-file/sony-camera-remote-api-beta-sdk/


Projects used by FreeDcam
=========================

MetadataExtractor  
https://github.com/drewnoakes/metadata-extractor

libtiff  
http://www.remotesensing.org/libtiff/

Special Thanks @ Dave Coffin for dcraw and the bayer extracting to 16bit  
https://www.cybercom.net/~dcoffin/dcraw/

libraw
https://github.com/LibRaw/LibRaw

TouchImageview
https://github.com/MikeOrtiz/TouchImageView


Camera Ui Icons
=========================

<table>
    <tr>
    <td>Modes:</td>
    <td>Manuals:</td>
    </tr>
    <tr>
        </td>
        <td>
            <table>
                <tr>
                    <td bgcolor="#6f6f6f"><img src="/app/src/main/res/drawable-hdpi/quck_set_wb.png" width="50" height="50"></td>
                    <td>Whitebalance Mode</td>
                </tr>
                <tr>
                    <td bgcolor="#6f6f6f"><img src="/app/src/main/res/drawable-hdpi/manual_iso.png" width="50" height="50"></td>
                    <td>Iso Mode</td>
                </tr>
                <tr>
                    <td bgcolor="#6f6f6f"><img src="/app/src/main/res/drawable-hdpi/quck_set_flash.png" width="50" height="50"></td>
                    <td>Flash Mode</td>
                </tr>
                <tr>
                    <td bgcolor="#6f6f6f"><img src="/app/src/main/res/drawable-hdpi/manual_focus.png" width="50" height="50"></td>
                    <td>Focus Mode</td>
                </tr>
                <tr>
                    <td bgcolor="#6f6f6f"><img src="/app/src/main/res/drawable-hdpi/quck_set_ae.png" width="50" height="50"></td>
                    <td>Exposure Mode Mode</td>
                </tr>
                <tr>
                    <td bgcolor="#6f6f6f"><img src="/app/src/main/res/drawable-hdpi/ae_priority.png" width="50" height="50"></td>
                    <td>Ae Priority Mode</td>
                </tr>
                <tr>
                    <td bgcolor="#6f6f6f"><img src="/app/src/main/res/drawable-hdpi/quck_set_night.png" width="50" height="50"></td>
                    <td>Night Mode</td>
                </tr>
                <tr>
                    <td bgcolor="#6f6f6f"><img src="/app/src/main/res/drawable-hdpi/quck_set_contin.png" width="50" height="50"></td>
                    <td>Continouse Capture Mode</td>
                </tr>
                <tr>
                    <td bgcolor="#6f6f6f"><img src="/app/src/main/res/drawable-hdpi/quck_set_hdr.png" width="50" height="50"></td>
                    <td>HDR Mode</td>
                </tr>
                <tr>
                    <td bgcolor="#6f6f6f"><img src="/app/src/main/res/drawable-hdpi/quck_set_format2.png" width="50" height="50"></td>
                    <td>Picture Format Mode</td>
                </tr>
                <tr>
                    <td bgcolor="#6f6f6f"><img src="/app/src/main/res/drawable-hdpi/quck_set_exit.png" width="50" height="50"></td>
                    <td>Exit App</td>
                </tr>
                <tr>
                    <td bgcolor="#6f6f6f"><img src="/app/src/main/res/drawable-hdpi/quck_set_mode.png" width="50" height="50"></td>
                    <td>Switch Mode/Module</td>
                </tr>
                <tr>
                    <td bgcolor="#6f6f6f"><img src="/app/src/main/res/drawable-hdpi/quck_set_zebra.png" width="50" height="50"></td>
                    <td>Exit App</td>
                </tr>
                <tr>
                    <td bgcolor="#6f6f6f"><img src="/app/src/main/res/drawable-hdpi/quck_set_cswitch.png" width="50" height="50"></td>
                    <td>Switch Camera</td>
                </tr>
            </table>
        </td>
        
        <td>
            <table>
                <tr>
                    <td bgcolor="#6f6f6f"><img src="/app/src/main/res/drawable-hdpi/manual_focus.png" width="50" height="50"></td>
                    <td>Manual Focus</td>
                </tr>
                <tr>
                    <td bgcolor="#6f6f6f"><img src="/app/src/main/res/drawable-hdpi/manual_iso.png" width="50" height="50"></td>
                    <td>Manual Iso</td>
                </tr>
                <tr>
                    <td bgcolor="#6f6f6f"><img src="/app/src/main/res/drawable-hdpi/manual_shutter.png" width="50" height="50"></td>
                    <td>Shutter/Exposure Time</td>
                </tr>
                <tr>
                    <td bgcolor="#6f6f6f"><img src="/app/src/main/res/drawable-hdpi/manual_fnum.png" width="50" height="50"></td>
                    <td>Manual Aperture/F~Number</td>
                </tr>
                <tr>
                    <td bgcolor="#6f6f6f"><img src="/app/src/main/res/drawable-hdpi/manual_exposure.png" width="50" height="50"></td>
                    <td>Manual Exposure</td>
                </tr>
                <tr>
                    <td bgcolor="#6f6f6f"><img src="/app/src/main/res/drawable-hdpi/brightness.png" width="50" height="50"></td>
                    <td>Manual Brightness</td>
                </tr>
                <tr>
                    <td bgcolor="#6f6f6f"><img src="/app/src/main/res/drawable-hdpi/manual_burst.png" width="50" height="50"></td>
                    <td>Burst Count</td>
                </tr>
                <tr>
                    <td bgcolor="#6f6f6f"><img src="/app/src/main/res/drawable-hdpi/manual_wb.png" width="50" height="50"></td>
                    <td>Manual WhiteBalance Correction</td>
                </tr>
                <tr>
                    <td bgcolor="#6f6f6f"><img src="/app/src/main/res/drawable-hdpi/manual_contrast.png" width="50" height="50"></td>
                    <td>Contrast</td>
                </tr>
                <tr>
                    <td bgcolor="#6f6f6f"><img src="/app/src/main/res/drawable-hdpi/manual_saturation.png" width="50" height="50"></td>
                    <td>Saturation</td>
                </tr>
                <tr>
                    <td bgcolor="#6f6f6f"><img src="/app/src/main/res/drawable-hdpi/manual_sharpness.png" width="50" height="50"></td>
                    <td>Sharpness</td>
                </tr>
                <tr>
                    <td bgcolor="#6f6f6f"><img src="/app/src/main/res/drawable-hdpi/manual_shift.png" width="50" height="50"></td>
                    <td>Program Shift</td>
                </tr>
                <tr>
                    <td bgcolor="#6f6f6f"><img src="/app/src/main/res/drawable-hdpi/manual_zoom.png" width="50" height="50"></td>
                    <td>Zoom</td>
                </tr>
                <tr>
                    <td bgcolor="#6f6f6f"><img src="/app/src/main/res/drawable-hdpi/manual_convergence.png" width="50" height="50"></td>
                    <td>3D deepth on on o3d</td>
                </tr>
            </table>
        </td>
    
    </tr>

</table>
