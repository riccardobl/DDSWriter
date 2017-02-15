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
import com.jme3.math.Vector4f;
import com.jme3.texture.Texture;
import com.jme3.texture.image.ImageRaster;

import ddswriter.DDSBodyWriterDelegator;
import ddswriter.delegators.common.CommonARGBHeaderDelegator;
import ddswriter.delegators.common.CommonHeaderDelegator;
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

//		Texel txl=new Texel(ir,new int[]{0,0},new int[]{w,h});

//		// Step 1 - Convert To RGB565 (16bpp)
//		for(int x=0;x<w;x++){
//			for(int y=0;y<h;y++){
//				int rgba[]=txl.getIntPixelRGBA(x,y);
//				rgba[0]=((rgba[0]>>3)<<11);
//				rgba[1]=((rgba[1]>>2)<<5);
//				rgba[2]=(rgba[2]>>3);
//				txl.setIntPixelRGBA(x,y,rgba);
//			}
//		}
//		

	

		// Step 2 - Divide The Image Into Blocks
		Texel blocks[][]=new Texel[(int)FastMath.ceil((float)w/pxXblock[0])][(int)FastMath.ceil((float)h/pxXblock[1])];

		for(int x=0;x<blocks.length;x++){
			for(int y=0;y<blocks[0].length;y++){
				int xl=x*pxXblock[0];
				int yl=y*pxXblock[1];
				
				Texel texel=new Texel(ir,new int[]{xl,yl},new int[]{xl+pxXblock[0],yl+pxXblock[1]});
				blocks[x][y]=texel;
			}
		}

		// Step 3 - Palette-Reduce The Blocks		
		for(int x=0;x<blocks.length;x++){
			for(int y=0;y<blocks[0].length;y++){
				TexelReducer.reduce(blocks[x][y]);
//				blocks[x][y].write(ir);
			}
		}
//		
//		BufferedOutputStream out=new BufferedOutputStream(new FileOutputStream("/tmp/block_red.jpg"));
//		BufferedImage bimg=ImageToAwt.convert(tx.getImage(),false,true,0);
//		ImageIO.write(bimg,"jpg",out);
//		out.close();
//		
		
		

		// Step 4 - Encode The Blocks
		for(int x=0;x<blocks.length;x++){
			for(int y=0;y<blocks[0].length;y++){
				if(FORMAT==Format.DXT1)writeDXT1(blocks[x][y],body);
			}

		}

	}


	private void writeDXT1(Texel texel, DDS_BODY body) throws Exception {
//		Vector4f sample0c=null;
//		Vector4f sample1c=null;
//		
//		int[] sample0=texel.getIntPixelRGBA(0,0);
//		int[] sample1=sample0;
//		sample1c=texel.getPixelRGBA(0,0);
//		for(int x=1;x<texel.getWidth();x++){
//			for(int y=0;y<texel.getHeight();y++){
//				sample1=texel.getIntPixelRGBA(x,y);
//				sample1c=texel.getPixelRGBA(x,y);
//				if(!sample1c.equals(sample0c)){
//					break;
//				}
//			}
//		}		
			
		
		// Color RGB565
//		short c0=(short)((sample0[0] << 11) | (sample0[1] << 5) | sample0[2]);
//		short c1=(short)((sample1[0] << 11) | (sample1[1] << 5) | sample1[2]);
		
		short c0=(short)(0x33743b74);
		short c1=(short)(0xebffffef);
		 
		// c1 must be always <=c0
		if(c1>c0){
			short aux=c1;
			c1=c0;
			c0=aux;
		}

		body.writeWord(c0);
		body.writeWord(c1);
		
		
		int j=0;
		int color_data=0;
		for(int y=0;y<texel.getHeight();y++){
			for(int x=0;x<texel.getWidth();x++){
//				if(texel.getPixelRGBA(x,y).equals(sample0c))color_data|=C0;
//				else color_data|=C1;
				if(j%2==0)	color_data|=C0;
				else color_data|=C1;
				color_data<<=2;
				j++;
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
