package freed.gl.program;

import android.opengl.GLES20;

import freed.gl.texture.GLTex;

public class MergeProgram extends PreviewProgram {

    private GLTex glTex2;
    private int gltex2_id;

    public MergeProgram(int glesVersion) {
        super(glesVersion);
    }

    public void setGlTex2(GLTex glTex2) {
        this.glTex2 = glTex2;
    }

    @Override
    public void createAndLinkProgram() {
        super.createAndLinkProgram();
        gltex2_id  = GLES20.glGetUniformLocation(hProgram, "sTexture1");
    }

    @Override
    protected void onBindTexture() {
        super.onBindTexture();
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        if (glTex2 != null)
            GLES20.glBindTexture(glTex2.getGLTextureType(), glTex2.getId());
        GLES20.glUniform1i(gltex2_id,1);
    }
}
