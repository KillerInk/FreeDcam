package freed.gl.shader;

public class FocuspeakShader extends Shader {
    public FocuspeakShader(int glesVersion) {
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
