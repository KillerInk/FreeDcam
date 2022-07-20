package freed.gl.program;

import android.opengl.GLES31;

import freed.utils.Log;

public abstract class GLProgram implements GLProgamInterface {
    protected float glesVersion;
    protected int hProgram = -1;

    private final static String TAG = GLProgram.class.getSimpleName();



    public GLProgram(float glesVersion)
    {
        this.glesVersion = glesVersion;
    }


    @Override
    public void createAndLinkProgram()
    {
        hProgram = GLES31.glCreateProgram();
        checkGlError("glCreateProgram");
    }

    @Override
    public void close() {
        Log.d(TAG, "close program id:" +hProgram);
        GLES31.glDeleteProgram(hProgram);
        checkGlError("glDeleteProgram");
        hProgram = -1;
    }



    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES31.glGetError()) != GLES31.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + getGLErrorString(error));
            //throw new RuntimeException(glOperation + ": glError " + getGLErrorString(error));
        }
    }

    private static String getGLErrorString(int error)
    {
        switch (error)
        {
            case GLES31.GL_INVALID_ENUM:
                return "GL_INVALID_ENUM";
                case GLES31.GL_INVALID_VALUE:
                return "GL_INVALID_VALUE";
                case GLES31.GL_INVALID_OPERATION:
                return "GL_INVALID_OPERATION";
                case GLES31.GL_OUT_OF_MEMORY:
                return "GL_OUT_OF_MEMORY";
            default:
                return  error+"";
        }
    }
}
