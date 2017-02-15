package ddswriter.delegators.s2tc;

import com.jme3.math.Vector4f;

import ddswriter.delegators.s2tc.Texel.PixelFormat;

public class RGB565{
	public static void convertTexel(Texel tx){
		int w=tx.getWidth();
		int h=tx.getHeight();
		for(int x=0;x<w;x++){
			for(int y=0;y<h;y++){
				Vector4f c=tx.get(PixelFormat.FLOAT_NORMALIZED_RGBA,x,y);
				c.x = Math.round(31.0f * c.x);
				c.y =Math.round (63.0f * c.y);
				c.z =Math.round( 31.0f * c.z);
				tx.set(PixelFormat.INT_RGBA,x,y,c);
			}
		}		
	}
	
	public static int packPixel(Vector4f c){
		return (((int)c.x) << 11)| (((int)c.y) << 5) | ((int)c.z);
	}
}
