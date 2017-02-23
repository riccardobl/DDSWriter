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


//
//	
//	private static Collection<DDSDelegator>  loadDefaultDelegators(Map<String,Object> options){
//		Collection<DDSDelegator>  delegators=new ArrayList<DDSDelegator> ();
//		
//		delegators.add(new MipmapGenDelegator());
//
//		if((boolean)options.getOrDefault("compress",false))delegators.add(new S2tcDelegator());
//		else delegators.add(new GenericDelegator());
//
//		return delegators;
//	}
//
//	public static void write(Texture tx, Map<String,Object> options, OutputStream output ) throws Exception {
//		write(tx,options,loadDefaultDelegators(options),output);
//	}

	/**
	 * 
	 * @param tx
	 * @param options
	 * @param delegators : one delegator must be a DDSBodyWriterDelegator
	 * @param output
	 * @throws Exception
	 */
	public static void write(Texture tx, Map<String,String> options,Collection<DDSDelegator> delegators, OutputStream output ) throws Exception {
		// TODO: Add support for DX10 HEADER
		// TODO: Add mipmap generation for texture 3d and texture cubemap
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