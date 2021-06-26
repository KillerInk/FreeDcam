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
    }
}
