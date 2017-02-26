package ddswriter.delegators.lwjgl2_rgtc;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ddswriter.DDSDelegator;
import ddswriter.delegators.lwjgl2.LWJGLCliModule;

/**
 * 
 * @author Riccardo Balbo
 */
public class RgtcCLI109Module  extends LWJGLCliModule{
	public RgtcCLI109Module(){

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
		help.add(i+1,"   RGTC1 (Compatible with ATI1, BC4, 3DC+), RGTC2 (Compatible with ATI2, BC5, 3DC)\n");
		delegators.add(new RGTC_LWJGL2CompressionDelegator());
	}

	@Override
	public void unload(Map<String,String> options, List<String> help, ArrayList<DDSDelegator> delegators) {
		endGL();

	}
}
