package ddswriter.delegators.lwjgl2_s3tc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ddswriter.DDSDelegator;
import ddswriter.delegators.lwjgl2.LWJGLCliModule;

/**
 * 
 * @author Riccardo Balbo
 */
public class S3tcCLI109Module extends LWJGLCliModule{
	public S3tcCLI109Module(){

	}

	@Override
	public void load(Map<String,String> options, List<String> help, ArrayList<DDSDelegator> delegators) {
		if(!startGL()) return;
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
		if(hwc==null||hwc.equals("false")) return;
		i=0;
		for(String s:help){
			if(s.startsWith("Output formats")){
				break;
			}else{
				i++;
			}
		}
		help.add(i+1,"   S3TC_DXT1 (BC1), S3TC_DXT3 (BC2), S3TC_DXT5(BC3)\n");
		delegators.add(new S3TC_LWJGL2CompressionDelegator());
	}

	@Override
	public void unload(Map<String,String> options, List<String> help, ArrayList<DDSDelegator> delegators) {
		endGL();

	}
}
