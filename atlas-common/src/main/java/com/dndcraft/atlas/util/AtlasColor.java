package com.dndcraft.atlas.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public enum AtlasColor{

    BLACK(0x1B1B1B), //Eerie Black - Crayola
    PUMPKIN(0xFF6D3A), //Smashed Pumpkin - Crayola
    BROWN(0x805533),
    NAVY_BLUE(0x3c37c9),
    GREEN(0x3AA655),
    CYAN(0x0a94a5), //Metallic Seaweed - Crayola (Light pitched up)
    LILAC(0xDB91EF),
    DANDELION(0xFED85D),
    DARK_RED(0xaa0000),
    PURPLE(0xaa00aa),
    GOLD(0xffaa00),
    GRAY(0xaaaaaa),
    DARK_GRAY(0x555555),
    BLUE(0x5555ff),
    LIME(0x55ff55),
    AQUA(0x55ffff),
    RED(0xff5555),
    YELLOW(0xffff55),
    ERROR_RED(0xFD0E35), //Scarlet - Crayola
    WARNING_YELLOW(0xFFDF46), //Gargoyle Cas - Crayola
    WARNING_TEXT_YELLOW(0xF2C649), //Maize - Crayola
    ERROR_TEXT_RED(0xED0A3F), //Red - Crayola
    MAGENTA(0xff55ff),
    RSV_COMMON_RARITY(0xAAF0D1),
    RSV_UNCOMMON_RARITY(0x66ff66),
    RSV_RARE_RARITY(0x50BFE6),
    RSV_EPIC_RARITY(0xD600AB),
    RSV_LEGENDARY_RARITY(0xFF9933),
    RSV_RELIC_RARITY(0xFF355E),
    BABY_BLUE(0x89cff0),
    PINK(0xff91af),
    WHITE(0xffffff);


    private final int colorHexCode;

    private final String[] exampleAdjectives = {"Colorful", "Excited", "Fascinated", "Kind", "Burly"};
    private final String[] exampleVerbs = {"squeaking", "marching", "singing", "loving", "flying"};
    private final String[] exampleNouns = {"fox", "rock", "bird", "penguin", "axolotl"};

    AtlasColor(int colorHexCode){
        this.colorHexCode = colorHexCode;
    }

    /**
     * Gets the current AtlasColor and converts it into a Kyori TextColor
     * @return TextColor of the given color hex code
     * */
    public TextColor toTextColor(){
        return TextColor.color(colorHexCode);
    }

    /**
     * Gets a Kyori Component with the AtlasColor applied to a "random" string of words to see a text example
     * @return Random string of words with the desired AtlasColor applied.
     * */
    public Component getExample(){
        String exampleText =
                exampleAdjectives[RandomUtil.roll(exampleAdjectives.length)] + " "
                + exampleVerbs[RandomUtil.roll(exampleVerbs.length)] + " "
                + exampleNouns[RandomUtil.roll(exampleNouns.length)];
        return Component.text(exampleText, toTextColor());
    }
}
