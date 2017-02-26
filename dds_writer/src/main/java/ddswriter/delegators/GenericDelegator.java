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
package ddswriter.delegators;

import static ddswriter.format.DDS_HEADER.DDSD_PITCH;
import static ddswriter.format.DDS_PIXELFORMAT.DDPF_ALPHAPIXELS;
import static ddswriter.format.DDS_PIXELFORMAT.DDPF_RGB;

import java.util.Map;

import com.jme3.math.Vector4f;
import com.jme3.texture.Texture;

import ddswriter.Texel;
import ddswriter.Texel.PixelFormat;
import ddswriter.encoders.ARGB8ColorBit;
import ddswriter.encoders.ColorBit;
import ddswriter.encoders.RGB565ColorBit;
import ddswriter.encoders.RGB8ColorBit;
import ddswriter.format.DDS_BODY;
import ddswriter.format.DDS_HEADER;

/**
 * 
 * @author Riccardo Balbo
 */
public class GenericDelegator extends CommonBodyDelegator{
	public static enum Format{
		ARGB8(new ARGB8ColorBit()),RGB8(new RGB8ColorBit()),

		RGB565(new RGB565ColorBit());

		public ColorBit colorbit;

		private Format(ColorBit bc){
			colorbit=bc;
		}

	}

	protected Format FORMAT;

	@Override
	public void header(Texture tx, Map<String,String> options, DDS_HEADER header) throws Exception {
		String format=((String)options.get("format"));
		
		if(format==null){
			format="ARGB8";

		}else{
			format=format.toUpperCase();
		}

		for(Format f:Format.values()){
			if(f.toString().equals(format)) FORMAT=f;
		}

		if(FORMAT==null){
			System.out.println(this.getClass()+" does not support "+format+". skip");
			skip();
			return;
		}

		System.out.println("Use "+this.getClass()+"  with format "+format+". ");
		super.header(tx,options,header);

		header.dwFlags|=DDSD_PITCH;
		header.dwPitchOrLinearSize=(tx.getImage().getWidth()*FORMAT.colorbit.getBPP()+7)/8;

		header.ddspf.dwRGBBitCount=FORMAT.colorbit.getBPP();

		header.ddspf.dwRBitMask=FORMAT.colorbit.getRColorMask();
		header.ddspf.dwGBitMask=FORMAT.colorbit.getGColorMask();
		header.ddspf.dwBBitMask=FORMAT.colorbit.getBColorMask();
		if(FORMAT.colorbit.hasAlpha()){
			header.ddspf.dwFlags|=DDPF_ALPHAPIXELS;
			header.ddspf.dwABitMask=FORMAT.colorbit.getAColorMask();
		}

		header.ddspf.dwFlags|=DDPF_RGB;

	}

	@Override
	public void body(Texture tx, Texel ir, int mipmap, int slice, Map<String,String> options, DDS_HEADER header, DDS_BODY body) throws Exception {
		if(FORMAT==null) return;
		int w=ir.getWidth();
		int h=ir.getHeight();
		for(int y=0;y<h;y++){
			for(int x=0;x<w;x++){
				Vector4f c=ir.get(PixelFormat.FLOAT_NORMALIZED_RGBA,x,y);
				body.writeColorBit(FORMAT.colorbit.getBytes(c));
			}
		}
	}

	@Override
	public void header(Texture tx, Texel ir, int mipmap, int slice, Map<String,String> options, DDS_HEADER header) throws Exception {

	}

}
