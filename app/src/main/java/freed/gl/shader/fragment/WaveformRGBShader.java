package freed.gl.shader.fragment;

import freed.gl.shader.Shader;

public class WaveformRGBShader extends Shader {
    public WaveformRGBShader(int glesVersion) {
        super(glesVersion);
    }

    @Override
    public String getShaderName() {
        return "waveform_rgb";
    }

    @Override
    public ShaderType getShaderType() {
        return ShaderType.fragment;
    }
}
