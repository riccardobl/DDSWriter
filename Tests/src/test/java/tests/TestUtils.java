package tests;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

import ddswriter.cli.utils.FileUtils;
import ddswriter.cli.utils.StreamUtils;

public class TestUtils{
	private static String TMP;
	static{
//		try{
			TMP=System.getProperty("java.io.tmpdir")+File.separator+"ddsw_tests";
			File tmpf=new File(TMP);
			if(!tmpf.exists()){
				System.out.println("Create tmp dir "+TMP);
				tmpf.mkdir();
			}
//			Runtime.getRuntime().addShutdownHook(new Thread(){
//				@Override
//				public void run() {
//					try{
//						FileUtils.delete(TMP);
//					}catch(Exception e){
//						e.printStackTrace();
//					}
//				}
//			});
			
//		}catch(IOException e){
//			e.printStackTrace();
//		}
	}

	
	
	public static void extractResources() throws IOException{
		String resources_list[]={
				"red.jpg",
				"redDXT1.dds",
				"test.png",
				"texture2D_MipMaps.dds",
				"texture2D.dds",
				"texture2D.jpg",
				"texture2D_2.png",

				"texture2DwithAlpha.png",
				"textureCubeMap_MipMaps.dds",
				"textureCubeMap.dds"						
		};
		for(String r:resources_list){
			File outf=new File(tmpPath(r));
			if(outf.exists()){
				continue;
			}
			System.out.println("Extract "+r+" in "+outf.getAbsolutePath());
			
			InputStream in=new BufferedInputStream(TestUtils.class.getResourceAsStream(r));
			OutputStream out=new BufferedOutputStream(new FileOutputStream(outf));
			StreamUtils.inputStreamToOutputStream(in,out);
			in.close();
			out.close();
		}
	}
	
	public static String tmpPath(String file){ 
		String tmp=TMP+File.separator+"tests";
		File tmp_f=new File(tmp);
		if(!tmp_f.exists()){
			tmp_f.mkdir();
		}
		return tmp+File.separator+file.replace("/",File.separator);		
	}
	
	public static void main(String[] args) throws IOException {
		extractResources();
	}
}
