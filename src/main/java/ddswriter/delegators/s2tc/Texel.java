



package ddswriter.delegators.s2tc;



import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.texture.image.ImageRaster;
/**
 * 
 * @author Riccardo Balbo
 *
 */

public class Texel implements Cloneable{
	public static enum PixelFormat{
		RGBA8_FLOAT,RGBA8_INT,RGBA5658_INT,RGBA565_PACKED
	}

	protected final Vector4f[] COLORS_PALETTE, ALPHA_PALETTE;
	protected final Vector4f[][] PIXELS;
	protected final PixelFormat FORMAT;
	protected Vector2f[] AREA;
	
	@Override
	public Texel clone(){
		Texel cloned= Texel.fromTexel(FORMAT,this,new Vector2f(),new Vector2f(getWidth(),getHeight()));
		cloned.AREA=AREA;
		return cloned;
	}

	public static Texel fromImageRaster(ImageRaster ir, Vector2f from, Vector2f to) {
		Vector4f pixels[][]=new Vector4f[(int)(to.x-from.x)][(int)(to.y-from.y)];
		for(int y=(int)from.y;y<to.y;y++){
			for(int x=(int)from.x;x<to.x;x++){
				int xl=(int)(x-from.x);
				int yl=(int)(y-from.y);
				Vector4f c=x>=ir.getWidth()||y>=ir.getHeight()?new Vector4f(0,0,0,0):ir.getPixel(x,y).toVector4f();
				pixels[xl][yl]=c;
			}
		}
		return new Texel(PixelFormat.RGBA8_FLOAT,pixels);
	}

	public static Texel fromTexel(PixelFormat dest_format, Texel tx, Vector2f from, Vector2f to) {
		Vector4f pixels[][]=new Vector4f[(int)(to.x-from.x)][(int)(to.y-from.y)];
		for(int y=(int)from.y;y<to.y;y++){
			for(int x=(int)from.x;x<to.x;x++){
				int xl=(int)(x-from.x);
				int yl=(int)(y-from.y);
				Vector4f c=x>=tx.getWidth()||y>=tx.getHeight()?new Vector4f(0,0,0,0):tx.get(dest_format,x,y);
				pixels[xl][yl]=c;
			}
		}
		return new Texel(dest_format,pixels);
	}

	public Texel(PixelFormat format,Vector4f pixels[][]) {
		this(format,pixels,new Vector2f[]{new Vector2f(0,0),// from
				new Vector2f(pixels.length,pixels[0].length)//to
		});

	}

	public Texel(PixelFormat format,Vector4f pixels[][],Vector2f area[]){
		PIXELS=pixels;
		COLORS_PALETTE=new Vector4f[2];
		ALPHA_PALETTE=new Vector4f[2];
		FORMAT=format;
		AREA=area;
	}

	private Vector4f convert(PixelFormat from, PixelFormat to, Vector4f c) {

		if(from==to) return c.clone();
		if(from==PixelFormat.RGBA8_FLOAT){
			switch(to){
				case RGBA8_INT:{
					Vector4f out=c.clone();
					out.x=(int)(c.x*255f);
					out.y=(int)(c.y*255f);
					out.z=(int)(c.z*255f);
					out.w=(int)(c.w*255f);
					return out;
				}
				case RGBA5658_INT:{
					return convert(PixelFormat.RGBA8_INT,PixelFormat.RGBA5658_INT,convert(PixelFormat.RGBA8_FLOAT,PixelFormat.RGBA8_INT,c));
				}
				case RGBA565_PACKED:{
					return convert(PixelFormat.RGBA5658_INT,PixelFormat.RGBA565_PACKED,convert(PixelFormat.RGBA8_FLOAT,PixelFormat.RGBA5658_INT,c));

				}
			}
		}else if(from==PixelFormat.RGBA8_INT){
			switch(to){
				case RGBA8_FLOAT:{
					Vector4f out=c.clone();
					out.x=(c.x/255f);
					out.y=(c.y/255f);
					out.z=(c.z/255f);
					out.w=(c.w/255f);
					return out;
				}
				case RGBA5658_INT:{
					Vector4f out=c.clone();
					out.x=(int)(c.x)&0b11111;
					out.y=((int)(c.y))&0b111111;
					out.z=((int)(c.z))&0b11111;
					out.w=(int)(c.w);
					return out;
				}
				case RGBA565_PACKED:{
					return convert(PixelFormat.RGBA5658_INT,PixelFormat.RGBA565_PACKED,c);

				}
			}
		}else if(from==PixelFormat.RGBA5658_INT){
			switch(to){
				case RGBA8_INT:{
					Vector4f out=c.clone();
					out.x=(c.x);
					out.y=(c.y);
					out.z=(c.z);
					out.w=(c.w);
					return out;
				}
				case RGBA8_FLOAT:{
					Vector4f out=c.clone();
					out.x=(c.x/255f);
					out.y=(c.y/255f);
					out.z=(c.z/255f);
					out.w=(c.w/255f);
					return out;
				}
				case RGBA565_PACKED:{
					Vector4f out=c.clone();
					int r=(int)c.x&0b11111;
					r<<=5;
					r|=(int)c.y&0b111111;
					r<<=6;
					r|=(int)c.z&0b11111;
					out.x=r;
					out.y=0;
					out.z=0;
					out.w=0;
					return out;
				}
			}
		}
		return null;
	}

