package ddswriter.delegators.s2tc;

import static ddswriter.format.DDS_HEADER.DDSD_LINEARSIZE;
import static ddswriter.format.DDS_PIXELFORMAT.DDPF_FOURCC;

import java.util.Map;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.texture.Texture;
import com.jme3.texture.image.ImageRaster;

import ddswriter.Texel;
import ddswriter.Texel.PixelFormat;
import ddswriter.delegators.common.CommonSlicedBodyDelegator;
import ddswriter.encoders.RGB565ColorBit;
import ddswriter.format.DDS_BODY;
import ddswriter.format.DDS_HEADER;

/**
 * 	
 * @author Lorenzo Catania, Riccardo Balbo
 */

// Ref: https://github.com/divVerent/s2tc/wiki

public class S2tcDelegator extends CommonSlicedBodyDelegator{

	public static final int ALPHA_A0=0b000;
	public static final int ALPHA_A1=0b001;
	public static final int ALPHA_TRANSPARENT=0b110;
	public static final int ALPHA_OPAQUE=0b111;

	public static final int COLOR_C0=0b00;
	public static final int COLOR_C1=0b01;

	public static enum Format{
		S2TC_DXT1("DXT1",8),S2TC_DXT3("DXT3",8),S2TC_DXT5("DXT5",16);
		public String internal_name;
		public int blocksize;

		private Format(String s,int blocksize){
			this.internal_name=s;
			this.blocksize=blocksize;
		}

	

	}

	protected Format FORMAT; // 1= dxt1 

	@Override
	public void header(Texture tx, Map<String,String> options, DDS_HEADER header) throws Exception {
		super.header(tx,options,header);

		String format=((String)options.get("format"));
		if(format==null) {

			SKIP=true;
			return;
		}

		format=format.toUpperCase();
		

		for(Format f:Format.values())if(f.name().equals(format)) FORMAT=f;


		if(FORMAT==null){
			SKIP=true;
			System.out.println(this.getClass()+" does not support "+format+". skip");

			return;
		}
		System.out.println("Use "+this.getClass()+"  with format "+format+". ");


		header.dwFlags|=DDSD_LINEARSIZE;
		header.ddspf.dwFlags|=DDPF_FOURCC;

		byte formatb[]=FORMAT.internal_name.getBytes();
		for(int i=0;i<formatb.length;i++)
			header.ddspf.dwFourCC[i]=formatb[i];

		int w=tx.getImage().getWidth();
		int h=tx.getImage().getHeight();
		header.dwPitchOrLinearSize=((w+3)/4)*((h+3)/4)*FORMAT.blocksize;

	}

	@Override
	public void body(Texture tx, Texel ir, int mipmap, int slice, Map<String,String> options, DDS_HEADER header, DDS_BODY body) throws Exception {
		if(FORMAT==null) return;

		int pxXblock[]=new int[]{4,4};

		// Step 1 - Convert To RGB565 (16bpp)
		// Step 2 - Divide The Image Into Blocks
		// Step 3 - Palette-Reduce The Blocks	
		// Step 4 - Encode The Blocks		

		for(int y=0;y<ir.getHeight();y+=pxXblock[1]){

			for(int x=0;x<ir.getWidth();x+=pxXblock[0]){

				Texel btx=Texel.fromTexel(PixelFormat.FLOAT_NORMALIZED_RGBA,ir,new Vector2f(x,y),new Vector2f(x+pxXblock[0],y+pxXblock[1]));
				RGB565ColorBit.convertTexel(btx);
				btx.genPalette();
				//				RGB565ColorBit.convertTexel(btx);
				//				TexelReducer.reduce2(btx);
				if(FORMAT==Format.S2TC_DXT1) writeDXT1(btx,body);
			}
		}

	}

	private void writeDXT1(Texel texel, DDS_BODY body) throws Exception {

		Vector4f palette[]=texel.getPalette(PixelFormat.INT_RGBA);
		int c0=RGB565ColorBit.packPixel(palette[0]);
		int c1=RGB565ColorBit.packPixel(palette[1]);

		body.writeWord(c0);
		body.writeWord(c1);

		int color_data=0;
		for(int y=0;y<texel.getHeight();y++){
			for(int x=texel.getWidth()-1;x>=0;x--){
				if(x!=0||y!=0) color_data<<=2;
				float i=texel.get(PixelFormat.MAPPED_TO_PALETTE,x,y).x;
				if(i>.5f) color_data|=COLOR_C1;
				else color_data|=COLOR_C0;
			}
		}
		body.writeInt(color_data);

	}

	@Override
	public void header(Texture tx, Texel ir, int mipmap, int slice, Map<String,String> options, DDS_HEADER header) throws Exception {	}

}
