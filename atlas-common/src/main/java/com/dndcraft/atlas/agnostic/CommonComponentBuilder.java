package com.dndcraft.atlas.agnostic;

import com.dndcraft.atlas.Atlas;

/**
 * "Why the hell does this class exist?"
 * Well because {@link Atlas} is an interface and doesnt actually hold an instance of AbstractComponentBuilder
 * Further {@link AbstractComponentBuilder} is Abstract and cant be instantiated
 * So here is a class that does the bare minimum and inherits those super useful functions in
 * {@link AbstractComponentBuilder}
 * @author nickrocky
 * */
public class CommonComponentBuilder extends AbstractComponentBuilder<CommonComponentBuilder> {

    public CommonComponentBuilder() {
        super("");
    }

    public CommonComponentBuilder(String initial) {
        super(initial);
    }

    /**
     * Gets the current builder for this component
     *
     * @return the current Builder this component
     */
    @Override
    public CommonComponentBuilder getThis() {
        return this;
    }
}
