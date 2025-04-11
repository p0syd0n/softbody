package com.posydon.softbody;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glShaderSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ShaderLoaderCompiler {
	public static String loadShaderFromResource(String filename) {
		InputStream inputStream = ShaderLoaderCompiler.class.getClassLoader().getResourceAsStream(filename);

		if (inputStream == null) {
			throw new RuntimeException("Shader file not found: "+filename);
		}
		StringBuilder source = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
			String line;
			while ((line = reader.readLine()) != null) {
				source.append(line).append("\n");
			}
		} catch(IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Shader Read Failed");
		}
		return source.toString();
	}
	
	public static int compileShader(String shaderSource, int shaderType) {
		int shader = glCreateShader(shaderType);
		glShaderSource(shader, shaderSource);
		glCompileShader(shader);

		if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
			String infoLog = glGetShaderInfoLog(shader);
			Log.err(infoLog);
			throw new RuntimeException("Shader Compilation Failed");
		}
		return shader;
	}
}
