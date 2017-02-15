package ddswriter.delegators.s2tc;

import static ddswriter.format.DDS_HEADER.*;
import static ddswriter.format.DDS_PIXELFORMAT.*;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;

import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.texture.Texture;
import com.jme3.texture.image.ImageRaster;

import ddswriter.DDSBodyWriterDelegator;
import ddswriter.delegators.common.CommonARGBHeaderDelegator;
import ddswriter.delegators.common.CommonHeaderDelegator;
import ddswriter.delegators.s2tc.Texel.PixelFormat;
import ddswriter.format.DDS_BODY;
import ddswriter.format.DDS_HEADER;
import jme3tools.converters.ImageToAwt;

/**
 * 	
 * @author Lorenzo Catania
 */

// Ref: https://github.com/divVerent/s2tc/wiki

public class S2tcDelegator extends CommonHeaderDelegator implements DDSBodyWriterDelegator{
	//	protected static final int IMAGE_BLOCK_SIZE = 4, 
	//							   PIXELS_PER_BLOCK = IMAGE_BLOCK_SIZE * IMAGE_BLOCK_SIZE;

	public static final int ALPHA_A0=0b000;
	public static final int ALPHA_A1=0b001;
	public static final int ALPHA_TRANSPARENT=0b110;
	public static final int ALPHA_OPAQUE=0b111;
	public static final int COLOR_C0=0b00;
	public static final int COLOR_C1=0b01;
	
	
	public static final int C0=0b00;
	public static final int C1=0b01;
	
	
	public static enum Format {
		DXT1("DXT1",8),
		DXT3("DXT3",8),
		DXT5("DXT5",16);
		String s;
		int blocksize;
		private Format(String s,int blocksize){
			this.s=s;
			this.blocksize=blocksize;
		}
		
		public int getBlockSize(){
			return blocksize;
		}
		
		@Override
		public String toString(){
			return s;
		}
	}
	protected Format FORMAT; // 1= dxt1 
	@Override
	public void header(Texture tx, ImageRaster ir, int mipmap, int slice, Map<String,Object> options, DDS_HEADER header) throws Exception {
		super.header(tx,ir,mipmap,slice,options,header);
		if(mipmap==0&&slice==0){
			String format=(String)options.getOrDefault("format","dxt1");
			
			switch(format){
				case "dxt1":
				default:
					FORMAT=Format.DXT1;					
			}
			
			header.dwFlags|=DDSD_LINEARSIZE;
			header.ddspf.dwFlags|=DDPF_FOURCC;

			byte formatb[]=FORMAT.toString().getBytes();
			for(int i=0;i<formatb.length;i++)header.ddspf.dwFourCC[i]=formatb[i];

			int w=tx.getImage().getWidth();
			int h=tx.getImage().getHeight();
			header.dwPitchOrLinearSize=((w+3)/4) * ((h+3)/4) * FORMAT.getBlockSize();
		}
	}

	
	@Override
	public void body(Texture tx, ImageRaster ir, int mipmap, int slice, Map<String,Object> options, DDS_HEADER header, DDS_BODY body) throws Exception {
		int w=ir.getWidth();
		int h=ir.getHeight();
		int pxXblock[]=new int[]{4,4};

		Texel txl=Texel.fromImageRaster(ir,new Vector2f(0,0),new Vector2f(ir.getWidth(),ir.getHeight()));

		// Step 1 - Convert To RGB565 (16bpp)
//		txl.convertFormat(PixelFormat.RGBA5658_INT);

		// Step 2 - Divide The Image Into Blocks
		Texel blocks[][]=new Texel[(int)FastMath.ceil((float)w/pxXblock[0])][(int)FastMath.ceil((float)h/pxXblock[1])];

		for(int x=0;x<blocks.length;x++){
			for(int y=0;y<blocks[0].length;y++){
				int xl=x*pxXblock[0];
				int yl=y*pxXblock[1];
				blocks[x][y]=Texel.fromTexel(PixelFormat.RGBA5658_INT,txl,new Vector2f(xl,yl),new Vector2f(xl+pxXblock[0],yl+pxXblock[1]));
			}
		}

		// Step 3 - Palette-Reduce The Blocks		
		for(int x=0;x<blocks.length;x++){
			for(int y=0;y<blocks[0].length;y++){
//				System.out.println("Before reduce");
				Texel texel=blocks[x][y];
//				for(int xx=0;xx<texel.getWidth();xx++){
//					for(int xy=0;xy<texel.getHeight();xy++){
//						System.out.println(texel.get(PixelFormat.RGBA8_FLOAT,xx,xy));
//						
//					}
//				}
				TexelReducer.reduce(texel);
//				System.out.println("After reduce");
//				for(int xx=0;xx<texel.getWidth();xx++){
//					for(int xy=0;xy<texel.getHeight();xy++){
//						System.out.println(texel.get(PixelFormat.RGBA8_FLOAT,xx,xy));
//						
//					}
//				}
			}
		}

		// Step 4 - Encode The Blocks
		for(int x=0;x<blocks.length;x++){
			for(int y=0;y<blocks[0].length;y++){
				if(FORMAT==Format.DXT1)writeDXT1(blocks[x][y],body);
			}
		}

	}


