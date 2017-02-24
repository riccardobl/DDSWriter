package ddswriter.delegators;
import java.util.Map;

import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.util.MipMapGenerator;

import ddswriter.DDSDelegator;
import ddswriter.format.DDS_BODY;
import ddswriter.format.DDS_HEADER;

/**
 * 
 * @author Riccardo Balbo
 */
@Deprecated
public class MipmapGenDelegator implements DDSDelegator{




	@Override
	public void body(Texture tx, Map<String,String> options, DDS_HEADER header, DDS_BODY body) throws Exception {

	}

	@Override
	public void header(Texture tx, Map<String,String> options, DDS_HEADER header) throws Exception {
		if(options.getOrDefault("mipmaps","false").equals("true")&&!tx.getImage().hasMipmaps()&&tx instanceof Texture2D) 
			MipMapGenerator.generateMipMaps(tx.getImage());
		
	}



}
