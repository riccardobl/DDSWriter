package ddswriter.format;

import java.io.IOException;

import ddswriter.DDSOutputStream;
import ddswriter.format.dumper.DumpableStruct;

/**
 * 
 * @author Riccardo Balbo
 *
 */
public abstract class WritableStruct extends DumpableStruct{
	public abstract void write(DDSOutputStream os) throws IOException;
}
