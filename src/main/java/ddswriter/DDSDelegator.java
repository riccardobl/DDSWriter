package ddswriter;

import java.util.Map;

import com.jme3.texture.Texture;

import ddswriter.format.DDS_BODY;
import ddswriter.format.DDS_HEADER;

/**
 * 
 * @author Riccardo Balbo
 */
public interface DDSDelegator{
	public void body(Texture tx,Map<String,String>  options, DDS_HEADER header,DDS_BODY body)throws Exception ; 
	public void header(Texture tx,Map<String,String>  options,DDS_HEADER header)throws Exception ; 
}
