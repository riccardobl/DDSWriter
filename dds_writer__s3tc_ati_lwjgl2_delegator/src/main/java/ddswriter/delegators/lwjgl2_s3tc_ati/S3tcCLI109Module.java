package ddswriter.delegators.lwjgl2_s3tc_ati;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.Pbuffer;
import org.lwjgl.opengl.PixelFormat;

import com.jme3.system.NativeLibraryLoader;

import ddswriter.DDSDelegator;
import ddswriter.cli.CLI109Module;

public class S3tcCLI109Module implements CLI109Module{
	public S3tcCLI109Module(){
		 
	}
	Pbuffer pbuffer;
	@Override
	public void load(Map<String,String> options, List<String> help, ArrayList<DDSDelegator> delegators) {
	
		int i=0;
		for(String s:help){
			if(s.startsWith("Input formats")){
				break;
			}else{
				i++;
			}
		}
		help.add(i,"   --use_lwjgl: Enable hardware compression with lwjgl\n");

		String hwc=options.get("use_lwjgl");
		if(hwc==null||hwc.equals("false"))return;
		i=0;
		for(String s:help){
			if(s.startsWith("Output formats")){
				break;
			}else{
				i++;
			}
		}
		help.add(i+1,"   S3TC_DXT1,S3TC_DXT3,S3TC_DXT5,ATI_3DC\n");
		delegators.add(new S3TC_ATI_HardwareCompressionDelegator()); 
        try{
			NativeLibraryLoader.loadNativeLibrary("lwjgl",true);
			pbuffer=new Pbuffer(8,8,new PixelFormat(), null, null);
            pbuffer.makeCurrent();
             
            if(pbuffer.isBufferLost()) {
              pbuffer.destroy();
              throw new Exception("pbuffer lost");
            }
             
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void unload(Map<String,String> options, List<String> help, ArrayList<DDSDelegator> delegators) {
        pbuffer.destroy();

	}
}
