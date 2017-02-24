package ddswriter.cli.utils;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.LinkedList;


public class ClassUtils{
	

	public static LinkedList<String> getAllClasses() {
		return getAllClasses("*",false);
	}

	public static LinkedList<String> getAllClasses(String path, boolean asFile) {
		LinkedList<String> out=new LinkedList<String>();
		if(path.equals("*")){
			ClassLoader cl=ClassLoader.getSystemClassLoader();
			URL[] urls=((URLClassLoader)cl).getURLs();
			for(URL u:urls){
				out.addAll(getAllClasses(u.getFile(),asFile));
			}
		}else{
			LinkedList<String> ou=new LinkedList<String>();
			if(path.endsWith(".zip")||path.endsWith(".jar")){
				try{
					ou=FileUtils.listZipElements(path,true,false);
				}catch(Exception e){}
			}else{
				ArrayList<File> ouu=FileUtils.listDirectory(path,true);
				for(File f:ouu){
					if(!f.getAbsolutePath().contains(path)) continue;
					String relpath=f.getAbsolutePath().split(path)[1];
					ou.add(relpath);
				}
			}
			ou.forEach(z -> {

				if(z.endsWith(".class")){
					if(asFile) out.add(z);
					else{
						String s=z.replace("\\",".").replace("/",".").replace(".class","");
						boolean add=true;
						if(s.contains("$")){
							if(s.split("\\$")[1].length()>1){
								s=s.replace("$",".");
							}else add=false;
						}
						if(add) out.add(s);
					}
				}
			});
		}

		return out;
	}
}