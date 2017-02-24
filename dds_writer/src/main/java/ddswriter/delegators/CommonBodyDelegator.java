package ddswriter.delegators;

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

import com.jme3.math.Vector2f;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture3D;
import com.jme3.texture.TextureCubeMap;

import ddswriter.DDSSlicedDelegator;
import ddswriter.Texel;
import ddswriter.format.DDS_BODY;
import ddswriter.format.DDS_HEADER;

/**
 * 
 * @author Riccardo Balbo
 */

public abstract class CommonBodyDelegator extends DDSSlicedDelegator{
	
	int numMipMaps(Vector2f s){
        return  1 + (int)(Math.ceil(Math.log(s.x>s.y?s.x:s.y) / Math.log(2f)))  ;

	}
	@Override
	public void header(Texture tx, Map<String,String> options, DDS_HEADER header) throws Exception {
		if(SKIP)return;
		int mipmaps_count=tx.getImage().hasMipmaps()?tx.getImage().getMipMapSizes().length:0;
		if(mipmaps_count==0&&options.getOrDefault("gen-mipmaps","false").equals("true")){
			mipmaps_count=numMipMaps(new Vector2f(tx.getImage().getWidth(),tx.getImage().getHeight()));
		}

		if(mipmaps_count>0) header.dwFlags|=DDSD_MIPMAPCOUNT;
		if(tx instanceof Texture3D) header.dwFlags|=DDSD_DEPTH;

		header.dwHeight=tx.getImage().getHeight();
		header.dwWidth=tx.getImage().getWidth();
		header.dwDepth=tx instanceof Texture3D?tx.getImage().getDepth():0;
		header.dwMipMapCount=mipmaps_count;

		if(mipmaps_count>0||tx instanceof Texture3D||tx instanceof TextureCubeMap) header.dwCaps|=DDSCAPS_COMPLEX;
		if(mipmaps_count>0) header.dwCaps|=DDSCAPS_MIPMAP;

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
	
	public  void process_slice(Texture tx, Texel ir, int mipmap, int slice, Map<String,String> options, DDS_HEADER header, DDS_BODY body) throws Exception{
		boolean gen_mipmaps=!tx.getImage().hasMipmaps()&&options.getOrDefault("gen-mipmaps","false").equals("true");
		if(mipmap==0&&gen_mipmaps){
			int n=numMipMaps(new Vector2f(tx.getImage().getWidth(),tx.getImage().getHeight()));
			Texel mipmaps[]=ir.getMipMap(n,false);
			super.process_slice(tx,ir,mipmap,slice,options,header,body);
			for(int i=0;i<mipmaps.length-1/*first mipmap is the image*/;i++){
				Texel m=mipmaps[i];
				super.process_slice(tx,m,i+1,slice,options,header,body);
			}
		}else{
			super.process_slice(tx,ir,mipmap,slice,options,header,body);
		}
	}


}
