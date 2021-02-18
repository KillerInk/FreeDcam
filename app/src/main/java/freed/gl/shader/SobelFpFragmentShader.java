package freed.gl.shader;

public class SobelFpFragmentShader extends PreviewFragmentShader {
    public SobelFpFragmentShader(int glesVersion) {
        super(glesVersion);
    }

    @Override
    String getShaderName() {
        return "focuspeak_sobel";
    }

    @Override
    ShaderType getShaderType() {
        return ShaderType.fragment;
    }


}
