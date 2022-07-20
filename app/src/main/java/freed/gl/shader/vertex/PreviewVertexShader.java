package freed.gl.shader.vertex;

import freed.gl.shader.Shader;

public class PreviewVertexShader extends Shader {
    public PreviewVertexShader(float glesVersion) {
        super(glesVersion);
    }

    @Override
    public String getShaderName() {
        return "preview";
    }

    @Override
    public ShaderType getShaderType() {
        return ShaderType.vertex;
    }
}
