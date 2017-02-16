package ddswriter.delegators;
import java.util.Map;

import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.image.ImageRaster;
import com.jme3.util.MipMapGenerator;

import ddswriter.DDSHeaderWriterDelegator;
import ddswriter.format.DDS_HEADER;

/**
 * 
 * @author Riccardo Balbo
 */
public class MipmapGenDelegator implements DDSHeaderWriterDelegator{



	@Override
	public void header(Texture tx,ImageRaster ir, int mipmap,int slice,Map<String,Object> options, DDS_HEADER header) throws Exception {
		if((boolean)options.getOrDefault("options",false)&&!tx.getImage().hasMipmaps()&&tx instanceof Texture2D) MipMapGenerator.generateMipMaps(tx.getImage());

	}



}
