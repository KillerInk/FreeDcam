package freed.cam.apis.sonyremote.sonystuff;


import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Auth {

    private final static String TAG = Auth.class.getSimpleName();
    // Tied to Methods list below, 64 _ASCII_ characters = 256 bit equivalant
    String AUTH_CONST_STRING = "90adc8515a40558968fe8318b5b023fdd48d3828a2dda8905f3b93a3cd8e58dc";

   public static String METHODS_TO_ENABLE =
                   "guide/getServiceProtocols:" +
                   "guide/getVersions:"+
                   "guide/getMethodTypes:" +
                   "camera/getVersions:"+
                   "camera/getMethodTypes:"+
                   "accessControl/getVersions:"+
                   "accessControl/getMethodTypes:"+
                   "avContent/getVersions:"+
                   "avContent/getMethodTypes:"+
                   "camera/getApplicationInfo:"+
                   "camera/getAvailableApiList:"+
                   "camera/setShootMode:"+
                   "camera/getShootMode:"+
                   "camera/getSupportedShootMode:"+
                   "camera/getAvailableShootMode:"+
                   "camera/setFlashMode:"+
                   "camera/getFlashMode:"+
                   "camera/getSupportedFlashMode:"+
                   "camera/getAvailableFlashMode:"+
                   "camera/setSelfTimer:"+
                   "camera/getSelfTimer:"+
                   "camera/getSupportedSelfTimer:"+
                   "camera/getAvailableSelfTimer:"+
                   "camera/setExposureCompensation:"+
                   "camera/getExposureCompensation:"+
                   "camera/getSupportedExposureCompensation:"+
                   "camera/getAvailableExposureCompensation:"+
                   "camera/setPostviewImageSize:"+
                   "camera/getPostviewImageSize:"+
                   "camera/getSupportedPostviewImageSize:"+
                   "camera/getAvailablePostviewImageSize:"+
                   "camera/setTouchAFPosition:"+
                   "camera/getTouchAFPosition:"+
                   "camera/cancelTouchAFPosition:"+
                   "camera/setFNumber:"+
                   "camera/getFNumber:"+
                   "camera/getSupportedFNumber:"+
                   "camera/getAvailableFNumber:"+
                   "camera/setShutterSpeed:"+
                   "camera/getShutterSpeed:"+
                   "camera/getSupportedShutterSpeed:"+
                   "camera/getAvailableShutterSpeed:"+
                   "camera/setIsoSpeedRate:"+
                   "camera/getIsoSpeedRate:"+
                   "camera/getSupportedIsoSpeedRate:"+
                   "camera/getAvailableIsoSpeedRate:"+
                   "camera/setExposureMode:"+
                   "camera/getExposureMode:"+
                   "camera/getSupportedExposureMode:"+
                   "camera/getAvailableExposureMode:"+
                   "camera/setWhiteBalance:"+
                   "camera/getWhiteBalance:"+
                   "camera/getSupportedWhiteBalance:"+
                   "camera/getAvailableWhiteBalance:"+
                   "camera/setProgramShift:"+
                   "camera/getSupportedProgramShift:"+
                   "camera/setZoomSetting:"+
                   "camera/getZoomSetting:"+
                   "camera/getSupportedZoomSetting:"+
                   "camera/getAvailableZoomSetting:"+
                   "camera/setContShootingMode:"+
                   "camera/getContShootingMode:"+
                   "camera/getSupportedContShootingMode:"+
                   "camera/getAvailableContShootingMode:"+
                   "camera/setContShootingSpeed:"+
                   "camera/getContShootingSpeed:"+
                   "camera/getSupportedContShootingSpeed:"+
                   "camera/getAvailableContShootingSpeed:"+
                   "camera/startRecMode:"+
                   "camera/stopRecMode:"+
                   "camera/startLiveview:"+
                   "camera/startLiveviewWithSize:"+
                   "camera/stopLiveview:"+
                   "camera/getLiveviewSize:"+
                   "camera/getSupportedLiveviewSize:"+
                   "camera/getAvailableLiveviewSize:"+
                   "camera/setLiveviewFrameInfo:"+
                   "camera/getLiveviewFrameInfo:"+
                   "camera/actTakePicture:"+
                   "camera/awaitTakePicture:"+
                   "camera/startContShooting:"+
                   "camera/stopContShooting:"+
                   "camera/startBulbShooting:"+
                   "camera/stopBulbShooting:"+
                   "camera/startMovieRec:"+
                   "camera/stopMovieRec:"+
                   "camera/actZoom:"+
                   "camera/setFocusMode:"+
                   "camera/getFocusMode:"+
                   "camera/getSupportedFocusMode:"+
                   "camera/getAvailableFocusMode:"+
                   "camera/actHalfPressShutter:"+
                   "camera/cancelHalfPressShutter:"+
                   "camera/getStorageInformation:"+
                   "camera/getEvent:"+
                   "camera/setCameraFunction:"+
                   "camera/getCameraFunction:"+
                   "camera/getSupportedCameraFunction:"+
                   "camera/getAvailableCameraFunction:"+
                   "camera/setSilentShootingSetting:"+
                   "camera/getSilentShootingSetting:"+
                   "camera/getSupportedSilentShootingSetting:"+
                   "camera/getAvailableSilentShootingSetting:"+
                   "avContent/getSchemeList:"+
                   "avContent/getSourceList:"+
                   "avContent/getContentCount:"+
                   "avContent/getContentList:"+
                   "avContent/setStreamingContent:"+
                   "avContent/startStreaming:"+
                   "avContent/pauseStreaming:"+
                   "avContent/seekStreamingPosition:"+
                   "avContent/stopStreaming:"+
                   "avContent/requestToNotifyStreamingStatus:"+
                   "avContent/deleteContent:"+
                   "accessControl\u002factEnableMethods";

public String SHA256 (String dg) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {


    SecretKeySpec localSecretKeySpec = new SecretKeySpec(dg.getBytes("UTF-8"), "hmacSHA256");
    Mac localMac = Mac.getInstance("hmacSHA256");
    localMac.init(localSecretKeySpec);
    String str = toHexString(localMac.doFinal(AUTH_CONST_STRING.getBytes("UTF-8")));
    return str;

        /*String m = AUTH_CONST_STRING +text;
        Log.d("Auth", m);
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(m.getBytes());
        Log.d(TAG, bytesToHex2(md.digest()));
        Log.d(TAG, bytesToHex(md.digest()));
        Log.d(TAG, toHexString(md.digest()));
        Log.d(TAG, Base64.encodeToString(md.digest(),Base64.DEFAULT));
        String t = Base64.encode(md.digest(), Base64.DEFAULT);
        m = bytesToHex(md.digest());
        return m;*/
    }

    private static String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static String toHexString(byte[] paramArrayOfByte)
    {
        StringBuffer localStringBuffer = new StringBuffer(2 * paramArrayOfByte.length);
        for (int i = 0;; i++)
        {
            if (i >= paramArrayOfByte.length) {
                return localStringBuffer.toString();
            }
            String str = Integer.toHexString(0xFF & paramArrayOfByte[i]);
            if (str.length() == 1) {
                localStringBuffer.append("0");
            }
            localStringBuffer.append(str);
        }
    }

    public static String bytesToHex2(byte[] bytes) {
        StringBuffer result = new StringBuffer();
        for (byte byt : bytes) result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
        return result.toString();
    }
}
