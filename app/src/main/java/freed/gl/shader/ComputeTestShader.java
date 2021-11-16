package freed.gl.shader;

public class ComputeTestShader extends Shader {
    public ComputeTestShader(int glesVersion) {
        super(glesVersion);
    }

    @Override
    String getShaderName() {
        return "computeTest";
    }

    @Override
    ShaderType getShaderType() {
        return ShaderType.compute;
    }
}
