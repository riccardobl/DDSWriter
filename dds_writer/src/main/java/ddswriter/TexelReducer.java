package ddswriter;

import com.jme3.math.FastMath;
import com.jme3.math.Vector4f;

import ddswriter.Texel.PixelFormat;

public class TexelReducer{

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
	
	public static void reduce(Texel texel,boolean apply) {
		int w=texel.getWidth();
		int h=texel.getHeight();
		
		Vector4f palette[]=new Vector4f[2];// Works only for 2.
		
		int colors = (w * h);
		Vector4f[] temp_palette=new Vector4f[colors];
		
		int lastFreeIndex=-1;
		for(int x=0; x<w; x++) {
			for(int y=0; y<h; y++) {
				temp_palette[++lastFreeIndex]=texel.get(PixelFormat.FLOAT_NORMALIZED_RGBA, x, y);
			}
		} 
		
		/** temp_palette should be divided into 2 subgroups, [0,length/2] and [length/2,length] **/
		/** those 2 groups will be interpolated into 2 single colors to form the palette 		**/
		for(int i=1; i<temp_palette.length/2; i++) {
			Vector4f c=temp_palette[i];
			float mediumDiff=diff(c, temp_palette[i-1]); //CONSIDER THE LAST COLOR THAT WAS SORTED (IF ANY)
			
			for(int j=temp_palette.length/2 + 1; j<temp_palette.length; j++) {
				Vector4f c1=temp_palette[j];
				float diff=diff(c1, temp_palette[j-1]);
				
				if(diff < mediumDiff) { //CHECK THE DIFFERENCE
					Vector4f aux=temp_palette[i];
					temp_palette[i]=temp_palette[j];
					temp_palette[j]=aux;
				}
			}
		}		
		
		palette[0]=temp_palette[0];
		palette[1]=temp_palette[temp_palette.length/2];
		
		float mediumDiff=diff(palette[0],palette[1]);
		
		//BALANCE FIRST PALETTE COLOR
		int balancingCounter=1;
		for(int i=1; i<temp_palette.length/2; i++) {
			if(diff(palette[0],temp_palette[i]) < mediumDiff) {
				palette[0].addLocal(temp_palette[i]);
				balancingCounter++;
			}
		}
		palette[0].divideLocal(balancingCounter);
		
		//BALANCE SECOND PALETTE COLOR
		balancingCounter=1;
		palette[1]=temp_palette[temp_palette.length/2];
		for(int i=temp_palette.length/2+1; i<temp_palette.length; i++) {
			if(diff(palette[1],temp_palette[i]) < mediumDiff) {
				palette[1].addLocal(temp_palette[i]);
				balancingCounter++;
			}
		}	
		palette[1].divideLocal(balancingCounter);
		
		texel.setPalette(PixelFormat.FLOAT_NORMALIZED_RGBA,palette);
	
		if(apply) apply(w,h,texel,palette);
	}
//

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
