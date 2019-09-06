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
	protected DataOutputStream DOS;


	public DDSOutputStream(OutputStream os){
		DOS=new DataOutputStream(os);
	}


	/**
	 * 
	 *  32bit
	 */
	public void writeDWord(int i) throws IOException {
		DOS.writeInt(Integer.reverseBytes(i));
	}

	/**
	 * 
	 *  32bit
	 */
	public void writeDWords(int... i) throws IOException {
		for(int k:i){
			writeDWord(k);
		}

	}


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
