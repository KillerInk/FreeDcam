package freed.gl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class PreviewModel
{

    public enum Colors
    {
        red,
        green,
        blue,
        white,
        yellow,
        magenta,
        cyan,
    }

    private FloatBuffer pVertex;
    private FloatBuffer pTexCoord;
    private float[] mTexRotateMatrix = new float[] {1, 0, 0, 0,   0, 1, 0, 0,   0, 0, 1, 0,   0, 0, 0, 1};
    private float[] vtmp = {1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f};
    private float[] ttmp = {1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f};
    private float[] peak_color = {1f,0f,0f,1f};
    private float peak_strength = 0.01f;
    private float[] textSize = {640,480};

    public PreviewModel()
    {
        pVertex = ByteBuffer.allocateDirect(8 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        pVertex.put(vtmp);
        pVertex.position(0);
        pTexCoord = ByteBuffer.allocateDirect(8 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        pTexCoord.put(ttmp);
        pTexCoord.position(0);
    }

    public float[] getmTexRotateMatrix() {
        return mTexRotateMatrix;
    }

    public FloatBuffer getpVertex() {
        return pVertex;
    }

    public FloatBuffer getpTexCoord() {
        return pTexCoord;
    }

    public float[] getTtmp() {
        return ttmp;
    }

    public float[] getVtmp() {
        return vtmp;
    }

    public float[] getPeak_color() {
        return peak_color;
    }

    public void setOrientation(int or)
    {
        android.opengl.Matrix.setRotateM(mTexRotateMatrix, 0,  or, 0f, 0f, 1f);
    }

    public void setRed(boolean on)
    {
        peak_color[0] = on ? 1:0;
        peak_color[3] = 1f;
    }

    public void setGreen(boolean on)
    {
        peak_color[1] = on ? 1:0;
        peak_color[3] = 1f;
    }

    public void setBlue(boolean on)
    {
        peak_color[2] = on ? 1:0;
        peak_color[3] = 1f;
    }

    public void setPeak_color(Colors color)
    {
        switch (color)
        {
            case red:
                peak_color[0] = 1f;
                peak_color[1] = 0f;
                peak_color[2] = 0f;
                peak_color[3] = 1f;
                break;
            case green:
                peak_color[0] = 0f;
                peak_color[1] = 1f;
                peak_color[2] = 0f;
                peak_color[3] = 1f;
                break;
            case blue:
                peak_color[0] = 0f;
                peak_color[1] = 0f;
                peak_color[2] = 1f;
                peak_color[3] = 1f;
                break;
            case white:
                peak_color[0] = 1f;
                peak_color[1] = 1f;
                peak_color[2] = 1f;
                peak_color[3] = 1f;
                break;
            case yellow:
                peak_color[0] = 1f;
                peak_color[1] = 1f;
                peak_color[2] = 0f;
                peak_color[3] = 1f;
                break;
            case magenta:
                peak_color[0] = 1f;
                peak_color[1] = 0f;
                peak_color[2] = 1f;
                peak_color[3] = 1f;
                break;
            case cyan:
                peak_color[0] = 0f;
                peak_color[1] = 1f;
                peak_color[2] = 1f;
                peak_color[3] = 1f;
                break;
        }
    }

    public float getPeak_strength() {
        return peak_strength;
    }

    public void setTextSize(int width, int height)
    {
        textSize[0] = width;
        textSize[1] = height;
    }

    public float[] getTextSize()
    {
        return textSize;
    }
}
