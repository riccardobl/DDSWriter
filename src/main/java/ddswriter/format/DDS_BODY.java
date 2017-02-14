package ddswriter.format;

import java.io.OutputStream;

import ddswriter.DDSOutputStream;
/**
 * 
 * @author Riccardo Balbo
 *
 */
public class DDS_BODY extends DDSOutputStream{
	public DDS_BODY(OutputStream os){
		super(os);
	}

	public DDS_BODY(OutputStream os,String pixelformat){
		super(os,pixelformat);
	}
}
