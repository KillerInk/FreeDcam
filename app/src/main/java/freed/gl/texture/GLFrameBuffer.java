package freed.gl.texture;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLES30;

import freed.gl.texture.GLTex;
import freed.utils.Log;

public class GLFrameBuffer {
    private static final String TAG = GLFrameBuffer.class.getSimpleName();
    int[] fbo;
    int[] depth;
    private int depth_id;
    private int id;
    private GLTex texture;
    public GLFrameBuffer()
    {

    }

    public void create()
    {
        fbo = new int[1];
        GLES20.glGenFramebuffers(1, fbo,0);
        this.id = fbo[0];
        Log.d(TAG,"createFrameBuffer " + id);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, id);
    }

    public void setOutputTexture(GLTex texture)
    {
        this.texture = texture;
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, texture.getGLTextureType(), texture.id, 0);
        if (!isSuccessfulLoaded())
            Log.e(TAG, "initFBO failed, status: " + GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER));
    }

    public GLTex getOutputTexture()
    {
        return texture;
    }

    public void setActive()
    {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, id);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        /*GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.id);*/
    }

    public void delete()
    {
        GLES20.glDeleteFramebuffers(1,fbo,id);
    }


    public boolean isSuccessfulLoaded()
    {
        return GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER) == GLES20.GL_FRAMEBUFFER_COMPLETE;
    }

    public int getId() {
        return id;
    }

    public void switchToDefaultFB()
    {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        /*GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);*/
    }
}
