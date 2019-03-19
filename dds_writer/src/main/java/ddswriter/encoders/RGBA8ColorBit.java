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
import ddswriter.Texel.PixelFormat;
/**
 * 
 * @author Riccardo Balbo
 */
public class RGBA8ColorBit implements ColorBit{

	@Override
	public byte getBPP() {
		return 32;
	}

	@Override
	public byte[] getBytes(Pixel float_color) {
		int b=(int)(float_color.b(PixelFormat.FLOAT_NORMALIZED_RGBA)*255f);
		int g=(int)(float_color.g(PixelFormat.FLOAT_NORMALIZED_RGBA)*255f);
		int r=(int)(float_color.r(PixelFormat.FLOAT_NORMALIZED_RGBA)*255f);
		int a=(int)(float_color.a(PixelFormat.FLOAT_NORMALIZED_RGBA)*255f);
		byte out[]=new byte[5];
		out[0]=getBPP();
		out[1]=(byte)a;
		out[2]=(byte)b;
		out[3]=(byte)g;
		out[4]=(byte)r;
		return out;
	}

	
	@Override
	public int getAColorMask() {
		return 0x000000FF;
	}

	@Override
	public int getRColorMask() {
		return 0xFF000000;
	}

	@Override
	public int getGColorMask() {
		return 0x00FF0000;
	}

	@Override
	public int getBColorMask() {
		return 0x0000FF00;
	}

	@Override
	public boolean hasAlpha() {
		return true;
	}

}
