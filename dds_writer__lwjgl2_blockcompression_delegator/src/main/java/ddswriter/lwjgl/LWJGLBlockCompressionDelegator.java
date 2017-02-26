package ddswriter.lwjgl;

import static ddswriter.format.DDS_HEADER.DDSD_LINEARSIZE;
import static ddswriter.format.DDS_PIXELFORMAT.DDPF_FOURCC;
import static org.lwjgl.opengl.ARBTextureCompression.GL_TEXTURE_COMPRESSED_IMAGE_SIZE_ARB;
import static org.lwjgl.opengl.ARBTextureCompression.glGetCompressedTexImageARB;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glGetTexLevelParameteri;
import static org.lwjgl.opengl.GL11.glTexImage2D;

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

public abstract class LWJGLBlockCompressionDelegator extends CommonBodyDelegator{

	public void lwjglHeader(String internal_name, int blocksize, Texture tx, Map<String,String> options, DDS_HEADER header) throws Exception {
		super.header(tx,options,header);

		header.dwFlags|=DDSD_LINEARSIZE;
		header.ddspf.dwFlags|=DDPF_FOURCC;

		byte formatb[]=internal_name.getBytes();
		for(int i=0;i<formatb.length;i++)
			header.ddspf.dwFourCC[i]=formatb[i];

		int w=tx.getImage().getWidth();
		int h=tx.getImage().getHeight();
		header.dwPitchOrLinearSize=((w+3)/4)*((h+3)/4)*blocksize;

	}

	public void lwjglBody(int gl_format, Texture tx, Texel ir, int mipmap, int slice, Map<String,String> options, DDS_HEADER header, DDS_BODY body) throws Exception {

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
		glTexImage2D(GL_TEXTURE_2D,0,gl_format,pixels.length,pixels[0].length,0,GL_RGBA,GL_FLOAT,bbf);

		int out_size=glGetTexLevelParameteri(GL_TEXTURE_2D,0,GL_TEXTURE_COMPRESSED_IMAGE_SIZE_ARB);
		ByteBuffer out=BufferUtils.createByteBuffer(out_size);
		glGetCompressedTexImageARB(GL_TEXTURE_2D,0,out);
		byte bytes[]=new byte[out_size];
		out.rewind();
		out.get(bytes);
		body.write(bytes);
		BufferUtils.destroyDirectBuffer(out);

		BufferUtils.destroyDirectBuffer(bbf);
		glBindTexture(GL_TEXTURE_2D,0);
		glDeleteTextures(id);
	}


}
