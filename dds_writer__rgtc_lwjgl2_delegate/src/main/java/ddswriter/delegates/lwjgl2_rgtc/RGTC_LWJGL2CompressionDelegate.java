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
package ddswriter.delegates.lwjgl2_rgtc;

import static org.lwjgl.opengl.GL30.GL_COMPRESSED_RED_RGTC1;
import static org.lwjgl.opengl.GL30.GL_COMPRESSED_RG_RGTC2;

import java.util.Map;

import com.jme3.texture.Texture;

import ddswriter.Texel;
import ddswriter.delegates.lwjgl2.LWJGLBlockCompressionDelegate;
import ddswriter.format.DDS_BODY;
import ddswriter.format.DDS_HEADER;

/**
 * 	
 * @author Riccardo Balbo
 */

public class RGTC_LWJGL2CompressionDelegate extends LWJGLBlockCompressionDelegate{

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
