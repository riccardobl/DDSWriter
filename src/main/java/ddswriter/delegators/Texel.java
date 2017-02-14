package ddswriter.delegators;

import com.jme3.math.ColorRGBA;
import com.jme3.texture.image.ImageRaster;

class Texel{
	protected ColorRGBA[][] PIXELS;
	protected  int[] FROM,TO;
	public Texel(ImageRaster ir,int from[], int to[]){
		PIXELS=new ColorRGBA[to[0]-from[0]][to[1]-from[1]];

		FROM=from;
		TO=to;
		for(int y=from[1];y<to[1];y++){
			for(int x=from[0];x<to[0];x++){
				int xl=x-from[0];
				int yl=y-from[1];
				ColorRGBA c=ir.getPixel(x,y);
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
				ColorRGBA c=getPixelRGBA(xl,yl);
				out.setPixel(x,y,c);
			}
		}
	}
	
	public void setPixelRGBA(int x,int y,ColorRGBA c){
		PIXELS[x][y]=c;
	}
	
	public void setIntPixelRGBA(int x,int y,int c[]){
		ColorRGBA p=getPixelRGBA(x,y);
		p.r=(float)c[0]/255f;
		p.g=(float)c[1]/255f;
		p.b=(float)c[2]/255f;
		p.a=(float)c[3]/255f;
		setPixelRGBA(x,y,p);
	}
	
	public ColorRGBA getPixelRGBA(int x,int y){
		return PIXELS[x][y];
	}
	
	public int[] getIntPixelRGBA(int x,int y){
		ColorRGBA c=getPixelRGBA(x,y);
		int b=(int)(c.b*255f);
		int g=(int)(c.g*255f);
		int r=(int)(c.r*255f);
		int a=(int)(c.a*255f);
		return new int[]{r,g,b,a};

	}
	
	
	
	
}