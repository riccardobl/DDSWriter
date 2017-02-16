package ddswriter.delegators;

import static ddswriter.format.DDS_HEADER.DDSD_PITCH;
import static ddswriter.format.DDS_PIXELFORMAT.DDPF_ALPHAPIXELS;
import static ddswriter.format.DDS_PIXELFORMAT.DDPF_RGB;

import java.util.Map;

import com.jme3.math.ColorRGBA;
import com.jme3.texture.Texture;
import com.jme3.texture.image.ImageRaster;

import ddswriter.DDSBodyWriterDelegator;
import ddswriter.colors.ARGB8ColorBit;
import ddswriter.colors.ColorBit;
import ddswriter.colors.RGB565ColorBit;
import ddswriter.colors.RGB8ColorBit;
import ddswriter.delegators.common.CommonHeaderDelegator;
import ddswriter.format.DDS_BODY;
import ddswriter.format.DDS_HEADER;

/**
 * 
 * @author Riccardo Balbo
 */
public class GenericDelegator extends CommonHeaderDelegator implements DDSBodyWriterDelegator{
	public static enum Format{
		ARGB8(new ARGB8ColorBit()),
		RGB8(new RGB8ColorBit()),

		RGB565(new RGB565ColorBit());
		
		public ColorBit colorbit;
		private Format(ColorBit bc){
			colorbit=bc;
		}

	}

	protected Format FORMAT;

	@Override
	public void header(Texture tx, ImageRaster ir, int mipmap, int slice, Map<String,Object> options, DDS_HEADER header) throws Exception {
		super.header(tx,ir,mipmap,slice,options,header);
		if(mipmap==0&&slice==0){
			String format=((String)options.get("format"));
			if(format==null){
				if(!(boolean)options.getOrDefault("compressed",false)){
					format="ARGB8";
				}
			}else{
				format=format.toUpperCase();
			}

			for(Format f:Format.values()){
				if(f.toString().equals(format)) FORMAT=f;
			}

			if(FORMAT==null){
				System.out.println("No format selected "+format+". skip");
				return;
			}
			
			System.out.println("Use format "+format+".");

			
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
	}

	@Override
	public void body(Texture tx, ImageRaster ir, int mipmap, int slice, Map<String,Object> options, DDS_HEADER header, DDS_BODY body) throws Exception {
		if(FORMAT==null) return;
		int w=ir.getWidth();
		int h=ir.getHeight();
		for(int y=0;y<h;y++){
			for(int x=0;x<w;x++){
				ColorRGBA c=ir.getPixel(x,y);
				body.writeColorBit(FORMAT.colorbit.getBytes(c.toVector4f()));
			}
		}
	}

}
