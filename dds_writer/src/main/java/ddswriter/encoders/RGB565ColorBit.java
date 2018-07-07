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
package ddswriter.encoders;

import com.jme3.math.Vector4f;

import ddswriter.Pixel;
import ddswriter.Texel;
import ddswriter.Texel.PixelFormat;
/**
 * 
 * @author Riccardo Balbo
 */
public class RGB565ColorBit implements ColorBit{
	// public static void convertTexel(Texel tx){
	// 	int w=tx.getWidth();
	// 	int h=tx.getHeight();
	// 	for(int x=0;x<w;x++){
	// 		for(int y=0;y<h;y++){
	// 			Pixel c=tx.get(x,y);
	// 			c=convertPixel(c);
	// 			tx.set(x,y,c);
	// 		}
	// 	}		
	// }
	
	public static Pixel convertPixel(Pixel float_color){
		// Vector4f c=float_color.toVector4f(PixelFormat.FLOAT_NORMALIZED_RGBA);
		float r = Math.round(31.0f *float_color.r(PixelFormat.FLOAT_NORMALIZED_RGBA));
		float g=Math.round (63.0f * float_color.g(PixelFormat.FLOAT_NORMALIZED_RGBA));
		float b =Math.round( 31.0f * float_color.b(PixelFormat.FLOAT_NORMALIZED_RGBA));
		return new Pixel(PixelFormat.INT_RGBA,r,g,b,1.0f);
	}
	
	public static int packPixel(Pixel c){
		return (((int)c.r(PixelFormat.INT_RGBA)) << 11)| (((int)c.g(PixelFormat.INT_RGBA)) << 5) | ((int)c.b(PixelFormat.INT_RGBA));
	}

	@Override
	public byte getBPP() {
		return 16;
	}

	@Override
	public byte[] getBytes(Pixel float_color) {
		int c=packPixel(convertPixel(float_color));
		byte out[]=new byte[3];
		out[0]=getBPP();
		out[1]=(byte)(c&0xff);
		c>>=8;
		out[2]=(byte)(c&0xff);
		return out;
	}
	

	@Override
	public int getAColorMask() {
		return 0;
	}

	@Override
	public int getRColorMask() {
		return 0b1111100000000000;
	}

	@Override
	public int getGColorMask() {
		return 0b0000011111100000;
	}

	@Override
	public int getBColorMask() {
		return 0b0000000000011111;
	}

	@Override
	public boolean hasAlpha() {
		return false;
	}


}
