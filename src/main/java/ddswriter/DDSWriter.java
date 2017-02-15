package ddswriter;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.jme3.math.ColorRGBA;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.Texture3D;
import com.jme3.texture.TextureCubeMap;
import com.jme3.texture.image.ImageRaster;

import ddswriter.delegators.MipmapGenDelegator;
import ddswriter.delegators.UncompressedARGBDelegator;
import ddswriter.delegators.s2tc.S2tcDelegator;
import ddswriter.format.DDS_BODY;
import ddswriter.format.DDS_HEADER;
import jme3tools.converters.MipMapGenerator;

/**
 * 
 * @author Riccardo Balbo
 */
public class DDSWriter{



	private static void applyDelegator(DDSDelegator delegator,Texture tx, Map<String,Object> options,DDS_HEADER header,DDS_BODY body) throws Exception{
		int mipmaps=!tx.getImage().hasMipmaps()?1:tx.getImage().getMipMapSizes().length;
		
		boolean is_header=body==null;
		
		if(tx instanceof Texture2D){
			for(int mipmap=0;mipmap<mipmaps;mipmap++){
				ImageRaster ir=ImageRaster.create(tx.getImage(),0,mipmap,false);
				if(is_header&&delegator instanceof DDSHeaderWriterDelegator){
					((DDSHeaderWriterDelegator)delegator).header(tx,ir,mipmap,0,options,header);

				}else if(delegator instanceof DDSBodyWriterDelegator){
					((DDSBodyWriterDelegator)delegator).body(tx,ir,mipmap,0,options,header,body);
				}
			}
		}else if(tx instanceof TextureCubeMap){
			for(int slice=0;slice<6;slice++){
				for(int mipmap=0;mipmap<mipmaps;mipmap++){
					ImageRaster ir=ImageRaster.create(tx.getImage(),slice,mipmap,false);
					if(is_header&&delegator instanceof DDSHeaderWriterDelegator){
						((DDSHeaderWriterDelegator)delegator).header(tx,ir,mipmap,slice,options,header);

					}else if(delegator instanceof DDSBodyWriterDelegator){
						((DDSBodyWriterDelegator)delegator).body(tx,ir,mipmap,slice,options,header,body);
					}
				}
			}
		}else if(tx instanceof Texture3D){
			for(int slice=0;slice<tx.getImage().getDepth();slice++){
				for(int mipmap=0;mipmap<mipmaps;mipmap++){
					ImageRaster ir=ImageRaster.create(tx.getImage(),slice,mipmap,false);
					if(is_header&&delegator instanceof DDSHeaderWriterDelegator){
						((DDSHeaderWriterDelegator)delegator).header(tx,ir,mipmap,slice,options,header);

					}else if(delegator instanceof DDSBodyWriterDelegator){
						((DDSBodyWriterDelegator)delegator).body(tx,ir,mipmap,slice,options,header,body);
					}
				}
			}

		}

	}
	
	
	private static Collection<DDSDelegator>  loadDefaultDelegators(Map<String,Object> options){
		Collection<DDSDelegator>  delegators=new ArrayList<DDSDelegator> ();
		
		delegators.add(new MipmapGenDelegator());

		if((boolean)options.getOrDefault("compress",false))delegators.add(new S2tcDelegator());
		else delegators.add(new UncompressedARGBDelegator());

		return delegators;
	}

	public static void write(Texture tx, Map<String,Object> options, OutputStream output ) throws Exception {
		write(tx,options,loadDefaultDelegators(options),output);
	}

	/**
	 * 
	 * @param tx
	 * @param options
	 * @param delegators : one delegator must be a DDSBodyWriterDelegator
	 * @param output
	 * @throws Exception
	 */
	public static void write(Texture tx, Map<String,Object> options,Collection<DDSDelegator> delegators, OutputStream output ) throws Exception {
		// TODO: Add support for RGB 
		// TODO: Add support for DX10 HEADER
		// TODO: Add mipmap generation for texture 3d and texture cubemap
		boolean debug=(boolean)options.getOrDefault("debug",false);
		
		DDSOutputStream os=new DDSOutputStream(output);
		
		DDS_HEADER header=new DDS_HEADER();
		for(DDSDelegator delegator:delegators){
			applyDelegator(delegator,tx, options, header,null);
		}
		if(debug){
			System.out.println(header.dump());
		}
		header.write(os);
		os.flush();
		
		DDS_BODY body=new DDS_BODY(os);
		for(DDSDelegator delegator:delegators){
			applyDelegator(delegator,tx, options, null,body);
		}
		
		body.flush();
		os.close();
	}



}