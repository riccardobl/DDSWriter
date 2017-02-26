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

import ddswriter.Texel;
import ddswriter.Texel.PixelFormat;
/**
 * 
 * @author Riccardo Balbo
 */
public class RGB565ColorBit implements ColorBit{
	public static void convertTexel(Texel tx){
		int w=tx.getWidth();
		int h=tx.getHeight();
		for(int x=0;x<w;x++){
			for(int y=0;y<h;y++){
				Vector4f c=tx.get(PixelFormat.FLOAT_NORMALIZED_RGBA,x,y);
				c=convertPixel(c);
				tx.set(PixelFormat.INT_RGBA,x,y,c);
			}
		}		
	}
	
	public static Vector4f convertPixel(Vector4f float_color){
		Vector4f c=float_color.clone();
		c.x = Math.round(31.0f * c.x);
		c.y =Math.round (63.0f * c.y);
		c.z =Math.round( 31.0f * c.z);
		return c;
	}
	
	public static int packPixel(Vector4f c){
		return (((int)c.x) << 11)| (((int)c.y) << 5) | ((int)c.z);
	}

	@Override
	public byte getBPP() {
		return 16;
	}

	@Override
	public byte[] getBytes(Vector4f float_color) {
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
