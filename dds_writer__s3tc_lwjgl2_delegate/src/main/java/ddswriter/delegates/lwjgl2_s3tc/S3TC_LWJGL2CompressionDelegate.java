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
package ddswriter.delegates.lwjgl2_s3tc;

import static org.lwjgl.opengl.EXTTextureCompressionS3TC.GL_COMPRESSED_RGBA_S3TC_DXT3_EXT;
import static org.lwjgl.opengl.EXTTextureCompressionS3TC.GL_COMPRESSED_RGB_S3TC_DXT1_EXT;
import static org.lwjgl.opengl.EXTTextureSRGB.*;
import java.util.Map;

import com.jme3.texture.Texture;

import ddswriter.Texel;
import ddswriter.delegates.lwjgl2.LWJGLBlockCompressionDelegate;
import ddswriter.format.DDS_BODY;
import ddswriter.format.DDS_HEADER;
import static ddswriter.format.DDS_HEADER.NSD_IS_LINEAR;

/**
 * 	
 * @author Riccardo Balbo
 */


public class S3TC_LWJGL2CompressionDelegate extends LWJGLBlockCompressionDelegate{
	protected Format FORMAT; 
	protected boolean SRGB;
	public static enum Format{		
		S3TC_DXT1("DXT1",8,GL_COMPRESSED_RGB_S3TC_DXT1_EXT,GL_COMPRESSED_SRGB_S3TC_DXT1_EXT,"BC1","DXT1","S3TC_DXT1"),
		S3TC_DXT3("DXT3",16,GL_COMPRESSED_RGBA_S3TC_DXT3_EXT,GL_COMPRESSED_SRGB_ALPHA_S3TC_DXT3_EXT,"BC2","DXT3","S3TC_DXT3"),
		S3TC_DXT5("DXT5",16,GL_COMPRESSED_RGBA_S3TC_DXT3_EXT,GL_COMPRESSED_SRGB_ALPHA_S3TC_DXT5_EXT,"BC3","DXT5","S3TC_DXT5");
		public String internal_name;
		public int gl,blocksize;
		public String[] aliases;
		public int gl_srgb;
		private Format(String s,int blocksize,int gl,int glsrgb,String... aliases){
			this.internal_name=s;
			this.gl=gl;
			this.blocksize=blocksize;
			this.gl_srgb=glsrgb;
			this.aliases=aliases;
		}


	}


	@Override
	public void end() {
		super.end();
		FORMAT=null;
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
			return;
		}
		
		System.out.println("Use "+this.getClass()+"  with format "+format+". ");

		super.lwjglHeader(FORMAT.internal_name,FORMAT.blocksize,tx,options,header);
		
	}

	@Override
	public void body(Texture tx, Texel ir, int mipmap, int slice, Map<String,String> options, DDS_HEADER header, DDS_BODY body) throws Exception {
		if(FORMAT==null) return;
		super.lwjglBody(SRGB?FORMAT.gl_srgb:FORMAT.gl,tx,ir,mipmap,slice,options,header,body);
	}

	@Override
	public void header(Texture tx, Texel ir, int mipmap, int slice, Map<String,String> options, DDS_HEADER header) throws Exception {

	}

}
