package com.posydon.softbody;

import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
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

public class Rectangle {
    public static int VAO;
    public static int shaderProgram;
    public static int texture;

    static  {

		float[] vertices = new float[]{
            // positions          // colors           // texture coords
            0.5f,  0.5f, 0.0f,   1.0f, 0.0f, 0.0f,   1.0f, 1.0f,   // top right
            0.5f, -0.5f, 0.0f,   0.0f, 1.0f, 0.0f,   1.0f, 0.0f,   // bottom right
            -0.5f, -0.5f, 0.0f,   0.0f, 0.0f, 1.0f,   0.0f, 0.0f,   // bottom left
            -0.5f,  0.5f, 0.0f,   1.0f, 1.0f, 0.0f,   0.0f, 1.0f    // top left 
		};

		int[] indices = new int[]{
			0, 1, 3,
			1, 2, 3
		};
		VAO = glGenVertexArrays();
		glBindVertexArray(VAO);

		int VBO = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, VBO);

		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer vertexBuffer = stack.mallocFloat(vertices.length);
			vertexBuffer.put(vertices).flip();
			glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
		} catch(Exception e) {
			Log.err("Error allocating space for vertices and setting gl state: " + e.toString());
		}

        glVertexAttribPointer(0, 9, GL_FLOAT, false, 9*Float.BYTES, 0);
		glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 9, GL_FLOAT, false, 9*Float.BYTES, 3*Float.BYTES);
		glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, 9, GL_FLOAT, false, 8*Float.BYTES, 6*Float.BYTES);
		glEnableVertexAttribArray(2);

		int EBO = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);

        
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer indexBuffer = stack.mallocInt(indices.length);
			indexBuffer.put(indices).flip();
			glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
		} catch(Exception e) {
			Log.err("Error allocating space for indices and setting gl state: " + e.toString());
		}




		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);


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
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
    }
}
