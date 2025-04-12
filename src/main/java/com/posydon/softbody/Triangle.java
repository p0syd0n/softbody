package com.posydon.softbody;

import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
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

public class Triangle {
    public static int VAO;
    public static int shaderProgram;

    static {
        		// Bind vertex array object (config box)
		VAO = glGenVertexArrays();
		glBindVertexArray(VAO);

		// Bind vertex buffer object (gpu memory thing that stores object info)
		int VBO = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, VBO);

		// Created in JVM memory
		float[] vertices = new float[]{
			// positions         // colors
			0.5f, -0.5f, 0.0f,  1.0f, 0.0f, 0.0f,   // bottom right
			-0.5f, -0.5f, 0.0f,  0.0f, 1.0f, 0.0f,   // bottom left
			0.0f,  0.5f, 0.0f,  0.0f, 0.0f, 1.0f    // top 
		};

		// glBufferDate wants native memory pointer. So we use special stuff to leave JVM memory and allocate on native memory
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer vertexBuffer = stack.mallocFloat(vertices.length);
			vertexBuffer.put(vertices).flip();
			glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
		} catch(Exception e) {
			Log.err("Error allocating space for vertices and sending it to GPU " + e.toString());
		}

		// Configure how the data going into the shaders will be "preprocessed"
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 6*Float.BYTES, 0);
		glEnableVertexAttribArray(0);

		glVertexAttribPointer(1, 3, GL_FLOAT, false, 6*Float.BYTES, 3*Float.BYTES);
		glEnableVertexAttribArray(1);

		float[] textureCoordinates = new float[]{
			0.0f, 0.0f,  // lower-left corner  
			1.0f, 0.0f,  // lower-right corner
			0.5f, 1.0f   // top-center corner
		};

		// Loading shaders from source, and compiling them

		String vertexShaderSource = Resources.loadShaderFromResource("shaders/vertex_shader.glsl");
		int vertexShader = Resources.compileShader(vertexShaderSource, GL_VERTEX_SHADER);

		String fragmentShaderSource = Resources.loadShaderFromResource("shaders/fragment_shader.glsl");
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
		glDrawArrays(GL_TRIANGLES, 0, 3);
	}
}
