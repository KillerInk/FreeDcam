package freed.gl;

import android.graphics.Point;
import android.opengl.GLES20;

import java.nio.IntBuffer;

public class MeteringProcessor {

    public interface MeteringEvent
    {
        void onMeteringDataChanged(int meters[]);
    }

    private MeteringEvent meteringEventListener;
    private int width;
    private int height;
    private int meters[];

                                            private Point center_y_plus_plus;
    private Point center_x_minus_y_plus;    private Point center_y_plus;        private Point center_x_plus_y_plus;
    private Point center_x_minus;           private Point center;               private Point center_x_plus;
    private Point center_y_minus_x_minus;   private Point center_y_minus;       private Point center_y_minus_x_plus;
                                            private Point center_y_minus_minus;

    private IntBuffer pixelBuffer;


    public void setMeteringEventListener(MeteringEvent meteringEventListener) {
        this.meteringEventListener = meteringEventListener;
    }

    public void setSize(int width, int height)
    {
        this.width = width;
        this.height = height;
        pixelBuffer = IntBuffer.allocate(1);
        meters = new int[11];
        calcPositionsToGet();
    }


    /*
            #
        #   #   #
    #       #       #
        #   #   #
            #
     */
    private void calcPositionsToGet() {
        int x_step = width/6;
        int y_step = height/6;
        int center_x = width /2;
        int center_y = height/2;

        center_y_plus_plus = new Point(center_x, center_y - y_step*2);
        center_y_plus = new Point(center_x, center_y - y_step);
        center = new Point(center_x,center_y);
        center_y_minus = new Point(center_x, center_y + y_step);
        center_y_minus_minus = new Point(center_x, center_y + y_step*2);

        center_x_minus_y_plus = new Point(center_x-x_step, center_y - y_step);
        center_x_plus_y_plus = new Point(center_x+x_step, center_y - y_step);

        center_x_minus = new Point(center_x - x_step*2,center_y);
        center_x_plus = new Point(center_x + x_step*2,center_y);

        center_y_minus_x_plus = new Point(center_x+x_step, center_y - y_step);
        center_y_minus_x_minus = new Point(center_x-x_step, center_y - y_step);

        center_y_minus_minus = new Point(center_x, center_y + y_step*2);

    }

    public void getMeters()
    {
                                                getColor(0,center_y_plus_plus);
        getColor(1,center_x_minus_y_plus);   getColor(2,center_y_plus);         getColor(3,center_x_plus_y_plus);
        getColor(4,center_x_minus);          getColor(5,center);                getColor(6,center_x_plus);
        getColor(7,center_y_minus_x_minus);  getColor(8,center_y_minus);        getColor(9,center_y_minus_x_plus);
                                                getColor(10,center_y_minus_minus);

        if(meteringEventListener != null)
            meteringEventListener.onMeteringDataChanged(meters);
    }

    private void getColor(int i, Point p)
    {
        GLES20.glReadPixels(p.x, p.y, 1, 1, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, pixelBuffer);
        meters[i] = pixelBuffer.get(0);
    }
}
