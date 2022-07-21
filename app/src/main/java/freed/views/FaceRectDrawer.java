package freed.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.params.Face;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.Arrays;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import freed.cam.apis.CameraApiManager;
import freed.cam.apis.camera2.CameraHolderApi2;
import freed.cam.previewpostprocessing.PreviewController;
import freed.settings.SettingsManager;
import freed.utils.Log;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
@AndroidEntryPoint
public class FaceRectDrawer extends View {

    private static final String TAG = FaceRectDrawer.class.getSimpleName();
    private Rect[] faces;
    private Paint paint;

    @Inject
    PreviewController previewController;
    @Inject
    CameraApiManager cameraApiManager;
    @Inject
    SettingsManager settingsManager;


    public FaceRectDrawer(Context context) {
        super(context);
        init();
    }

    public FaceRectDrawer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FaceRectDrawer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);
    }


    public void setFaces(Face[] faces)
    {
        this.faces = translateFaces(faces);
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (faces == null)
            return;
        for (Rect f : faces)
        {
            canvas.drawRect(f,paint);
        }
    }

    private Rect[] translateFaces(Face[] faces)
    {
        Rect sensorSize =  ((CameraHolderApi2)cameraApiManager.getCamera().getCameraHolder()).characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);

        Rect[] ret = new Rect[faces.length];
        int i =0;
        int previewWidthWithoutMargin = previewController.getViewWidth()-previewController.getMargineLeft()*2;
        for (Face f : faces)
        {
            Rect rect = new Rect();
            rect.top = getPosForScreen(f.getBounds().top, sensorSize.height(),previewController.getViewHeight());
            rect.bottom = getPosForScreen(f.getBounds().bottom, sensorSize.height(),previewController.getViewHeight());
            if (settingsManager.getIsFrontCamera()) {
                rect.left = previewWidthWithoutMargin - getPosForScreen(f.getBounds().left, sensorSize.width(), previewWidthWithoutMargin);
                rect.right = previewWidthWithoutMargin - getPosForScreen(f.getBounds().right, sensorSize.width(), previewWidthWithoutMargin);
            }
            else
            {
                rect.left = getPosForScreen(f.getBounds().left, sensorSize.width(), previewWidthWithoutMargin);
                rect.right = getPosForScreen(f.getBounds().right, sensorSize.width(), previewWidthWithoutMargin);
            }
            rect.left += previewController.getMargineLeft();
            rect.right += previewController.getMargineLeft();

            Log.d(TAG, "----------------------------------------------------------------");
            Log.d(TAG, "Sensor:" + sensorSize.width() + "/" +sensorSize.height());
            Log.d(TAG, "Preview:" + previewController.getViewWidth() + "/" +previewController.getViewHeight() + " Margin left/right:" + previewController.getMargineLeft() +"/" +previewController.getMargineTop());
            Log.d(TAG, "Sensor Pos:" + f.getBounds().toString());
            Log.d(TAG, "Screen Pos:" + rect.toString());
            ret[i++] = rect;
        }

        return ret;
    }

    private int getPosForScreen(float pos, float sensor_size, float preview_size)
    {
        return (int) ((1f/sensor_size*pos)*preview_size);
    }
}
