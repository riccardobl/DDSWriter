/**
Copyright 2017 Riccardo Balbo

Permission is hereby granted, free of charge, to any person obtaining a copy of this software 
and associated documentation files (the "Software"), to deal in the Software without restriction, 
including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, 
and/or sell copies of the Software, and to permit persons to whom the Software is furnished 
to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE 
USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
package ddswriter;

import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;

import com.jme3.texture.Texture;

import ddswriter.format.DDS_BODY;
import ddswriter.format.DDS_HEADER;

/**
 * 
 * @author Riccardo Balbo
 */
public class DDSWriter{



	public static void write(Texture tx, Map<String,String> options,Collection<DDSDelegate> delegates, OutputStream output ) throws Exception {
		// TODO: Add support for DX10 HEADER
		String debugs=options.get("debug");
		if(debugs==null)debugs="false";
		boolean debug=debugs.equals("true");
		DDSOutputStream os=new DDSOutputStream(output);
		
		DDS_HEADER header=new DDS_HEADER();
		for(DDSDelegate delegate:delegates){
			delegate.header(tx, options, header);
		}
		if(debug){
			System.out.println(header.dump());
		}
		header.write(os);
		os.flush();
		
		DDS_BODY body=new DDS_BODY(os);
		for(DDSDelegate delegate:delegates){
			delegate.body(tx, options, header,body);
		}
		
		body.flush();
		os.close();
	}



}