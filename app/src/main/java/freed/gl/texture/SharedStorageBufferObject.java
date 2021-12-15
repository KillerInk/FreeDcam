package freed.gl.texture;



import static freed.gl.program.GLProgram.checkGlError;

import android.opengl.GLES31;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Arrays;

import freed.gl.program.GLProgram;

public class SharedStorageBufferObject {
    IntBuffer ssbo;
    private IntBuffer pixelBuffer;
    private int size_in_bytes;
    private int size;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void create(int index, int size)
    {
        this.size = size;
        size_in_bytes = size*4;
        ByteBuffer b = ByteBuffer.allocateDirect(size_in_bytes);
        b.order(ByteOrder.nativeOrder());
        pixelBuffer = b.asIntBuffer();
        ssbo = IntBuffer.allocate(1);
        GLES31.glGenBuffers(1,ssbo);
        GLES31.glBindBuffer(GLES31.GL_SHADER_STORAGE_BUFFER, ssbo.array()[0]);
        GLES31.glBufferData(GLES31.GL_SHADER_STORAGE_BUFFER, size_in_bytes, pixelBuffer, GLES31.GL_STATIC_DRAW);
        GLES31.glBindBufferBase(GLES31.GL_SHADER_STORAGE_BUFFER, index, ssbo.array()[0]);
        GLES31.glBindBuffer(GLES31.GL_SHADER_STORAGE_BUFFER, 0);
        checkGlError("create SharedStorageBufferObject");
    }

    public int getId()
    {
        return ssbo.array()[0];
    }

    public void delete()
    {
        GLES31.glDeleteBuffers(1,ssbo);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public int[] getHistogramChannel()
    {
        int[] red = null;
        GLES31.glBindBuffer(GLES31.GL_SHADER_STORAGE_BUFFER,getId());
        GLProgram.checkGlError("bind ssbo");
        ByteBuffer buffer = (ByteBuffer)GLES31.glMapBufferRange(GLES31.GL_SHADER_STORAGE_BUFFER,0, size_in_bytes,GLES31.GL_MAP_READ_BIT);
        GLProgram.checkGlError("getBuffer");
        if (buffer != null)
        {
            buffer.order(ByteOrder.nativeOrder());
            IntBuffer intbuf = buffer.asIntBuffer();
            red = toArray(intbuf);
        }
        GLES31.glUnmapBuffer(GLES31.GL_SHADER_STORAGE_BUFFER);
        GLES31.glBindBuffer(GLES31.GL_SHADER_STORAGE_BUFFER,0);
        return red;
    }

    public int[] toArray(IntBuffer b) {
        if(b.hasArray()) {
            if(b.arrayOffset() == 0)
                return b.array();

            return Arrays.copyOfRange(b.array(), b.arrayOffset(), b.array().length);
        }

        b.rewind();
        int[] foo = new int[b.remaining()];
        b.get(foo);

        return foo;
    }

    public void clearBuffer()
    {
        pixelBuffer.clear();
        GLES31.glBindBuffer(GLES31.GL_SHADER_STORAGE_BUFFER, ssbo.array()[0]);
        GLES31.glBufferData(GLES31.GL_SHADER_STORAGE_BUFFER, size_in_bytes, pixelBuffer, GLES31.GL_STATIC_DRAW);
        GLES31.glBindBuffer(GLES31.GL_SHADER_STORAGE_BUFFER, 0);
    }
}
