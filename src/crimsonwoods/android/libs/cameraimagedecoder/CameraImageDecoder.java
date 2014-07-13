package crimsonwoods.android.libs.cameraimagedecoder;

public class CameraImageDecoder {
	static {
		System.loadLibrary("camimgdec");
	}
	public static native void decodeNV21(int[] rgba, byte[] yu12, int width, int height);
	public static native void decodeYUY2(int[] rgba, byte[] yu12, int width, int height);
}
