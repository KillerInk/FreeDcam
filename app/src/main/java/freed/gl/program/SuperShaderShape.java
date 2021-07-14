package freed.gl.program;

import android.opengl.GLES20;

import freed.gl.PreviewModel;

public class SuperShaderShape extends OesProgram
{
    int float_position;
    int zebra_low;
    private int zebra_high;
    private int peakh;
    private int peaksh;
    private int texRes;
    private int focuspeak;
    private int zebra;

    public SuperShaderShape(int glesVersion, PreviewModel previewModel) {
        super(glesVersion, previewModel);
    }

    @Override
    public void createAndLinkProgram() {
        super.createAndLinkProgram();
        float_position = GLES20.glGetUniformLocation (hProgram, "float_position");
        zebra_low = GLES20.glGetUniformLocation(hProgram, "zebra_low");
        zebra_high = GLES20.glGetUniformLocation (hProgram, "zebra_high");
        peakh = GLES20.glGetUniformLocation (hProgram, "peak_color");
        peaksh = GLES20.glGetUniformLocation (hProgram, "peak_strength");
        texRes = GLES20.glGetUniformLocation (hProgram, "texRes");
        focuspeak = GLES20.glGetUniformLocation(hProgram, "show_focuspeak");
        zebra = GLES20.glGetUniformLocation (hProgram, "show_zebra");
    }

    @Override
    protected void setData() {
        super.setData();
        if (float_position > 0)
            GLES20.glUniform1f(float_position, previewModel.getFloat_position());

        if (zebra_low > 0)
            GLES20.glUniform1f(zebra_low, previewModel.getZebra_low());

        if (zebra_high > 0)
            GLES20.glUniform1f(zebra_high, previewModel.getZebra_high());

        if (peakh > 0)
            GLES20.glUniform4fv(peakh, 1, previewModel.getPeak_color(),0);

        if (peaksh > 0)
            GLES20.glUniform1f(peaksh, previewModel.getPeak_strength());

        if (texRes > 0)
            GLES20.glUniform2fv(texRes,1, previewModel.getTextSize(),0);

        if (focuspeak > 0)
            GLES20.glUniform1i(focuspeak, previewModel.isFocusPeak() ? 1:0);
        if (zebra > 0)
            GLES20.glUniform1i(zebra, previewModel.isZebra() ? 1:0);
    }
}
