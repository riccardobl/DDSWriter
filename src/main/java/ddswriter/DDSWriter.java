package ddswriter;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.jme3.math.ColorRGBA;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.Texture3D;
import com.jme3.texture.TextureCubeMap;
import com.jme3.texture.image.ImageRaster;

import ddswriter.delegators.CompressedRGBADelegator;
import ddswriter.delegators.UncompressedRGBADelegator;
import jme3tools.converters.MipMapGenerator;

/**
 * 
 * @author Riccardo Balbo
 */
public class DDSWriter{
	public static class Options{
		public boolean gen_mipmaps=false;
		public boolean compress=false;
	}

	public static void write(Texture tx, OutputStream output, Options options) throws Exception {
		DDSWriter dds=new DDSWriter();
		dds.doExport(tx,output,options);
	}

	protected DataOutputStream OSTREAM;
	protected DDSDelegator DELEGATOR;

	// ################# FLAGS #####################
	// Required in every .dds file.
	public static final int DDSD_CAPS=0x1;
	// Required in every .dds file.
	public static final int DDSD_HEIGHT=0x2;
	// Required in every .dds file.
	public static final int DDSD_WIDTH=0x4;
	// Required when pitch is provided for an uncompressed texture.
	public static final int DDSD_PITCH=0x8;
	// Required in every .dds file.
	public static final int DDSD_PIXELFORMAT=0x1000;
	// Required in a mipmapped texture.
	public static final int DDSD_MIPMAPCOUNT=0x20000;
	// Required when pitch is provided for a compressed texture.
	public static final int DDSD_LINEARSIZE=0x80000;
	// Required in a depth texture.
	public static final int DDSD_DEPTH=0x800000;

	// ################# CAPS #####################
	// Optional; must be used on any file that contains more than one surface (a mipmap, a cubic environment map, or mipmapped volume texture).
	public static final int DDSCAPS_COMPLEX=0x8;
	// Optional; should be used for a mipmap.
	public static final int DDSCAPS_MIPMAP=0x400000;
	// Required
	public static final int DDSCAPS_TEXTURE=0x1000;

	// ################# CAPS2 #####################
	//Required for a cube map.
	public static final int DDSCAPS2_CUBEMAP=0x200;
	//Required when these surfaces are stored in a cube map.
	public static final int DDSCAPS2_CUBEMAP_POSITIVEX=0x400;
	//Required when these surfaces are stored in a cube map.
	public static final int DDSCAPS2_CUBEMAP_NEGATIVEX=0x800;
	//Required when these surfaces are stored in a cube map.
	public static final int DDSCAPS2_CUBEMAP_POSITIVEY=0x1000;
	//Required when these surfaces are stored in a cube map.
	public static final int DDSCAPS2_CUBEMAP_NEGATIVEY=0x2000;
	//Required when these surfaces are stored in a cube map.
	public static final int DDSCAPS2_CUBEMAP_POSITIVEZ=0x4000;
	//Required when these surfaces are stored in a cube map.
	public static final int DDSCAPS2_CUBEMAP_NEGATIVEZ=0x8000;
	//Required for a volume texture.
	public static final int DDSCAPS2_VOLUME=0x200000;

	// ################# PIXEL FLAGS #####################
	//Texture contains alpha data; dwRGBAlphaBitMask contains valid data.
	public static final int DDPF_ALPHAPIXELS=0x1;
	//Used in some older DDS files for alpha channel only uncompressed data (dwRGBBitCount contains the alpha channel bitcount; dwABitMask contains valid data)
	public static final int DDPF_ALPHA=0x2;
	//Texture contains compressed RGB data; dwFourCC contains valid data.
	public static final int DDPF_FOURCC=0x4;
	//Texture contains uncompressed RGB data; dwRGBBitCount and the RGB masks (dwRBitMask, dwGBitMask, dwBBitMask) contain valid data.
	public static final int DDPF_RGB=0x40;
	//Used in some older DDS files for YUV uncompressed data (dwRGBBitCount contains the YUV bit count; dwRBitMask contains the Y mask, dwGBitMask contains the U mask, dwBBitMask contains the V mask)
	public static final int DDPF_YUV=0x200;
	//Used in some older DDS files for single channel color uncompressed data (dwRGBBitCount contains the luminance channel bit count; dwRBitMask contains the channel mask). Can be combined with DDPF_ALPHAPIXELS for a two channel DDS file.
	public static final int DDPF_LUMINANCE=0x20000;

