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
				writer.BYTE((int)(c.a*255f));
				writer.BYTE((int)(c.b*255f));
				writer.BYTE((int)(c.g*255f));
				writer.BYTE((int)(c.r*255f));
			}
		}
		return null;
	}

}
