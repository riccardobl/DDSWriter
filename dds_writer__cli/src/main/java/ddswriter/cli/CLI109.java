package ddswriter.cli;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
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

import ddswriter.DDSDelegator;
import ddswriter.DDSWriter;
import ddswriter.cli.utils.ClassUtils;
import ddswriter.delegators.GenericDelegator;

/**
 * 
 * @author Riccardo Balbo
 */
//--in /tmp/test.png --out /tmp/test_output.dds --compress

public class CLI109{
	protected static final Logger LOGGER=LogManager.getLogger(CLI109.class);

	static List<String> genericHelp() {
		List<String> out=new ArrayList<String>();
		out.add("Usage: <CMD> --in path/file.png --out path/out.dds [options]\n");
		out.add("Options: \n");
		out.add("   --in <FILE>: Input file\n");
		out.add("   --out <FILE.dds>: Output file\n");
		out.add("   --format: Output format. Default: ARGB8 (uncompressed)\n");
		out.add("   --mipmaps: Generate mipmaps [Works only with 2d textures]\n");
		out.add("   --exit: Exit interactive console\n");
		out.add("   --debug: Show debug informations\n");
		out.add("Input formats:\n");
		out.add("   jpg,bmp,png,tga,dds\n");
		out.add("Output formats:\n");
		out.add("   ARGB8,RGB8,RGB565\n");
		return out;
	}
	
	

	static void run(String[] _args) throws Exception {
		Map<String,String> options=new HashMap<String,String>();
		options.put("debug","true");
		List<String> help=genericHelp();
		ArrayList<DDSDelegator> delegators=new ArrayList<DDSDelegator>();
		delegators.add(new GenericDelegator());
		
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
		
		for(CLI109Module m:modules)m.load(options,help,delegators);
			
				
		if(out==null||in==null) {			
			System.out.println(toString(help));
			System.exit(1);
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
			System.exit(1);
		}

		OutputStream fo=new BufferedOutputStream(new FileOutputStream(new File(out)));
		DDSWriter.write(tx,options,delegators,fo);
		fo.close();
		for(CLI109Module m:modules)m.unload(options,help,delegators);

	}

	private static String toString(List<String> c) {
		String out="";
		for(String s:c){
			out+=s; 
		}
		return out;
	}



	public static void main(String[] _args) throws Exception {
		if(_args.length==0){
			Scanner s=new Scanner(System.in);
			while(true){
				System.out.print("Interactive console:~$ ");
				_args=s.nextLine().split(" ");
				run(_args);
			}
		}else{
			run(_args);
		}
	}
}
