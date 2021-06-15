package freed.gl.program;

import android.opengl.GLES20;

import freed.gl.PreviewModel;

public class FPShape extends PreviewShape {


    public FPShape(int glesVersion, PreviewModel previewModel) {
        super(glesVersion, previewModel);
    }

    @Override
    protected void bind() {
        super.bind();
        int peakh = GLES20.glGetUniformLocation (hProgram, "peak_color");
        if (peakh > 0)
            GLES20.glUniform4fv(peakh, 1, previewModel.getPeak_color(),0);

        int peaksh = GLES20.glGetUniformLocation (hProgram, "peak_strength");
        if (peaksh > 0)
            GLES20.glUniform1f(peaksh, previewModel.getPeak_strength());

        int texSizeh = GLES20.glGetUniformLocation (hProgram, "texRes");
        if (texSizeh > 0)
            GLES20.glUniform2fv(texSizeh,1, previewModel.getTextSize(),0);
    }
}
