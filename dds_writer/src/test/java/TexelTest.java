

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.jme3.math.Vector4f;

import ddswriter.Pixel;
import ddswriter.Texel;
import ddswriter.Texel.PixelFormat;

public class TexelTest{
	
	@Test 
	public void testConversion(){
		Pixel pixels[][]=new Pixel[2][2];
		pixels[0][0]=new Pixel(PixelFormat.INT_RGBA,11,11,11,11);pixels[1][0]=new Pixel(PixelFormat.INT_RGBA,22,22,22,22);
		pixels[0][1]=new Pixel(PixelFormat.INT_RGBA,255,255,255,255);pixels[1][1]=new Pixel(PixelFormat.INT_RGBA,1,1,1,1);
		for(Pixel pxs[]:pixels){
			for(Pixel px:pxs){
				System.out.println(px);
			}
		}

		Texel texel=new Texel(pixels);

		Vector4f pixel1=texel.get(0,0).toVector4f(PixelFormat.FLOAT_NORMALIZED_RGBA);
		Vector4f pixel2=new Vector4f(11f/255,11f/255,11f/255,11f/255);
		assertTrue("RGBA8_INT 2 RGBA8_FLOAT: "+pixel1+" != "+pixel2,pixel1.equals(pixel2));

	}

}
