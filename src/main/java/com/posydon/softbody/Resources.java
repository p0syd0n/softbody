package com.posydon.softbody;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;



public class Resources {
	public static String loadShaderFromResource(String filename) {
		InputStream inputStream = Resources.class.getClassLoader().getResourceAsStream(filename);

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

	public static void setUniformf(float value, String uniformName, int currentShaderActive) {
		glUniform1f(glGetUniformLocation(currentShaderActive, uniformName), value);
	}

	public static void setUniformb(boolean value, String uniformName, int currentShaderActive) {
		glUniform1i(glGetUniformLocation(currentShaderActive, uniformName), value?1:0);
	}

	public static void setUniformi(int value, String uniformName, int currentShaderActive) {
		glUniform1i(glGetUniformLocation(currentShaderActive, uniformName), value);
	}
}