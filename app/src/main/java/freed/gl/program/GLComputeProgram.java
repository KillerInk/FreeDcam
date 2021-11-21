package freed.gl.program;

import android.opengl.GLES31;

import freed.gl.shader.Shader;

public abstract class GLComputeProgram extends GLProgram implements GLComputeProgramInterace{

    private Shader computeShader;

    public GLComputeProgram(int glesVersion) {
        super(glesVersion);
    }

    @Override
    public void setComputeShader(Shader shader) {
        this.computeShader = shader;
        createAndLinkProgram();
    }

    @Override
    public void compute(int width, int height, int input, int output) {

    }

    @Override
    public void createAndLinkProgram() {
        super.createAndLinkProgram();
        if (computeShader != null) {
            GLES31.glAttachShader(hProgram, computeShader.getHandel());
            checkGlError("glAttachShader compute");
        }
        GLES31.glLinkProgram(hProgram);
        checkGlError("glLinkProgram");
    }

}
