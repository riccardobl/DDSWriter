package ddswriter.delegators.common;

import static ddswriter.format.DDS_HEADER.DDSD_PITCH;
import static ddswriter.format.DDS_PIXELFORMAT.DDPF_ALPHAPIXELS;
import static ddswriter.format.DDS_PIXELFORMAT.DDPF_RGB;

import java.util.HashMap;
import java.util.Map;

import com.jme3.texture.Texture;
import com.jme3.texture.image.ImageRaster;

import ddswriter.format.DDS_HEADER;

/**
 * 
 * @author Riccardo Balbo
 *
 */
public abstract class CommonARGBHeaderDelegator extends CommonHeaderDelegator{

	@Override
	public void header(Texture tx, ImageRaster ir,int mipmap,int slice, Map<String,Object> options, DDS_HEADER header) throws Exception {
		super.header(tx,ir,mipmap,slice,options,header);
		
		header.ddspf.dwFlags|=DDPF_ALPHAPIXELS;

	}

}
