package freed.gl.shader;

public class SuperShader extends Shader {
    public SuperShader(int glesVersion) {
        super(glesVersion);
    }

    @Override
    String getShaderName() {
        return "supershader";
    }

    @Override
    ShaderType getShaderType() {
        return ShaderType.fragment;
    }
}
