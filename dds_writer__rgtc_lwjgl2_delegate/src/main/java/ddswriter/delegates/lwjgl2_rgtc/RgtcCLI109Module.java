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
package ddswriter.delegates.lwjgl2_rgtc;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ddswriter.DDSDelegate;
import ddswriter.delegates.lwjgl2.LWJGLCliModule;

/**
 * 
 * @author Riccardo Balbo
 */
public class RgtcCLI109Module  extends LWJGLCliModule{
	public RgtcCLI109Module(){

	}


	@Override
	public void load(Map<String,String> options, List<String> help, ArrayList<DDSDelegate> delegates) {
		if(!startGL()) return;
		int i=0;
		for(String s:help){
			if(s.startsWith("Input formats")){
				break;
			}else{
				i++;
			}
		}
		help.add(i,"   --use-lwjgl: Enable hardware compression with lwjgl\n");

		String hwc=options.get("use_lwjgl");
		if(hwc==null)options.get("use-lwjgl");
		if(hwc==null||hwc.equals("false")) return;
		i=0;
		for(String s:help){
			if(s.startsWith("Output formats")){
				break;
			}else{
				i++;
			}
		}
		help.add(i+1,"   RGTC1 (Compatible with ATI1, BC4, 3DC+), RGTC2 (Compatible with ATI2, BC5, 3DC)\n");
		delegates.add(new RGTC_LWJGL2CompressionDelegate());
	}

	@Override
	public void unload(Map<String,String> options, List<String> help, ArrayList<DDSDelegate> delegates) {
		endGL();

	}
}
