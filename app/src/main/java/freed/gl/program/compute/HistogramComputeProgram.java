package freed.gl.program.compute;

import static android.opengl.GLES30.GL_MAP_INVALIDATE_RANGE_BIT;
import static android.opengl.GLES30.GL_MAP_READ_BIT;
import static android.opengl.GLES30.GL_MAP_UNSYNCHRONIZED_BIT;
import static android.opengl.GLES30.GL_MAP_WRITE_BIT;

import android.opengl.GLES20;
import android.opengl.GLES31;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.nio.Buffer;

import freed.gl.program.GLComputeProgram;
import freed.gl.program.GLProgram;
import freed.gl.texture.GLFrameBuffer;
import freed.gl.texture.HistoSSBO;

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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void computeFB(int width, int height, GLFrameBuffer input, HistoSSBO r,HistoSSBO g,HistoSSBO b)
    {
        GLES31.glBindBuffer(GLES31.GL_SHADER_STORAGE_BUFFER,0);

        GLES31.glUseProgram(hProgram);
        GLES31.glBindImageTexture(0, input.getOutputTexture().getId(), 0, false, 0, GLES31.GL_READ_ONLY, GLES31.GL_RGBA8);
        checkGlError("imagetex bind input");
        GLES31.glBindBufferBase(GLES31.GL_SHADER_STORAGE_BUFFER, 0, input.getId());
        checkGlError("GL_SHADER_STORAGE_BUFFER bind input to 0");

        GLES31.glBindBuffer(GLES31.GL_SHADER_STORAGE_BUFFER,r.getId());
        checkGlError("GL_SHADER_STORAGE_BUFFER bind histogram r");
        GLES31.glBindBufferBase(GLES31.GL_SHADER_STORAGE_BUFFER, 1, r.getId());
        checkGlError("GL_SHADER_STORAGE_BUFFER bind histogram r to 1");

        GLES31.glBindBuffer(GLES31.GL_SHADER_STORAGE_BUFFER,g.getId());
        checkGlError("GL_SHADER_STORAGE_BUFFER bind histogram g");
        GLES31.glBindBufferBase(GLES31.GL_SHADER_STORAGE_BUFFER, 2, g.getId());
        checkGlError("GL_SHADER_STORAGE_BUFFER bind histogram g to 2");

        GLES31.glBindBuffer(GLES31.GL_SHADER_STORAGE_BUFFER,b.getId());
        checkGlError("GL_SHADER_STORAGE_BUFFER bind histogram b");
        GLES31.glBindBufferBase(GLES31.GL_SHADER_STORAGE_BUFFER, 3, b.getId());
        checkGlError("GL_SHADER_STORAGE_BUFFER bind histogram b to 3");

        GLES31.glDispatchCompute(width, height, 1);
        checkGlError("compute");
        GLES31.glMemoryBarrier(GLES31.GL_TEXTURE_UPDATE_BARRIER_BIT);
        GLES31.glMemoryBarrier(GLES31.GL_ALL_SHADER_BITS);


        GLES31.glBindBuffer(GLES31.GL_SHADER_STORAGE_BUFFER,0);
        //GLES31.glBindBuffer(GLES31.GL_SHADER_STORAGE_BUFFER,0);

        GLProgram.checkGlError("getBuffer");
    }



}
