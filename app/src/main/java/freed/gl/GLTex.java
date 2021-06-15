package freed.gl;

import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLES30;

public class GLTex {
    protected int[] hTex;
    protected SurfaceTexture mSTexture;
    private int id;

    public GLTex(int id)
    {
        this.id = id;
        initTex();
        mSTexture = new SurfaceTexture(getHandel());
        setActive();
    }

    public void setActive()
    {
        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + id);

        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, getHandel());
    }

    private void initTex() {
        hTex = new int[1];
        rebind();
    }

    public int[] getTex()
    {
        return hTex;
    }

    private void rebind()
    {
        GLES20.glGenTextures(1, hTex, id);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, hTex[0]);
        GLES20.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
    }

    public int getHandel()
    {
        return hTex[0];
    }

    public SurfaceTexture getmSTexture() {
        return mSTexture;
    }

    public int getId() {
        return id;
    }
}
