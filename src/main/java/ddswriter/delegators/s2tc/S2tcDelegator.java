package ddswriter.delegators.s2tc;

import static ddswriter.format.DDS_HEADER.DDSD_LINEARSIZE;
import static ddswriter.format.DDS_PIXELFORMAT.DDPF_FOURCC;

import java.util.Map;

import com.jme3.math.Vector2f;
import com.jme3.texture.Texture;
import com.jme3.texture.image.ImageRaster;

import ddswriter.DDSBodyWriterDelegator;
import ddswriter.colors.RGB565ColorBit;
import ddswriter.delegators.common.CommonHeaderDelegator;
import ddswriter.delegators.s2tc.Texel.PixelFormat;
import ddswriter.format.DDS_BODY;
import ddswriter.format.DDS_HEADER;

/**
 * 	
 * @author Lorenzo Catania, Riccardo Balbo
 */

// Ref: https://github.com/divVerent/s2tc/wiki

public class S2tcDelegator extends CommonHeaderDelegator implements DDSBodyWriterDelegator{

	public static final int ALPHA_A0=0b000;
	public static final int ALPHA_A1=0b001;
	public static final int ALPHA_TRANSPARENT=0b110;
	public static final int ALPHA_OPAQUE=0b111;

	public static final int COLOR_C0=0b00;
	public static final int COLOR_C1=0b01;

	public static enum Format{
		DXT1("DXT1",8),DXT3("DXT3",8),DXT5("DXT5",16);
		public String name;
		public int blocksize;

		private Format(String s,int blocksize){
			this.name=s;
			this.blocksize=blocksize;
		}
		
		@Override
		public String toString(){
			return name;
		}

	
	
	}

	protected Format FORMAT; // 1= dxt1 

	@Override
	public void header(Texture tx, ImageRaster ir, int mipmap, int slice, Map<String,Object> options, DDS_HEADER header) throws Exception {
		super.header(tx,ir,mipmap,slice,options,header);
		if(mipmap==0&&slice==0){
			String format=((String)options.get("format"));
			if(format==null){
				if((boolean)options.getOrDefault("compressed",false)){
					format="DXT1";
				}
			}else{
				format=format.toUpperCase();
			}
			
			for(Format f:Format.values()){
				if(f.name.equals(format)) FORMAT=f;
			}
			
			if(FORMAT==null)return;

			header.dwFlags|=DDSD_LINEARSIZE;
			header.ddspf.dwFlags|=DDPF_FOURCC;

			byte formatb[]=FORMAT.name.getBytes();
			for(int i=0;i<formatb.length;i++)
				header.ddspf.dwFourCC[i]=formatb[i];

			int w=tx.getImage().getWidth();
			int h=tx.getImage().getHeight();
			header.dwPitchOrLinearSize=((w+3)/4)*((h+3)/4)*FORMAT.blocksize;

		}
	}

	@Override
	public void body(Texture tx, ImageRaster ir, int mipmap, int slice, Map<String,Object> options, DDS_HEADER header, DDS_BODY body) throws Exception {
		if(FORMAT==null) return;

		int pxXblock[]=new int[]{4,4};

		// Step 1 - Convert To RGB565 (16bpp)
		// Step 2 - Divide The Image Into Blocks
		// Step 3 - Palette-Reduce The Blocks	
		// Step 4 - Encode The Blocks		

		for(int y=0;y<ir.getHeight();y+=pxXblock[1]){

			for(int x=0;x<ir.getWidth();x+=pxXblock[0]){

				Texel btx=Texel.fromImageRaster(ir,new Vector2f(x,y),new Vector2f(x+pxXblock[0],y+pxXblock[1]));
				RGB565ColorBit.convertTexel(btx);
				TexelReducer.reduce2(btx);
				if(FORMAT==Format.DXT1) writeDXT1(btx,body);
			}
		}

	}

	private void writeDXT1(Texel texel, DDS_BODY body) throws Exception {

		int c0=RGB565ColorBit.packPixel(texel.get(PixelFormat.INT_RGBA,0,0));
		int c1=c0;
		for(int x=0;x<texel.getWidth();x++){
			for(int y=0;y<texel.getHeight();y++){
				int c=RGB565ColorBit.packPixel(texel.get(PixelFormat.INT_RGBA,x,y));
				if(c!=c0){
					c1=c;
					break;
				}
			}
		}

		if(c0>c1){
			int aux=c0;
			c0=c1;
			c1=aux;
		}

		body.writeWord(c0);
		body.writeWord(c1);

		int color_data=0;
		for(int y=0;y<texel.getHeight();y++){

			for(int x=texel.getWidth()-1;x>=0;x--){

				if(x!=0||y!=0) color_data<<=2;
				int ct=RGB565ColorBit.packPixel(texel.get(PixelFormat.INT_RGBA,x,y));
				if(ct==c1) color_data|=COLOR_C1;
				else if(ct==c0) color_data|=COLOR_C0;
				else{
					System.err.print("Palette generation is wrong! "+ct+" "+c0+" "+c1);
					System.exit(1);
				}
			}
		}
		body.writeInt(color_data);

	}

}
