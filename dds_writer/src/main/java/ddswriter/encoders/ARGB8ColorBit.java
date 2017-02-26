package ddswriter.encoders;

import com.jme3.math.Vector4f;
/**
 * 
 * @author Riccardo Balbo
 */
public class ARGB8ColorBit implements ColorBit{

	@Override
	public byte getBPP() {
		return 32;
	}

	@Override
	public byte[] getBytes(Vector4f float_color) {
		int b=(int)(float_color.z*255f);
		int g=(int)(float_color.y*255f);
		int r=(int)(float_color.x*255f);
		int a=(int)(float_color.w*255f);
		byte out[]=new byte[5];
		out[0]=getBPP();
		out[1]=(byte)b;
		out[2]=(byte)g;
		out[3]=(byte)r;
		out[4]=(byte)a;
		return out;
	}

	
	@Override
	public int getAColorMask() {
		return 0xFF000000;
	}

	@Override
	public int getRColorMask() {
		return 0x00FF0000;
	}

	@Override
	public int getGColorMask() {
		return 0x0000FF00;
	}

	@Override
	public int getBColorMask() {
		return 0x000000FF;
	}

	@Override
	public boolean hasAlpha() {
		return true;
	}

}
