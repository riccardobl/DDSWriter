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
