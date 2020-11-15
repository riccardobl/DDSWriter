/**
Copyright 2017 Riccardo Balbo

Permission is hereby granted, free of charge, to any person obtaining a copy of this software 
and associated documentation files (the "Software"), to deal in the Software without restriction, 
including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, 
and/or sell copies of the Software, and to permit persons to whom the Software is furnished 
to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE 
USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
package ddswriter.delegates.lwjgl3;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_SRGB_CAPABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import ddswriter.DDSDelegate;
import ddswriter.cli.CLI109Module;

/**
 * 
 * @author Riccardo Balbo
 *
 */
public abstract class LWJGLCliModule implements CLI109Module{

	static boolean ADDED_TO_HELP=false;
	Long window;

	public void load(Map<String,String> options, List<String> help, ArrayList<DDSDelegate> delegates) {
		if(ADDED_TO_HELP) return;
		int i=0;
		for(String s:help){
			if(s.startsWith("Input formats")){
				break;
			}else{
				i++;
			}
		}
		help.add(i,"   --use-opengl: Enable hardware compression with opengl\n");
		ADDED_TO_HELP=true;
	}

	public boolean startGL() {

		long context=glfwGetCurrentContext();
		if(context != NULL) return true;
		glfwInit();

		GLFWErrorCallback.createPrint(System.err).set();
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT,GLFW_TRUE);
		glfwWindowHint(GLFW_OPENGL_PROFILE,GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR,3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR,2);
		glfwWindowHint(GLFW_SRGB_CAPABLE,GLFW_TRUE);
		glfwWindowHint(GLFW_VISIBLE,GL_FALSE);
		window=glfwCreateWindow(1024,768,"",NULL,NULL);
		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);
		GL.createCapabilities();
		return true;
	}

	public void endGL() {
		if(window!=null){
			glFinish();
			glfwPollEvents();
			glfwFreeCallbacks(window);
			glfwDestroyWindow(window);
			glfwTerminate();
			glfwSetErrorCallback(null).free();
			glfwDestroyWindow(window);
		}

	}
	
}
