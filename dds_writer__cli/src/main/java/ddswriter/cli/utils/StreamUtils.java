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
package ddswriter.cli.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Scanner;
/**
 * 
 * @author Riccardo Balbo
 */
public class StreamUtils{
	public static void inputStreamToOutputStream(InputStream in,OutputStream out) throws IOException{
		byte[] buffer = new byte[1024*50];
		int len;
		while ((len = in.read(buffer)) != -1) out.write(buffer, 0, len);
	}
	
	public static void inputStreamToOutputStreamWriter(InputStream in,OutputStreamWriter out) throws IOException{
		char[] buffer = new char[1024*50];
		int len;
		InputStreamReader read=new InputStreamReader(in);
		while ((len = read.read(buffer)) != -1) out.write(buffer, 0, len);
		read.close();
	}
	
	
	public static String inputStreamToString(InputStream in) {
		Scanner s=new Scanner(in);
		StringBuilder out=new StringBuilder();
		boolean first=true;
		while(s.hasNextLine()){
			if(first){
				first=false;
			}else{
				out.append("\n");
			}
			out.append(s.nextLine());
		}
		s.close();
		return out.toString();
	}
}
