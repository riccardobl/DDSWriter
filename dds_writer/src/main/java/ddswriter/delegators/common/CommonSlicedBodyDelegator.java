package ddswriter.delegators.common;

import static ddswriter.format.DDS_HEADER.DDSCAPS2_CUBEMAP;
import static ddswriter.format.DDS_HEADER.DDSCAPS2_CUBEMAP_NEGATIVEX;
import static ddswriter.format.DDS_HEADER.DDSCAPS2_CUBEMAP_NEGATIVEY;
import static ddswriter.format.DDS_HEADER.DDSCAPS2_CUBEMAP_NEGATIVEZ;
import static ddswriter.format.DDS_HEADER.DDSCAPS2_CUBEMAP_POSITIVEX;
import static ddswriter.format.DDS_HEADER.DDSCAPS2_CUBEMAP_POSITIVEY;
import static ddswriter.format.DDS_HEADER.DDSCAPS2_CUBEMAP_POSITIVEZ;
import static ddswriter.format.DDS_HEADER.DDSCAPS2_VOLUME;
import static ddswriter.format.DDS_HEADER.DDSCAPS_COMPLEX;
import static ddswriter.format.DDS_HEADER.DDSCAPS_MIPMAP;
import static ddswriter.format.DDS_HEADER.DDSD_DEPTH;
import static ddswriter.format.DDS_HEADER.DDSD_MIPMAPCOUNT;

import java.util.Map;

import com.jme3.texture.Texture;
import com.jme3.texture.Texture3D;
import com.jme3.texture.TextureCubeMap;
import com.jme3.texture.image.ImageRaster;

import ddswriter.DDSSlicedDelegator;
import ddswriter.format.DDS_BODY;
import ddswriter.format.DDS_HEADER;

/**
 * 
 * @author Riccardo Balbo
 */

public abstract class CommonSlicedBodyDelegator extends DDSSlicedDelegator{
	@Override
	public void header(Texture tx, Map<String,String> options, DDS_HEADER header) throws Exception {
		if(SKIP)return;
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
		super.header(tx,options,header);

	}
	


}
