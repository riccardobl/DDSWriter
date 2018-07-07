/**
Copyright 2017 Riccardo Balbo

Permission is hereby granted, free of charge, to any person obtaining a copy of this software 
and associated documentation files (the "Software"), to deal in the Software without restriction, 
including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, 
and/or sell copies of the Software, and to permit persons to whom the Software is furnished 
to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE 
USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
package ddswriter.delegates.lwjgl2;

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
import static org.lwjgl.opengl.GL13.glGetCompressedTexImage;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Map;

import org.lwjgl.opengl.GL13;

import com.jme3.math.Vector4f;
import com.jme3.texture.Texture;
import com.jme3.util.BufferUtils;

import ddswriter.Pixel;
import ddswriter.Texel;
import ddswriter.Texel.PixelFormat;
import ddswriter.delegates.CommonBodyDelegate;
import ddswriter.format.DDS_BODY;
import ddswriter.format.DDS_HEADER;

/**
 * 
 * @author Riccardo Balbo
 *
 */
public abstract class LWJGLBlockCompressionDelegate extends CommonBodyDelegate{

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
		Pixel pixels[][]=ir.getPixels();

		FloatBuffer bbf=BufferUtils.createFloatBuffer(pixels.length*pixels[0].length*4);
		for(int y=0;y<pixels[0].length;y++){
			for(int x=0;x<pixels.length;x++){
				bbf.put(pixels[x][y].r(PixelFormat.FLOAT_NORMALIZED_RGBA));
				bbf.put(pixels[x][y].g(PixelFormat.FLOAT_NORMALIZED_RGBA));
				bbf.put(pixels[x][y].b(PixelFormat.FLOAT_NORMALIZED_RGBA));
				bbf.put(pixels[x][y].a(PixelFormat.FLOAT_NORMALIZED_RGBA));
			}
		}
		bbf.rewind();
		glTexImage2D(GL_TEXTURE_2D,0,gl_format,pixels.length,pixels[0].length,0,GL_RGBA,GL_FLOAT,bbf);

		int out_size=glGetTexLevelParameteri(GL_TEXTURE_2D,0,GL_TEXTURE_COMPRESSED_IMAGE_SIZE_ARB);
		ByteBuffer out=BufferUtils.createByteBuffer(out_size);
//		glGetCompressedTexImageARB(GL_TEXTURE_2D,0,out);
		glGetCompressedTexImage(GL_TEXTURE_2D,0,out);
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
