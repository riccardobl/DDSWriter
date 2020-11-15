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
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 * 
 * @author Riccardo Balbo
 */
public class FileUtils{
	protected static final Logger LOGGER=LogManager.getLogger(FileUtils.class);

	public static final String _SEP=File.separator;
			;
	public static String _TEMP_DIR;
	static{
		try{
			_TEMP_DIR=Files.createTempDirectory("ddsio").toString()+_SEP;
			Runtime.getRuntime().addShutdownHook(new Thread(){
				@Override
				public void run() {
					try{
						FileUtils.delete(_TEMP_DIR);
					}catch(Exception e){
						LOGGER.fatal(e);
					}
				}
			});
			
		}catch(IOException e){
			LOGGER.fatal(e);
		}
	}


	public static void delete(String f) {
		delete(new File(f));
	}

	public static void delete(File fl) {
		if(fl.isDirectory()&&fl.list().length!=0){
			for(File file:fl.listFiles())delete(file);
			delete(fl);
		}else{
			try{
				fl.delete();
				LOGGER.debug("Deleting {} ...",fl.getPath());
			}catch(Exception e){
				LOGGER.debug(e);
			}
		}
	}

	public static String[] listDirectoryInString(String f, String ext) {
		ArrayList<String> out=new ArrayList<String>();
		File fl=new File(f);
		for(File file:fl.listFiles())
			if(ext.equals("*")||file.getPath().endsWith(ext)) out.add(file.getAbsolutePath());
		String[] o=new String[out.size()];
		for(int i=0;i<out.size();i++)
			o[i]=out.get(i);
		return o;
	}


	public static ArrayList<File> listDirectory(String dir, String ext) {
		return listDirectory(dir,ext,false);
	}

	public static ArrayList<File> listDirectory(String dir, String ext, boolean recursive) {
		return listDirectory(new File(dir),ext,recursive);
	}

	public static ArrayList<File> listDirectory(String dir) {
		return listDirectory(dir,false);
	}

	public static ArrayList<File> listDirectory(String dir, boolean recursive) {
		return listDirectory(dir,"*",recursive);
	}

	public static ArrayList<File> listDirectory(File dir, boolean recursive) {
		return listDirectory(dir,"*",recursive);
	}

	public static ArrayList<File> listDirectory(File fl, String ext) {
		return listDirectory(fl,ext,false);
	}

	public static ArrayList<File> listDirectory(File fl, String ext, boolean recursive) {
		return listDirectory(fl,"*",ext,recursive);
	}

	public static ArrayList<File> listDirectory(File fl, String pref, String ext) {
		return listDirectory(fl,pref,ext,false);
	}

	public static ArrayList<File> listDirectory(File fl, String pref, String ext, boolean recursive) {
		ArrayList<File> out=new ArrayList<File>();
		listDirectory(fl,pref,ext,out,recursive);
		return out;
	}

	public static void listDirectory(File fl, String pref, String ext, ArrayList<File> out, boolean recursive) {
		File files[]=fl.listFiles();
		if(files==null)return;
		for(File file:files)
			if(recursive&&file.isDirectory()) listDirectory(file,pref,ext,out,recursive);
			else if((ext.equals("*")||file.getPath().endsWith(ext))&&(pref.equals("*")||file.getName().startsWith(pref))) out.add(file);
	}

	
	public static interface FileConsumer {
		public void accept(File f);
	}
	public static void forEachFile(File fl, FileConsumer c) {
		File[] l=fl.listFiles();
		if(l!=null) for(File file:l){
			if(file.isDirectory()){
				forEachFile(file,c);
			}
			c.accept(file);
		}
		c.accept(fl);

	}

	public static LinkedList<String> listZipElements(String archive) throws Exception {
		return listZipElements(archive,false,false);
	}

	public static LinkedList<String> listZipElements(String archive, boolean recursive) throws Exception {
		return listZipElements(new File(archive),recursive,true);
	}

	public static LinkedList<String> listZipElements(String archive, boolean recursive, boolean subzipasdir) throws Exception {
		// FileInputStream fi=new FileInputStream(new File(archive));
		// LinkedList<String> ss= listZipElements(fi,recursive);
		// fi.close();
		return listZipElements(new File(archive),recursive,subzipasdir);
	}

	public static LinkedList<String> listZipElements(File archive, boolean recursive) throws Exception {
		return listZipElements(archive,recursive,true);
	}

	public static LinkedList<String> listZipElements(File file, boolean recursive, final boolean subzipasdir) throws Exception {
		final LinkedList<String> o=new LinkedList<String>();
		// ZipInputStream zipfile = new ZipInputStream(file);
		// ZipEntry e;
		// while ((e=zipfile.getNextEntry())!=null) {
		ZipFile zipfile=new ZipFile(file);
		Enumeration<? extends ZipEntry> en=zipfile.entries();

		while(en.hasMoreElements()){
			ZipEntry e=en.nextElement();

			final String f=e.getName();

			if(f.endsWith(".zip")&&recursive){
				File tempfile=new File(FileUtils._TEMP_DIR+"FileUtils_zipListTemp"+(Math.random()*Float.MAX_VALUE)+".tmp");
				FileOutputStream bout=new FileOutputStream(tempfile);
				try{
					StreamUtils.inputStreamToOutputStream(zipfile.getInputStream(e),bout);
				}catch(Exception exc){}
				bout.close();
				LinkedList<String> o2=listZipElements(tempfile,true);
				FileUtils.delete(tempfile);
				for(String o2s:o2){
					if(subzipasdir) o.add(f+"/"+o2s);
					else o.add(o2s);
				}
			}else o.add(f);
		}
		zipfile.close();
		return o;
	}

	
}
