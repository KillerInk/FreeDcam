package freed.gl.shader;

public class WaveformRGBShader extends Shader {
    public WaveformRGBShader(int glesVersion) {
        super(glesVersion);
    }

    @Override
    String getShaderName() {
        return "waveform_rgb";
    }

    @Override
    ShaderType getShaderType() {
        return ShaderType.fragment;
    }
}
