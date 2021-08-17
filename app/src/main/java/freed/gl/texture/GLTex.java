package freed.gl.texture;

import android.opengl.GLES20;

import freed.utils.Log;

public abstract class GLTex {
    private final  String TAG = GLTex.class.getSimpleName();
    protected int[] hTex;
    protected int id;
    public GLTex()
    {
    }

    public void create(int width, int height)
    {
        Log.d(TAG,"create " + width +"/" + height);
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

    public void delete()
    {
        Log.d(TAG,"delete");
        GLES20.glDeleteTextures(1,hTex,0);
    }
}
