package ddswriter.delegators;
import com.jme3.math.ColorRGBA;
import com.jme3.texture.image.ImageRaster;

import ddswriter.DDSDelegator;
import ddswriter.DDSWriter;

/**
 * 
 * @author Lorenzo Catania
 */

public class CompressedRGBADelegator implements DDSDelegator{
	protected static final int IMAGE_BLOCK_SIZE = 4;
	
	@Override
	public byte[] process(ImageRaster ir, DDSWriter writer) throws Exception {
		int w=ir.getWidth();
		int h=ir.getHeight();
		
		for(int y=0; y<h; y+=IMAGE_BLOCK_SIZE){ 
			for(int x=0; x<w; x+=IMAGE_BLOCK_SIZE) {
				byte a0,a1;
				byte[] alphaData=new byte[6];
				
				short c0,c1;
				short[] colorData=new short[2];
				
				//First
				
				/*//FIRST
				for(int blockY=y; blockY<y+BLOCK_SIZE; blockY++) {
					for(int blockX=x; blockX<x+BLOCK_SIZE; blockX++) {

					}
				}
				
				//SECOND
				for(int blockY=y+BLOCK_SIZE; blockY<y+BLOCK_SIZE*2; blockY++) {
					for(int blockX=x; blockX<x+BLOCK_SIZE; blockX++) {
					
					}
				}
				
				//THIRD
				for(int blockY=y; blockY<y+BLOCK_SIZE; blockY++) {
					for(int blockX=x+BLOCK_SIZE; blockX<x+BLOCK_SIZE*2; blockX++) {
					
					}
				}
				
				//FOURTH
				for(int blockY=y+BLOCK_SIZE; blockY<y+BLOCK_SIZE*2; blockY++) {
					for(int blockX=x+BLOCK_SIZE; blockX<x+BLOCK_SIZE*2; blockX++) {
					
					}
				}*/
			}
		}
		return null;
	}

}