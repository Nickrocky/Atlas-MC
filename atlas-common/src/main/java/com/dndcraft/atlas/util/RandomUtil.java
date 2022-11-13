package com.dndcraft.atlas.util;

import java.util.concurrent.ThreadLocalRandom;

public class RandomUtil {

    /**
     * This convenience method rolls a die and returns a boolean if the roll is higher than the required roll
     * @param maxNumber The highest number possible when rolling
     * @param requiredRoll the number required to pass the check
     * @param numberOfDiceToRoll the number of dice rolled to check against the requiredRoll
     * @return Whether or not the player successfully rolled higher than the requiredRoll
     * */
    public static boolean roll(int maxNumber, int requiredRoll, int numberOfDiceToRoll){
        int totalRoll = 0;
        for(int i = 0; i < numberOfDiceToRoll; i++){
            totalRoll += roll(maxNumber);
        }
        if(totalRoll >= requiredRoll) return true;
        return false;
    }

    /**
     * This method purely focuses on the random number aspect, rolling a number between 1 and the max number
     * @param maxNumber The highest number possible when rolling
     * @return The number generated from rolling
     * */
    public static int roll(int maxNumber){
        return ThreadLocalRandom.current().nextInt(1, maxNumber+1);
    }

}
