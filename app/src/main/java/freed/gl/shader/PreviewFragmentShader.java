package freed.gl.shader;

public class PreviewFragmentShader extends Shader {
    public PreviewFragmentShader(int glesVersion) {
        super(glesVersion);
    }

    @Override
    String getShaderName() {
        return "preview";
    }

    @Override
    ShaderType getShaderType() {
        return ShaderType.fragment;
    }
}
