package freed.gl.texture;

import android.opengl.GLES20;

public abstract class GLTex {
    protected int[] hTex;
    protected int id;
    public GLTex()
    {
    }

    public void create(int width, int height)
    {
        hTex = new int[1];
        GLES20.glGenTextures(1, hTex, 0);
        this.id = hTex[0];
    }

    public int[] getTex()
    {
        return hTex;
    }
    public int getId() {
        return id;
    }

    public abstract int getGLTextureType();
}
