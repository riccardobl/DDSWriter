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
