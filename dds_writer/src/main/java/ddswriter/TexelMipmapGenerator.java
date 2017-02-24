package ddswriter;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector4f;
import com.jme3.texture.Image;
import com.jme3.texture.image.ImageRaster;
import com.jme3.util.BufferUtils;

import ddswriter.Texel.PixelFormat;

public class TexelMipmapGenerator{

	public static Texel scaleImage(Texel input, int outputWidth, int outputHeight) {
		Texel output=new Texel(PixelFormat.FLOAT_NORMALIZED_RGBA,outputWidth,outputHeight);

		float xRatio=((float)(input.getWidth()-1))/output.getWidth();
		float yRatio=((float)(input.getHeight()-1))/output.getHeight();

		Vector4f outputColor=new Vector4f();
		Vector4f bottomLeft=new Vector4f();
		Vector4f bottomRight=new Vector4f();
		Vector4f topLeft=new Vector4f();
		Vector4f topRight=new Vector4f();

		for(int y=0;y<outputHeight;y++){
			for(int x=0;x<outputWidth;x++){
				float x2f=x*xRatio;
				float y2f=y*yRatio;

				int x2=(int)x2f;
				int y2=(int)y2f;

				float xDiff=x2f-x2;
				float yDiff=y2f-y2;

				bottomLeft=input.get(PixelFormat.FLOAT_NORMALIZED_RGBA,x2,y2);
				bottomRight=input.get(PixelFormat.FLOAT_NORMALIZED_RGBA,x2+1,y2);
				topLeft=input.get(PixelFormat.FLOAT_NORMALIZED_RGBA,x2,y2+1);
				topRight=input.get(PixelFormat.FLOAT_NORMALIZED_RGBA,x2+1,y2+1);

				bottomLeft.multLocal((1f-xDiff)*(1f-yDiff));
				bottomRight.multLocal((xDiff)*(1f-yDiff));
				topLeft.multLocal((1f-xDiff)*(yDiff));
				topRight.multLocal((xDiff)*(yDiff));

				outputColor.set(bottomLeft).addLocal(bottomRight).addLocal(topLeft).addLocal(topRight);

				output.set(PixelFormat.FLOAT_NORMALIZED_RGBA,x,y,outputColor);
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
			if(height==1||width==1) break;
			height/=2;
			width/=2;
			//			}
			//				System.out.println("Gen mipmap "+width+"x"+height);
			current=scaleImage(current,width,height);
			mipmaps[i]=current;
		}
		return mipmaps;
	}
}
