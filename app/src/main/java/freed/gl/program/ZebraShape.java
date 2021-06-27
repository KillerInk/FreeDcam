package freed.gl.program;

import android.opengl.GLES20;

import freed.gl.PreviewModel;

public class ZebraShape extends PreviewShape {
    public ZebraShape(int glesVersion, PreviewModel previewModel) {
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
    }
}
