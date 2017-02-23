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
// Ref: https://msdn.microsoft.com/en-us/library/windows/desktop/bb943991(v=vs.85).aspx
public class DDS_HEADER extends WritableStruct{


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
	
	public int dwMagic=0x20534444;
	public int dwSize=124;
	
	@DumpableBitfield(possible_values={"DDSD_CAPS","DDSD_HEIGHT","DDSD_WIDTH","DDSD_PITCH","DDSD_PIXELFORMAT","DDSD_MIPMAPCOUNT","DDSD_LINEARSIZE","DDSD_DEPTH"})
	public int dwFlags=DDSD_CAPS|DDSD_HEIGHT|DDSD_WIDTH|DDSD_PIXELFORMAT;
	
	public int dwHeight;
	public int dwWidth;
	public int dwPitchOrLinearSize;
	public int dwDepth;
	public int dwMipMapCount;
	public final int dwReserved1[]=new int[11];
	public final DDS_PIXELFORMAT ddspf=new DDS_PIXELFORMAT();
	
	@DumpableBitfield(possible_values={"DDSCAPS_COMPLEX","DDSCAPS_MIPMAP","DDSCAPS_TEXTURE"})
	public int dwCaps=DDSCAPS_TEXTURE;
	
	@DumpableBitfield(possible_values={	"DDSCAPS2_VOLUME","DDSCAPS2_CUBEMAP","DDSCAPS2_CUBEMAP_POSITIVEX","DDSCAPS2_CUBEMAP_NEGATIVEX","DDSCAPS2_CUBEMAP_POSITIVEY","DDSCAPS2_CUBEMAP_NEGATIVEY","DDSCAPS2_CUBEMAP_POSITIVEZ","DDSCAPS2_CUBEMAP_NEGATIVEZ"}) 
	public int dwCaps2;
	
	@DumpableBitfield public int dwCaps3;
	@DumpableBitfield public int dwCaps4;
	@DumpableBitfield public int dwReserved2;
	
	@Override
	public void write(DDSOutputStream os) throws IOException{
		os.writeDWord(dwMagic); 
		os.writeDWord(dwSize); 
		os.writeDWord(dwFlags); 
		os.writeDWord(dwHeight); 
		os.writeDWord(dwWidth); 
		os.writeDWord(dwPitchOrLinearSize);
		os.writeDWord(dwDepth); 
		os.writeDWord(dwMipMapCount); 
		os.writeDWords(dwReserved1); 
		ddspf.write(os);
		os.writeDWord(dwCaps);
		os.writeDWord(dwCaps2);
		os.writeDWord(dwCaps3); 
		os.writeDWord(dwCaps4);
		os.writeDWord(dwReserved2); 
	}
	
	protected void dumpField(Field f,Collection<Field> flags,StringBuilder sb) throws IllegalArgumentException, IllegalAccessException{
		if(f.getName().equals("ddspf")){
			sb.append("ddspf {\n");
			String ddspf_ss[]=ddspf.dump().split("\n");
			for(String s:ddspf_ss){
				sb.append("    ").append(s).append("\n");
			}
			sb.append("}");
		}else{
			super.dumpField(f,flags,sb);
		}
	}


}
