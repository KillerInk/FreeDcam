package freed.gl.program.compute;

import android.opengl.GLES20;
import android.opengl.GLES31;
import android.os.Build;

import androidx.annotation.RequiresApi;

import freed.gl.program.GLComputeProgram;

public class HistogramComputeProgram extends GLComputeProgram {
    public HistogramComputeProgram(int glesVersion) {
        super(glesVersion);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void compute(int width, int height, int input, int r, int g, int b)
    {
        GLES31.glUseProgram(hProgram);
        GLES31.glBindImageTexture(0, input, 0, false, 0, GLES31.GL_READ_ONLY, GLES31.GL_RGBA8);
        GLES31.glBindImageTexture(1, r, 0, false, 0, GLES31.GL_WRITE_ONLY, GLES31.GL_RGBA8);
        GLES31.glBindImageTexture(2, g, 0, false, 0, GLES31.GL_WRITE_ONLY, GLES31.GL_RGBA8);
        GLES31.glBindImageTexture(2, b, 0, false, 0, GLES31.GL_WRITE_ONLY, GLES31.GL_RGBA8);
        GLES31.glDispatchCompute(width, height, 1);
        GLES31.glMemoryBarrier(GLES31.GL_TEXTURE_UPDATE_BARRIER_BIT);
        GLES31.glMemoryBarrier(GLES31.GL_ALL_SHADER_BITS);
    }



}
