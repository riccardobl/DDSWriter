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
package ddswriter.cli;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.plugins.AWTLoader;
import com.jme3.texture.plugins.TGALoader;

import ddswriter.DDSDelegate;
import ddswriter.DDSWriter;
import ddswriter.cli.utils.ClassUtils;
import ddswriter.delegates.GenericDelegate;

/**
 * 
 * @author Riccardo Balbo
 */

public class CLI109{
	protected static final Logger LOGGER=LogManager.getLogger(CLI109.class);

	static List<String> genericHelp() {
		List<String> out=new ArrayList<String>();
		out.add("Usage: <CMD> --in path/file.png --out path/out.dds [options]\n");
		out.add("Options: \n");
		out.add("   --in <FILE>: Input file\n");
		out.add("   --out <FILE.dds>: Output file\n");
		out.add("   --format: Output format. Default: ARGB8 (uncompressed)\n");
		out.add("   --gen-mipmaps: Generate mipmaps\n");
		out.add("   --exit: Exit interactive console\n");
		out.add("   --debug: Show debug informations\n");
		out.add("Input formats:\n");
		out.add("   jpg,bmp,png,tga,dds\n");
		out.add("Output formats:\n");
		out.add("   ARGB8,RGB8,RGB565\n");
		return out;
	}
	
	

	static int run(String[] _args) throws Exception {
		Map<String,String> options=new HashMap<String,String>();
		List<String> help=genericHelp();
		ArrayList<DDSDelegate> delegates=new ArrayList<DDSDelegate>();
		delegates.add(new GenericDelegate());
		
		for(int i=0;i<_args.length;i++){
			String cmd=_args[i];		
			if(cmd.startsWith("--")){
				cmd=cmd.substring(2);
				String arg=i+1<_args.length?_args[i+1]:"--";
				if(!arg.startsWith("--")){
					i++;
				}else{
					arg="true";
				}
				options.put(cmd,arg);
			}
		}
		

		String in=options.get("in");
		String out=options.get("out");
		
		ArrayList<CLI109Module> modules=new ArrayList<CLI109Module> ();
		
		LinkedList<String> classes=ClassUtils.getAllClasses();
		for(String c:classes){
			if(c.endsWith("CLI109Module")&&c.charAt(c.indexOf("CLI109Module")-1)!='.'){
				LOGGER.info("Load {}",c);
				CLI109Module module=(CLI109Module)Class.forName(c).newInstance();
				modules.add(module);
			}
		}
		
		for(CLI109Module m:modules)m.load(options,help,delegates);
			
				
		if(out==null||in==null) {			
			System.out.println(toString(help));
			return 1;
		}


		Texture tx=null;

		String ext=in.substring(in.lastIndexOf(".")+1);

		switch(ext){
			case "jpg":
			case "jpeg":
			case "bmp":
			case "png":{
				AWTLoader loader=new AWTLoader();
				Image img=loader.load(ImageIO.read(new File(in)),false);
				tx=new Texture2D(img);
				break;
			}
			case "tga":{
				InputStream is=new BufferedInputStream(new FileInputStream(new File(in)));
				Image img=TGALoader.load(is,false);
				tx=new Texture2D(img);
				is.close();
				break;
			}
			case "dds":{
				InputStream is=new BufferedInputStream(new FileInputStream(new File(in)));
				tx=DDSLoaderI.load(is);
				is.close();
			}
		}

		if(tx==null){
			System.err.println("Input format not supported: "+ext);
			return 1;
		}

		OutputStream fo=new BufferedOutputStream(new FileOutputStream(new File(out)));
		DDSWriter.write(tx,options,delegates,fo);
		fo.close();
		for(CLI109Module m:modules)m.unload(options,help,delegates);

		return 0;
	}

	private static String toString(List<String> c) {
		String out="";
		for(String s:c){
			out+=s; 
		}
		return out;
	}



	public static void main(String[] _args) throws Exception {
		boolean interactive=false;
		for(int i=0;i<_args.length;i++){
			if(_args[i].equals("--interactive")){
				interactive=true;
			}
		}
		if(interactive){
			int i=0;
			Scanner s=new Scanner(System.in);
			do{
				System.out.print("Interactive console:~$ ");
				String i_args[]=s.nextLine().split(" ");
				String f_args[]=new String[i_args.length+_args.length];
				System.arraycopy(_args,0,f_args,0,_args.length);
				System.arraycopy(i_args,0,f_args,_args.length,i_args.length);				
				i=run(f_args);
			}while(i==0);
			s.close();
		}else{
			run(_args);
		}
	}
}
