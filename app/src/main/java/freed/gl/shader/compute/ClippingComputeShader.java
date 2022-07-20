package freed.gl.shader.compute;

import freed.gl.shader.Shader;

public class ClippingComputeShader extends Shader {
    public ClippingComputeShader(float glesVersion) {
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