	public void convertFormat(PixelFormat format) {
		for(int x=0;x<getWidth();x++){
			for(int y=0;y<getHeight();y++){
				PIXELS[x][y]=convert(FORMAT,format,PIXELS[x][y]);// todo
			}
		}
	}	

	public void genPalette() {
		TexelReducer.reduce(this);
	}
	
	protected float mapColor(Vector4f color) {
		if(color.distance(COLORS_PALETTE[0]) >= color.distance(COLORS_PALETTE[1])) 
			return 0f;
		else return 1f;
	}
	
	protected float mapAlpha(Vector4f alpha) {
		if(alpha.distance(ALPHA_PALETTE[0]) >= alpha.distance(ALPHA_PALETTE[1])) 
			return 0f;
		else return 1f;
	}

	public Vector4f get(PixelFormat f, int x, int y) {
		return convert(FORMAT,f,PIXELS[x][y]);
	}

	public void set(PixelFormat f, int x, int y, Vector4f c) {
		PIXELS[x][y]=convert(f,FORMAT,c).clone();
	}

	public int getWidth() {
		return PIXELS.length;
	}

	public int getHeight() {
		return PIXELS[0].length;
	}

	public Vector4f[][] getPixels() {
		return PIXELS;
	}

	public Vector2f[] getArea() {
		return AREA;
	}

	public PixelFormat getFormat() {
		return FORMAT;
	}

	public void write(Texel dst) {
		write(dst,getArea()[0],getArea()[1]);
	}

	public void write(Texel dst, Vector2f from, Vector2f to) {
		for(int y=(int)from.y;y<to.y;y++){
			for(int x=(int)from.x;x<to.x;x++){
				int xl=(int)(x-from.x);
				int yl=(int)(y-from.y);
				Vector4f c=get(dst.getFormat(),xl,yl);
				dst.set(dst.getFormat(),x,y,c);
			}
		}
	}

	public void write(ImageRaster dst) {
		write(dst,getArea()[0],getArea()[1]);
	}

	public void write(ImageRaster dst, Vector2f from, Vector2f to) {
		for(int y=(int)from.y;y<to.y;y++){
			for(int x=(int)from.x;x<to.x;x++){
				int xl=(int)(x-from.x);
				int yl=(int)(y-from.y);
				Vector4f c=get(PixelFormat.RGBA8_FLOAT,xl,yl);
				ColorRGBA crgba=new ColorRGBA(c.x,c.y,c.z,c.w);
				dst.setPixel(x,y,crgba);
			}
		}
	}

	@Deprecated
	private ColorRGBA colorRGBAfromVec4(Vector4f c) {
		return new ColorRGBA(c.x,c.y,c.z,c.w);
	}

	@Deprecated
	public void setPixelRGBA(int x, int y, Vector4f c) {
		set(PixelFormat.RGBA8_FLOAT,x,y,c);
	}

	@Deprecated
	public void setIntPixelRGBA(int x, int y, int c[]) {
		Vector4f v=new Vector4f(c[0],c[1],c[2],c[3]);
		set(PixelFormat.RGBA8_INT,x,y,v);

		//		Vector4f p=getPixelRGBA(x,y);
		//		p.x=(float)c[0]/255f;
		//		p.y=(float)c[1]/255f;
		//		p.z=(float)c[2]/255f;
		//		p.w=(float)c[3]/255f;
		//		setPixelRGBA(x,y,p);
	}

	@Deprecated
	public Vector4f getPixelRGBA(int x, int y) {
		return get(PixelFormat.RGBA8_FLOAT,x,y);

	}

	@Deprecated
	public int[] getIntPixelRGBA(int x, int y) {
		int out[]=new int[4];
		Vector4f v=get(PixelFormat.RGBA8_INT,x,y);
		out[0]=(int)v.x;
		out[1]=(int)v.y;
		out[2]=(int)v.z;
		out[3]=(int)v.w;
		return out;
		//		Vector4f c=getPixelRGBA(x,y);
		//		int r=(int)(c.x*255f);
		//		int g=(int)(c.y*255f);
		//		int b=(int)(c.z*255f);
		//		int a=(int)(c.w*255f);
		//		return new int[]{r,g,b,a};

	}

	@Deprecated
	public Texel(ImageRaster ir,int from[],int to[]){
		Texel tmp=Texel.fromImageRaster(ir,new Vector2f(from[0],from[1]),new Vector2f(to[0],to[1]));
		PIXELS=tmp.getPixels();
		COLORS_PALETTE=new Vector4f[2];
		ALPHA_PALETTE=new Vector4f[2];
		FORMAT=tmp.getFormat();
		AREA=tmp.getArea();
	}

	@Deprecated
	public void write(ImageRaster out, int from[], int to[]) {
		write(out,new Vector2f(from[0],from[1]),new Vector2f(to[0],to[1]));
	}

}