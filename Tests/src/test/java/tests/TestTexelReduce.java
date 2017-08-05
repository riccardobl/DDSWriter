package tests;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.texture.Image;
import com.jme3.texture.Texture2D;
import com.jme3.texture.image.ImageRaster;
import com.jme3.texture.plugins.AWTLoader;

import ddswriter.DDSDelegate;
import ddswriter.DDSWriter;
import ddswriter.Texel;
import ddswriter.Texel.PixelFormat;
import ddswriter.delegates.GenericDelegate;
import ddswriter.delegates.s2tc.S2tcDelegate;
import ddswriter.delegates.s2tc.TexelReducer;

public class TestTexelReduce{
	public static void main(String[] args) throws Exception {
		TestUtils.extractResources();

		InputStream is=new BufferedInputStream(new FileInputStream(		TestUtils.tmpPath("texture2D.jpg")));
		AWTLoader loader=new AWTLoader();
		Image img=loader.load(is,false);
		is.close();

		ImageRaster ir=ImageRaster.create(img);
		int subsample[]=new int[]{4,4};

		for(int x=0;x<ir.getWidth();x+=subsample[0]){
			for(int y=0;y<ir.getHeight();y+=subsample[1]){
				Texel tx=Texel.fromImageRaster(ir,new Vector2f(x,y),new Vector2f(x+subsample[0],y+subsample[1]));
				TexelReducer.reduce(tx,true);

				Vector4f ca=tx.get(PixelFormat.FLOAT_NORMALIZED_RGBA,0,0);
				Vector4f cb=null;
				for(int xx=0;xx<tx.getWidth();xx++){
					for(int yx=0;yx<tx.getHeight();yx++){
						Vector4f xyt=tx.get(PixelFormat.FLOAT_NORMALIZED_RGBA,xx,yx);
						if(!xyt.equals(ca)){
							if(cb==null) cb=xyt;
							else if(!cb.equals(xyt)){
								System.out.println("Palette is wrong. 3 colors found :"+ca+" "+cb+" "+xyt);
							}
						}

					}
				}
				tx.write(ir);
			}
		}

		Map<String,String> options=new HashMap<String,String>();
		options.put("format","ARGB8");
		OutputStream fo=new BufferedOutputStream(new FileOutputStream(new File(TestUtils.tmpPath("texture2D_REDUCED.dds"))));
		ArrayList<DDSDelegate> delegates=new ArrayList<DDSDelegate>();
		delegates.add(new S2tcDelegate());
		delegates.add(new GenericDelegate());

		DDSWriter.write(new Texture2D(img),options,delegates,fo);
		fo.close();
		//		BufferedOutputStream out=new BufferedOutputStream(new FileOutputStream("/tmp/reduced.jpg"));
		//		BufferedImage bimg=ImageToAwt.convert(img,false,true,0);
		//		ImageIO.write(bimg,"bmp",out);

		//		out.close();
	}
}