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


    private float[] mTexRotateMatrix = new float[] {1, 0, 0, 0,   0, 1, 0, 0,   0, 0, 1, 0,   0, 0, 0, 1};
    private float[] peak_color = {1f,0f,0f,1f};
    private float peak_strength = 0.01f;
    private float[] textSize = {640,480};
    private float float_position = 0;
    private float zebra_high = 0.001f;
    private float zebra_low = 0.01f;
    private boolean focuspeak = false;
    private boolean zebra = false;

    public PreviewModel()
    {

    }

    public float[] getmTexRotateMatrix() {
        return mTexRotateMatrix;
    }

    public float[] getPeak_color() {
        return peak_color;
    }

    public float getFloat_position() {
        return float_position;
    }

    public void setFloat_position(float float_position) {
        this.float_position = float_position;
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

    public void setZebra_high(float zebra_high) {
        this.zebra_high = zebra_high;
    }

    public float getZebra_high() {
        return zebra_high;
    }

    public void setZebra_low(float zebra_low) {
        this.zebra_low = zebra_low;
    }

    public float getZebra_low() {
        return zebra_low;
    }

    public boolean isFocusPeak()
    {
        return focuspeak;
    }

    public void setFocuspeak(boolean focuspeak) {
        this.focuspeak = focuspeak;
    }

    public void setZebra(boolean zebra) {
        this.zebra = zebra;
    }

    public boolean isZebra() {
        return zebra;
    }
}
