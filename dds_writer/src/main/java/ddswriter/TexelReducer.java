package ddswriter;

import java.util.ArrayList;
import java.util.HashMap;

import com.jme3.math.FastMath;
import com.jme3.math.Vector4f;

import ddswriter.Texel.PixelFormat;
/**
 * 
 * @author Lorenzo Catania, Riccardo Balbo
 */

public class TexelReducer{
	public static final float BASE_DIFF=diff(new Vector4f(1f,0f,0f,1f),new Vector4f(0f,0f,1f,1f));

//	public static float val(Vector4f c) {
//
//		return (0.2126f*c.x+0.7152f*c.y+0.0722f*c.z);
//	}

	public static float diff(Vector4f a, Vector4f b) {
		//		return val(b)-val(a);
		int ia[]={(int)(a.x*255f), (int)(a.y*255f), (int)(a.z*255f)};
		int ib[]={(int)(b.x*255f), (int)(b.y*255f), (int)(b.z*255f)};
		
		return color_dist_srgb_mixed(ia,ib);
	}

	public static int SHRR(int a, int n) {
		return (((a)+(1<<((n)-1)))>>(n));
	}

	public static int srgb_get_y(int a[]) {
		// convert to linear
		int r=a[0]*(int)a[0];
		int g=a[1]*(int)a[1];
		int b=a[2]*(int)a[2];
		// find luminance
		int y=37*(r*21*2*2+g*72+b*7*2*2); // multiplier: 14555800
		// square root it (!)
		y=(int)(FastMath.sqr(y)+0.5f); // now in range 0 to 3815
		return y;
	}

	public static int color_dist_srgb_mixed(int a[], int b[]) {
		// get Y
		int ay=srgb_get_y(a);
		int by=srgb_get_y(b);
		// get UV
		int au=a[0]*191-ay;
		int av=a[2]*191-ay;
		int bu=b[0]*191-by;
		int bv=b[2]*191-by;
		// get differences
		int y=ay-by;
		int u=au-bu;
		int v=av-bv;
		return ((y*y)<<3)+SHRR(u*u,1)+SHRR(v*v,2);
		// weight for u: ???
		// weight for v: ???
	}
	
	public static void reduce2(Texel texel) {
		int w=texel.getWidth();
		int h=texel.getHeight();

		Vector4f palette[]=new Vector4f[2];// Works only for 2.

		int loop=2;
		for(int l_id=0;l_id<loop;l_id++){
			for(int palette_i=0;palette_i<palette.length;palette_i++){
				int a_i=palette_i-1;
				if(a_i<0) a_i=palette.length+a_i;

				Vector4f base=palette[a_i];
				Vector4f best_pick=palette[palette_i];
				float d;
				if(base==null||best_pick==null) d=0;
				else d=diff(base,best_pick);

				for(int x=0;x<w;x++){
					for(int y=0;y<h;y++){
						Vector4f px=texel.getPixelRGBA(x,y);
						if(base==null){
							base=px;
							best_pick=px;
							continue;
						}

						float d1=diff(px,base);
						if(d1>d||best_pick==null){
							d=d1;
							best_pick=px;
						}
					}
				}

				palette[palette_i]=best_pick;
			}
		}

		//		boolean dithering=true;
		//		for(int loop_i=0;loop_i<loop;loop_i++){
		//			if(loop_i==loop-1) dithering=false;

		for(int x=0;x<w;x++){
			for(int y=0;y<h;y++){
				Vector4f px=texel.getPixelRGBA(x,y);
				Vector4f nearest_palette=palette[0];
				float d=diff(px,nearest_palette);
				for(int i=1;i<palette.length;i++){
					float d1=diff(px,palette[i]);
					if(d1<d){
						d=d1;
						nearest_palette=palette[i];
					}
				}

				//					if(dithering){
				//
				//						Vector4f oldColor=texel.getPixelRGBA(x,y);
				//						Vector4f newColor=nearest_palette;
				//						texel.setPixelRGBA(x,y,nearest_palette);
				//						Vector4f err=oldColor.subtract(newColor);
				//
				//						if(x+1<w){
				//							texel.setPixelRGBA(y,x+1,texel.getPixelRGBA(y,x+1).add(err.mult(7.f/16f)));
				//						}
				//
				//						if(x-1>=0&&y+1<h){
				//							texel.setPixelRGBA(y+1,x-1,texel.getPixelRGBA(y+1,x-1).add(err.mult(3.f/16f)));
				//						}
				//
				//						if(y+1<h){
				//							texel.setPixelRGBA(y+1,x,texel.getPixelRGBA(y+1,x).add(err.mult(5.f/16f)));
				//						}
				//
				//						if(x+1<w&&y+1<h){
				//							texel.setPixelRGBA(y+1,x+1,texel.getPixelRGBA(y+1,x+1).add(err.mult(1.f/16f)));
				//						}
				//
				//					}else{
				Vector4f npx=nearest_palette.clone();
				npx.w=px.w;
				texel.setPixelRGBA(x,y,nearest_palette);

				//					}
				//				}
			}
		}

}
	
	public static void reduce(Texel texel) {
		reduce(texel,false);
	}
	
