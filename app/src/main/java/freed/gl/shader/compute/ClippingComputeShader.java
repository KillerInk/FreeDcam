package freed.gl.shader.compute;

import freed.gl.shader.Shader;

public class ClippingComputeShader extends Shader {
    public ClippingComputeShader(int glesVersion) {
        super(glesVersion);
    }

    @Override
    public String getShaderName() {
        return "computeClipping";
    }

    @Override
    public ShaderType getShaderType() {
        return ShaderType.compute;
    }
}
