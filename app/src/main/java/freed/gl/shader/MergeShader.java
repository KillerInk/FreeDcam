package freed.gl.shader;

public class MergeShader extends Shader {
    public MergeShader(int glesVersion) {
        super(glesVersion);
    }

    @Override
    String getShaderName() {
        return "merge";
    }

    @Override
    ShaderType getShaderType() {
        return ShaderType.fragment;
    }
}
