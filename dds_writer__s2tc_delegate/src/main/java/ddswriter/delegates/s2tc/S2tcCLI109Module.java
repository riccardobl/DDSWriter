package ddswriter.delegates.s2tc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ddswriter.DDSDelegate;
import ddswriter.cli.CLI109Module;

public class S2tcCLI109Module implements CLI109Module{
	public S2tcCLI109Module(){
		
	}
	@Override
	public void load(Map<String,String> options, List<String> help, ArrayList<DDSDelegate> delegates) {
		int i=0;
		for(String s:help){
			if(s.startsWith("Output formats")){
				break;
			}else{
				i++;
			}
		}
		help.add(i+1,"   S2TC_DXT1\n");
		delegates.add(new S2tcDelegate());
	}
	
	@Override
	public void unload(Map<String,String> options, List<String> help, ArrayList<DDSDelegate> delegates) {
		
	}

}
