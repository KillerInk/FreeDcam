package freed.gl.shader;

public class OesVertexShader extends Shader {
    public OesVertexShader(int glesVersion) {
        super(glesVersion);
    }

    @Override
    String getShaderName() {
        return "oes";
    }

    @Override
    ShaderType getShaderType() {
        return ShaderType.vertex;
    }
}
