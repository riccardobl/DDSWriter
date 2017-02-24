package ddswriter;

import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;

import com.jme3.texture.Texture;

import ddswriter.format.DDS_BODY;
import ddswriter.format.DDS_HEADER;

/**
 * 
 * @author Riccardo Balbo
 */
public class DDSWriter{



	public static void write(Texture tx, Map<String,String> options,Collection<DDSDelegator> delegators, OutputStream output ) throws Exception {
		// TODO: Add support for DX10 HEADER
		boolean debug=options.getOrDefault("debug","false").equals("true");
		
		DDSOutputStream os=new DDSOutputStream(output);
		
		DDS_HEADER header=new DDS_HEADER();
		for(DDSDelegator delegator:delegators){
			delegator.header(tx, options, header);
		}
		if(debug){
			System.out.println(header.dump());
		}
		header.write(os);
		os.flush();
		
		DDS_BODY body=new DDS_BODY(os);
		for(DDSDelegator delegator:delegators){
			delegator.body(tx, options, header,body);
		}
		
		body.flush();
		os.close();
	}



}