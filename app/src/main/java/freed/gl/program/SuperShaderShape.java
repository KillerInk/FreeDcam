package freed.gl.program;

import android.opengl.GLES20;

import freed.gl.PreviewModel;

public class SuperShaderShape extends PreviewShape
{

    public SuperShaderShape(int glesVersion, PreviewModel previewModel) {
        super(glesVersion, previewModel);
    }

    @Override
    protected void bind() {
        super.bind();
        int float_pos = GLES20.glGetUniformLocation (hProgram, "float_position");
        if (float_pos > 0)
            GLES20.glUniform1f(float_pos, previewModel.getFloat_position());

        int zebra_low = GLES20.glGetUniformLocation (hProgram, "zebra_low");
        if (zebra_low > 0)
            GLES20.glUniform1f(zebra_low, previewModel.getZebra_low());

        int zebra_high = GLES20.glGetUniformLocation (hProgram, "zebra_high");
        if (zebra_high > 0)
            GLES20.glUniform1f(zebra_high, previewModel.getZebra_high());

        int peakh = GLES20.glGetUniformLocation (hProgram, "peak_color");
        if (peakh > 0)
            GLES20.glUniform4fv(peakh, 1, previewModel.getPeak_color(),0);

        int peaksh = GLES20.glGetUniformLocation (hProgram, "peak_strength");
        if (peaksh > 0)
            GLES20.glUniform1f(peaksh, previewModel.getPeak_strength());

        int texSizeh = GLES20.glGetUniformLocation (hProgram, "texRes");
        if (texSizeh > 0)
            GLES20.glUniform2fv(texSizeh,1, previewModel.getTextSize(),0);

        int focuspeak = GLES20.glGetUniformLocation (hProgram, "show_focuspeak");
        if (focuspeak > 0)
            GLES20.glUniform1i(focuspeak, previewModel.isFocusPeak() ? 1:0);

        int zebra = GLES20.glGetUniformLocation (hProgram, "show_zebra");
        if (zebra > 0)
            GLES20.glUniform1i(zebra, previewModel.isZebra() ? 1:0);
    }
}
