package com.dndcraft.atlas.command.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(METHOD)
public @interface Cmd {
	String value();
	String permission() default "";
	String alias() default "";
	String[] aliases() default {};
	boolean flags() default true;
}
