package freed.gl.program;

import freed.gl.GLTex;
import freed.gl.shader.Shader;

public interface GLProgamInterface
{
    void setVertexShader(Shader vertexShader);
    void setFragmentShader(Shader vertexShader);
    void setGlTex(GLTex glTex);
    void createAndLinkProgram();
    void draw();
    void close();
}
