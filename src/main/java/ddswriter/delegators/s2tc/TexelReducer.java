package ddswriter.delegators.s2tc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.texture.Image;
import com.jme3.texture.Texture2D;
import com.jme3.texture.image.ImageRaster;
import com.jme3.texture.plugins.AWTLoader;

import ddswriter.DDSWriter;
import ddswriter.colors.RGB565ColorBit;
import ddswriter.delegators.s2tc.Texel.PixelFormat;

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
		int w=texel.getWidth();
		int h=texel.getHeight();
//		int dd=0;
//		
//		for(int x=0;x<w;x++){
//			for(int y=0;y<h;y++){
//				dd++;
//				if(dd%2==0) texel.set(PixelFormat.FLOAT_NORMALIZED_RGBA,x,y,new Vector4f(1,1,1,1));
//				else texel.set(PixelFormat.FLOAT_NORMALIZED_RGBA,x,y,new Vector4f(1,0,1,1));
//
//			}
//		}
//		if(1==1)return;
		Vector4f palette[]=new Vector4f[2];// Works only for 2.

		
		int colors = w + h -1;
		Vector4f[] temp_palette=new Vector4f[colors];
		
		for(int x=0; x<w; x++) {
			temp_palette[x]=texel.get(PixelFormat.FLOAT_NORMALIZED_RGBA,x, x);				//FIRST BIAS
			temp_palette[x+w-1]=texel.get(PixelFormat.FLOAT_NORMALIZED_RGBA,w-x-1, h-x-1);	//SECOND BIAS
		}
		
		/** temp_palette should be divided into 2 subgroups, [0,length/2] and [length/2,length] **/
		/** those 2 groups will be interpolated into 2 single colors to form the palette 		**/
		for(int i=0; i<temp_palette.length/2; i++) {
			Vector4f c=temp_palette[i];
			float mediumDiff;	//MEDIUM DIFFERENCE BETWEEN THE CURRENT COLOR AND THE LAST COLOR THAT WAS SORTED
			
			if(i>0) mediumDiff=diff(c,temp_palette[i-1]); //CONSIDER THE LAST COLOR THAT WAS SORTED (IF ANY)
			else mediumDiff=diff(c,temp_palette[0]);	  //CONSIDER THE COLOR ITSELF IF IT IS THE FIRST OF THE ARRAY
			
			for(int j=i; j<temp_palette.length; j++) {
				Vector4f c1=temp_palette[j];
				float diff;
				
				if(i>0) diff=diff(temp_palette[i-1],c1);
				else diff=diff(temp_palette[0],c1);
				
				if(diff < mediumDiff) { //CHECK THE DIFFERENCE
					Vector4f aux=temp_palette[i];
					temp_palette[i]=temp_palette[j];
					temp_palette[j]=aux;
				}
			}
		}		
		
		palette[0]=temp_palette[0];
		palette[1]=temp_palette[temp_palette.length/2];
		
		float totalDiff=diff(palette[0], palette[1]);
		
		//BALANCE FIRST PALETTE COLOR
		for(int i=1; i<temp_palette.length/2; i++) {
			if(diff(palette[0],temp_palette[i]) > totalDiff)
				palette[0]=palette[0].add(temp_palette[i]).divide(2);
		}
		
		//BALANCE SECOND PALETTE COLOR
		palette[1]=temp_palette[temp_palette.length/2];
		for(int i=temp_palette.length/2+1; i<temp_palette.length; i++) {
			if(diff(palette[1],temp_palette[i]) > totalDiff)
				palette[1]=palette[1].add(temp_palette[i]).divide(2);
		}		
		
		//INTERPOLATE (TRYING TO REDUCE BLOCKINESS)
		palette[1].interpolateLocal(palette[0], .5f);
		
		/*for(int i=0; i<temp_palette.length; i++) 	
			temp_palette[i] = texel.getPixelRGBA(i,i);
		
		for(int i=0; i<temp_palette.length; i++) {				
			if(temp_palette[i] == null) {
				temp_palette[i] = Vector4f.UNIT_XYZW;
			} else {
				for(int x=0; x<w; x++) {
					for(int y=0; y<h; y++) {
						
					}
				}
			}
		}*/

	
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


//				Vector4f npx=nearest_palette.clone();
//				npx.w=px.w;
				texel.set(PixelFormat.FLOAT_NORMALIZED_RGBA,x,y,nearest_palette);

			}
		}

	}

	public static void main(String[] args) throws Exception {
		InputStream is=new BufferedInputStream(new FileInputStream("/tmp/tobereduced.jpg"));
		AWTLoader loader=new AWTLoader();
		Image img=loader.load(is,false);
		is.close();

		ImageRaster ir=ImageRaster.create(img);
		int subsample[]=new int[]{4,4};

		for(int x=0;x<ir.getWidth();x+=subsample[0]){
			for(int y=0;y<ir.getHeight();y+=subsample[1]){
				Texel tx=Texel.fromImageRaster(ir,new Vector2f(x,y),new Vector2f(x+subsample[0],y+subsample[1]));
				reduce2(tx);
				
				
				Vector4f ca=tx.get(PixelFormat.FLOAT_NORMALIZED_RGBA,0,0);
				Vector4f cb=null;
				for(int xx=0;xx<tx.getWidth();xx++){
					for(int yx=0;yx<tx.getHeight();yx++){
						Vector4f xyt=tx.get(PixelFormat.FLOAT_NORMALIZED_RGBA,xx,yx);
						if(!xyt.equals(ca)){
							if(cb==null)cb=xyt;
							else if(!cb.equals(xyt)){
								System.out.println("Palette is wrong. 3 colors found :"+ca+" "+cb+" "+xyt);
							}
						}
						
					}
				}
				tx.write(ir);
			}
		}

		Map<String,Object> options=new HashMap<String,Object> ();
		options.put("format","RGB565");
		OutputStream fo=new BufferedOutputStream(new FileOutputStream(new File("/tmp/reduced.dds")));
		DDSWriter.write(new Texture2D(img),options,fo);
		fo.close();
//		BufferedOutputStream out=new BufferedOutputStream(new FileOutputStream("/tmp/reduced.jpg"));
//		BufferedImage bimg=ImageToAwt.convert(img,false,true,0);
//		ImageIO.write(bimg,"bmp",out);

//		out.close();
	}
}
