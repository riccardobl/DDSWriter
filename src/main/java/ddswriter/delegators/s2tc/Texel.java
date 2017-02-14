package ddswriter.delegators.s2tc;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector4f;
import com.jme3.texture.image.ImageRaster;

public class Texel{
	protected Vector4f[][] PIXELS;
	protected  int[] FROM,TO;
	public Texel(ImageRaster ir,int from[], int to[]){
		PIXELS=new Vector4f[to[0]-from[0]][to[1]-from[1]];

		FROM=from;
		TO=to;
		for(int y=from[1];y<to[1];y++){
			for(int x=from[0];x<to[0];x++){
				int xl=x-from[0];
				int yl=y-from[1];
				Vector4f c=ir.getPixel(x,y).toVector4f();
				PIXELS[xl][yl]=c;
			}
		}
	}
	

	public int getWidth(){
		return PIXELS.length;
	}
	
	public int getHeight(){
		return PIXELS[0].length;
	}
	public void write(ImageRaster out){
		write(out,FROM,TO);
	}
	public void write(ImageRaster out,int from[], int to[]){
		for(int y=from[1];y<to[1];y++){
			for(int x=from[0];x<to[0];x++){
				int xl=x-from[0];
				int yl=y-from[1];
				Vector4f c=getPixelRGBA(xl,yl);
				out.setPixel(x,y,colorRGBAfromVec4(c));
			}
		}
	}
	
	private ColorRGBA colorRGBAfromVec4(Vector4f c) {
		return new ColorRGBA(c.x,c.y,c.z,c.w);
		
	}


	public void setPixelRGBA(int x,int y,Vector4f c){
		PIXELS[x][y]=c;
	}
	
	public void setIntPixelRGBA(int x,int y,int c[]){
		Vector4f p=getPixelRGBA(x,y);
		p.x=(float)c[0]/255f;
		p.y=(float)c[1]/255f;
		p.z=(float)c[2]/255f;
		p.w=(float)c[3]/255f;
		setPixelRGBA(x,y,p);
	}
	
	public Vector4f getPixelRGBA(int x,int y){
		return PIXELS[x][y];
	}
	
	public int[] getIntPixelRGBA(int x,int y){
		Vector4f c=getPixelRGBA(x,y);
		int r=(int)(c.x*255f);
		int g=(int)(c.y*255f);
		int b=(int)(c.z*255f);
		int a=(int)(c.w*255f);
		return new int[]{r,g,b,a};

	}
	
	
	
	
}