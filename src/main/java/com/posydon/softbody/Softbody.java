package com.posydon.softbody;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Softbody {
	private long window;
	private static int windowWidth = 800;
	private static int windowHeight = 600;
	public int run() {
		glfwInit();
		Log.info("Initialized");
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		Log.info("Done hinting");
		this.window = glfwCreateWindow(windowWidth, windowHeight, "Softbody", NULL, NULL);

		if (this.window == 0) {
			System.err.println("failed to create the window :(");
			glfwTerminate();
			return -1;
		}
		Log.info("Made window");
		glfwMakeContextCurrent(this.window);
		GL.createCapabilities();
		Log.info("Window is context, capabilities created");
		glViewport(0, 0, Softbody.windowWidth, Softbody.windowHeight);
		Log.info("Set viewport");
		/*
		 * Create callback functions after window creation and before render loop initialization
		 */
		glfwSetFramebufferSizeCallback(this.window, new GLFWFramebufferSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				glViewport(0, 0, width, height);
			}
		});

		// // Bind vertex array object (config box)
		// int VAO = glGenVertexArrays();
		// glBindVertexArray(VAO);

		// // Bind vertex buffer object (gpu memory thing that stores object info)
		// int VBO = glGenBuffers();
		// glBindBuffer(GL_ARRAY_BUFFER, VBO);

		// // Created in JVM memory
		// float[] vertices = new float[]{
		// 	-0.5f, -0.5f, 0.0f,
		// 	0.5f, -0.5f, 0.0f,
		// 	0.0f,  0.5f, 0.0f
		// };

		// // glBufferDate wants native memory pointer. So we use special stuff to leave JVM memory and allocate on native memory
		// try(MemoryStack stack = MemoryStack.stackPush()) {
		// 	FloatBuffer vertexBuffer = stack.mallocFloat(vertices.length);
		// 	vertexBuffer.put(vertices).flip();
		// 	glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
		// } catch(Exception e) {
		// 	Log.err("Error allocating space for vertices and setting gl state: " + e.toString());
		// 	return -1;
		// }
		// // Loading shaders from source, and compiling them

		// String vertexShaderSource = ShaderLoaderCompiler.loadShaderFromResource("shaders/vertex_shader.glsl");
		// int vertexShader = ShaderLoaderCompiler.compileShader(vertexShaderSource, GL_VERTEX_SHADER);

		// String fragmentShaderSource = ShaderLoaderCompiler.loadShaderFromResource("shaders/fragment_shader.glsl");
		// int fragmentShader = ShaderLoaderCompiler.compileShader(fragmentShaderSource, GL_FRAGMENT_SHADER);

		// // Creating the program (links shaders to each other)
		// int shaderProgram = glCreateProgram();
		// glAttachShader(shaderProgram, vertexShader);
		// glAttachShader(shaderProgram, fragmentShader);
		// // Linking the shader program to the gl context/state
		// glLinkProgram(shaderProgram);

		// // Make sure we didn't mess up
		// if (glGetProgrami(shaderProgram, GL_LINK_STATUS) == GL_FALSE) {
		// 	String infoLog = glGetProgramInfoLog(shaderProgram);
		// 	Log.err("Issues linking the shaders to the shader program: " + infoLog);
		// 	return -1;
		// }

		// // Delete these shaders, we've already attached them to the shaderProgram
		// glDeleteShader(vertexShader);
		// glDeleteShader(fragmentShader);

		// // Configure how the data going into the shaders will be "preprocessed"
		// glVertexAttribPointer(0, 3, GL_FLOAT, false, 3*Float.BYTES, 0);
		// glEnableVertexAttribArray(0);
		// glBindVertexArray(0);





		/*
		 * Render loop
		 * 1 iteration =  1 frame
		 */
		while (!glfwWindowShouldClose(this.window)) {
			processInput(this.window);

			glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
			glClear(GL_COLOR_BUFFER_BIT);

			glUseProgram(Triangle.shaderProgram);
			glBindVertexArray(Triangle.VAO);
			glDrawArrays(GL_TRIANGLES, 0, 3);

			glfwPollEvents();
			glfwSwapBuffers(this.window);
		}

		glfwTerminate();

		return 0;
	}

	private void processInput(long window) {
		if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS) {
			Log.info("Escape pressed, exiting");
			glfwSetWindowShouldClose(window, true);
		}
	}
}
