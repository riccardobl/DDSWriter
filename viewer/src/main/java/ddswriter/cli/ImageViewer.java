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

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Scanner;

import com.jme3.app.SimpleApplication;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.MagFilter;
import com.jme3.texture.Texture.MinFilter;
import com.jme3.texture.Texture2D;
import com.jme3.texture.image.ColorSpace;
import com.jme3.texture.plugins.AWTLoader;
import com.jme3.ui.Picture;

/**
 * 
 * @author Riccardo Balbo
 */

public class ImageViewer extends SimpleApplication{
	static File PATH;

	public static void main(String[] _args) throws Exception {
		if(_args.length==0){
			System.out.print("Interactive console:~$ ");
			Scanner r=new Scanner(System.in);
			_args=r.nextLine().split(" ");
			r.close();
		}
		PATH=new File(_args[0].replace("/",File.separator));
		new ImageViewer().start();

	}

	public Spatial loadViewer(Texture tx){
		if(tx instanceof Texture2D){

			int w=tx.getImage().getWidth();
			int h=tx.getImage().getHeight();
			// GraphicsDevice gd=GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
			if(h>settings.getHeight()){
				float rt=w/h;
				h=settings.getHeight();
				w=(int)(rt*h);
			}
			// settings.setWidth(w);
			// settings.setHeight(h);
	
			// setSettings(settings);
			// restart();

			tx.setMinFilter(MinFilter.Trilinear);
			tx.setMagFilter(MagFilter.Bilinear);

			Picture view=new Picture("Texture2D Viewer");
			view.setTexture(assetManager,(Texture2D) tx, false);
			view.setWidth(w);
			view.setHeight(h);
			view.setPosition((settings.getWidth()-w)/2,(settings.getHeight()-h)/2);

			if(tx.getImage().getColorSpace()==ColorSpace.sRGB){
				renderer.setMainFrameBufferSrgb(true);
			}
			return view;
		}

		return null;

	}

	public static Texture loadImage(File f) throws Exception {
		//		if(IMAGE!=null) IMAGE.getImage().dispose();
		Texture out=null;
		InputStream is=new BufferedInputStream(new FileInputStream(f));
		String ext=f.getAbsolutePath().substring(f.getAbsolutePath().lastIndexOf(".")+1);

		switch(ext){
			case "dds":
				out=DDSLoaderI.load(is,true);
				break;
			case "bmp":
			case "png":
			case "jpg":
			case "jpeg":
				AWTLoader awt_loader=new AWTLoader();
				out=new Texture2D(awt_loader.load(is,true));
				break;
			default:
				System.err.println("Format "+ext+" not supported!");

		}
		is.close();
		return out;
	}

	public ImageViewer() throws Exception{
		setShowSettings(false);
		AppSettings settings=new AppSettings(true);
		settings.setResizable(true);
		settings.setFrameRate(15);
		GraphicsDevice gd=GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		settings.setWidth(gd.getDisplayMode().getHeight()/2);
		settings.setHeight(gd.getDisplayMode().getHeight()/2);
		settings.setTitle("Image Viewer");
		setSettings(settings);
	


	}

	Texture LOADED_IMAGE;

	@Override
	public void simpleInitApp() {
		try{
			setPauseOnLostFocus(false);
			setDisplayStatView(false);
			setDisplayFps(false);
			flyCam.setDragToRotate(true);
			renderer.setLinearizeSrgbImages(true);

			LOADED_IMAGE=loadImage(PATH);
			
		}catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException("Error",e);
		}
	}

	@Override
	public void simpleUpdate(float tpf) {
		if(LOADED_IMAGE!=null){
			Spatial viewer=loadViewer(LOADED_IMAGE);
			if(viewer!=null){
				guiNode.detachAllChildren();
				guiNode.attachChild(viewer);
			}
		}
	}

}
