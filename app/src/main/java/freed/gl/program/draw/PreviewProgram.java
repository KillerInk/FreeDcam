package freed.gl.program.draw;

import android.opengl.GLES20;

import freed.gl.program.GLDrawProgram;

public class PreviewProgram extends GLDrawProgram {

    private static final String TAG = PreviewProgram.class.getSimpleName();
    protected int uTexRotateMatrix;
    private int orientaion;
    private int doMirror_id;
    private int doMirror = 1;

    private float[] mTexRotateMatrix = new float[] {1, 0, 0, 0,   0, 1, 0, 0,   0, 0, 1, 0,   0, 0, 0, 1};

    public PreviewProgram(float glesVersion) {
        super(glesVersion);
    }

    @Override
    public void createAndLinkProgram() {
        super.createAndLinkProgram();
        uTexRotateMatrix = GLES20.glGetUniformLocation (hProgram, "uTexRotateMatrix" );
        doMirror_id = GLES20.glGetUniformLocation (hProgram, "doMirror" );
        checkGlError("uTexRotateMatrix");
    }




    @Override
    protected void onSetData() {
        super.onSetData();
        GLES20.glUniformMatrix4fv(uTexRotateMatrix, 1, false, mTexRotateMatrix, 0);
        GLES20.glUniform1i(doMirror_id, doMirror);
        checkGlError("set uTexRotateMatrix");
    }

    public void setOrientation(int or)
    {
        this.orientaion = or;
        android.opengl.Matrix.setRotateM(mTexRotateMatrix, 0,  or, 0f, 0f, 1f);
    }

    public void inverseOrientation(boolean rotate)
    {
        if (rotate) {
            android.opengl.Matrix.setRotateM(mTexRotateMatrix, 0, orientaion, 0f, 0f, 1f);
            doMirror = 1;
        }
        else {
            android.opengl.Matrix.setRotateM(mTexRotateMatrix, 0, orientaion - 180, 0f, 0f, 1f);
            doMirror = 0;
        }
    }

    public int getOrientaion() {
        return orientaion;
    }

    @Override
    protected void onClear() {
        //super.onClear();
    }

    public void doClear()
    {
        super.onClear();
    }
}
