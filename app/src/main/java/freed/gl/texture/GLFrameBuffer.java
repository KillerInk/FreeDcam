package freed.gl.texture;

import android.opengl.GLES11Ext;
import android.opengl.GLES31;
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
        GLES31.glGenFramebuffers(1, fbo,0);
        this.id = fbo[0];
        Log.d(TAG,"createFrameBuffer " + id);
        GLES31.glBindFramebuffer(GLES31.GL_FRAMEBUFFER, id);
    }

    public void setOutputTexture(GLTex texture)
    {
        this.texture = texture;
        GLES31.glFramebufferTexture2D(GLES31.GL_FRAMEBUFFER, GLES31.GL_COLOR_ATTACHMENT0, texture.getGLTextureType(), texture.id, 0);
        if (!isSuccessfulLoaded())
            Log.e(TAG, "initFBO failed, status: " + GLES31.glCheckFramebufferStatus(GLES31.GL_FRAMEBUFFER));
    }

    public GLTex getOutputTexture()
    {
        return texture;
    }

    public void setActive()
    {
        GLES31.glBindFramebuffer(GLES31.GL_FRAMEBUFFER, id);
        GLES31.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES31.glClear(GLES31.GL_COLOR_BUFFER_BIT);
        /*GLES31.glActiveTexture(GLES31.GL_TEXTURE0);
        GLES31.glBindTexture(GLES31.GL_TEXTURE_2D, texture.id);*/
    }

    public void delete()
    {
        GLES31.glDeleteFramebuffers(1,fbo,0);
    }


    public boolean isSuccessfulLoaded()
    {
        return GLES31.glCheckFramebufferStatus(GLES31.GL_FRAMEBUFFER) == GLES31.GL_FRAMEBUFFER_COMPLETE;
    }

    public int getId() {
        return id;
    }

    public static void switchToDefaultFB()
    {
        GLES31.glBindFramebuffer(GLES31.GL_FRAMEBUFFER, 0);
        GLES31.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES31.glClear(GLES31.GL_COLOR_BUFFER_BIT | GLES31.GL_DEPTH_BUFFER_BIT);
        /*GLES31.glActiveTexture(GLES31.GL_TEXTURE0);
        GLES31.glBindTexture(GLES31.GL_TEXTURE_2D, 0);*/
    }
}
