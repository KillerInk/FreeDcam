package freed.gl.shader;

public class FpFragmentShader extends Shader {
    public FpFragmentShader(int glesVersion) {
        super(glesVersion);
    }

    @Override
    String getShaderName() {
        return "focuspeak";
    }

    @Override
    ShaderType getShaderType() {
        return ShaderType.fragment;
    }
}
