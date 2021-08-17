package freed.gl.shader;

public class ClippingShader extends Shader {
    public ClippingShader(int glesVersion) {
        super(glesVersion);
    }

    @Override
    String getShaderName() {
        return "clipping";
    }

    @Override
    ShaderType getShaderType() {
        return ShaderType.fragment;
    }
}
