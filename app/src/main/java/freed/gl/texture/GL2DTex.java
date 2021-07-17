package freed.gl.texture;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;

public class GL2DTex extends GLTex {
    public GL2DTex()
    {
        super();
    }

    @Override
    public void create(int width, int height) {
        super.create(width,height);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
    }

    @Override
    public int getGLTextureType() {
        return GLES20.GL_TEXTURE_2D;
    }
}
