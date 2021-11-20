package freed.gl.shader;

public class FocuspeakComputeShader extends Shader {
    public FocuspeakComputeShader(int glesVersion) {
        super(glesVersion);
    }

    @Override
    String getShaderName() {
        return "computeFocuspeak";
    }

    @Override
    ShaderType getShaderType() {
        return ShaderType.compute;
    }
}
