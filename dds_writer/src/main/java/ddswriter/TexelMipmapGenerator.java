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

import com.jme3.math.FastMath;
import com.jme3.math.Vector4f;

import ddswriter.Texel.PixelFormat;

public class TexelMipmapGenerator{


	

	static float BSpline( float x )
	{
		float f = x;
		if( f < 0.0f ) {
			f = -f;
		}
	  
		if( f >= 0.0f && f <= 1.0f ) {
			return ( 2.0f / 3.0f ) + ( 0.5f ) * ( f* f * f ) - (f*f);
		}
		else if( f > 1.0f && f <= 2.0f ) {
			return 1.0f / 6.0f * FastMath.pow( ( 2.0f - f  ), 3.0f );
		}
		return 1.0f;
	}

	public static float Triangular(float f) {
		f=f/2.0f;
		if(f<0.0f){
			return (f+1.0f);
		}else{
			return (1.0f-f);
		}
	}

	public static Texel scaleImage(Texel input, int outputWidth, int outputHeight, boolean srgb) {
		Texel output=new Texel(outputWidth,outputHeight);

		float xRatio=((float)(input.getWidth()))/output.getWidth();
		float yRatio=((float)(input.getHeight()))/output.getHeight();

		Vector4f tmp=new Vector4f();

		for(int yi=0;yi<outputHeight;yi++){
			for(int xi=0;xi<outputWidth;xi++){
				float nSumX=0,nSumY=0,nSumZ=0,nSumW=0;
				float nDenom=0;

				float x2f=xi*xRatio;
				float y2f=yi*yRatio;

				int x2=(int)x2f;
				int y2=(int)y2f;


				for(int m=0;m<2;m++){
					for(int n=0;n<2;n++){
						int offsetX=x2+m;
						int offsetY=y2+n;

						if(offsetX>input.getWidth()-1) offsetX=input.getWidth()-1;
						if(offsetY>input.getHeight()-1) offsetY=input.getHeight()-1;
						if(offsetX<0) offsetX=0;
						if(offsetY<0) offsetY=0;

						input.get(offsetX,offsetY).toVector4f(PixelFormat.FLOAT_NORMALIZED_RGBA,tmp);
						if(srgb){
							// linearize
							tmp.x = (float)Math.pow(tmp.x, 2.2f);
							tmp.y = (float)Math.pow(tmp.y, 2.2f);
							tmp.z = (float)Math.pow(tmp.z, 2.2f);
						}


						nSumX+=tmp.x;
						nSumY+=tmp.y;
						nSumZ+=tmp.z;
						nSumW+=tmp.w;

						nDenom++;
					}
				}

				nSumX/=nDenom;
				nSumY/=nDenom;
				nSumZ/=nDenom;
				nSumW/=nDenom;

				if(srgb){
					// to srgb
					nSumX= (float)Math.pow(nSumX, 1f/2.2f);
					nSumY = (float)Math.pow(nSumY, 1f/2.2f);
					nSumZ = (float)Math.pow(nSumZ, 1f/2.2f);
					//
				}
				Pixel outpx=new Pixel(PixelFormat.FLOAT_NORMALIZED_RGBA,nSumX,nSumY,nSumZ,nSumW);
				
				output.set(xi,yi,outpx);

			}
		}
		return output;
	}

	public static Texel[] generateMipMaps(Texel image, int n, boolean srgb) {
		Texel mipmaps[]=new Texel[n];
		int width=image.getWidth();
		int height=image.getHeight();
		Texel current=image;

		for(int i=0;i<n;i++){

			height/=2;
			width/=2;
			if(height<2) height=2;
			if(width<2) width=2;
			current=scaleImage(current,width,height,srgb);
			mipmaps[i]=current;
		}
		return mipmaps;
	}
}
