package freed.gl.program;

import android.opengl.GLES20;

public class FocuspeakProgram extends GLProgram{
    private int peak_color_id;
    private int peak_strength_id;
    private float[] peak_color = {1f,0f,0f,1f};
    private float peak_strength = 0.01f;

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

    public FocuspeakProgram(int glesVersion) {
        super(glesVersion);
    }

    @Override
    public void createAndLinkProgram() {
        super.createAndLinkProgram();
        peak_color_id = GLES20.glGetUniformLocation (hProgram, "peak_color");
        checkGlError("link peak color");
        peak_strength_id = GLES20.glGetUniformLocation (hProgram, "peak_strength");
        checkGlError("link peak strength");
    }


    @Override
    protected void onSetData() {
        super.onSetData();
        GLES20.glUniform4fv(peak_color_id, 1, peak_color,0);
        checkGlError("set peak color");
        GLES20.glUniform1f(peak_strength_id, peak_strength);
        checkGlError("peak strength");

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
}
