package freed.gl.shader;

public class ClippingComputeShader extends Shader{
    public ClippingComputeShader(int glesVersion) {
        super(glesVersion);
    }

    @Override
    String getShaderName() {
        return "computeClipping";
    }

    @Override
    ShaderType getShaderType() {
        return ShaderType.compute;
    }
}
