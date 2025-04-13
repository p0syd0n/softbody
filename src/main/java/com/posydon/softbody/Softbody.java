package com.posydon.softbody;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import static org.lwjgl.glfw.GLFW.*;
//import static org.lwjgl.opengl.*;
import static org.lwjgl.opengl.GL30.*;
//import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_QUERY_RESULT;
import static org.lwjgl.opengl.GL15.glBeginQuery;
import static org.lwjgl.opengl.GL15.glEndQuery;
import static org.lwjgl.opengl.GL15.glGenQueries;
//import static org.lwjgl.opengl.GL20.*;

import static org.lwjgl.system.MemoryUtil.*;

public class Softbody {
	private long window;
	private static int windowWidth = 800;
	private static int windowHeight = 600;
	private long iterations = 0;

	private long nanoSecondsGPU = 0;
	private long nanoSecondsCPU = 0;

	public int run() {
		glfwInit();
		Log.info("Initialized");
		// opengl version compatability things
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		// Use core profile
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		Log.info("Done hinting");
		// Create window
		this.window = glfwCreateWindow(windowWidth, windowHeight, "Softbody", NULL, NULL);

		if (this.window == 0) {
			System.err.println("failed to create the window :(");
			glfwTerminate();
			return -1;
		}
		Log.info("Made window");
		// Set the context/state for window
		glfwMakeContextCurrent(this.window);
		GL.createCapabilities();
		Log.info("Window is context, capabilities created");
		// Set viewport
		glViewport(0, 0, Softbody.windowWidth, Softbody.windowHeight);
		Log.info("Set viewport");
		Log.info(glGetInteger(GL_MAX_VERTEX_ATTRIBS)+"");
		glEnable(0x8642); // GL_PROGRAM_POINT_SIZE?
		
		// Wireframes
		// glPolygonMode(GL_FRONT_AND_BACK, GL_LINE); 

		// Enable culling. Set to clockwise, because source vertices are given in clockwise order
		// glEnable(GL_CULL_FACE);
		// glCullFace(GL_BACK);
		// glFrontFace(GL_CW);
		

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
			// glViewport(0, 0, Softbody.windowWidth, Softbody.windowHeight);
 
			// GPU time measuring start
			int queryID = glGenQueries();
			glBeginQuery(GL33.GL_TIME_ELAPSED, queryID);
			// CPU time measuring start
			long start = System.nanoTime();

			processInput(this.window);
			//glViewport(0, 0, Softbody.windowWidth, Softbody.windowHeight);
			glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
			glClear(GL_COLOR_BUFFER_BIT);

			glUseProgram(Particle.shaderProgram);
			glBindVertexArray(Particle.VAO);
			//Resources.setUniformf((float)((Math.sin(glfwGetTime())/2.0f)+0.5f), "horizontal_offset", Rectangle.shaderProgram);
			Particle.draw();


			glfwPollEvents();
			glfwSwapBuffers(this.window);

			// GPU time measuring end
			glEndQuery(GL33.GL_TIME_ELAPSED);
			long timeElapsed = GL15.glGetQueryObjecti(queryID, GL_QUERY_RESULT); // nanoseconds
			nanoSecondsGPU += timeElapsed;
			// CPU time measuring end
			long end = System.nanoTime();
			long duration = end - start; // nanoseconds
			nanoSecondsCPU += duration;
			iterations ++;
		}

		glfwTerminate();

		return 0;
	}

	private void processInput(long window) {
		if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS) {
			Log.info("Escape pressed, exiting");
			Log.info("Average GPU time: "+nanoSecondsGPU/iterations+" nS : " + nanoSecondsGPU/1_000_000.0/iterations + " mS : " + nanoSecondsGPU/1_000_000_000.0/iterations + " s");
			Log.info("Average CPU time: "+nanoSecondsCPU/iterations+" nS : " + nanoSecondsCPU/1_000_000.0/iterations + " mS : " + nanoSecondsCPU/1_000_000_000.0/iterations + " s");

			glfwSetWindowShouldClose(window, true);
		}
	}
}
