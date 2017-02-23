package ddswriter.cli.utils;

import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;


@SuppressWarnings("unchecked")
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