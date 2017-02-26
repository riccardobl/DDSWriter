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

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.LinkedList;
/**
 * 
 * @author Riccardo Balbo
 */

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