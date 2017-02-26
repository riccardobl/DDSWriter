package ddswriter.delegators.s2tc;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.texture.image.ImageRaster;

import ddswriter.Texel;

public class TexelReduced extends Texel{
	protected final Vector4f[] PALETTE;
	
	public TexelReduced(Texel tx,Vector4f palette[]){
		super(PixelFormat.FLOAT_NORMALIZED_RGBA,tx.getPixels(PixelFormat.FLOAT_NORMALIZED_RGBA)); 
		PALETTE=new Vector4f[2];
		setPalette(PixelFormat.FLOAT_NORMALIZED_RGBA,palette);
	}

	public float map(int x, int y) {
		Vector4f px=get(PixelFormat.FLOAT_NORMALIZED_RGBA,x,y);
		Vector4f nearest_palette=PALETTE[0];
		int k=0;
		float d=TexelReducer.diff(px,nearest_palette);
		for(int i=1;i<PALETTE.length;i++){
			float d1=TexelReducer.diff(px,PALETTE[i]);
			if(d1<d){
				d=d1;
				nearest_palette=PALETTE[i];
				k=i;
			}
		}
		return k;
	}

	protected void setPalette(PixelFormat f, Vector4f[] palette) {
		palette=new Vector4f[]{convert(f,FORMAT,palette[0]),convert(f,FORMAT,palette[1])};
		boolean rv=palette[0].length()>palette[1].length();;
		if(rv){
			PALETTE[1]=palette[0];
			PALETTE[0]=palette[1];
		}else{
			PALETTE[0]=palette[0];
			PALETTE[1]=palette[1];
		}
	}

	public Vector4f[] getPalette(PixelFormat f) {
		return new Vector4f[]{convert(FORMAT,f,PALETTE[0]),convert(FORMAT,f,PALETTE[1])};
	}

}
