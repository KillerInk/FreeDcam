package freed.gl.program.compute;

import android.opengl.GLES20;
import android.opengl.GLES31;
import android.os.Build;

import androidx.annotation.RequiresApi;

import freed.gl.program.GLComputeProgram;

public class FocusPeakComputeProgram extends GLComputeProgram {

    private int peak_color_id;
    private int peak_strength_id;
    private final float[] peak_color = {1f,0f,0f,1f};
    private final float peak_strength = 0.01f;

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

    public FocusPeakComputeProgram(float glesVersion) {
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void compute(int width, int height, int input, int output)
    {
        GLES31.glUseProgram(hProgram);
        GLES20.glUniform4fv(peak_color_id, 1, peak_color,0);
        checkGlError("set peak color");
        GLES20.glUniform1f(peak_strength_id, peak_strength);
        checkGlError("peak strength");
        GLES31.glBindImageTexture(0, input, 0, false, 0, GLES31.GL_READ_ONLY, GLES31.GL_RGBA8);
        GLES31.glBindImageTexture(1, output, 0, false, 0, GLES31.GL_WRITE_ONLY, GLES31.GL_RGBA8);
        GLES31.glDispatchCompute(width, height, 1);
        GLES31.glMemoryBarrier(GLES31.GL_TEXTURE_UPDATE_BARRIER_BIT);
        GLES31.glMemoryBarrier(GLES31.GL_ALL_SHADER_BITS);
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

    public void setPeak_color(FocusPeakComputeProgram.Colors color)
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
