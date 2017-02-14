package ddswriter.cli;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;

public class Viewer extends SimpleApplication{
	private static File IMAGEF;
	private static Texture IMAGE;

	private static long LAST_MODIFIED;
	private static Picture VIEWER;

	public static void main(String[] _args) throws IOException {
		if(_args.length==0){
			System.out.print("Interactive console:~$ ");
			Scanner r=new Scanner(System.in);
			_args=r.nextLine().split(" ");
			r.close();
		}
		IMAGEF=new File(_args[0].replace("/",File.separator));
		new Viewer().start();

	}

	public Viewer() throws IOException{
		setShowSettings(false);
		AppSettings settings=new AppSettings(true);
		settings.setResizable(true);
		settings.setFrameRate(10);
		settings.setWidth(640);
		settings.setHeight(480);
		settings.setTitle("DDS Viewer");

		reloadImage();

		int w=IMAGE.getImage().getWidth();
		int h=IMAGE.getImage().getHeight();
		GraphicsDevice gd=GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

		if(w<gd.getDisplayMode().getWidth()/2&&h<gd.getDisplayMode().getHeight()){
			settings.setWidth(w);
			settings.setHeight(h);
		}

		setSettings(settings);

	}

	private static void reloadImage() throws IOException {
		if(IMAGE!=null) IMAGE.getImage().dispose();
		InputStream is=new BufferedInputStream(new FileInputStream(IMAGEF));
		IMAGE=DDSLoaderI.load(is,true);
		is.close();
		LAST_MODIFIED=IMAGEF.lastModified();

		if(VIEWER!=null){
			VIEWER.removeFromParent();
			VIEWER=null;
		}

	}

	@Override
	public void simpleInitApp() {
		try{
			setDisplayStatView(false);
			setDisplayFps(false);
			flyCam.setEnabled(false);

		}catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException("Error",e);
		}
	}

	@Override
	public void simpleUpdate(float tpf) {

		if(LAST_MODIFIED!=IMAGEF.lastModified()){
			try{
				reloadImage();
			}catch(IOException e){
				e.printStackTrace();
			}
		}

		if(VIEWER==null){
			VIEWER=new Picture("Viewer");
			VIEWER.setTexture(assetManager,(Texture2D)IMAGE,true);
			guiNode.attachChild(VIEWER);
		}

		int sw=getContext().getSettings().getWidth();
		int sh=getContext().getSettings().getHeight();

		int w=IMAGE.getImage().getWidth();
		int h=IMAGE.getImage().getHeight();

		float ratio=Math.min(sw/w,sh/h);

		w*=ratio;
		h*=ratio;

		VIEWER.setWidth(w);
		VIEWER.setHeight(h);

		int padding_top=(int)((sh-h)/2);
		int padding_left=(int)((sw-w)/2);
		VIEWER.setPosition(padding_left,padding_top);

	}

}
