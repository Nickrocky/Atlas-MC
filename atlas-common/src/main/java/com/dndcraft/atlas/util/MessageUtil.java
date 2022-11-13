package com.dndcraft.atlas.util;

import com.dndcraft.atlas.agnostic.AbstractComponentBuilder;
import com.dndcraft.atlas.agnostic.CommonComponentBuilder;
import net.kyori.adventure.text.Component;


import static net.kyori.adventure.text.format.TextDecoration.ITALIC;

public final class MessageUtil {
	private MessageUtil() {}

	/**
	 * YOU CANNOT USE {@link AbstractComponentBuilder} in here.
	 * YOU CAN USE {@link CommonComponentBuilder} in here (its legit the same shit just ones abstract)
	 * */
	public static Component getLoggerComponent(String logSymbol, AtlasColor symbolColor, AtlasColor logMessageColor, String logMessage, Component suggestionToFix){
		var builder = new CommonComponentBuilder();
		builder
				.append(logSymbol+" ", symbolColor)
				.append(logMessage, logMessageColor, ITALIC);
		if(suggestionToFix!=null){
			builder.hoverText(suggestionToFix);
		}
		return builder.build();
	}

	public static Component getWarningComponent(String warning){
		return getLoggerComponent("⚠", AtlasColor.WARNING_YELLOW, AtlasColor.WARNING_TEXT_YELLOW, warning, null);
	}

	public static Component getErrorComponent(String error, Component suggestionToFix){
		return getLoggerComponent("⚠", AtlasColor.ERROR_RED, AtlasColor.ERROR_TEXT_RED, error, suggestionToFix);
	}

	public static Component getErrorComponent(String error, String suggestionToFix){
		return getErrorComponent(error, Component.text(suggestionToFix));
	}

	public static Component getErrorComponent(String error){
		return getErrorComponent(error, (Component) null); //You have to cast it to Component so it knows which method to call
	}

}
