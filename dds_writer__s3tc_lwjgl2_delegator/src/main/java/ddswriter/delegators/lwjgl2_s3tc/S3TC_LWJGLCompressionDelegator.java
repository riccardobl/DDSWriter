package ddswriter.delegators.lwjgl2_s3tc;

import static ddswriter.format.DDS_HEADER.DDSD_LINEARSIZE;
import static ddswriter.format.DDS_PIXELFORMAT.DDPF_FOURCC;
import static org.lwjgl.opengl.ARBTextureCompression.GL_TEXTURE_COMPRESSED_IMAGE_SIZE_ARB;
import static org.lwjgl.opengl.ARBTextureCompression.glGetCompressedTexImageARB;
import static org.lwjgl.opengl.EXTTextureCompressionS3TC.GL_COMPRESSED_RGBA_S3TC_DXT3_EXT;
import static org.lwjgl.opengl.EXTTextureCompressionS3TC.GL_COMPRESSED_RGB_S3TC_DXT1_EXT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glGetTexLevelParameteri;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL30.GL_COMPRESSED_RG_RGTC2;
import static org.lwjgl.opengl.GL30.GL_COMPRESSED_RED_RGTC1;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Map;

import com.jme3.math.Vector4f;
import com.jme3.texture.Texture;
import com.jme3.util.BufferUtils;

import ddswriter.Texel;
import ddswriter.Texel.PixelFormat;
import ddswriter.delegators.CommonBodyDelegator;
import ddswriter.delegators.lwjgl2.LWJGLBlockCompressionDelegator;
import ddswriter.format.DDS_BODY;
import ddswriter.format.DDS_HEADER;

/**
 * 	
 * @author Riccardo Balbo
 */


public class S3TC_LWJGLCompressionDelegator extends LWJGLBlockCompressionDelegator{
	protected Format FORMAT; 

	public static enum Format{		
		S3TC_DXT1("DXT1",8,GL_COMPRESSED_RGB_S3TC_DXT1_EXT,"BC1","DXT1"),
		S3TC_DXT3("DXT3",16,GL_COMPRESSED_RGBA_S3TC_DXT3_EXT,"BC2","DXT3"),
		S3TC_DXT5("DXT5",16,GL_COMPRESSED_RGBA_S3TC_DXT3_EXT,"BC3","DXT5");
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




	@Override
	public void header(Texture tx, Map<String,String> options, DDS_HEADER header) throws Exception {

		String format=((String)options.get("format"));
		if(format==null) {
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
		
		if(FORMAT==null) {
			skip();
			System.out.println(this.getClass()+" does not support "+format+". skip");
			return;
		}
		
		System.out.println("Use "+this.getClass()+"  with format "+format+". ");

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
