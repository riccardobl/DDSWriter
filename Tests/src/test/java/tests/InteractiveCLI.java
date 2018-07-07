package tests;
import ddswriter.cli.CLI109;
import ddswriter.delegates.lwjgl2_s3tc.S3tcCLI109Module;

public class InteractiveCLI{
	public static void main(String[] args) throws Exception {
		// S3tcCLI109Module.class.newInstance();
		CLI109.main(new String[]{"--debug","--interactive","--use-lwjgl"});
	// 	CLI109.main(new String[]{"--debug","--in","D:\\Assets\\AGEN\\thelab_map\\textures\\Ramp_BaseColorMap.png","--out","C:\\Users\\Win7rb\\AppData\\Local\\Temp\\test.dds","--use-opengl",
	// "--format","S3TC_DXT1","--gen-mipmaps"
	// });
	}
}
