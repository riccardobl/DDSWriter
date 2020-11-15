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

import ddswriter.Pixel;
import ddswriter.Texel.PixelFormat;
/**
 * 
 * @author Riccardo Balbo
 */
public class RGB8ColorBit implements ColorBit{

	@Override
	public byte getBPP() {
		return 24;
	}

	@Override
	public byte[] getBytes(Pixel float_color) {
		byte r=(byte)Math.ceil(float_color.r(PixelFormat.FLOAT_NORMALIZED_RGBA)*255f);
		byte g=(byte)Math.ceil(float_color.g(PixelFormat.FLOAT_NORMALIZED_RGBA)*255f);
		byte b=(byte)Math.ceil(float_color.b(PixelFormat.FLOAT_NORMALIZED_RGBA)*255f);
		return new byte[]{getBPP(),b,g,r};
	}



	@Override
	public int getAColorMask() {
		return 0;
	}

	@Override
	public int getRColorMask() {
		return 0xFF0000;
	}

	@Override
	public int getGColorMask() {
		return 0x00FF00;
	}

	@Override
	public int getBColorMask() {
		return 0x0000FF;
	}

	@Override
	public boolean hasAlpha() {
		return false;
	}

}
