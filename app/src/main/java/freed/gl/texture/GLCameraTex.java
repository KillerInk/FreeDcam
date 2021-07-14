package freed.gl.texture;

import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLES30;

public class GLCameraTex extends GLTex {
    protected SurfaceTexture mSTexture;

    public GLCameraTex()
    {
        super();
    }

    @Override
    public void create(int width, int height) {
        super.create(width,height);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, id);
        GLES20.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        mSTexture = new SurfaceTexture(id);
        //setActive();
    }

    public SurfaceTexture getSurfaceTexture()
    {
        return mSTexture;
    }
}
