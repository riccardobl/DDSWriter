package ddswriter.cli;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Scanner;

import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;

public class Viewer extends SimpleApplication{
	protected static File IMAGE;

	public static void main(String[] _args) {
		if(_args.length==0){
			System.out.print("Interactive console:~$ ");

			Scanner r=new Scanner(System.in);
			_args=r.nextLine().split(" ");
			r.close();
		}
		IMAGE=new File(_args[0].replace("/",File.separator));
		
		new Viewer().start();
		

	}

	@Override
	public void simpleInitApp() {
		try{
			viewPort.setBackgroundColor(ColorRGBA.Pink);
			flyCam.setEnabled(false);
			InputStream is=new BufferedInputStream(new FileInputStream(IMAGE));
			Texture tx=DDSLoaderI.load(is);
			is.close();
			Picture p=new Picture("Viewer");
			float w = getContext().getSettings().getWidth();
			float h = getContext().getSettings().getHeight();
			p.setWidth(w);
			p.setHeight(h);
			p.setTexture(assetManager,(Texture2D)tx,true);
			guiNode.attachChild(p);
		}catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException("Error",e);
		}
	}

}
