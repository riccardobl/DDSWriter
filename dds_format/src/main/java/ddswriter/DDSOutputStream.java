package ddswriter;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 
 * @author Riccardo Balbo
 *
 */
public class DDSOutputStream extends OutputStream{
//	protected String PIXEL_FORMAT;
	protected DataOutputStream DOS;

//	public DDSOutputStream(OutputStream os){
//		this(os,"argb");
//	}

	public DDSOutputStream(OutputStream os){//,String pixelformat){
		DOS=new DataOutputStream(os);
//		PIXEL_FORMAT=new StringBuilder(pixelformat).reverse().toString();
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
	
	
//
//	public void writePixel(int r, int g, int b, int a) throws IOException {
//		for(int i=0;i<PIXEL_FORMAT.length();i++){
//			switch(PIXEL_FORMAT.charAt(i)){
//				case 'r':
//					DOS.writeByte(r);
//					break;
//				case 'g':
//					DOS.writeByte(g);
//					break;
//				case 'b':
//					DOS.writeByte(b);
//					break;
//				case 'a':
//					DOS.writeByte(a);
//					break;
//
//			}
//		}
//	}
//
//	public void writePixel(ColorRGBA c) throws IOException {
//		int b=(int)(c.b*255f);
//		int g=(int)(c.g*255f);
//		int r=(int)(c.r*255f);
//		int a=(int)(c.a*255f);
//		writePixel(r,g,b,a);
//	}
	
	

	protected Byte COLORBIT_ACCUMULATOR;
	protected int COLORBIT_ACCUMULATEDN;
	public void writeColorBit(byte bytes[]) throws IOException {
		int nbits=bytes[0];
		int j=1;
		int b=((int)bytes[j++])&0xff;
		for(int i=0;i<nbits;i++){
			if(i>0&&i%8==0){
				b=((int)bytes[j++])&0xff;
			}
			writeBit(b&0b1);
			b>>=1;	
		}	
	}
	
	private int bits_accumulator=0;
	private int acumulated_bits;

	private void writeBit(int bit) throws IOException {
		bits_accumulator|=bit<<acumulated_bits;
		acumulated_bits++;
		if(acumulated_bits==8){
			write(bits_accumulator);
			bits_accumulator=0;
			acumulated_bits=0;
		}
	}

	private void flushBits() throws IOException {
		if(acumulated_bits==0) return;
		for(int i=acumulated_bits;i<8;i++){
			writeBit(0);
		}

	}


	@Override
	public void write(int b) throws IOException {
		DOS.write(b);
	}

	
	@Override
	public void close() throws IOException{
		flushBits();
		DOS.close();
	}
	
	@Override
	public void flush() throws IOException{
		flushBits();
		DOS.flush();
	}
}
