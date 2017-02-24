package ddswriter.delegators.s2tc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ddswriter.DDSDelegator;
import ddswriter.cli.CLI109Module;

public class S2tcCLI109Module implements CLI109Module{
	public S2tcCLI109Module(){
		
	}
	@Override
	public void load(Map<String,String> options, List<String> help, ArrayList<DDSDelegator> delegators) {
		int i=0;
		for(String s:help){
			if(s.startsWith("Output formats")){
				break;
			}else{
				i++;
			}
		}
		help.add(i+1,"   S2TC_DXT1,S2TC_DXT3(TODO),S2TC_DXT5(TODO)\n");
		delegators.add(new S2tcDelegator());
	}
	
	@Override
	public void unload(Map<String,String> options, List<String> help, ArrayList<DDSDelegator> delegators) {
		
	}

}
