package freed.gl.shader.vertex;

import freed.gl.shader.Shader;

public class OesVertexShader extends Shader {
    public OesVertexShader(float glesVersion) {
        super(glesVersion);
    }

    @Override
    public String getShaderName() {
        return "oes";
    }

    @Override
    public ShaderType getShaderType() {
        return ShaderType.vertex;
    }
}
