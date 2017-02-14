package ddswriter.format.dumper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
/**
 * 
 * @author Riccardo Balbo
 * @description Used only for debug
 *
 */
public @interface DumpableBitfield {
	public String[] possible_values() default {};
}
