package freed.gl.shader.compute;

import freed.gl.shader.Shader;

public class FocuspeakComputeShader extends Shader {
    public FocuspeakComputeShader(int glesVersion) {
        super(glesVersion);
    }

    @Override
    public String getShaderName() {
        return "computeFocuspeak";
    }

    @Override
    public ShaderType getShaderType() {
        return ShaderType.compute;
    }
}
