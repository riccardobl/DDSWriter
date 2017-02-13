package ddswriter;
import com.jme3.texture.image.ImageRaster;

/**
 * 
 * @author Riccardo Balbo
 */
public interface DDSDelegator{
	public byte[] process(ImageRaster ir,DDSWriter writer) throws Exception ; 
	
}
