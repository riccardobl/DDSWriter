package ddswriter.colors;

import com.jme3.math.Vector4f;

public class RGB8ColorBit implements ColorBit{

	@Override
	public byte getBPP() {
		return 24;
	}

	@Override
	public byte[] getBytes(Vector4f float_color) {
		byte r=(byte)Math.ceil(float_color.x*255f);
		byte g=(byte)Math.ceil(float_color.y*255f);
		byte b=(byte)Math.ceil(float_color.z*255f);
		return new byte[]{getBPP(),b,g,r};
	}



	@Override
	public int getAColorMask() {
		return 0;
	}

	@Override
	public int getRColorMask() {
		return 0xFF0000;
	}

	@Override
	public int getGColorMask() {
		return 0x00FF00;
	}

	@Override
	public int getBColorMask() {
		return 0x0000FF;
	}

	@Override
	public boolean hasAlpha() {
		return false;
	}

}
