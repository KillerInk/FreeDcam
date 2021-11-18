package freed.gl.program;

import android.opengl.GLES31;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class ComputeTestProgram extends GLProgram{
    public ComputeTestProgram(int glesVersion) {
        super(glesVersion);
    }

    int width = 1920;
    int height = 1080;

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    protected void onClear() {
        //super.onClear();
    }

    @Override
    protected void onSetData() {
        //super.onSetData();
    }

    private int gIndexBufferBinding = 0;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onBindTexture() {
        //GLES31.glBindBufferBase(GLES31.GL_SHADER_STORAGE_BUFFER, gIndexBufferBinding, glTex.getId());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw() {
        //super.onDraw();
        GLES31.glDispatchCompute(width, height, 1);
        GLES31.glMemoryBarrier(GLES31.GL_TEXTURE_UPDATE_BARRIER_BIT);

        GLES31.glMemoryBarrier(GLES31.GL_ALL_SHADER_BITS);
    }
}
