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
package ddswriter.format;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;

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

	
	protected void dumpField(Field f,Collection<Field> flags,StringBuilder sb) throws IllegalArgumentException, IllegalAccessException{
		if(f.getName().equals("dwFourCC")){
			sb.append("dwFourCC =  ");
			sb.append(new String(dwFourCC));
		}else{
			super.dumpField(f,flags,sb);
		}
	}
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
