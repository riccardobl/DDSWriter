package ddswriter.delegators;

import static ddswriter.format.DDS_HEADER.*;
import static ddswriter.format.DDS_PIXELFORMAT.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import com.jme3.math.ColorRGBA;
import com.jme3.texture.Texture;
import com.jme3.texture.image.ImageRaster;

import ddswriter.DDSBodyWriterDelegator;
import ddswriter.delegators.common.CommonARGBHeaderDelegator;
import ddswriter.format.DDS_BODY;
import ddswriter.format.DDS_HEADER;

/**
 * 	
 * @author Lorenzo Catania
 */

// Ref: https://github.com/divVerent/s2tc/wiki

public class S2tcARGBDelegator extends CommonARGBHeaderDelegator implements DDSBodyWriterDelegator {
	protected static final int IMAGE_BLOCK_SIZE = 4, 
							   PIXELS_PER_BLOCK = IMAGE_BLOCK_SIZE * IMAGE_BLOCK_SIZE;
	
	protected static final int
				ALPHA_A0=0b000,
				ALPHA_A1=0b001,
				
				ALPHA_TRANSPARENT=0b110,
				ALPHA_OPAQUE=0b111,
				
				COLOR_C0=0b00,
				COLOR_C1=0b01;
	
	private ArrayList<Texel> texels;
	
	@Override
	public void header(Texture tx,ImageRaster ir,int mipmap,int slice, Map<String,Object> options, DDS_HEADER header) throws Exception {
		super.header(tx,ir,mipmap,slice,options,header);
		if(mipmap==0&&slice==0){
			header.dwFlags|=DDSD_LINEARSIZE;
			header.ddspf.dwFlags|=DDPF_FOURCC;
			header.ddspf.dwFourCC[0]='D';
			header.ddspf.dwFourCC[1]='X';
			header.ddspf.dwFourCC[2]='T';
			header.ddspf.dwFourCC[3]='5';	
			
			//header.dwPitchOrLinearSize=(short) (Math.max(1, ((tx.getImage().getWidth()+3)/4) )* IMAGE_BLOCK_SIZE);
			header.dwPitchOrLinearSize=Math.max(1, ((tx.getImage().getWidth()+3)/4) * PIXELS_PER_BLOCK * PIXELS_PER_BLOCK/2 );
		}
	}
	
	@Override
	public void body(Texture tx, ImageRaster ir,int mipmap,int slice ,Map<String, Object> options, DDS_HEADER header, DDS_BODY body) throws Exception {
		int w=ir.getWidth();
		int h=ir.getHeight();		
		
		/*for(int i=0;i<header.dwPitchOrLinearSize;i++) {
			body.write(0);
		}*/
		
		texels=new ArrayList<Texel>();
		int subsample[]=new int[]{IMAGE_BLOCK_SIZE,IMAGE_BLOCK_SIZE};

		for(int x=0;x<=ir.getWidth()-subsample[0];x+=subsample[0]){
			for(int y=0;y<=ir.getHeight()-subsample[1];y+=subsample[1]){				
				Texel texel=new Texel(ir,new int[]{x,y},new int[]{x+subsample[0],y+subsample[1]});
				
				texels.add(texel);
			}
		}
		
		for(Texel texel:texels) {
			elaborateBlock(texel,body,IMAGE_BLOCK_SIZE);
		}
	}

	private void elaborateBlock(Texel texel,DDS_BODY body,int size) throws Exception {
		ColorRGBA sample0 = ColorRGBA.White; //block.pixels[0][0];
		ColorRGBA sample1 = ColorRGBA.White; //block.pixels[size-1][size-1];
		
		byte a0,a1;
		long alphaData = 0; // has to contain 48 bits
		
		short c0,c1;
		int colorData = 0; // has to contain 32 bits 0xFFFFFFFF
		
		// Alpha //
		a0=(byte) (Integer.reverseBytes((int) (sample0.a*255f) & 0xff));
		a1=(byte) (Integer.reverseBytes((int) (sample1.a*255f) & 0xff));
		
		if(a1 < a0) {
			byte aux=a0;
			a0=a1;
			a1=aux;
		}
		
		Random randomizer=new Random();
		
		System.out.println("ALPHA DATA");
		for(int i=0; i < PIXELS_PER_BLOCK; i++) {
			int aval=randomizer.nextBoolean() ? ALPHA_A1 : ALPHA_A1;
			
			alphaData|= ( ((aval 	 )&1) << (i*3) 	   ) |
						( ((aval >> 1)&1) << ((i*3)+1) ) |
						( ((aval >> 2)&1) << ((i*3)+2) );
			
			System.out.println("i="+i+"\t"+Long.toBinaryString(alphaData));
		}	
		
		// Color //
		c0=(short) ((byte) sample0.b | ((byte)  sample0.g )<<5 | ((byte) sample0.r)<<11);
		c1=(short) ((byte) sample1.b | ((byte)  sample1.g )<<5 | ((byte) sample1.r)<<11);
		
		if(c1 < c0) {
			short aux=c0;
			c0=c1;
			c1=aux;
		}
		
		//System.out.println("COLOR DATA");
		for(int i=0; i < PIXELS_PER_BLOCK; i++) {
			int cval=randomizer.nextBoolean() ? COLOR_C0 : COLOR_C1;
			colorData |= ( ((cval  	  )&1) << (i*2) ) |
						 ( ((cval >> 1)&1) << ((i*2)+1) );
			//System.out.println(Integer.toBinaryString(colorData));
		}
		
		/*System.out.println("Writing texel:" +
				"\n 	index=" + texels.indexOf(texel) +
				",\n	a0=" + byteToBinary(a0) + ", a1=" + byteToBinary(a1) +
				",\n	alphaData=" + Long.toBinaryString(alphaData) + 
				",\n	c0="+shortToBinary(c0)+", c1="+shortToBinary(c1)+
				",\n	colorData="+Integer.toBinaryString(colorData)
		);*/
		
		writeBlock(body,a0,a1,alphaData,c0,c1,colorData);
	}	
	
	private void writeBlock(DDS_BODY body,byte a0,byte a1,long alphaData,short c0,short c1,int colorData) throws Exception {		
		// Writing
		body.writeByte(a0);
		body.writeByte(a1);
		for(int i=0; i < 6; i++) body.writeByte( (byte) (alphaData>>i*8) );
		
		body.writeByte((byte) c0);
		body.writeByte((byte) c0>>8);
		body.writeByte((byte) c1);	
		body.writeByte((byte) c1>>8);		
		for(int i=0; i < 4; i++) body.writeByte( (byte) (colorData>>i*8) );
	}
	
	/*********/
	/**DEBUG**/
	/*********/
	private static String byteToBinary(byte b) {
		return Integer.toBinaryString(b & 0xFF).replace(' ', '0');
	}

	private static String shortToBinary(short s) {
		return Integer.toBinaryString(s & 0xFFFF).replace(' ', '0');
	}
}
