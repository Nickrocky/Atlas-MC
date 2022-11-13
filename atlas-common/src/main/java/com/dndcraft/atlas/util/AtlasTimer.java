package com.dndcraft.atlas.util;

import com.dndcraft.atlas.Atlas;
import com.google.common.collect.Maps;
import org.apache.commons.lang.Validate;

import java.util.Map;

public class AtlasTimer {

    private Map<String, Long> timings = Maps.newConcurrentMap();

    public void startTiming(String why){
        long time = System.nanoTime();
        Validate.notNull(why);
        timings.put(why, time);
    }

    public void stopTiming(String why){
        long time = System.nanoTime();

        Validate.notNull(why);

        if(timings.containsKey(why)){
            long took = (time - timings.get(why))/1000;
            Atlas.get().getLogger().info("[DEBUG] operation '" + why + "' took " + took + "μs");
        }
    }

}
