package com.posydon.softbody;

import java.nio.*;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_POINTS;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import org.lwjgl.system.MemoryStack;

public class Particle {
    public static int VAO;
    public static int shaderProgram;
	private static float[]  vertices;

    static {
        		// Bind vertex array object (config box)
		VAO = glGenVertexArrays();
		glBindVertexArray(VAO);

		// Bind vertex buffer object (gpu memory thing that stores object info)
		int VBO = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, VBO);

		// Created in JVM memory
		//vertices = new float[36];
		// for (int i = 0; i < 33; i+=3) {
		// 	vertices[i] = (float)i*10;
		// 	System.out.println((float)i*10);
		// 	vertices[i+1] = 50f;
		// 	vertices[i+2] = 5; 
		// } 

		vertices = new float[] {-100f, -100f, 15, -100f, 100, 15, 100f, 100f, 15, 100f, -100f, 15 };

		// glBufferDate wants native memory pointer. So we use special stuff to leave JVM memory and allocate on native memory
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer vertexBuffer = stack.mallocFloat(vertices.length);
			vertexBuffer.put(vertices).flip();
			glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
		} catch(Exception e) {
			Log.err("Error allocating space for vertices and sending it to GPU " + e.toString());
		}

		// Configure how the data going into the shaders will be "preprocessed"
		glVertexAttribPointer(0, 2, GL_FLOAT, false, 3 * Float.BYTES, 0);
		glEnableVertexAttribArray(0);

		glVertexAttribPointer(1, 1, GL_FLOAT, false, 3 * Float.BYTES, 2 * Float.BYTES);
		glEnableVertexAttribArray(1);


		// Loading shaders from source, and compiling them

		String vertexShaderSource = Resources.loadShaderFromResource("shaders/particles/vertex_particle.glsl");
		int vertexShader = Resources.compileShader(vertexShaderSource, GL_VERTEX_SHADER);

		String fragmentShaderSource = Resources.loadShaderFromResource("shaders/particles/fragment_particle.glsl");
		int fragmentShader = Resources.compileShader(fragmentShaderSource, GL_FRAGMENT_SHADER);

		// Creating the program (links shaders to each other)
		shaderProgram = glCreateProgram();
		glAttachShader(shaderProgram, vertexShader);
		glAttachShader(shaderProgram, fragmentShader);
		// Linking the shader program to the gl context/state
		glLinkProgram(shaderProgram);

		// Make sure we didn't mess up
		if (glGetProgrami(shaderProgram, GL_LINK_STATUS) == GL_FALSE) {
			String infoLog = glGetProgramInfoLog(shaderProgram);
			Log.err("Issues linking the shaders to the shader program: " + infoLog);
		}

		// Delete these shaders, we've already attached them to the shaderProgram
		glDeleteShader(vertexShader);
		glDeleteShader(fragmentShader);
    }

	public static void draw() {
		glDrawArrays(GL_POINTS, 0, vertices.length / 3);
	}
}
