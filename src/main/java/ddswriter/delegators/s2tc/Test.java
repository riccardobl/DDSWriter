package ddswriter.delegators.s2tc;

import ddswriter.cli.Main;

public class Test{
	public static void main(String[] _args) throws Exception {
		String args[]="--compress --in /tmp/tobecompressed.jpg --out /tmp/compressed.dds".split(" ");
		Main.main(args);
	}
}
