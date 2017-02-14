package ddswriter.delegators;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.jme3.math.ColorRGBA;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.Texture3D;
import com.jme3.texture.TextureCubeMap;
import com.jme3.texture.image.ImageRaster;
import com.jme3.util.MipMapGenerator;

import ddswriter.DDSBodyWriterDelegator;
import ddswriter.DDSDelegator;
import ddswriter.DDSHeaderWriterDelegator;
import ddswriter.DDSWriter;
import ddswriter.format.DDS_BODY;
import ddswriter.format.DDS_HEADER;
import static ddswriter.format.DDS_HEADER.*;
import static ddswriter.format.DDS_PIXELFORMAT.*;

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
