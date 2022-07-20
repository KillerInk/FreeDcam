package freed.gl.shader.compute;

import freed.gl.shader.Shader;

public class AvgLumaComputeShader extends Shader {
    public AvgLumaComputeShader(float glesVersion) {
        super(glesVersion);
    }

    @Override
    public String getShaderName() {
        return "computeAvgLuma";
    }

    @Override
    public ShaderType getShaderType() {
        return ShaderType.compute;
    }
}
