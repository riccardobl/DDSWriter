package ddswriter.lwjgl;

import org.lwjgl.opengl.Pbuffer;
import org.lwjgl.opengl.PixelFormat;

import com.jme3.system.NativeLibraryLoader;

import ddswriter.cli.CLI109Module;

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
