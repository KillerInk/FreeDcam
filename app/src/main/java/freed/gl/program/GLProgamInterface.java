package freed.gl.program;

import freed.gl.shader.Shader;

public interface GLProgamInterface
{
    void setVertexShader(Shader vertexShader);
    void setFragmentShader(Shader vertexShader);
    void createAndLinkProgram();
    void draw();
    void close();
}
