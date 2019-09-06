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
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.jme3.texture.Texture.Type;
import com.jme3.texture.plugins.AWTLoader;
import com.jme3.texture.plugins.TGALoader;
import com.jme3.util.BufferUtils;

import ddswriter.DDSDelegate;
import ddswriter.DDSWriter;
import ddswriter.cli.utils.ClassUtils;
import ddswriter.delegates.GenericDelegate;
import jme3tools.converters.ImageToAwt;

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
		out.add("   --inlist <FILE1,FILE2,FILE3>: Csv list of multiple input files\n");
		out.add("   --format: Output format. Default: ARGB8 (uncompressed). When --inlist is used, this params becomes a csv list.\n");
		out.add("   --gen-mipmaps: Generate mipmaps\n");
		out.add("   --srgb: Treat input and output as srgb\n");
		out.add("   --srgblist <true,false,false>: Treat input and output as srgb\n");
		out.add("   --interactive: Open interactive console\n");
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
		boolean in_as_list=false;
		boolean out_as_list=false;
		if(in==null){
			in=options.get("inlist");
			if(in!=null)in_as_list=true;
		}
		String out=options.get("out");
		if(out==null){
			out=options.get("outlist");
			if(out!=null)out_as_list=true;
		}
		String _multires=options.get("multires");
		List<String[]> multires=null;
		if(_multires!=null){
			multires=new ArrayList<String[]>();
			String vls[]=_multires.split(",");
			for(String v:vls){
				String x[]=v.split(":");
				String prefix=x.length==1?"":x[0];
				String size=x[x.length-1];
				multires.add(new String[]{prefix,size});
			}
		}
		
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


		List<String> ins=new ArrayList<String>();
		if(in_as_list){
			ins.addAll(Arrays.asList(in.split(",")));
		}else{
			ins.add(in);
		}

		ArrayList<String> outs=new ArrayList<String>();
		if(out_as_list){
			outs.addAll(Arrays.asList(out.split(",")));

		}else{
			outs.add(out);
		}
	
		if(in_as_list&&out_as_list){
			if(ins.size()!=outs.size()){
				System.err.println("--inlist and --outlist must have the same number of values");
			}
		}

		String formats[]=options.containsKey("format")?options.get("format").split(","):null;
		String srgbs[]=options.containsKey("srgblist")?options.get("srgblist").split(","):null;
		int i=0;
		for(String xin:ins){
			String xout=outs.get(out_as_list?i:0);
			String ext=xin.substring(xin.lastIndexOf(".")+1);
			if(formats!=null)	options.put("format",formats[i>=formats.length?formats.length-1:i]);
			if(srgbs!=null){
				if(srgbs[i>=formats.length?formats.length-1:i].toLowerCase().equals("true")){
					options.put("srgb","true");
				}else{
					options.remove("srgb");
				}				
			}
			switch(ext){
				case "jpg":
				case "jpeg":
				case "bmp":
				case "png":{
					AWTLoader loader=new AWTLoader();
					Image img=loader.load(ImageIO.read(new File(xin)),false);
					tx=new Texture2D(img);
					break;
				}
				case "tga":{
					InputStream is=new BufferedInputStream(new FileInputStream(new File(xin)));
					Image img=TGALoader.load(is,false);
					tx=new Texture2D(img);
					is.close();
					break;
				}
				case "dds":{
					InputStream is=new BufferedInputStream(new FileInputStream(new File(xin)));
					tx=DDSLoaderI.load(is);
					is.close();
				}
			}

			if(tx==null){
				System.err.println("Input format not supported: "+xin);
				if(!in_as_list) return 1;
				else continue;
			}

			File outf=new File(xout);
			if(outf.isDirectory()){
				File inf=new File(xin);
				outf=new File(outf.getAbsolutePath(),inf.getName()+".dds");
			}
			OutputStream fo=new BufferedOutputStream(new FileOutputStream(outf));
			System.out.println("Convert  "+xin+" to "+xout);
			DDSWriter.write(tx,options,delegates,fo);
			fo.close();
			
			i++;
		}
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
