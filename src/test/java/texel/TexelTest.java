package texel;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.jme3.math.Vector4f;

import ddswriter.Texel;
import ddswriter.Texel.PixelFormat;

public class TexelTest{
	
	@Test 
	public void testConversion(){
		Vector4f pixels[][]=new Vector4f[2][2];
		pixels[0][0]=new Vector4f(11,11,11,11);pixels[1][0]=new Vector4f(22,22,22,22);
		pixels[0][1]=new Vector4f(255,255,255,255);pixels[1][1]=new Vector4f(1,1,1,1);
		for(Vector4f pxs[]:pixels){
			for(Vector4f px:pxs){
				System.out.println(px);
			}
		}

		Texel texel=new Texel(PixelFormat.INT_RGBA,pixels);

		Vector4f pixel1=texel.get(PixelFormat.FLOAT_NORMALIZED_RGBA,0,0);
		Vector4f pixel2=new Vector4f(11f/255,11f/255,11f/255,11f/255);
		assertTrue("RGBA8_INT 2 RGBA8_FLOAT: "+pixel1+" != "+pixel2,pixel1.equals(pixel2));

	}

}
