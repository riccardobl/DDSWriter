package ddswriter.delegators.s2tc;

import ddswriter.cli.CLI109;

public class Test{
	public static void main(String[] _args) throws Exception {
		String args[]="--format S2TC_DXT1 --in /tmp/tobecompressed.jpg --out /tmp/compressed.dds".split(" ");
		CLI109.main(args);
	}
}
