package freed.gl.shader.fragment;

import freed.gl.shader.Shader;

public class PreviewFragmentShader extends Shader {
    public PreviewFragmentShader(int glesVersion) {
        super(glesVersion);
    }

    @Override
    public String getShaderName() {
        return "preview";
    }

    @Override
    public ShaderType getShaderType() {
        return ShaderType.fragment;
    }
}
