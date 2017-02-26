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
package ddswriter;

import java.util.Map;

import com.jme3.math.Vector2f;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.Texture3D;
import com.jme3.texture.TextureCubeMap;
import com.jme3.texture.image.ImageRaster;

import ddswriter.format.DDS_BODY;
import ddswriter.format.DDS_HEADER;

/**
 * 
 * @author Riccardo Balbo
 */
public abstract class DDSSlicedDelegator implements DDSDelegator{
	private boolean SKIP;

	public void skip() {
		SKIP=true;
	}

	public abstract void body(Texture tx, Texel ir, int mipmap, int slice, Map<String,String> options, DDS_HEADER header, DDS_BODY body) throws Exception;

	public abstract void header(Texture tx, Texel ir, int mipmap, int slice, Map<String,String> options, DDS_HEADER header) throws Exception;

	
	@Override
	public void body(Texture tx, Map<String,String> options, DDS_HEADER header, DDS_BODY body) throws Exception {
		if(SKIP) return;
		slice(tx,options,header,body);
	}
	
	@Override
	public void header(Texture tx, Map<String,String> options, DDS_HEADER header) throws Exception {
//		if(SKIP) return;
		slice(tx,options,header,null);
	}

	public void process_slice(Texture tx, Texel ir, int mipmap, int slice, Map<String,String> options, DDS_HEADER header, DDS_BODY body) throws Exception {
		boolean is_header=body==null;

		if(is_header){
			header(tx,ir,mipmap,slice,options,header);

		}else{
			body(tx,ir,mipmap,slice,options,header,body);
		}

	}

	private void slice(Texture tx, Map<String,String> options, DDS_HEADER header, DDS_BODY body) throws Exception {

		int mipmaps=!tx.getImage().hasMipmaps()?1:tx.getImage().getMipMapSizes().length;
		//		boolean is_header=body==null;

		if(tx instanceof Texture2D){
			for(int mipmap=0;mipmap<mipmaps;mipmap++){
				Texel ir=Texel.fromImageRaster(ImageRaster.create(tx.getImage(),0,mipmap,false),new Vector2f(0,0),new Vector2f(tx.getImage().getWidth(),tx.getImage().getHeight()));
				process_slice(tx,ir,mipmap,0,options,header,body);
			}
		}else if(tx instanceof TextureCubeMap){
			for(int slice=0;slice<6;slice++){
				for(int mipmap=0;mipmap<mipmaps;mipmap++){
					Texel ir=Texel.fromImageRaster(ImageRaster.create(tx.getImage(),slice,mipmap,false),new Vector2f(0,0),new Vector2f(tx.getImage().getWidth(),tx.getImage().getHeight()));
					//					if(is_header){
					//						header(tx,ir,mipmap,slice,options,header);
					//
					//					}else{
					//						body(tx,ir,mipmap,slice,options,header,body);
					//					}
					process_slice(tx,ir,mipmap,0,options,header,body);

				}
			}
		}else if(tx instanceof Texture3D){
			for(int slice=0;slice<tx.getImage().getDepth();slice++){
				for(int mipmap=0;mipmap<mipmaps;mipmap++){
					Texel ir=Texel.fromImageRaster(ImageRaster.create(tx.getImage(),slice,mipmap,false),new Vector2f(0,0),new Vector2f(tx.getImage().getWidth(),tx.getImage().getHeight()));
					//					if(is_header){
					//						header(tx,ir,mipmap,slice,options,header);
					//
					//					}else{
					//						body(tx,ir,mipmap,slice,options,header,body);
					//					}
					process_slice(tx,ir,mipmap,0,options,header,body);

				}
			}

		}

	}

}