	public static void reduce1(Texel texel,boolean apply) {
		int w=texel.getWidth();
		int h=texel.getHeight();

		int colors = 2; // Works only for 2.
		
		Vector4f palette[] = new Vector4f[colors];
		
		ArrayList<Vector4f> colorMap = new ArrayList<>();
		ArrayList<Integer> weightMap = new ArrayList<>();
		
		float mediumTempDiff = 0f;
		
		for(int x=0; x<w; x++) {
			for(int y=0; y<h; y++) {
				boolean toBeAdded = true;
				Vector4f color=texel.get(PixelFormat.FLOAT_NORMALIZED_RGBA, x, y);
				
				for(Vector4f tempColor:colorMap) {
					if(diff(color,tempColor) < mediumTempDiff) {
						toBeAdded = false; 
						int colorIndex = colorMap.indexOf(tempColor);
						
						weightMap.set(colorIndex, weightMap.get(colorIndex) + 1);
						break;
					}
				}
				
				if(toBeAdded) {
					colorMap.add(color);
					weightMap.add(0);
					
					if(colorMap.size() > 1) {
						mediumTempDiff += diff(color, colorMap.get(colorMap.size()-2));
						mediumTempDiff /= 2;
					}
				}
			}
		}
		
		int[] paletteWeight = {0,0};
		
		for(int i=0; i<colorMap.size(); i++) {
			Vector4f tempColor = colorMap.get(i);
			
			if(palette[0] == null)
				palette[0] = tempColor.clone();
			else if(palette[1] == null) 
				palette[1] = tempColor.clone();
			else {
				if(/*diff(palette[0], tempColor) > diff(palette[0],palette[1]) &&*/ weightMap.get(i) > paletteWeight[1] / 2) {
					palette[1] = tempColor.clone();
					paletteWeight[1] = weightMap.get(i);
				} else if(/*diff(tempColor,palette[1]) > diff(palette[0],palette[1]) &&*/ weightMap.get(i) > paletteWeight[1] / 2) {
					palette[0] = tempColor.clone();
					paletteWeight[0] = weightMap.get(i);
				}
			}
		}
		
		if(palette[1] == null) {
			if(palette[0] == null) 
				palette[0] = new Vector4f(1f, 1f, 1f, 1f);
			
			palette[1] = palette[0].clone();
		}
	
		texel.setPalette(PixelFormat.FLOAT_NORMALIZED_RGBA, palette);
		if(apply) 
			apply(w,h,texel,palette);
	}
	
	private static final ArrayList<Vector4f> GLOBAL_PALETTE = new ArrayList<>();
	private static float GLOBAL_DIFF = 0f;
	
	public static void reduce(Texel texel,boolean apply) {
		int w=texel.getWidth();
		int h=texel.getHeight();

		int colors = 2; // Works only for 2.
		
		Vector4f palette[] = new Vector4f[colors];
		ArrayList<Vector4f> tempPalette = new ArrayList<>();
		
		float mediumTempDiff = 0f;
		
		for(int x=0; x<w; x++) {
			for(int y=0; y<h; y++) {
				boolean toBeAdded = true;
				Vector4f color=texel.get(PixelFormat.FLOAT_NORMALIZED_RGBA, x, y);
				
				for(Vector4f tempColor:tempPalette) {
					if(diff(color,tempColor) < mediumTempDiff) {
						toBeAdded = false; 
						break;
					}
				}
				
				if(toBeAdded) {
					tempPalette.add(color);
					if(tempPalette.size() > 1) {
						mediumTempDiff += diff(tempPalette.get(tempPalette.size()-1), tempPalette.get(tempPalette.size()-2));
						mediumTempDiff /= 2;
					}
				}
			}
		}
		
		for(Vector4f tempColor:tempPalette) {
			if(palette[0] == null)
				palette[0] = tempColor.clone();
			else if(palette[1] == null) 
				palette[1] = tempColor.clone();
			else {
				if(diff(palette[0], tempColor) > diff(palette[0],palette[1])) {
					palette[1] = tempColor.clone();
				} else if(diff(tempColor,palette[1]) > diff(palette[0],palette[1])) {
					palette[0] = tempColor.clone();
				}
			}
		}
		
		if(palette[1] == null) {
			if(palette[0] == null) 
				palette[0] = new Vector4f(1f, 1f, 1f, 1f);
			
			palette[1] = palette[0].clone();
		} 
		
		if(GLOBAL_PALETTE.size() > 2) {
			for(int i=0; i<palette.length; i++) {
				boolean unsimilarColor = true;
				
				for(Vector4f globalColor:GLOBAL_PALETTE) {
					if(diff(palette[i],globalColor) < GLOBAL_DIFF) {
						unsimilarColor=false;
						
						palette[i].interpolateLocal(globalColor, .05f);
						break;
					} 
				}
				
				if(unsimilarColor) {
					GLOBAL_PALETTE.add(palette[i].clone());	
					
					GLOBAL_DIFF += diff(palette[i], GLOBAL_PALETTE.get(GLOBAL_PALETTE.size() - 2));
					GLOBAL_DIFF /= 2;
				} 
			}
		} else {
			GLOBAL_PALETTE.add(palette[0]);
			GLOBAL_PALETTE.add(palette[1]);
		}
	
		texel.setPalette(PixelFormat.FLOAT_NORMALIZED_RGBA, palette);
		if(apply) apply(w,h,texel,palette);
	}
	
	public static void apply(float w,float h,Texel texel,Vector4f[] palette) {
		for(int x=0;x<w;x++){
			for(int y=0;y<h;y++){
				Vector4f px=texel.get(PixelFormat.FLOAT_NORMALIZED_RGBA,x,y);
				Vector4f nearest_palette=palette[0];
				
				float d=diff(px,nearest_palette);
				for(int i=1;i<palette.length;i++){
					float d1=diff(px,palette[i]);
					if(d1<d){
						d=d1;
						nearest_palette=palette[i];
					}
				}
				Vector4f npx=nearest_palette.clone();
				npx.w=px.w;
				texel.set(PixelFormat.FLOAT_NORMALIZED_RGBA,x,y,nearest_palette);
			}
		}
	}
}
