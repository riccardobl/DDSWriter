package ddswriter;

import java.util.HashMap;
import java.util.Map;

import com.jme3.texture.Texture;
import com.jme3.texture.image.ImageRaster;

import ddswriter.format.DDS_BODY;
import ddswriter.format.DDS_HEADER;

/**
 * 
 * @author Riccardo Balbo
 *
 */
public interface DDSHeaderWriterDelegator extends DDSDelegator{
	public void header(Texture tx,ImageRaster ir,int mipmap,int slice,Map<String,Object>  options,DDS_HEADER header)throws Exception ; 

}
