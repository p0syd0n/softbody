package com.posydon.softbody;

import java.nio.*;
import java.util.Arrays;

import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.opengl.GL42.GL_ALL_BARRIER_BITS;
import static org.lwjgl.opengl.GL42.glMemoryBarrier;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_POINTS;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_COPY;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL15.glGetBufferSubData;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindBufferBase;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL30.glUniform1ui;

import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

public class ParticleSystem {
    public static int VAO;
	public static int ssbo;
    public static int renderProgram;
	public static int computeProgram;
	public static int computeShader;
	private static float[] vertices;
	private static int workGroups;

    public ParticleSystem() {
        // Step 1: Create and bind the Vertex Array Object (VAO)
        Log.info("Creating and binding VAO");
        VAO = glGenVertexArrays();
        glBindVertexArray(VAO);
        Log.info("VAO created and bound with ID: " + VAO);

        // Step 2: Create and bind the Shader Storage Buffer Object (SSBO)
        Log.info("Creating and binding SSBO");
        ssbo = glGenBuffers();
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);
        Log.info("SSBO created and bound with ID: " + ssbo);

        // Step 3: Define vertices (positions and radius)
        Log.info("Defining particle vertices");
        vertices = new float[]{
            0f, 0f, 50f, 20f, 5f
        };
        Log.info("Vertices array: " + Arrays.toString(vertices));

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer vertexBuffer = stack.mallocFloat(vertices.length);
            vertexBuffer.put(vertices).flip();

            // Step 4: Upload vertices data to GPU buffer (SSBO)
            Log.info("Uploading vertex data to SSBO");
            glBufferData(GL_SHADER_STORAGE_BUFFER, vertexBuffer, GL_DYNAMIC_COPY);
            Log.info("Vertex data uploaded successfully");
        }

        // Step 5: Bind SSBO to binding point 0
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, ssbo);
        Log.info("SSBO bound to binding point 0");

        // Step 6: Load and compile shaders
        Log.info("Loading and compiling shaders: vertex");
        String vertexShaderSource = Resources.loadShaderFromResource("shaders/particles/vertex_particle.glsl");
        int vertexShader = Resources.compileShader(vertexShaderSource, GL_VERTEX_SHADER);
		Log.info("Loading and compiling shaders: fragment");
        String fragmentShaderSource = Resources.loadShaderFromResource("shaders/particles/fragment_particle.glsl");
        int fragmentShader = Resources.compileShader(fragmentShaderSource, GL_FRAGMENT_SHADER);
		Log.info("Loading and compiling shaders: compute");
        String computeShaderSource = Resources.loadShaderFromResource("shaders/particles/compute_particle.glsl");
        computeShader = Resources.compileShader(computeShaderSource, GL_COMPUTE_SHADER);

        // Step 7: Create render program and attach shaders
        Log.info("Creating render program and attaching vertex and fragment shaders");
        renderProgram = glCreateProgram();
        glAttachShader(renderProgram, vertexShader);
        glAttachShader(renderProgram, fragmentShader);
        glLinkProgram(renderProgram);
        Log.info("Render program linked");

        // Step 8: Create compute program and attach compute shader
        Log.info("Creating compute program and attaching compute shader");
        computeProgram = glCreateProgram();
        glAttachShader(computeProgram, computeShader);
        glLinkProgram(computeProgram);
        Log.info("Compute program linked");

        // Step 9: Check for linking errors in both programs
        if (glGetProgrami(renderProgram, GL_LINK_STATUS) == GL_FALSE) {
            String infoLog = glGetProgramInfoLog(renderProgram);
            Log.err("Issues linking the shaders to the render program: " + infoLog);
        }

        if (glGetProgrami(computeProgram, GL_LINK_STATUS) == GL_FALSE) {
            String infoLog = glGetProgramInfoLog(computeProgram);
            Log.err("Issues linking the shaders to the compute program: " + infoLog);
        }

        // Step 10: Compute the workgroups size for dispatching compute shader
        workGroups = (vertices.length / 5 + 256 - 1) / 256;
        Log.info("Workgroups size calculated: " + workGroups);

        // Step 11: Delete the shaders after they are attached to the programs
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
        glDeleteShader(computeShader);
        Log.info("Shaders deleted after program linking");

    }

	public static void debugReadSSBO() {
		glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);
		FloatBuffer result = BufferUtils.createFloatBuffer(vertices.length);
		glGetBufferSubData(GL_SHADER_STORAGE_BUFFER, 0, result);
		result.rewind();
		
		float[] data = new float[vertices.length];
		result.get(data);
		System.out.println("SSBO Data: " + Arrays.toString(data));
	}

	public static void draw() {
		// Update
		glUseProgram(computeProgram);
		glUniform1f(glGetUniformLocation(computeProgram, "deltaTime"), 0.016f);
		glUniform1ui(glGetUniformLocation(computeProgram, "numParticles"), vertices.length/5);
		glDispatchCompute(workGroups, 1, 1);
		glMemoryBarrier(GL_ALL_BARRIER_BITS);

		// Debug readback (optional)
		debugReadSSBO();

		// Render
		glUseProgram(renderProgram);
		glBindVertexArray(VAO);
		glDrawArrays(GL_POINTS, 0, vertices.length/5);
	}
}
