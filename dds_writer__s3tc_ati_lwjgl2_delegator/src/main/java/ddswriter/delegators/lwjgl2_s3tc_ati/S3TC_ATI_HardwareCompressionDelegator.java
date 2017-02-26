package ddswriter.delegators.lwjgl2_s3tc_ati;

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
import ddswriter.format.DDS_BODY;
import ddswriter.format.DDS_HEADER;

/**
 * 	
 * @author Riccardo Balbo
 */


public class S3TC_ATI_HardwareCompressionDelegator extends CommonBodyDelegator{

	public static enum Format{		
		S3TC_DXT1("DXT1",8,GL_COMPRESSED_RGB_S3TC_DXT1_EXT,"BC1","DXT1"),
		S3TC_DXT3("DXT3",16,GL_COMPRESSED_RGBA_S3TC_DXT3_EXT,"BC2","DXT3"),
		S3TC_DXT5("DXT5",16,GL_COMPRESSED_RGBA_S3TC_DXT3_EXT,"BC3","DXT5"),
		ATI1("ATI1",8,GL_COMPRESSED_RED_RGTC1,"ATI_3DC+","BC4","3DC+"),
		ATI2("ATI2",16,GL_COMPRESSED_RG_RGTC2,"ATI_3DC","BC5","3DC");
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
		super.header(tx,options,header);

		String format=((String)options.get("format"));
		if(format==null) {
			SKIP=true;
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
			SKIP=true;
			System.out.println(this.getClass()+" does not support "+format+". skip");
			return;
		}
		
		System.out.println("Use "+this.getClass()+"  with format "+format+". ");


		header.dwFlags|=DDSD_LINEARSIZE;
		header.ddspf.dwFlags|=DDPF_FOURCC;

		byte formatb[]=FORMAT.internal_name.getBytes();
		for(int i=0;i<formatb.length;i++)
			header.ddspf.dwFourCC[i]=formatb[i];

		int w=tx.getImage().getWidth();
		int h=tx.getImage().getHeight();
		header.dwPitchOrLinearSize=((w+3)/4)*((h+3)/4)*FORMAT.blocksize;

	}

	@Override
	public void body(Texture tx, Texel ir, int mipmap, int slice, Map<String,String> options, DDS_HEADER header, DDS_BODY body) throws Exception {
		if(FORMAT==null) return;

		IntBuffer intBuf1=BufferUtils.createIntBuffer(1);
		glGenTextures(intBuf1);
		int id=intBuf1.get(0);

		glBindTexture(GL_TEXTURE_2D,id);

		Vector4f pixels[][]=ir.getPixels(PixelFormat.FLOAT_NORMALIZED_RGBA);

		FloatBuffer bbf=BufferUtils.createFloatBuffer(pixels.length*pixels[0].length*4);
		for(int y=0;y<pixels[0].length;y++){
			for(int x=0;x<pixels.length;x++){
				bbf.put(pixels[x][y].x);
				bbf.put(pixels[x][y].y);
				bbf.put(pixels[x][y].z);
				bbf.put(pixels[x][y].w);
			}
		}
		bbf.rewind();
		glTexImage2D(GL_TEXTURE_2D,0,FORMAT.gl,pixels.length,pixels[0].length,0,GL_RGBA,GL_FLOAT,bbf);
		
//		int compressed=glGetTexLevelParameteri(GL_TEXTURE_2D,0,FORMAT.gl);
		
//		if(compressed==GL_TRUE){
			int out_size=glGetTexLevelParameteri(GL_TEXTURE_2D,0,GL_TEXTURE_COMPRESSED_IMAGE_SIZE_ARB);
			ByteBuffer out=BufferUtils.createByteBuffer(out_size);			
			glGetCompressedTexImageARB(GL_TEXTURE_2D,0,out);
			byte bytes[]=new byte[out_size];
			out.rewind();
			out.get(bytes);
			body.write(bytes);
			BufferUtils.destroyDirectBuffer(out);
//		}else{
//			Util.checkGLError();
//			throw new Exception("Can't do hw compression for format "+FORMAT.internal_name);
//
//		}
		
		BufferUtils.destroyDirectBuffer(bbf);
		glBindTexture(GL_TEXTURE_2D,0);
		glDeleteTextures(id);
	}

	@Override
	public void header(Texture tx, Texel ir, int mipmap, int slice, Map<String,String> options, DDS_HEADER header) throws Exception {

	}

}
