#version 430 core

layout (std430, binding = 0) buffer Particles {
    float data[]; // flat array
};

layout (local_size_x = 256) in;

uniform float deltaTime; // Make sure this is set from the CPU side
uniform uint numParticles;
float damping = 0.9999;
float stopPoint = 0.03;
float drag = 0.999;
float friction = 0.999;

vec2 acceleration = vec2(0.0, -70);

vec2 velocityVerlet(vec2 position, vec2 velocity, vec2 acceleration, float dt, out vec2 newPos) {
    newPos = position + velocity * dt + 0.5 * acceleration * dt * dt;
    return velocity + acceleration * dt;
}


void main() {
    uint id = gl_GlobalInvocationID.x;  // Use uint instead of int
    uint i = id * 5;  // This calculates the index into the data buffer
    
    if (id >= numParticles) return;

    // Fetch current and previous positions from the buffer (data array)
    vec2 position = vec2(data[i], data[i + 1]);
    vec2 velocity = vec2(data[i + 2], data[i + 3]);
    float radius = data[i + 4];

    vec2 newPos;
    vec2 newVel = velocityVerlet(position, velocity, acceleration, deltaTime, newPos);
    position = newPos;
    velocity = newVel;

    if (position.y - radius <  -100) {
        position.y = -100 + radius;
        velocity.y *= -0.9 * friction;
    } else if (position.y + radius > 100) {
        position.y = 100 - radius;
        velocity.y *= -0.9 * friction;
    } else if (position.x + radius > 100) {
        position.x = 100 - radius;
        velocity.x *= - 0.9 * friction;
    } else if (position.x - radius < -100) {
        position.x = -100 + radius;
        velocity.x *= -0.9* friction;
    }
    if (abs(velocity.x) <= stopPoint) velocity.x = 0;
    if (abs(velocity.y) <= stopPoint) velocity.y = 0;

    if (abs(position.y-100) <= stopPoint) position.y = -100;



    
    data[i + 2] = velocity.x;
    data[i + 3] = velocity.y;
    data[i] = position.x;
    data[i + 1] = position.y;
}
