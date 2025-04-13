#version 330 core
out vec4 FragColor;

void main() {
    // gl_PointCoord is a built-in vec2 from (0,0) to (1,1) over the point
    vec2 coord = gl_PointCoord * 2.0 - 1.0; // convert to [-1,1]
    if (dot(coord, coord) > 1.0) discard;  // outside circle

    FragColor = vec4(1.0, 1.0, 1.0, 1.0); // white color
}
