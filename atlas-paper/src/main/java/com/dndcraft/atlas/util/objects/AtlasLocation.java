package com.dndcraft.atlas.util.objects;

import com.dndcraft.atlas.util.exception.AtlasLocationConversionException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.NumberConversions;

@Getter
@RequiredArgsConstructor
public class AtlasLocation {

    private final double x,y,z;
    private final String world;

    public AtlasLocation(double x, double y, double z){
        this(x, y, z, "Null-World");
    }

    public AtlasLocation(double x, double y, double z, World world){
        this(x, y, z, world.getName());
    }

    public AtlasLocation(Location location){
        this(location.getX(), location.getY(), location.getZ(), location.getWorld());
    }

    public double distanceSquared(AtlasLocation o){
        return NumberConversions.square(this.x - o.x) + NumberConversions.square(this.y - o.y) + NumberConversions.square(this.z - o.z);
    }

    public Location toLocation() throws AtlasLocationConversionException {
        if(world.equals("Null-World"))
            throw new AtlasLocationConversionException();
        return new Location(Bukkit.getWorld(world), x, y, z);
    }

}
