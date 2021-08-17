package freed.gl.shader;

public class PreviewVertexShader extends Shader{
    public PreviewVertexShader(int glesVersion) {
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
