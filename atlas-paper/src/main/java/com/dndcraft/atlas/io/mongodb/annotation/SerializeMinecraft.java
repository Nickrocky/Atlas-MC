package com.dndcraft.atlas.io.mongodb.annotation;

import com.dndcraft.atlas.io.mongodb.MinecraftMongoObjectSerializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SerializeMinecraft {
    /**
     * ElementNameOverride simply overrides the name of the field when being stored by MongoDB, YML or another supported
     * medium.
     * */
    String elementNameOverride() default "";

    /**
     * Note the Serializer for this Annotation is the {@link MinecraftMongoObjectSerializer}
     */

}
