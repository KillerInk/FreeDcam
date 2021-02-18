package freed.gl.shader;

public class DefaultVertexShader extends Shader {
    public DefaultVertexShader(int glesVersion) {
        super(glesVersion);
    }

    @Override
    String getShaderName() {
        return "preview";
    }

    @Override
    ShaderType getShaderType() {
        return ShaderType.vertex;
    }
}
