package freed.jni;

/**
 * Created by GeorgeKiarie on 10/27/2016.
 */
public class DngMatrixCalc {

    static
    {
        System.loadLibrary("freedcam");
    }

    public DngMatrixCalc()
    {

    }

    private static native void calc();

    public void CalcualteD65()
    {
        calc();
    }
}
