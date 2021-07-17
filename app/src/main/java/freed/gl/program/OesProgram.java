package freed.gl.program;

import android.opengl.GLES20;

import freed.gl.PreviewModel;


public class OesProgram extends GLProgram
{

    protected final PreviewModel previewModel;
    protected int uTexRotateMatrix;
    protected int vTexCoord;
    protected int vPosition;
    protected int sTexture;

    public OesProgram(int glesVersion, PreviewModel previewModel)
    {
        super(glesVersion);
        this.previewModel = previewModel;
    }

    @Override
    public void createAndLinkProgram() {
        super.createAndLinkProgram();
        uTexRotateMatrix = GLES20.glGetUniformLocation (hProgram, "uTexRotateMatrix" );
        vPosition = GLES20.glGetAttribLocation(hProgram, "vPosition");
        vTexCoord = GLES20.glGetAttribLocation(hProgram, "vTexCoord");
        sTexture = GLES20.glGetAttribLocation(hProgram, "sTexture");
    }

    @Override
    public void draw()
    {
        super.draw();
    }

    @Override
    protected void onDraw() {
        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glEnableVertexAttribArray(vTexCoord);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisableVertexAttribArray(vPosition);
        GLES20.glDisableVertexAttribArray(vTexCoord);
    }

    @Override
    protected void onSetData() {
        GLES20.glUniformMatrix4fv(uTexRotateMatrix, 1, false, previewModel.getmTexRotateMatrix(), 0);
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 4 * 2, vertexBuffer);
        GLES20.glVertexAttribPointer(vTexCoord, 2, GLES20.GL_FLOAT, false, 4 * 2, textureBuffer);
    }
}
