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
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
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
		// Wireframes
		// glPolygonMode(GL_FRONT_AND_BACK, GL_LINE); 

		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
		glFrontFace(GL_CCW);
		/*
		 * Create callback functions after window creation and before render loop initialization
		 */
		glfwSetFramebufferSizeCallback(this.window, new GLFWFramebufferSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				glViewport(0, 0, width, height);
			}
		});




		/*
		 * Render loop
		 * 1 iteration =  1 frame
		 */
		while (!glfwWindowShouldClose(this.window)) {
			processInput(this.window);
			glViewport(0, 0, Softbody.windowWidth, Softbody.windowHeight);
			glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
			glClear(GL_COLOR_BUFFER_BIT);
			glUseProgram(TriangleGradient.shaderProgram);
			glBindVertexArray(TriangleGradient.VAO);
			TriangleGradient.draw();


			// glUseProgram(Triangle.shaderProgram);
			// glBindVertexArray(Triangle.VAO);
			// glDrawArrays(GL_TRIANGLES, 0, 3);

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
