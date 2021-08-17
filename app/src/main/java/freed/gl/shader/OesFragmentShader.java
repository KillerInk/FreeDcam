package freed.gl.shader;

public class OesFragmentShader extends Shader {
    public OesFragmentShader(int glesVersion) {
        super(glesVersion);
    }

    @Override
    String getShaderName() {
        return "oes";
    }

    @Override
    ShaderType getShaderType() {
        return ShaderType.fragment;
    }
}
