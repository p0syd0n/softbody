#version 430 core

layout(std430, binding = 0) buffer Particles {
    float data[]; // x, y, x_prev, y_prev, radius, repeated per particle
};

void main() {
    int i = gl_VertexID * 5; // 5 floats per particle: x, y, prev_x, prev_y, radius

    vec2 aPos = vec2(data[i], data[i + 1]);
    float radius = data[i + 4];

    mat4 projection = mat4(
        2.0 / 200.0, 0.0,        0.0, 0.0,
        0.0,        2.0 / 200.0, 0.0, 0.0,
        0.0,        0.0,        -1.0, 0.0,
        0.0,        0.0,         0.0, 1.0
    );

    gl_Position = projection * vec4(aPos, 0.0, 1.0);
    gl_PointSize = radius * 2.0;
}
