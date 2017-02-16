package ddswriter;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.jme3.math.ColorRGBA;

/**
 * 
 * @author Riccardo Balbo
 *
 */
public class DDSOutputStream extends OutputStream{
	protected String PIXEL_FORMAT;
	protected DataOutputStream DOS;

	public DDSOutputStream(OutputStream os){
		this(os,"argb");
	}

	public DDSOutputStream(OutputStream os,String pixelformat){
		DOS=new DataOutputStream(os);
		PIXEL_FORMAT=new StringBuilder(pixelformat).reverse().toString();
	}

	public void writeInt(int i) throws IOException{
		DOS.writeInt(i);
	}
	
	/**
	 * 
	 * @description 16bit
	 */
	public void writeWord(int i) throws IOException {
		DOS.writeShort(Short.reverseBytes((short)i));

	}
	
	/**
	 * 
	 * @description 16bit
	 */
	public void writeWords(int... ws) throws IOException {
		for(int w:ws)
			writeWord(w);
	}

	/**
	 * 
	 * @description 32bit
	 */
	public void writeDWord(int i) throws IOException {
		DOS.writeInt(Integer.reverseBytes(i));
	}

	/**
	 * 
	 * @description 32bit
	 */
	public void writeDWords(int... i) throws IOException {
		for(int k:i){
			writeDWord(k);
		}

	}
	
	public void writeByte(int b) throws IOException{
		DOS.writeByte(b);
	}
	
	public void writeBytes(byte ...bs){
		for(byte b:bs){
			writeBytes(bs);
		}
	}
	
	

	public void writePixel(int r, int g, int b, int a) throws IOException {
		for(int i=0;i<PIXEL_FORMAT.length();i++){
			switch(PIXEL_FORMAT.charAt(i)){
				case 'r':
					DOS.writeByte(r);
					break;
				case 'g':
					DOS.writeByte(g);
					break;
				case 'b':
					DOS.writeByte(b);
					break;
				case 'a':
					DOS.writeByte(a);
					break;

			}
		}
	}

	public void writePixel(ColorRGBA c) throws IOException {
		int b=(int)(c.b*255f);
		int g=(int)(c.g*255f);
		int r=(int)(c.r*255f);
		int a=(int)(c.a*255f);
		writePixel(r,g,b,a);
	}

	@Override
	public void write(int b) throws IOException {
		DOS.write(b);
	}

	
	@Override
	public void close() throws IOException{
		DOS.close();
	}
	
	@Override
	public void flush() throws IOException{
		DOS.flush();
	}
}
