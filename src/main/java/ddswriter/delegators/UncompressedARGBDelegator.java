package ddswriter.delegators;
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
import ddswriter.delegators.common.CommonARGBHeaderDelegator;
import ddswriter.format.DDS_BODY;
import ddswriter.format.DDS_HEADER;
import static ddswriter.format.DDS_HEADER.*;
import static ddswriter.format.DDS_PIXELFORMAT.*;

/**
 * 
 * @author Riccardo Balbo
 */
public class UncompressedARGBDelegator extends CommonARGBHeaderDelegator implements DDSBodyWriterDelegator{

	@Override
	public void header(Texture tx,ImageRaster ir,int mipmap,int slice, Map<String,Object> options, DDS_HEADER header) throws Exception {
		super.header(tx,ir,mipmap,slice,options,header);
		if(mipmap==0&&slice==0){
			header.dwFlags|=DDSD_PITCH;
			header.dwPitchOrLinearSize=(tx.getImage().getWidth()*32+7)/8;

			header.ddspf.dwRGBBitCount=32;

			header.ddspf.dwRBitMask=0x00FF0000;
			header.ddspf.dwGBitMask=0x0000FF00;
			header.ddspf.dwBBitMask=0x000000FF;
			header.ddspf.dwABitMask=0xFF000000;
			
			header.ddspf.dwFlags|=DDPF_RGB;
		}
	}

	@Override
	public void body(Texture tx,ImageRaster ir,int mipmap,int slice, Map<String,Object> options,DDS_HEADER header, DDS_BODY body) throws Exception {
		int w=ir.getWidth();
		int h=ir.getHeight();
		for(int y=0;y<h;y++){
			for(int x=0;x<w;x++){
				ColorRGBA c=ir.getPixel(x,y);
				body.writePixel(c);		
			}
		}
	}


}
