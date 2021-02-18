package freed.gl.shader;

import android.opengl.GLES20;

import java.io.IOException;

import freed.gl.ShaderUtil;

public abstract class Shader {

    public enum ShaderType
    {
        vertex,
        fragment,
    }

    private int handel;
    private int glesVersion;

    public Shader(int glesVersion)
    {
        this.glesVersion = glesVersion;
    }

    public void createShader()
    {
        if (getShaderType() == ShaderType.vertex)
            handel = ShaderUtil.createShader(loadShader(), getShaderName() + " vertex", GLES20.GL_VERTEX_SHADER);
        else
            handel = ShaderUtil.createShader(loadShader(),getShaderName() +" fragment",GLES20.GL_FRAGMENT_SHADER);
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

    abstract String getShaderName();
    abstract ShaderType getShaderType();

    public int getHandel() {
        return handel;
    }
}
