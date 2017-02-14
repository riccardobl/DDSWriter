package ddswriter.cli;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

import javax.imageio.ImageIO;

import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.plugins.AWTLoader;
import com.jme3.texture.plugins.TGALoader;

import ddswriter.DDSWriter;

/**
 * 
 * @author Riccardo Balbo
 */

public class Main{
	static void help() {
		System.out.println("Usage: <CMD> --in path/file.png --out path/out.dds [--compress] [--exit]");
		System.out.println("   --out <FILE.dds>: Output file");
		System.out.println("   --in <FILE>: Input file");
		System.out.println("   --compress: Enable s2tc compression");
		System.out.println("   --mipmaps: Generate mipmaps [Works only with 2d textures]");
		System.out.println("   --exit: Exit interactive console");
	}

	static void run(String[] _args) throws Exception {
		DDSWriter.Options options=new DDSWriter.Options();

		String in=null;
		String out=null;
		for(int i=0;i<_args.length;i++){
			String cmd=_args[i];
			switch(cmd){
				case "--help":{
					help();
					return;
				}
				case "--in":{
					in=_args[++i].replace("/",File.separator);
					break;
				}
				case "--out":{
					out=_args[++i].replace("/",File.separator);
					break;
				}
				case "--compress":{
					options.compress=true;
					break;
				}
				case "--mipmaps":{
					options.gen_mipmaps=true;
					break;
				}
				case "--close":{
					System.exit(0);
					break;
				}
			}
		}
		if(out==null||in==null) {
			help();
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
				is.close();
				tx=new Texture2D(img);
				break;
			}
			case "dds":{
				InputStream is=new BufferedInputStream(new FileInputStream(new File(in)));
				tx=DDSLoaderI.load(is);
				is.close();
			}
		}

		if(tx==null){
			System.err.println("Format not supported: "+ext);
			System.exit(1);
		}

		OutputStream fo=new BufferedOutputStream(new FileOutputStream(new File(out)));
		DDSWriter.write(tx,fo,options);
		fo.close();
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
