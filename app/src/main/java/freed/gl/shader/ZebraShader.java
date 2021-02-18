package freed.gl.shader;

public class ZebraShader extends Shader {
    public ZebraShader(int glesVersion) {
        super(glesVersion);
    }

    @Override
    String getShaderName() {
        return "zebra";
    }

    @Override
    ShaderType getShaderType() {
        return ShaderType.fragment;
    }
}
