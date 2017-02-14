package ddswriter.delegators;

import static ddswriter.format.DDS_HEADER.*;
import static ddswriter.format.DDS_PIXELFORMAT.*;

import java.util.Map;

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
	protected static final int IMAGE_BLOCK_SIZE = 16;
	
	protected static final int
				ALPHA_A0=0b000,
				ALPHA_A1=0b001,
				
				ALPHA_TRANSPARENT=0b110,
				ALPHA_OPAQUE=0b111,
				
				COLOR_C0=0b00,
				COLOR_C1=0b01;
	
	@Override
	public void header(Texture tx,ImageRaster ir, Map<String,Object> options, DDS_HEADER header) throws Exception {
		super.header(tx,ir,options,header);
		header.dwFlags|=DDSD_LINEARSIZE;
		header.ddspf.dwFlags|=DDPF_FOURCC;
		header.ddspf.dwFourCC[0]='D';
		header.ddspf.dwFourCC[1]='X';
		header.ddspf.dwFourCC[2]='T';
		header.ddspf.dwFourCC[3]='5';	
		
		header.dwPitchOrLinearSize=(short) (Math.max(1, ((tx.getImage().getWidth()+3)/4) )* IMAGE_BLOCK_SIZE);
	}
	
	@Override
	public void body(Texture tx, ImageRaster ir, Map<String, Object> options, DDS_HEADER header, DDS_BODY body) throws Exception {
		int w=ir.getWidth();
		int h=ir.getHeight();
		
		for(int x=0, y=0; x<h && y<w; x+=IMAGE_BLOCK_SIZE*2, y+=IMAGE_BLOCK_SIZE*2) { 						
			//// First ////
			elaborateBlock(ir,body,
					x,y,
			IMAGE_BLOCK_SIZE);

			elaborateBlock(ir,body,
					x, y+IMAGE_BLOCK_SIZE,
			IMAGE_BLOCK_SIZE);
			
			elaborateBlock(ir,body,
					x+IMAGE_BLOCK_SIZE, y,
			IMAGE_BLOCK_SIZE);
			
			elaborateBlock(ir,body,
					x+IMAGE_BLOCK_SIZE, y+IMAGE_BLOCK_SIZE,
			IMAGE_BLOCK_SIZE);
		}
	}

	private void elaborateBlock(ImageRaster ir,DDS_BODY body,int x,int y,int size) throws Exception {
		ColorRGBA sample0=ir.getPixel(x, y);
		ColorRGBA sample1=ir.getPixel(x+size-1, y+size-1);
		
		byte a0,a1;
		long alphaData = 0xF; // has to contain 48 bits
		
		short c0,c1;
		int colorData = 0xFFFF; // has to contain 32 bits
		
		// Alpha //
		a0=(byte) (Integer.reverseBytes((int) (sample0.a*255f) & 0xff));
		a1=(byte) (Integer.reverseBytes((int) (sample1.a*255f) & 0xff));
		
		if(a1 < a0) {
			byte aux=a0;
			a0=a1;
			a1=aux;
		}
		
		for(int i=0; i < 16; i++)
			alphaData=(alphaData>>i*3) & ALPHA_OPAQUE;
		
		// Color //
		c0=(short) ((byte) sample0.b | ((byte)  sample0.g )<<5 | ((byte) sample0.r)<<11);
		c1=(short) ((byte) sample1.b | ((byte)  sample1.g )<<5 | ((byte) sample1.r)<<11);
		
		if(c1 < c0) {
			short aux=c0;
			c0=c1;
			c1=aux;
		}
		
		System.out.println("COLOR DATA");
		for(int i=0; i < 16; i++) {
			colorData=(colorData>>i*2) & COLOR_C1;
			System.out.println(Integer.toBinaryString(colorData));
		}
		
		/*System.out.println("Writing block:\nx=" + x + ", y= " + y +
				",\n	a0=" + byteToBinary(a0) + ", a1=" + byteToBinary(a1) +
				",\n	alphaData=" + Long.toBinaryString(alphaData) + 
				",\n	c0="+shortToBinary(c0)+", c1="+shortToBinary(c1)+
				",\n	colorData="+colorData
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
