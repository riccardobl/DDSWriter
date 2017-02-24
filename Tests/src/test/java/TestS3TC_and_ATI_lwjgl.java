

import ddswriter.cli.CLI109;

public class TestS3TC_and_ATI_lwjgl{
	public static void main(String[] _args) throws Exception {
		String args[]="--use_lwjgl --format S3TC_DXT1 --in /tmp/tobecompressed.jpg --out /tmp/compressed_dxt1.dds".split(" ");
		CLI109.main(args);
		args="--use_lwjgl --format S3TC_DXT3 --in /tmp/tobecompressed.jpg --out /tmp/compressed_dxt3.dds".split(" ");
		CLI109.main(args);
		args="--use_lwjgl --format S3TC_DXT5 --in /tmp/tobecompressed.jpg --out /tmp/compressed_dxt5.dds".split(" ");
		CLI109.main(args);
		args="--use_lwjgl --format ATI_3DC --in /tmp/tobecompressed.jpg --out /tmp/compressed_3dc.dds".split(" ");
		CLI109.main(args);
	
		args="--use_lwjgl --gen-mipmaps --format S3TC_DXT3 --in /tmp/tobecompressed.dds --out /tmp/compressed_dxt3_dds.dds".split(" ");
		CLI109.main(args);
	}
}