	public void doExport(Texture tx, OutputStream output, Options options) throws Exception {
		// TODO: Add support for RGB 
		// TODO: Add support for DX10 HEADER
		// TODO: Add mipmap generation for texture 3d and texture cubemap

		if(options.gen_mipmaps&&!tx.getImage().hasMipmaps()&&tx instanceof Texture2D) MipMapGenerator.generateMipMaps(tx.getImage());

		OSTREAM=new DataOutputStream(output);

		if(options.compress){
			DELEGATOR=new CompressedRGBADelegator();
		}else{
			DELEGATOR=new UncompressedRGBADelegator();
		}
		
		// ################# HEADER #####################
		DWORD(0x20534444); // dwMagic
		DWORD(124); // dwSize

		{// dwFlags
			int flags=DDSD_CAPS|DDSD_HEIGHT|DDSD_WIDTH|DDSD_PIXELFORMAT;
			flags|=options.compress?DDSD_LINEARSIZE:DDSD_PITCH;
			if(tx.getImage().hasMipmaps()) flags|=DDSD_MIPMAPCOUNT;
			if(tx instanceof Texture3D) flags|=DDSD_DEPTH;
			DWORD(flags);
		}

		DWORD(tx.getImage().getHeight()); // dwHeight
		DWORD(tx.getImage().getWidth()); // dwWidth

		{// dwPitchOrLinearSize 			
			if(options.compress){
				/*
				 * For block-compressed formats, compute the pitch as: max( 1, ((width+3)/4) ) * block-size
				 */
				DWORD(DELEGATOR.dwPitchOrLinearSize(tx.getImage().getWidth()));
			}else{
				/*
				 * For other formats, compute the pitch as: ( width * bits-per-pixel + 7 ) / 8 				
				 */
				DWORD((tx.getImage().getWidth()*32+7)/8);
			}
		}

		DWORD(tx instanceof Texture3D?tx.getImage().getDepth():0); // dwDepth
		DWORD(tx.getImage().hasMipmaps()?tx.getImage().getMipMapSizes().length:0); // dwMipMapCount
		DWORD(0,11); // dwReserved1[11]

		{// ddspf
			DWORD(32); // dwSize
			DWORD(DDPF_ALPHAPIXELS|(options.compress?DDPF_FOURCC:DDPF_RGB)); // dwFlags

			{// dwFourCC 
				if(options.compress){
					BYTE('D');
					BYTE('X');
					BYTE('T');
					BYTE('5'); // TODO: Make a setting or smth
				}else{
					DWORD(0);
				}
			}

			DWORD(32); // dwRGBBitCount
			// argb TODO: make a setting for this
			DWORD(0x00FF0000); // dwRBitMask
			DWORD(0x0000FF00); // dwGBitMask
			DWORD(0x000000FF); // dwBBitMask
			DWORD(0xFF000000); // dwABitMask
		}

		{// dwCaps
			int caps=DDSCAPS_TEXTURE;
			if(tx.getImage().hasMipmaps()||tx instanceof Texture3D||tx instanceof TextureCubeMap) caps|=DDSCAPS_COMPLEX;
			if(tx.getImage().hasMipmaps()) caps|=DDSCAPS_MIPMAP;
			DWORD(caps);
		}

		{// dwCaps2
			int caps2=0;
			if(tx instanceof TextureCubeMap){
				caps2|=DDSCAPS2_CUBEMAP;
				caps2|=DDSCAPS2_CUBEMAP_POSITIVEX;
				caps2|=DDSCAPS2_CUBEMAP_NEGATIVEX;
				caps2|=DDSCAPS2_CUBEMAP_POSITIVEY;
				caps2|=DDSCAPS2_CUBEMAP_NEGATIVEY;
				caps2|=DDSCAPS2_CUBEMAP_POSITIVEZ;
				caps2|=DDSCAPS2_CUBEMAP_NEGATIVEZ;
			}else if(tx instanceof Texture3D){
				caps2|=DDSCAPS2_VOLUME;
			}
			DWORD(caps2);
		}

		DWORD(0); // dwCaps3
		DWORD(0); // dwCaps4
		DWORD(0); // dwReserved2

		// ################# BODY #####################		

		//		CompressedRGBADelegator urgba=new CompressedRGBADelegator();
		//UncompressedRGBADelegator urgba=new UncompressedRGBADelegator();

		int mipmaps=!tx.getImage().hasMipmaps()?1:tx.getImage().getMipMapSizes().length;

		if(tx instanceof Texture2D){
			for(int mipmap=0;mipmap<mipmaps;mipmap++){
				ImageRaster ir=ImageRaster.create(tx.getImage(),0,mipmap,false);
				DELEGATOR.process(ir,this);
			}
		}else if(tx instanceof TextureCubeMap){
			for(int slice=0;slice<6;slice++){
				for(int mipmap=0;mipmap<mipmaps;mipmap++){
					ImageRaster ir=ImageRaster.create(tx.getImage(),slice,mipmap,false);
					DELEGATOR.process(ir,this);
				}
			}
		}else if(tx instanceof Texture3D){
			for(int slice=0;slice<tx.getImage().getDepth();slice++){
				for(int mipmap=0;mipmap<mipmaps;mipmap++){
					ImageRaster ir=ImageRaster.create(tx.getImage(),slice,mipmap,false);
					DELEGATOR.process(ir,this);
				}
			}

		}

		OSTREAM.flush();
		OSTREAM=null;
	}

	protected void WORD(int i) throws IOException {
		byte[] dword=new byte[2];
		dword[0]=(byte)(i&0x00FF);
		dword[1]=(byte)((i>>8)&0x000000FF);
		OSTREAM.write(dword);
	}

	protected void WORD(int i, int j) throws IOException {
		for(int k=0;k<j;k++)
			WORD(i);
	}

	protected void DWORD(int i) throws IOException {
		byte[] dword=new byte[4];
		dword[0]=(byte)(i&0x00FF);
		dword[1]=(byte)((i>>8)&0x000000FF);
		dword[2]=(byte)((i>>16)&0x000000FF);
		dword[3]=(byte)((i>>24)&0x000000FF);
		OSTREAM.write(dword);
	}

	protected void DWORD(int i, int j) throws IOException {
		for(int k=0;k<j;k++)
			DWORD(i);
	}

	public void BYTE(int i) throws IOException {
		OSTREAM.writeByte((byte)i);
	}

	public void PIXEL(int r, int g, int b, int a) throws IOException {
		// argb TODO: make a setting for this
		BYTE(b);
		BYTE(g);
		BYTE(r);
		BYTE(a);
	}

	public void PIXEL(ColorRGBA c) throws IOException {
		int b=(int)(c.b*255f);
		int g=(int)(c.g*255f);
		int r=(int)(c.r*255f);
		int a=(int)(c.a*255f);
		PIXEL(r,g,b,a);
	}

}