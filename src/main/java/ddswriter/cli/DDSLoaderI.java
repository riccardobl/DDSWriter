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

	public static Texture load(InputStream is,boolean flip) throws IOException {

		byte type[]={0}; // 0=2d 1=3d 2=cube

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
