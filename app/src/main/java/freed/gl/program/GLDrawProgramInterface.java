package freed.gl.program;

import freed.gl.shader.Shader;

public interface GLDrawProgramInterface {
    void setVertexShader(Shader vertexShader);
    void setFragmentShader(Shader vertexShader);
    void create();
    void draw();
}
