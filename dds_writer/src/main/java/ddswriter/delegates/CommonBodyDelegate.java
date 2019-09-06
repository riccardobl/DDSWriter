/**
Copyright 2017 Riccardo Balbo

Permission is hereby granted, free of charge, to any person obtaining a copy of this software 
and associated documentation files (the "Software"), to deal in the Software without restriction, 
including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, 
and/or sell copies of the Software, and to permit persons to whom the Software is furnished 
to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE 
USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
package ddswriter.delegates;

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

import ddswriter.DDSSlicedDelegate;
import ddswriter.Texel;
import ddswriter.format.DDS_BODY;
import ddswriter.format.DDS_HEADER;

/**
 * 
 * @author Riccardo Balbo
 */

public abstract class CommonBodyDelegate extends DDSSlicedDelegate{
	
	int numMipMaps(Vector2f s){
        return  1 + (int)(Math.ceil(Math.log(s.x>s.y?s.x:s.y) / Math.log(2f)))  ;

	}
	@Override
	public void header(Texture tx, Map<String,String> options, DDS_HEADER header) throws Exception {
		String gen_mm=options.get("gen-mipmaps");
		if(gen_mm==null)gen_mm="false";
		int mipmaps_count=tx.getImage().hasMipmaps()?tx.getImage().getMipMapSizes().length:0;
		if(mipmaps_count==0&&gen_mm.equals("true")){
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
		String gen_mm=options.get("gen-mipmaps");
		if(gen_mm==null)gen_mm="false";
		boolean gen_mipmaps=!tx.getImage().hasMipmaps()&&gen_mm.equals("true");
		if(mipmap==0&&gen_mipmaps){
			int n=numMipMaps(new Vector2f(tx.getImage().getWidth(),tx.getImage().getHeight()))-1/*first mipmap is the image*/;
			String input_srgb=options.get("srgb");

			Texel mipmaps[]=ir.getMipMap(n,false,input_srgb!=null);
			super.process_slice(tx,ir,mipmap,slice,options,header,body);
			for(int i=0;i<n;i++){
				Texel m=mipmaps[i];
				super.process_slice(tx,m,i+1,slice,options,header,body); 
			}
		}else{
			super.process_slice(tx,ir,mipmap,slice,options,header,body);
		}
	}


}
