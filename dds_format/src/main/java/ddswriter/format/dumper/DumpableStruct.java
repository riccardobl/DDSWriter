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
package ddswriter.format.dumper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * 
 * @author Riccardo Balbo
 *
 */
public class DumpableStruct{
	public String dump() {
		return dump((String[])null);
	}

	protected void dumpField(Field f,Collection<Field> flags,StringBuilder sb) throws IllegalArgumentException, IllegalAccessException{
		sb.append(f.getName()).append(" = ");
		Object v=f.get(this);
		
		if(v instanceof Integer){
			DumpableBitfield ann=f.getAnnotation(DumpableBitfield.class);
			if(ann!=null){		
				sb.append("0x").append(Integer.toHexString((int)v)).append("[ ");
				Collection<String> supported_flags=Arrays.asList(ann.possible_values());
				boolean firstf=true;
				for(Field flag:flags){
					if(!supported_flags.contains(flag.getName()))continue;
					int fv=(int)flag.get(null);
					int vv=(int)v;
					if((vv&fv)==fv){
						if(firstf)firstf=false;
						else sb.append(" | ");
						sb.append(flag.getName());
					}
				}
				sb.append(" ] ");
			}else{
				sb.append("0x").append(Integer.toHexString((int)v));
				
			}
		}else if(v.getClass().isArray() ){
			sb.append(Arrays.asList(v).toString());			
		}else{
			sb.append(v);
		}
	}
	
	public String dump(String... vars) {
		try{
			ArrayList<Field> fields=new ArrayList<Field>();
			if(vars==null||vars[0]==null||vars.length==0){
				Field r_f[]=getClass().getDeclaredFields();
				for(Field f:r_f){
					if(!Modifier.isStatic(f.getModifiers()))fields.add(f);
				}
			}else{
				for(String v:vars){
					fields.add(getClass().getDeclaredField(v));
				}
			}
			
			ArrayList<Field> flags=new ArrayList<Field>();
			Field[] r_f=getClass().getDeclaredFields();
			for(Field f:r_f){
				if(f.getType()==int.class&&Modifier.isStatic(f.getModifiers())){
					flags.add(f);
				}
			}
			
			StringBuilder sb=new StringBuilder();
			for(Field f:fields){
				dumpField(f,flags,sb);
				sb.append("\n");				
			}
			return sb.toString();
		}catch(Exception e){
			e.printStackTrace();
			return "";
		}
	}

}
