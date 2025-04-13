#version 330 core
layout (location = 0) in vec2 aPos;
layout (location = 1) in float radius;

void main() {
    // Hardcoded projection matrix
mat4 projection = mat4(2.0 / 200.0, 0.0, 0.0, 0.0,
                       0.0, 2.0 / 200.0, 0.0, 0.0,
                       0.0, 0.0, -1.0, 0.0,
                       0.0, 0.0, 0.0, 1.0);

    gl_Position = projection * vec4(aPos, 0.0, 1.0);
    gl_PointSize = radius * 2.0;
}