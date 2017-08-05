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
package ddswriter.delegates.lwjgl2;

import org.lwjgl.opengl.Pbuffer;
import org.lwjgl.opengl.PixelFormat;

import com.jme3.system.NativeLibraryLoader;

import ddswriter.cli.CLI109Module;
/**
 * 
 * @author Riccardo Balbo
 *
 */
public abstract class LWJGLCliModule implements CLI109Module{

	public static Pbuffer pbuffer;

	public boolean startGL() {
		if(pbuffer!=null) return true;
		try{
			NativeLibraryLoader.loadNativeLibrary("lwjgl",true);
			pbuffer=new Pbuffer(8,8,new PixelFormat(),null,null);
			pbuffer.makeCurrent();

			if(pbuffer.isBufferLost()){
				pbuffer.destroy();
				throw new Exception("pbuffer lost");
			}

			return true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}

	public void endGL() {
		if(pbuffer!=null){
			pbuffer.destroy();
			pbuffer=null;
		}

	}
}
