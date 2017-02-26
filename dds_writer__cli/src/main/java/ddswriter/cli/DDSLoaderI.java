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
package ddswriter.cli;

import java.io.IOException;
import java.io.InputStream;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.TextureKey;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.Type;
import com.jme3.texture.Texture2D;
import com.jme3.texture.Texture3D;
import com.jme3.texture.TextureCubeMap;
import com.jme3.texture.plugins.DDSLoader;

/**
 * 
 * @author Riccardo Balbo
 */
public class DDSLoaderI{
	public static Texture load(InputStream is) throws IOException {
		return load(is,false);
	}

	public static Texture load(final InputStream is,final boolean flip) throws IOException {

		final byte type[]={0}; // 0=2d 1=3d 2=cube

		TextureKey key=new TextureKey(){
			public boolean isFlipY() {
				return flip;
			}

			public void setTextureTypeHint(Type textureTypeHint) {
				if(textureTypeHint==Type.ThreeDimensional){
					type[0]=1;
				}else if(textureTypeHint==Type.CubeMap){
					type[0]=2;
				}
			}
		};

		AssetInfo ai=new AssetInfo(null,key){
			@Override
			public InputStream openStream() {
				return is;
			}

		};

		DDSLoader loader=new DDSLoader();
		Image img=(Image)loader.load(ai);

		Texture tx;

		switch(type[0]){
			default:{
				tx=new Texture2D(img);
				break;
			}
			case 1:{
				tx=new Texture3D(img);
				break;
			}
			case 2:{
				tx=new TextureCubeMap(img);
				break;
			}
		}
		return tx;

	}

}
