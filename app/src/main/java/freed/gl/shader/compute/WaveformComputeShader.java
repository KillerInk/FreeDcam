package freed.gl.shader.compute;

import freed.gl.shader.Shader;

public class WaveformComputeShader extends Shader {
    public WaveformComputeShader(int glesVersion) {
        super(glesVersion);
    }

    @Override
    public String getShaderName() {
        return "computeWaveform";
    }

    @Override
    public ShaderType getShaderType() {
        return ShaderType.compute;
    }
}
