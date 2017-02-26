package ddswriter.encoders;

import com.jme3.math.Vector4f;
/**
 * 
 * @author Riccardo Balbo
 */
public interface ColorBit{
	public byte getBPP();
	public  byte[] getBytes(Vector4f float_color);
	public int getAColorMask();
	public int getRColorMask();
	public int getGColorMask();
	public int getBColorMask();
	public boolean hasAlpha();

}
