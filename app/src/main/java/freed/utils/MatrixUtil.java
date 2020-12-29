package freed.utils;

import android.graphics.Matrix;
import android.graphics.RectF;

public class MatrixUtil {
    private static final String MATRIXTAG = MatrixUtil.class.getSimpleName();

    public static Matrix getTransFormMatrix(int input_w, int input_h, int output_w, int output_h, int rotation, boolean renderscript)
    {
        Matrix matrix = new Matrix();
        matrix.reset();
        RectF inputRect = new RectF(0, 0, input_w, input_h);
        Log.d(MATRIXTAG, "PreviewSize:" + input_w +"x"+ input_h);
        Log.d(MATRIXTAG,"DisplaySize:" + output_w +"x"+ output_h);

        float viewRatio = output_w / output_h;
        float inputRatio = inputRect.width() /inputRect.height();

        Log.d(MATRIXTAG,"previewratio : " + viewRatio + " inputratio :" + inputRatio);

        RectF viewRect = new RectF(0, 0, output_w, output_h);

        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();

        /*
          input is like that when holding device in landscape

            ________
            |      |                               _____________________
            |      |                               |                   |
            |      |  need to get transformed to:  |                   | viewrect
            |      |                               |___________________|
            ________
         */

        float scaleX;
        float scaleY;
        if (renderscript)
        {
            //renderscript has already set the width and height due the Allocation
            //we have to use the real width and height from the Allocation
            if (rotation == 90 || rotation == 270) {
                Log.d(MATRIXTAG, "orientation 90/270");
                scaleY = input_w / viewRect.height();
                scaleX = input_h / viewRect.width();
            } else {
                Log.d(MATRIXTAG, "orientation 0/180");
                scaleY = input_h / viewRect.height();
                scaleX = input_w / viewRect.width();
            }
        }
        else {
            if (rotation == 90 || rotation == 270) {
                Log.d(MATRIXTAG, "orientation 90/270");
                scaleY= inputRect.width() / viewRect.height();
                scaleX = inputRect.height() / viewRect.width();
            } else {
                Log.d(MATRIXTAG, "orientation 0/180");
                scaleY = inputRect.height() / viewRect.height();
                scaleX = inputRect.width() / viewRect.width();
            }
        }
        Log.d(MATRIXTAG,"scaleX:" +scaleX + " scaleY:" +scaleY + " centerX:"+centerX +" centerY:" +centerY + " rotation:" + rotation);

        inputRect.offset(centerX - inputRect.centerX(), centerY - inputRect.centerY());
        matrix.setRectToRect(inputRect,viewRect, Matrix.ScaleToFit.CENTER);
        matrix.postScale(scaleX, scaleY, inputRect.centerX(), inputRect.centerY());
        matrix.postRotate(rotation, inputRect.centerX(), inputRect.centerY());
        return matrix;
    }
}
