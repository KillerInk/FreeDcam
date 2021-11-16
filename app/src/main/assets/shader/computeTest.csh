#version 310 es

layout(binding = 0) writeonly buffer Output {
    vec4 elements[];
} output_data;
layout(binding = 1) readonly buffer Input {
    vec4 elements[];
} input_data0;

layout (local_size_x = 64, local_size_y = 1, local_size_z = 1) in;

void main() {
    uint storePos = gl_GlobalInvocationID.x;
    output_data.elements[storePos] = input_data0.elements[storePos];
}
