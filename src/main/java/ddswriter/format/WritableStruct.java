package ddswriter.format;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

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
