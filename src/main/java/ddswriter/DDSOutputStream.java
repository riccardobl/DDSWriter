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
public class DDSOutputStream extends DataOutputStream{
	protected String PIXEL_FORMAT;

	public DDSOutputStream(OutputStream os){
		this(os,"argb");
	}
	
	
	public DDSOutputStream(OutputStream os,String pixelformat){
		super(os);
		PIXEL_FORMAT=new StringBuilder(pixelformat).reverse().toString();
	}
	
	

		
	public void writeWord(int i) throws IOException {
		byte[] dword=new byte[2];
		dword[0]=(byte)(i&0x00FF);
		dword[1]=(byte)((i>>8)&0x000000FF);
		write(dword);
	}

	public void writeWords(int... ws) throws IOException {
		for(int w:ws)writeWord(w);
	}

	public void writeDWord(int i) throws IOException {
		byte[] dword=new byte[4];
		dword[0]=(byte)(i&0x00FF);
		dword[1]=(byte)((i>>8)&0x000000FF);
		dword[2]=(byte)((i>>16)&0x000000FF);
		dword[3]=(byte)((i>>24)&0x000000FF);
		write(dword);
	}
	
	public void writeDWords(int... i) throws IOException {
		for(int k:i){
			writeDWord(k);
		}
	}



	public void writePixel(int r, int g, int b, int a) throws IOException {
		for(int i=0;i<PIXEL_FORMAT.length();i++){
			switch(PIXEL_FORMAT.charAt(i)){
				case 'r':
					writeByte(r);
					break;
				case 'g':
					writeByte(g);
					break;
				case 'b':
					writeByte(b);
					break;
				case 'a':
					writeByte(a);
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


	
	
}
