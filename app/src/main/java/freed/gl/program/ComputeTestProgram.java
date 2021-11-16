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
        super.onClear();
    }

    @Override
    protected void onSetData() {
        //super.onSetData();
    }

    private int gIndexBufferBinding = 0;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onBindTexture() {
        GLES31.glBindBufferBase(GLES31.GL_SHADER_STORAGE_BUFFER, gIndexBufferBinding, glTex.getId());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw() {
        GLES31.glDispatchCompute(4, 1, 1);
        GLES31.glBindBufferBase(GLES31.GL_SHADER_STORAGE_BUFFER, gIndexBufferBinding, 0);
        GLES31.glMemoryBarrier(GLES31.GL_VERTEX_ATTRIB_ARRAY_BARRIER_BIT);
    }
}
