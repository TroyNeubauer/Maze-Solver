package com.troy.compsci.maze.graphics;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class GLUtil {

	public static String getError(int err) {
		switch (err) {
		case GL_NO_ERROR:
			return "No error";
		case GL_INVALID_ENUM:
			return "Invalid enum";
		case GL_INVALID_VALUE:
			return "Invalid value";
		case GL_INVALID_OPERATION:
			return "Invalid operation";
		case GL_STACK_OVERFLOW:
			return "Stack overflow";
		case GL_STACK_UNDERFLOW:
			return "Stack underflow";
		case GL_OUT_OF_MEMORY:
			return "Out of memory";
		case GL_INVALID_FRAMEBUFFER_OPERATION:
			return "Invalid framebuffer operation";
		}
		return "Unknown error " + err;
	}

	public static void checkForErrors(String message) {
		int err = glGetError();
		if (err != GL_NO_ERROR) {
			System.err.println("Open GL Error detected! " + getError(err) +" Doing " + message);
		}
	}

	public static void terminate() {
		glfwTerminate();
	}

}
