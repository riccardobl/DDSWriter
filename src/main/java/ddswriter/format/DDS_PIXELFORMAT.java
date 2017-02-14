package ddswriter.format;

import java.io.IOException;

import ddswriter.DDSOutputStream;
import ddswriter.format.dumper.DumpableBitfield;

/**
 * 
 * @author Riccardo Balbo
 *
 */
// Ref: https://msdn.microsoft.com/en-us/library/windows/desktop/bb943984(v=vs.85).aspx
public class DDS_PIXELFORMAT extends WritableStruct{
	DDS_PIXELFORMAT(){}

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
	
	public int dwSize=32;
	
	@DumpableBitfield(possible_values={"DDPF_ALPHAPIXELS","DDPF_ALPHA","DDPF_FOURCC","DDPF_RGB","DDPF_YUV","DDPF_LUMINANCE"})
	public int dwFlags;
	
	public final byte dwFourCC[]=new byte[4];
	public int dwRGBBitCount;
	public int dwRBitMask;
	public int dwGBitMask;
	public int dwBBitMask;
	public int dwABitMask;

	@Override
	public void write(DDSOutputStream os) throws IOException {
		os.writeDWord(dwSize);
		os.writeDWord(dwFlags);
		os.write(dwFourCC);
		os.writeDWord(dwRGBBitCount);
		os.writeDWord(dwRBitMask);
		os.writeDWord(dwGBitMask);
		os.writeDWord(dwBBitMask);
		os.writeDWord(dwABitMask);
	}
}
