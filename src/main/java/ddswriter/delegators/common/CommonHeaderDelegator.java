package ddswriter.delegators.common;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.jme3.math.ColorRGBA;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture3D;
import com.jme3.texture.TextureCubeMap;
import com.jme3.texture.image.ImageRaster;

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

public abstract class CommonHeaderDelegator implements DDSHeaderWriterDelegator{

	@Override
	public void header(Texture tx, ImageRaster ir,int mipmap,int slice, Map<String,Object> options, DDS_HEADER header) throws Exception {

		if(tx.getImage().hasMipmaps()) header.dwFlags|=DDSD_MIPMAPCOUNT;
		if(tx instanceof Texture3D) header.dwFlags|=DDSD_DEPTH;

		header.dwHeight=tx.getImage().getHeight();
		header.dwWidth=tx.getImage().getWidth();
		header.dwDepth=tx instanceof Texture3D?tx.getImage().getDepth():0;
		header.dwMipMapCount=tx.getImage().hasMipmaps()?tx.getImage().getMipMapSizes().length:0;

		if(tx.getImage().hasMipmaps()||tx instanceof Texture3D||tx instanceof TextureCubeMap) header.dwCaps|=DDSCAPS_COMPLEX;
		if(tx.getImage().hasMipmaps()) header.dwCaps|=DDSCAPS_MIPMAP;

		if(tx instanceof TextureCubeMap){
			header.dwCaps2|=DDSCAPS2_CUBEMAP;
			header.dwCaps2|=DDSCAPS2_CUBEMAP_POSITIVEX;
			header.dwCaps2|=DDSCAPS2_CUBEMAP_NEGATIVEX;
			header.dwCaps2|=DDSCAPS2_CUBEMAP_POSITIVEY;
			header.dwCaps2|=DDSCAPS2_CUBEMAP_NEGATIVEY;
			header.dwCaps2|=DDSCAPS2_CUBEMAP_POSITIVEZ;
			header.dwCaps2|=DDSCAPS2_CUBEMAP_NEGATIVEZ;
		}else if(tx instanceof Texture3D){
			header.dwCaps2|=DDSCAPS2_VOLUME;
		}

	}

}
