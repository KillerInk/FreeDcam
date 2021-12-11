package freed.gl.program.compute;

import android.opengl.GLES20;
import android.opengl.GLES31;
import android.os.Build;

import androidx.annotation.RequiresApi;

import freed.gl.program.GLComputeProgram;
import freed.gl.program.GLProgram;
import freed.gl.texture.GLFrameBuffer;
import freed.gl.texture.SharedStorageBufferObject;


public class WaveformComputeProgam extends GLComputeProgram {

    private int show_color = 0;
    int show_color_id;

    public WaveformComputeProgam(int glesVersion) {
        super(glesVersion);
    }

    @Override
    public void createAndLinkProgram() {
        super.createAndLinkProgram();
        show_color_id = GLES20.glGetUniformLocation(hProgram, "show_color");
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void compute(int width, int height, GLFrameBuffer input, SharedStorageBufferObject waveformout)
    {
        GLES31.glBindBuffer(GLES31.GL_SHADER_STORAGE_BUFFER,0);

        GLES31.glUseProgram(hProgram);
        GLES20.glUniform1i(show_color_id, show_color);
        GLES31.glBindImageTexture(0, input.getOutputTexture().getId(), 0, false, 0, GLES31.GL_READ_ONLY, GLES31.GL_RGBA8);
        checkGlError("imagetex bind input");
        GLES31.glBindBufferBase(GLES31.GL_SHADER_STORAGE_BUFFER, 0, input.getId());
        checkGlError("GL_SHADER_STORAGE_BUFFER bind input to 0");

        GLES31.glBindBuffer(GLES31.GL_SHADER_STORAGE_BUFFER,waveformout.getId());
        checkGlError("GL_SHADER_STORAGE_BUFFER bind waveform");
        GLES31.glBindBufferBase(GLES31.GL_SHADER_STORAGE_BUFFER, 1, waveformout.getId());
        checkGlError("GL_SHADER_STORAGE_BUFFER bind waveform to 1");


        GLES31.glDispatchCompute(width, height, 1);
        checkGlError("compute");
        GLES31.glMemoryBarrier(GLES31.GL_TEXTURE_UPDATE_BARRIER_BIT);
        GLES31.glMemoryBarrier(GLES31.GL_ALL_SHADER_BITS);


        GLES31.glBindBuffer(GLES31.GL_SHADER_STORAGE_BUFFER,0);
        //GLES31.glBindBuffer(GLES31.GL_SHADER_STORAGE_BUFFER,0);

        GLProgram.checkGlError("getBuffer");
    }

    public void setColorWaveForm(boolean on)
    {
        this.show_color++;
        if (show_color == 3)
            show_color = 0;
    }
}
