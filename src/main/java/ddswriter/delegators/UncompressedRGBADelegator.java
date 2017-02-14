package ddswriter.delegators;
import com.jme3.math.ColorRGBA;
import com.jme3.texture.image.ImageRaster;

import ddswriter.DDSDelegator;
import ddswriter.DDSWriter;

/**
 * 
 * @author Riccardo Balbo
 */
public class UncompressedRGBADelegator implements DDSDelegator{

	@Override
	public byte[] process(ImageRaster ir, DDSWriter writer) throws Exception {
		int w=ir.getWidth();
		int h=ir.getHeight();
		for(int y=0;y<h;y++){
			for(int x=0;x<w;x++){
				ColorRGBA c=ir.getPixel(x,y);
				writer.PIXEL(c);
			

			}
		}
		return null;
	}

	@Override
	public short dwPitchOrLinearSize(int width) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

}
