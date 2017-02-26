package ddswriter.delegators.s2tc;

import ddswriter.cli.CLI109;
import tests.TestUtils;

public class Test{
	public static void main(String[] _args) throws Exception {
		TestUtils.extractResources();
		String args[]=("--format S2TC_DXT1 --in "+TestUtils.tmpPath("texture2D.jpg")
		+" --out "+TestUtils.tmpPath("S2TC_DXT1.dds")).split(" ");
		CLI109.main(args);
	}
}
