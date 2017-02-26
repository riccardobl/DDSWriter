package ddswriter.cli;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ddswriter.DDSDelegator;
/**
 * 
 * @author Riccardo Balbo
 */
public interface CLI109Module{
	public void load(Map<String,String> options, List<String> help,ArrayList<DDSDelegator> delegators);
	public void unload(Map<String,String> options, List<String> help,ArrayList<DDSDelegator> delegators);

}
