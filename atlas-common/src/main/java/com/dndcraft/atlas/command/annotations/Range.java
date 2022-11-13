package com.dndcraft.atlas.command.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(PARAMETER)
public @interface Range {
	double min() default Integer.MIN_VALUE;
	double max() default Integer.MAX_VALUE;
}
