package freed.gl.shader.compute;

import freed.gl.shader.Shader;

public class HistogramShader extends Shader {
    public HistogramShader(int glesVersion) {
        super(glesVersion);
    }

    @Override
    public String getShaderName() {
        return "computeHistogram";
    }

    @Override
    public ShaderType getShaderType() {
        return ShaderType.compute;
    }
}
