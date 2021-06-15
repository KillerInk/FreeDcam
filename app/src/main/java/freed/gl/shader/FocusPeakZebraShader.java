package freed.gl.shader;

public class FocusPeakZebraShader extends Shader {
    public FocusPeakZebraShader(int glesVersion) {
        super(glesVersion);
    }

    @Override
    String getShaderName() {
        return "focuspeak_zebra";
    }

    @Override
    ShaderType getShaderType() {
        return ShaderType.fragment;
    }
}
