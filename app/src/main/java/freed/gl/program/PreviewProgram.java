package freed.gl.program;

import android.opengl.GLES20;

import freed.utils.Log;

public class PreviewProgram extends GLProgram {

    private static final String TAG = PreviewProgram.class.getSimpleName();
    protected int uTexRotateMatrix;

    private float[] mTexRotateMatrix = new float[] {1, 0, 0, 0,   0, 1, 0, 0,   0, 0, 1, 0,   0, 0, 0, 1};

    public PreviewProgram(int glesVersion) {
        super(glesVersion);
    }

    @Override
    public void createAndLinkProgram() {
        super.createAndLinkProgram();
        uTexRotateMatrix = GLES20.glGetUniformLocation (hProgram, "uTexRotateMatrix" );
        checkGlError("uTexRotateMatrix");
    }


    @Override
    protected void onSetData() {
        super.onSetData();
        GLES20.glUniformMatrix4fv(uTexRotateMatrix, 1, false, mTexRotateMatrix, 0);
        checkGlError("set uTexRotateMatrix");
    }

    public void setOrientation(int or)
    {
        Log.d(TAG, "set Orientation to :" + or);
        android.opengl.Matrix.setRotateM(mTexRotateMatrix, 0,  or, 0f, 0f, 1f);
    }

}
