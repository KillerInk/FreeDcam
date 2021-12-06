package freed.gl.texture;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES11Ext;
import android.opengl.GLES31;
import android.opengl.GLES31;
import android.opengl.GLUtils;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class GL2DTex extends GLTex {
    public GL2DTex()
    {
        super();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void create(int width, int height) {
        super.create(width,height);
        GLES31.glBindTexture(GLES31.GL_TEXTURE_2D, id);
        //GLES31.glTexImage2D(GLES31.GL_TEXTURE_2D, 0, GLES31.GL_RGBA, width, height, 0, GLES31.GL_RGBA, GLES31.GL_UNSIGNED_BYTE, null);
        GLES31.glTexStorage2D(GLES31.GL_TEXTURE_2D, 1, GLES31.GL_RGBA8, width, height);
        GLES31.glTexParameterf(GLES31.GL_TEXTURE_2D, GLES31.GL_TEXTURE_MIN_FILTER, GLES31.GL_LINEAR);
        GLES31.glTexParameterf(GLES31.GL_TEXTURE_2D, GLES31.GL_TEXTURE_MAG_FILTER, GLES31.GL_LINEAR);
        GLES31.glTexParameteri(GLES31.GL_TEXTURE_2D, GLES31.GL_TEXTURE_WRAP_S, GLES31.GL_CLAMP_TO_EDGE);
        GLES31.glTexParameteri(GLES31.GL_TEXTURE_2D, GLES31.GL_TEXTURE_WRAP_T, GLES31.GL_CLAMP_TO_EDGE);
        Bitmap bitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        GLUtils.texSubImage2D(GLES31.GL_TEXTURE_2D, 0, 0, 0, bitmap);
        bitmap.recycle();
    }

    @Override
    public int getGLTextureType() {
        return GLES31.GL_TEXTURE_2D;
    }
}
