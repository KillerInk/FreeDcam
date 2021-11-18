package freed.gl.program;

import android.opengl.GLES31;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class ComputeTestProgram extends GLProgram{
    public ComputeTestProgram(int glesVersion) {
        super(glesVersion);
    }

    @Override
    protected void onClear() {
        //super.onClear();
    }

    @Override
    protected void onSetData() {
        //super.onSetData();
    }

    @Override
    protected void onBindTexture() {
        //GLES31.glBindBufferBase(GLES31.GL_SHADER_STORAGE_BUFFER, gIndexBufferBinding, glTex.getId());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw() {
        //super.onDraw();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void compute(int width, int height, int input, int output)
    {
        GLES31.glUseProgram(hProgram);
        GLES31.glBindImageTexture(0, input, 0, false, 0, GLES31.GL_READ_ONLY, GLES31.GL_RGBA8);
        GLES31.glBindImageTexture(1, output, 0, false, 0, GLES31.GL_WRITE_ONLY, GLES31.GL_RGBA8);
        GLES31.glDispatchCompute(width, height, 1);
        GLES31.glMemoryBarrier(GLES31.GL_TEXTURE_UPDATE_BARRIER_BIT);
        GLES31.glMemoryBarrier(GLES31.GL_ALL_SHADER_BITS);
    }
}
