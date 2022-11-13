package com.dndcraft.atlas.util;

import com.dndcraft.atlas.agnostic.CommonComponentBuilder;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;

public class TextUtil {

    public static List<String> descriptionToListString(String string){
        List<String> loreStrings = new ArrayList<>();
        StringBuilder thisString = new StringBuilder();
        String[] strings = string.split(" ");
        for (String word : strings) {
            int currentLength = 0;
            if (thisString.toString().length() > 0) {
                currentLength = thisString.toString().length() + 1;
            }
            String[] result = processWord(currentLength, word);
            while (result.length > 1) {
                thisString.append(result[0]);
                loreStrings.add(thisString.toString());
                thisString = new StringBuilder();
                result = processWord(0, result[1]);
            }
            thisString.append(result[0]);
        }
        if (thisString.length() > 0){
            loreStrings.add(thisString.toString());
        }
        return loreStrings;
    }

    public static List<Component> descriptionToList(String string) {
        List<Component> lore = new ArrayList<>();
        StringBuilder thisString = new StringBuilder();
        String[] strings = string.split(" ");
        for (String word : strings) {
            int currentLength = 0;
            if (thisString.toString().length() > 0) {
                currentLength = thisString.toString().length() + 1;
            }
            String[] result = processWord(currentLength, word);
            while (result.length > 1) {
                thisString.append(result[0]);
                var comp = new CommonComponentBuilder().append(thisString.toString(), AtlasColor.GRAY).build();
                lore.add(comp);

                thisString = new StringBuilder();
                result = processWord(0, result[1]);
            }
            thisString.append(result[0]);
        }
        if (thisString.length() > 0){
            var comp = new CommonComponentBuilder().append(thisString.toString(), AtlasColor.GRAY).build();
            lore.add(comp);
        }
        return lore;
    }
    private static String[] processWord(int currentLength, String word) {
        String first = word;
        String second = null;
        int width = 25;

        if (first.length() > width) {
            int size = width - currentLength - 1;

            if (size > 0) {
                StringBuilder hyphenated = new StringBuilder();
                StringBuilder leftovers = new StringBuilder();
                for (int i = 0; i < size; i++) {
                    hyphenated.append(first.toCharArray()[i]);
                }
                for (int i = size; i < first.length(); i++) {
                    leftovers.append(first.toCharArray()[i]);
                }
                hyphenated.append("-");

                first = hyphenated.toString();
                second = leftovers.toString();
            } else {
                second = first;
                first = "";
            }
        } else if (currentLength + first.length() > width) {
            second = first;
            first = "";
        }

        if (currentLength > 0 && first.length() > 0) {
            first = " " + first;
        }

        if (second == null) {
            return new String[] { first };
        } else {
            return new String[] { first, second };
        }
    }
}
