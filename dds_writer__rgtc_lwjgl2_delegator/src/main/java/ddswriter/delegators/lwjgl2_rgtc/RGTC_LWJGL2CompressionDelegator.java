package ddswriter.delegators.lwjgl2_rgtc;

import static org.lwjgl.opengl.GL30.GL_COMPRESSED_RED_RGTC1;
import static org.lwjgl.opengl.GL30.GL_COMPRESSED_RG_RGTC2;

import java.util.Map;

import com.jme3.texture.Texture;

import ddswriter.Texel;
import ddswriter.delegators.lwjgl2.LWJGLBlockCompressionDelegator;
import ddswriter.format.DDS_BODY;
import ddswriter.format.DDS_HEADER;

/**
 * 	
 * @author Riccardo Balbo
 */

public class RGTC_LWJGL2CompressionDelegator extends LWJGLBlockCompressionDelegator{

	public static enum Format{
		RGTC1("ATI1",8,GL_COMPRESSED_RED_RGTC1,"ATI1","ATI_3DC+","BC4","3DC+"),
		RGTC2("ATI2",16,GL_COMPRESSED_RG_RGTC2,"ATI2","ATI_3DC","BC5","3DC");
		public String internal_name;
		public int gl,blocksize;
		public String[] aliases;

		private Format(String s,int blocksize,int gl,String... aliases){
			this.internal_name=s;
			this.gl=gl;
			this.blocksize=blocksize;
			this.aliases=aliases;
		}

	}

	protected Format FORMAT;

	@Override
	public void header(Texture tx, Map<String,String> options, DDS_HEADER header) throws Exception {

		String format=((String)options.get("format"));
		if(format==null){
			skip();
			return;
		}

		format=format.toUpperCase();
		for(Format f:Format.values()){
			if(f.name().equals(format)) FORMAT=f;
			else{
				for(String a:f.aliases){
					if(format.equals(a)){
						FORMAT=f;
						break;
					}
				}
			}
		}

		if(FORMAT==null){
			skip();
			System.out.println(this.getClass()+" does not support "+format+". skip");
			return;
		}
		super.lwjglHeader(FORMAT.internal_name,FORMAT.blocksize,tx,options,header);

	}

	@Override
	public void body(Texture tx, Texel ir, int mipmap, int slice, Map<String,String> options, DDS_HEADER header, DDS_BODY body) throws Exception {
		if(FORMAT==null) return;
		super.lwjglBody(FORMAT.gl,tx,ir,mipmap,slice,options,header,body);
	}

	@Override
	public void header(Texture tx, Texel ir, int mipmap, int slice, Map<String,String> options, DDS_HEADER header) throws Exception {

	}

}
