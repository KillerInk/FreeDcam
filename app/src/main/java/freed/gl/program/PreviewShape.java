package freed.gl.program;

import android.opengl.GLES20;

import freed.gl.PreviewModel;


public class PreviewShape extends GLProgram
{

    protected final PreviewModel previewModel;

    public PreviewShape(int glesVersion, PreviewModel previewModel)
    {
        super(glesVersion);
        this.previewModel = previewModel;
    }

    @Override
    public void createAndLinkProgram() {
        super.createAndLinkProgram();
        bind();
    }

    protected void bind()
    {
        int trmh = GLES20.glGetUniformLocation (hProgram, "uTexRotateMatrix" );
        GLES20.glUniformMatrix4fv(trmh, 1, false, previewModel.getmTexRotateMatrix(), 0);

        int ph = GLES20.glGetAttribLocation(hProgram, "vPosition");
        GLES20.glVertexAttribPointer(ph, 2, GLES20.GL_FLOAT, false, 4 * 2, previewModel.getpVertex());

        int tch = GLES20.glGetAttribLocation(hProgram, "vTexCoord");
        GLES20.glVertexAttribPointer(tch, 2, GLES20.GL_FLOAT, false, 4 * 2, previewModel.getpTexCoord());

        GLES20.glEnableVertexAttribArray(ph);
        GLES20.glEnableVertexAttribArray(tch);
    }



    @Override
    public void draw()
    {
        super.draw();
        bind();

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }
}
