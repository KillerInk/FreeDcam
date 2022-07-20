package freed.gl.texture;

import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES30;
import android.opengl.GLES31;

public class GLCameraTex extends GLTex {
    protected SurfaceTexture mSTexture;

    public GLCameraTex()
    {
        super();
    }

    @Override
    public void create(int width, int height) {
        super.create(width,height);
        GLES31.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, id);
        GLES31.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES31.GL_TEXTURE_WRAP_S, GLES31.GL_CLAMP_TO_EDGE);
        GLES31.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES31.GL_TEXTURE_WRAP_T, GLES31.GL_CLAMP_TO_EDGE);
        GLES31.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES31.GL_TEXTURE_MIN_FILTER, GLES31.GL_LINEAR);
        GLES31.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES31.GL_TEXTURE_MAG_FILTER, GLES31.GL_LINEAR);
        mSTexture = new SurfaceTexture(id);
        //setActive();
    }

    @Override
    public int getGLTextureType() {
        return GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
    }

    public SurfaceTexture getSurfaceTexture()
    {
        return mSTexture;
    }

}
