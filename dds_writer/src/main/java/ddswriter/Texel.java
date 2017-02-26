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

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.texture.image.ImageRaster;



/**
 * 
 * @author Riccardo Balbo
 *
 */

public class Texel implements Cloneable{
	public static enum PixelFormat{
		FLOAT_NORMALIZED_RGBA,INT_RGBA,PACKED_ARGB
	}

	protected final Vector4f[][] PIXELS;
	protected final PixelFormat FORMAT;
	protected Vector2f[] AREA;
	private static Vector4f PADDINGPX_COLOR=new Vector4f(1,1,0,1);

	@Override
	public Texel clone() {
		Texel cloned=Texel.fromTexel(FORMAT,this,new Vector2f(),new Vector2f(getWidth(),getHeight()));
		cloned.AREA=AREA;
		return cloned;
	}

	public static Texel fromImageRaster(ImageRaster ir, Vector2f from, Vector2f to) {
		Vector4f pixels[][]=new Vector4f[(int)(to.x-from.x)][(int)(to.y-from.y)];
		for(int y=(int)from.y;y<to.y;y++){
			for(int x=(int)from.x;x<to.x;x++){
				int xl=(int)(x-from.x);
				int yl=(int)(y-from.y);
				Vector4f c=x>=ir.getWidth()||y>=ir.getHeight()?PADDINGPX_COLOR:ir.getPixel(x,y).toVector4f();
				pixels[xl][yl]=c;
			}
		}
		Texel tx=new Texel(PixelFormat.FLOAT_NORMALIZED_RGBA,pixels);
		tx.AREA=new Vector2f[]{from,to

		};
		return tx;
	}

	public static Texel fromTexel(PixelFormat dest_format, Texel tx, Vector2f from, Vector2f to) {
		Vector4f pixels[][]=new Vector4f[(int)(to.x-from.x)][(int)(to.y-from.y)];
		for(int y=(int)from.y;y<to.y;y++){
			for(int x=(int)from.x;x<to.x;x++){
				int xl=(int)(x-from.x);
				int yl=(int)(y-from.y);
				Vector4f c=x>=tx.getWidth()||y>=tx.getHeight()?PADDINGPX_COLOR:tx.get(dest_format,x,y);
				pixels[xl][yl]=c;
			}
		}
		Texel tnx=new Texel(PixelFormat.FLOAT_NORMALIZED_RGBA,pixels);

		tnx.AREA=new Vector2f[]{from,to

		};
		return tnx;
	}
	public Texel(PixelFormat format,int w,int h){
		this(format,new Vector4f[w][h]);
	}
	public Texel(PixelFormat format,Vector4f pixels[][]){
		this(format,pixels,new Vector2f[]{new Vector2f(0,0),// from
				new Vector2f(pixels.length,pixels[0].length)//to
		});

	}

	public Texel(PixelFormat format,Vector4f pixels[][],Vector2f area[]){
		PIXELS=pixels;
		FORMAT=format;
		AREA=area;
	}

	protected Vector4f convert(PixelFormat from, PixelFormat to, Vector4f c) {
		if(from==to) return c.clone();
		if(from==PixelFormat.FLOAT_NORMALIZED_RGBA){
			switch(to){
				case INT_RGBA:{
					Vector4f out=c.clone();
					out.x=(int)(c.x*255f);
					out.y=(int)(c.y*255f);
					out.z=(int)(c.z*255f);
					out.w=(int)(c.w*255f);
					return out;
				}
				case PACKED_ARGB:{
					Vector4f out=convert(PixelFormat.FLOAT_NORMALIZED_RGBA,PixelFormat.INT_RGBA,c);
					out=convert(PixelFormat.INT_RGBA,PixelFormat.PACKED_ARGB,out);
					return out;
				}
			}
		}else if(from==PixelFormat.INT_RGBA){
			switch(to){
				case FLOAT_NORMALIZED_RGBA:{
					Vector4f out=c.clone();
					out.x=(c.x/255f);
					out.y=(c.y/255f);
					out.z=(c.z/255f);
					out.w=(c.w/255f);
					return out;
				}
				case PACKED_ARGB:{
					Vector4f out=new Vector4f();
					int p=(int)c.w<<24|(int)c.x<<16|(int)c.y<<8|(int)c.z;
					out.x=p;
					return out;
				}
			}
		}
		return null;
	}

	public void convertFormat(PixelFormat format) {
		for(int x=0;x<getWidth();x++){
			for(int y=0;y<getHeight();y++){
				PIXELS[x][y]=convert(FORMAT,format,PIXELS[x][y]);// todo
			}
		}
	}

//	public void genPalette() {
//		TexelReducer.reduce(this);
//	}

	public Vector4f get(PixelFormat f, int x, int y) {
		return convert(FORMAT,f,PIXELS[x][y]);
	}

	public void set(PixelFormat f, int x, int y, Vector4f c) {
		PIXELS[x][y]=convert(f,FORMAT,c).clone();
	}


	public int getWidth() {
		return PIXELS.length;
	}

	public int getHeight() {
		return PIXELS[0].length;
	}

	public Vector4f[][] getPixels(PixelFormat f) {
		Vector4f out[][]=new Vector4f[PIXELS.length][PIXELS[0].length];
		for(int y=0;y<PIXELS[0].length;y++){
			for(int x=0;x<PIXELS.length;x++){
				out[x][y]=get(f,x,y);				
			}
		}
		return out;
	}

	public Vector2f[] getArea() {
		return AREA;
	}

	public PixelFormat getFormat() {
		return FORMAT;
	}

	public void write(Texel dst) {
		write(dst,getArea()[0],getArea()[1]);
	}

	public void write(Texel dst, Vector2f from, Vector2f to) {
		for(int y=(int)from.y;y<to.y;y++){
			for(int x=(int)from.x;x<to.x;x++){
				int xl=(int)(x-from.x);
				int yl=(int)(y-from.y);
				Vector4f c=get(dst.getFormat(),xl,yl);
				dst.set(dst.getFormat(),x,y,c);
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
				Vector4f c=get(PixelFormat.FLOAT_NORMALIZED_RGBA,xl,yl);
				ColorRGBA crgba=new ColorRGBA(c.x,c.y,c.z,c.w);
				if(x>=dst.getWidth()||y>=dst.getHeight()) continue;
				dst.setPixel(x,y,crgba);
			}
		}
	}

	protected Texel[] MIPMAPS;
	public Texel[] getMipMap(int n, boolean regen) {
		if(MIPMAPS!=null&&n==MIPMAPS.length&&!regen)return MIPMAPS;
		MIPMAPS=TexelMipmapGenerator.generateMipMaps(this,n);
		return MIPMAPS;
	}

}