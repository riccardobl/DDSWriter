package ddswriter.delegators;

import static ddswriter.format.DDS_HEADER.DDSD_PITCH;
import static ddswriter.format.DDS_PIXELFORMAT.DDPF_ALPHAPIXELS;
import static ddswriter.format.DDS_PIXELFORMAT.DDPF_RGB;

import java.util.Map;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector4f;
import com.jme3.texture.Texture;
import com.jme3.texture.image.ImageRaster;

import ddswriter.Texel;
import ddswriter.Texel.PixelFormat;
import ddswriter.colors.ARGB8ColorBit;
import ddswriter.colors.ColorBit;
import ddswriter.colors.RGB565ColorBit;
import ddswriter.colors.RGB8ColorBit;
import ddswriter.delegators.common.CommonSlicedBodyDelegator;
import ddswriter.format.DDS_BODY;
import ddswriter.format.DDS_HEADER;

/**
 * 
 * @author Riccardo Balbo
 */
public class GenericDelegator extends CommonSlicedBodyDelegator{
	public static enum Format{
		ARGB8(new ARGB8ColorBit()),RGB8(new RGB8ColorBit()),

		RGB565(new RGB565ColorBit());

		public ColorBit colorbit;

		private Format(ColorBit bc){
			colorbit=bc;
		}

	}

	protected Format FORMAT;

	@Override
	public void header(Texture tx, Map<String,String> options, DDS_HEADER header) throws Exception {
		super.header(tx,options,header);
		String format=((String)options.get("format"));
		
		if(format==null){
			format="ARGB8";

		}else{
			format=format.toUpperCase();
		}

		for(Format f:Format.values()){
			if(f.toString().equals(format)) FORMAT=f;
		}

		if(FORMAT==null){
			System.out.println(this.getClass()+" does not support "+format+". skip");
			SKIP=true;
			return;
		}

		System.out.println("Use "+this.getClass()+"  with format "+format+". ");

		header.dwFlags|=DDSD_PITCH;
		header.dwPitchOrLinearSize=(tx.getImage().getWidth()*FORMAT.colorbit.getBPP()+7)/8;

		header.ddspf.dwRGBBitCount=FORMAT.colorbit.getBPP();

		header.ddspf.dwRBitMask=FORMAT.colorbit.getRColorMask();
		header.ddspf.dwGBitMask=FORMAT.colorbit.getGColorMask();
		header.ddspf.dwBBitMask=FORMAT.colorbit.getBColorMask();
		if(FORMAT.colorbit.hasAlpha()){
			header.ddspf.dwFlags|=DDPF_ALPHAPIXELS;
			header.ddspf.dwABitMask=FORMAT.colorbit.getAColorMask();
		}

		header.ddspf.dwFlags|=DDPF_RGB;

	}

	@Override
	public void body(Texture tx, Texel ir, int mipmap, int slice, Map<String,String> options, DDS_HEADER header, DDS_BODY body) throws Exception {
		if(FORMAT==null) return;
		int w=ir.getWidth();
		int h=ir.getHeight();
		for(int y=0;y<h;y++){
			for(int x=0;x<w;x++){
				Vector4f c=ir.get(PixelFormat.FLOAT_NORMALIZED_RGBA,x,y);
				body.writeColorBit(FORMAT.colorbit.getBytes(c));
			}
		}
	}

	@Override
	public void header(Texture tx, Texel ir, int mipmap, int slice, Map<String,String> options, DDS_HEADER header) throws Exception {

	}

}
