package freed.gl.shader.fragment;

import freed.gl.shader.Shader;

public class OesFragmentShader extends Shader {
    public OesFragmentShader(int glesVersion) {
        super(glesVersion);
    }

    @Override
    public String getShaderName() {
        return "oes";
    }

    @Override
    public ShaderType getShaderType() {
        return ShaderType.fragment;
    }
}
