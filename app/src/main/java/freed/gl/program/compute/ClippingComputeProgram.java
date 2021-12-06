package freed.gl.program.compute;

import android.opengl.GLES20;
import android.opengl.GLES31;
import android.os.Build;

import androidx.annotation.RequiresApi;

import freed.gl.program.GLComputeProgram;

public class ClippingComputeProgram extends GLComputeProgram {

    int float_position_id;
    int zebra_low_id;
    private int zebra_high_id;

    private int float_position = 0;
    private float zebra_high = 0.001f;
    private float zebra_low = 0.01f;

    public ClippingComputeProgram(int glesVersion) {
        super(glesVersion);
    }

    @Override
    public void createAndLinkProgram() {
        super.createAndLinkProgram();
        float_position_id = GLES20.glGetUniformLocation (hProgram, "float_position");
        zebra_low_id = GLES20.glGetUniformLocation(hProgram, "zebra_low");
        zebra_high_id = GLES20.glGetUniformLocation (hProgram, "zebra_high");
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void compute(int width, int height, int input, int output)
    {
        GLES31.glUseProgram(hProgram);
        GLES20.glUniform1i(float_position_id, float_position);
        GLES20.glUniform1f(zebra_low_id, zebra_low);
        GLES20.glUniform1f(zebra_high_id, zebra_high);
        GLES31.glBindImageTexture(0, input, 0, false, 0, GLES31.GL_READ_ONLY, GLES31.GL_RGBA8);
        GLES31.glBindImageTexture(1, output, 0, false, 0, GLES31.GL_WRITE_ONLY, GLES31.GL_RGBA8);
        GLES31.glDispatchCompute(width, height, 1);
        GLES31.glMemoryBarrier(GLES31.GL_TEXTURE_UPDATE_BARRIER_BIT);
        GLES31.glMemoryBarrier(GLES31.GL_ALL_SHADER_BITS);
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

    public int getFloat_position() {
        return float_position;
    }

    public void setFloat_position(int float_position) {
        this.float_position = float_position;
    }

}