	private void writeDXT1(Texel texel, DDS_BODY body) throws Exception {
		
		
		short c0=(short)texel.get(PixelFormat.RGBA565_PACKED,0,0).x;
		short c1=-1;
		for(int x=1;x<texel.getWidth();x++){
			for(int y=0;y<texel.getHeight();y++){
				c1=(short)texel.get(PixelFormat.RGBA565_PACKED,x,y).x;
				
				if(c1!=c0){
					break;
				}
			}
		}		
			
		
		System.out.println("Palette: c0="+c0+"; c1="+c1);
		 
		// c1 must be always <=c0
		if(c1>c0){
			short aux=c1;
			c1=c0;
			c0=aux;
		}

		body.writeWord(c0);
		body.writeWord(c1);
		
		
		int color_data=0;
		for(int y=0;y<texel.getHeight();y++){
			for(int x=0;x<texel.getWidth();x++){
				short ct=(short)texel.get(PixelFormat.RGBA565_PACKED,x,y).x;
				if(ct==c0)color_data|=C0;
				else if(ct==c1)color_data|=C1;
				else {
					System.err.print("Palette generation is wrong! "+ct+" "+c0+" "+c1);
					System.exit(1);					
				}
				color_data<<=2;
			}
		}
		body.writeDWord(color_data);

	}
	
	
//	private void writeDXT5(Texel texel, DDS_BODY body) throws Exception {
//		int[] sample0=texel.getIntPixelRGBA(0,0);
//		int[] sample1=sample0;
//		for(int x=1;x<texel.getWidth();x++){
//			for(int y=0;y<texel.getHeight();y++){
//				sample1=texel.getIntPixelRGBA(x,y);
//				if(sample1[0]!=sample0[0]||sample1[1]!=sample0[1]||sample1[2]!=sample0[2]||sample1[3]!=sample0[3]){
//					break;
//				}
//			}
//		}		
//		
//		// Alpha 
//		byte a0=0;
//		byte a1=0;
//		
//		if(a1<a0){ // a1 must be always >=a0
//			byte aux=a1;
//			a1=a0;
//			a0=aux;
//		}
//		body.writeByte(a0);
//		body.writeByte(a1);		
//		for(int i=0;i<6;i++)body.writeByte(0xFFFF);
//			
//		
//		// Color
//		short c0;
//		short c1;
//		 
//		c0=(short)((byte)sample0[0]|((byte)sample0[1])<<5|((byte)sample0[1])<<11);
//		c1=(short)((byte)sample1[0]|((byte)sample1[1])<<5|((byte)sample1[2])<<11);
//
//		// c1 must be always <=c0
//		if(c1>c0){
//			short aux=c1;
//			c1=c0;
//			c0=aux;
//		}
//
//		body.writeWord(c0);
//		body.writeWord(c1);
//		int color_data=0;
//		for(int x=0;x<texel.getWidth();x++){
//			for(int y=0;y<texel.getHeight();y++){
//				color_data|=COLOR_C0;
//				color_data<<=2;
//			}
//		}
//		body.writeDWord(color_data);
//
//	}

}


