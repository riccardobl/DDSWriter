package tests;


import ddswriter.cli.CLI109;
import  static tests.TestUtils.*;

public class TestS3TC_and_ATI_lwjgl{
	public static void main(String[] _args) throws Exception {
		extractResources();
		String args[]=("--debug --use-opengl --format S3TC_DXT1 "
				+ " --in "+tmpPath("texture2D.jpg")
				+ " --out "+tmpPath("texture2D_S3TC_DXT1.dds")).split(" ");
		CLI109.main(args);
		args=("--debug --use-opengl --format S3TC_DXT3 "
				+ " --in "+tmpPath("texture2DwithAlpha.png")
				+ " --out "+tmpPath("texture2DwithAlpha_S3TC_DXT3.dds")).split(" ");
		CLI109.main(args);
		args=("--debug --use-opengl --format S3TC_DXT5 "
				+ " --in "+tmpPath("texture2DwithAlpha.png")
				+ " --out "+tmpPath("texture2DwithAlpha_S3TC_DXT5.dds")).split(" ");
		CLI109.main(args);
		args=("--debug --use-opengl --format ATI_3DC "
				+ " --in "+tmpPath("texture2D.jpg")
				+ " --out "+tmpPath("texture2D_ATI2.dds")).split(" ");
		CLI109.main(args);	
		args=("--debug --use-opengl --format ATI_3DC+ "
				+ " --in "+tmpPath("texture2D.jpg")
				+ " --out "+tmpPath("texture2D_ATI1.dds")).split(" ");
		CLI109.main(args);	
		args=("--debug --use-opengl --gen-mipmaps --format S3TC_DXT3 "
				+ " --in "+tmpPath("texture2D.dds")
				+ " --out "+tmpPath("texture2D_S3TC_DXT3_MIPMAPS.dds")).split(" ");
		CLI109.main(args);
	}
}
