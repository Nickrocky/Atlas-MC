package com.dndcraft.atlas.io.mongodb;

import com.dndcraft.atlas.AtlasPaper;
import org.bson.Document;
import org.bukkit.Location;

import java.time.LocalDateTime;

public class MinecraftAdapter {
    public static LocalDateTime localDateTimeFromDocument(Document document){
        return LocalDateTime.of(document.getInteger("Year"), document.getInteger("Month"), document.getInteger("Day"), document.getInteger("Hour"), document.getInteger("Minute"), document.getInteger("Second"));
    }

    public static Location fromLocationDocument(Document document){
        double x,y,z;
        String world;
        x = document.getDouble("x");
        y = document.getDouble("y");
        z = document.getDouble("z");
        world = document.getString("world");
        return new Location(AtlasPaper.get().getServer().getWorld(world), x,y,z);
    }

    public static Document toDocument(LocalDateTime localDateTime){
        Document document = new Document();
        document
                .append("Year", localDateTime.getYear())
                .append("Month", localDateTime.getMonthValue())
                .append("Day", localDateTime.getDayOfMonth())
                .append("Hour", localDateTime.getHour())
                .append("Minute", localDateTime.getMinute())
                .append("Second", localDateTime.getSecond());
        return document;
    }

    public static Document toDocument(Location location){
        Document document = new Document();
        document
                .append("x", location.getX())
                .append("y", location.getY())
                .append("z", location.getZ())
                .append("world", location.getWorld().getName());
        return document;
    }
}
