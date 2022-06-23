package freed.gl.shader;

import android.opengl.GLES31;

import java.io.IOException;

public abstract class Shader<T extends Shader> {

    public enum ShaderType
    {
        vertex,
        fragment,
        compute,
    }

    private int handel;
    private float glesVersion;

    public Shader(float glesVersion)
    {
        this.glesVersion = glesVersion;
        createShader();
    }

    private void createShader()
    {
        if (getShaderType() == ShaderType.vertex)
            handel = ShaderUtil.createShader(loadShader(), getShaderName() + " vertex", GLES31.GL_VERTEX_SHADER);
        else if (getShaderType() == ShaderType.compute)
            handel = ShaderUtil.createShader(loadShader(), getShaderName() + " vertex", GLES31.GL_COMPUTE_SHADER);
        else
            handel = ShaderUtil.createShader(loadShader(),getShaderName() +" fragment",GLES31.GL_FRAGMENT_SHADER);
    }

    private String loadShader()
    {
        try {
            return ShaderUtil.getShader(glesVersion,getShaderName(),getShaderType());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public abstract String getShaderName();
    public abstract ShaderType getShaderType();

    public int getHandel() {
        return handel;
    }
}
