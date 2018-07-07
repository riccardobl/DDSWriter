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

package ddswriter;

import com.jme3.math.Vector4f;

import ddswriter.Texel.PixelFormat;

public class TexelMipmapGenerator{
	private static Vector4f bottomLeft=new Vector4f();
	private static Vector4f bottomRight=new Vector4f();
	private	static Vector4f topLeft=new Vector4f();
	private static Vector4f topRight=new Vector4f();

	public static Texel scaleImage(Texel input, int outputWidth, int outputHeight) {
		Texel output=new Texel(outputWidth,outputHeight);

		float xRatio=((float)(input.getWidth()-1))/output.getWidth();
		float yRatio=((float)(input.getHeight()-1))/output.getHeight();
	

		for(int y=0;y<outputHeight;y++){
			for(int x=0;x<outputWidth;x++){
				float x2f=x*xRatio;
				float y2f=y*yRatio;

				int x2=(int)x2f;
				int y2=(int)y2f;

				float xDiff=x2f-x2;
				float yDiff=y2f-y2;

				input.get(x2,y2).toVector4f(PixelFormat.FLOAT_NORMALIZED_RGBA,bottomLeft);
				input.get(x2+1,y2).toVector4f(PixelFormat.FLOAT_NORMALIZED_RGBA,bottomRight);
				input.get(x2,y2+1).toVector4f(PixelFormat.FLOAT_NORMALIZED_RGBA,topLeft);
				input.get(x2+1,y2+1).toVector4f(PixelFormat.FLOAT_NORMALIZED_RGBA,topRight);

				bottomLeft.multLocal((1f-xDiff)*(1f-yDiff));
				bottomRight.multLocal((xDiff)*(1f-yDiff));
				topLeft.multLocal((1f-xDiff)*(yDiff));
				topRight.multLocal((xDiff)*(yDiff));

				Pixel outpx=new Pixel(PixelFormat.FLOAT_NORMALIZED_RGBA,null);
				outpx.getRawPixel().set(bottomLeft).addLocal(bottomRight).addLocal(topLeft).addLocal(topRight);
				output.set(x,y,outpx);
			}
		}
		return output;
	}

	public static Texel[] generateMipMaps(Texel image, int n) {
		//		System.out.println("Gen "+n+" mipmaps");
		Texel mipmaps[]=new Texel[n];
		int width=image.getWidth();
		int height=image.getHeight();
		Texel current=image;

		for(int i=0;i<n;i++){

			height/=2;
			width/=2;
			if(height<2)height=2;
			if(width<2) width=2;
			//			}
			//				System.out.println("Gen mipmap "+width+"x"+height);
			current=scaleImage(current,width,height);
			mipmaps[i]=current;
		}
		return mipmaps;
	}
}
