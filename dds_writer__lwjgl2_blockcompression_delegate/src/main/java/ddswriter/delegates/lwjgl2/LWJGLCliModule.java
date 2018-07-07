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



import java.io.File;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Pbuffer;
import org.lwjgl.opengl.PixelFormat;

import com.jme3.system.NativeLibraryLoader;

import ddswriter.DDSDelegate;
import ddswriter.cli.CLI109Module;
import ddswriter.cli.utils.FileUtils;
/**
 * 
 * @author Riccardo Balbo
 *
 */
public abstract class LWJGLCliModule implements CLI109Module{

	public static Pbuffer pbuffer;
	static boolean ADDED_TO_HELP=false;

	public void load(Map<String,String> options, List<String> help, ArrayList<DDSDelegate> delegates) {
		if(ADDED_TO_HELP)return;
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
		if(pbuffer!=null) return true;
		try{
			String extf=System.getProperty("nativePath");
			if(extf!=null)NativeLibraryLoader.setCustomExtractionFolder(extf);	  
			NativeLibraryLoader.loadNativeLibrary("lwjgl",true);

			
			PixelFormat pixel_format = new PixelFormat(8,8,0);
			
			ContextAttribs attr = new ContextAttribs(3,2)
			    .withForwardCompatible(false)
			    .withProfileCore(true);
			
			pbuffer=new Pbuffer(8,8,pixel_format,null,null,attr);
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
