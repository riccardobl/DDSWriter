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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.texture.Image;
import com.jme3.texture.image.ImageRaster;

/**
 * 
 * @author Riccardo Balbo
 *
 */

public class Texel implements Cloneable{
	protected static final Logger LOGGER=LogManager.getLogger(Texel.class);

	public static enum PixelFormat{
		FLOAT_NORMALIZED_RGBA,INT_RGBA,PACKED_ARGB
	}

	protected final Pixel[][] PIXELS;
	protected Vector2f[] AREA;
	private static Pixel PADDINGPX_COLOR=new Pixel(PixelFormat.FLOAT_NORMALIZED_RGBA ,1,1,0,1);

	@Override
	public Texel clone() {
		Texel cloned=Texel.fromTexel(this,new Vector2f(),new Vector2f(getWidth(),getHeight()));
		cloned.AREA=AREA;
		return cloned;
	}


	public static Texel fromImage(Image img, int slice,int mipmap) {
		ImageRaster ir=ImageRaster.create(img,slice,mipmap,false);
		int width=ir.getWidth();
		int height=ir.getHeight();

		Pixel pixels[][]=new Pixel[width][height];
		ColorRGBA tmp=new ColorRGBA();	
		Vector4f tmp2=new Vector4f();

		for(int y=0;y<height;y++){
			for(int x=0;x<width;x++){				
				Pixel c;
				if(x>=ir.getWidth()||y>=ir.getHeight()||x<0||y<0){
					c=PADDINGPX_COLOR;
				}else{
					ir.getPixel(x,y,tmp);
					tmp2.x=tmp.r;
					tmp2.y=tmp.g;
					tmp2.z=tmp.b;
					tmp2.w=tmp.a;
					c=new Pixel(PixelFormat.FLOAT_NORMALIZED_RGBA,tmp2);
				}
				pixels[x][y]=c;
			}		
		}
		Texel tx=new Texel(pixels);
		tx.AREA=new Vector2f[]{new Vector2f(0,0),new Vector2f(width,height)};
		return tx;
	}

	public static Texel fromTexel(Texel tx, Vector2f from, Vector2f to) {
		Pixel pixels[][]=new Pixel[(int)(to.x-from.x)][(int)(to.y-from.y)];
		for(int y=(int)from.y;y<to.y;y++){
			for(int x=(int)from.x;x<to.x;x++){
				int xl=(int)(x-from.x);
				int yl=(int)(y-from.y);

				Pixel c;
				if(x>=tx.getWidth()||y>=tx.getHeight()||x<0||y<0){
					LOGGER.warn("Invalid coordinates x{} y{} for texel w{} h{}. Use padding color.",x,y,tx.getWidth(),tx.getHeight());
					c=PADDINGPX_COLOR;

				}else{
					c=tx.get(x,y);
				}
				pixels[xl][yl]=c;
			}
		}
		Texel tnx=new Texel(pixels);

		tnx.AREA=new Vector2f[]{from,to

		};
		return tnx;
	}

	public Texel(int w,int h){
		this(new Pixel[w][h]);
	}

	public Texel(Pixel pixels[][]){
		this(pixels,new Vector2f[]{new Vector2f(0,0),// from
				new Vector2f(pixels.length,pixels[0].length)//to
		});

	}

	public Texel(Pixel pixels[][],Vector2f area[]){
		PIXELS=pixels;
		AREA=area;
	}



	public Pixel get(int x, int y) {
		return PIXELS[x][y];
	}

	public void set(int x, int y, Pixel c) {
		PIXELS[x][y]=c;
	}

	public int getWidth() {
		return PIXELS.length;
	}

	public int getHeight() {
		return PIXELS[0].length;
	}

	public Pixel[][] getPixels() {
		Pixel out[][]=new Pixel[PIXELS.length][PIXELS[0].length];
		for(int y=0;y<PIXELS[0].length;y++){
			for(int x=0;x<PIXELS.length;x++){
				out[x][y]=get(x,y);
			}
		}
		return out;
	}

	public Vector2f[] getArea() {
		return AREA;
	}

	

	public void write(Texel dst) {
		write(dst,getArea()[0],getArea()[1]);
	}

	public void write(Texel dst, Vector2f from, Vector2f to) {
		for(int y=(int)from.y;y<to.y;y++){
			for(int x=(int)from.x;x<to.x;x++){
				int xl=(int)(x-from.x);
				int yl=(int)(y-from.y);
				Pixel c=get(xl,yl);
				dst.set(x,y,c);
			}
		}
	}

	public void write(ImageRaster dst) {
		write(dst,getArea()[0],getArea()[1]);
	}

	public void write(ImageRaster dst, Vector2f from, Vector2f to) {
		for(int y=(int)from.y;y<to.y;y++){
			for(int x=(int)from.x;x<to.x;x++){
				int xl=(int)(x-from.x);
				int yl=(int)(y-from.y);
				Pixel c=get(xl,yl);
				ColorRGBA crgba=new ColorRGBA(c.r(PixelFormat.FLOAT_NORMALIZED_RGBA),c.g(PixelFormat.FLOAT_NORMALIZED_RGBA),c.b(PixelFormat.FLOAT_NORMALIZED_RGBA),c.a(PixelFormat.FLOAT_NORMALIZED_RGBA));
				if(x>=dst.getWidth()||y>=dst.getHeight()) continue;
				dst.setPixel(x,y,crgba);
			}
		}
	}

	protected Texel[] MIPMAPS;

	public Texel[] getMipMap(int n, boolean regen,boolean srgb) {
		if(MIPMAPS!=null&&n==MIPMAPS.length&&!regen) return MIPMAPS;
		MIPMAPS=TexelMipmapGenerator.generateMipMaps(this,n,srgb);
		return MIPMAPS;
	}

}