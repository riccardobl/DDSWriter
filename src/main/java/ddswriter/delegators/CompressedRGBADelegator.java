package ddswriter.delegators;
import com.jme3.math.ColorRGBA;
import com.jme3.texture.image.ImageRaster;

import ddswriter.DDSDelegator;
import ddswriter.DDSWriter;

/**
 * 
 * @author Lorenzo Catania
 */

public class CompressedRGBADelegator implements DDSDelegator {
	protected static final int IMAGE_BLOCK_SIZE = 4;
	
	protected static final int
			ALPHA_A0=0x000,
			ALPHA_A1=0x001,
			
			ALPHA_TRANSPARENT=110,
			ALPHA_OPAQUE=111,
			
			COLOR_C0=0x00,
			COLOR_C1=0x01;
							
	
	@Override
	public byte[] process(ImageRaster ir, DDSWriter writer) throws Exception {
		int w=ir.getWidth();
		int h=ir.getHeight();
		
		for(int y=0; y<h; y+=IMAGE_BLOCK_SIZE*2){ 
			for(int x=0; x<w; x+=IMAGE_BLOCK_SIZE*2) {
				byte a0,a1;
				byte[] alphaData=new byte[6];
				
				short c0,c1;
				byte[] colorData=new byte[4];
				
				//// First ////
				ColorRGBA sample0=ir.getPixel(x, y);
				ColorRGBA sample1=ir.getPixel(x+IMAGE_BLOCK_SIZE, y+IMAGE_BLOCK_SIZE);
				
				// Alpha //
				a0=(byte) (sample0.a*255f);
				a1=(byte) (sample1.a*255f);	
				
				if(a1 < a0) {
					byte aux=a0;
					a0=a1;
					a1=aux;
				}
				
				for(int i=0; i<6; i++)
					alphaData[i] = ALPHA_OPAQUE;
				
				// Color //
				c0=(short) ((byte) sample0.b | ((byte)  sample0.g )<<5 | ((byte) sample0.r)<<11);
				c1=(short) ((byte) sample1.b | ((byte)  sample1.g )<<5 | ((byte) sample1.r)<<11);
				
				if(c1 < c0) {
					short aux=c0;
					c0=c1;
					c1=aux;
				}
				
				for(int i=0; i<4; i++)
					colorData[i] = COLOR_C0;
				
				// Writing
				writer.BYTE(a0);
				writer.BYTE(a1);
				for(int i=0; i<6; i++) writer.BYTE(alphaData[i]);
				
				writer.BYTE(c0);
				writer.BYTE(c1);
				
				for(int i=0; i<4; i++) writer.BYTE(colorData[i]);
			}
		}
		return null;
	}


	@Override
	public short dwPitchOrLinearSize(int width) throws Exception {
		// For block-compressed formats, compute the pitch as: max( 1, ((width+3)/4) ) * block-size
		return (short) (Math.max(1, ((width+3)/4) )* IMAGE_BLOCK_SIZE);
	}

}