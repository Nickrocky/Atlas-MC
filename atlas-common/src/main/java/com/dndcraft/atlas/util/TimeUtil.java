package com.dndcraft.atlas.util;

import com.dndcraft.atlas.Atlas;
import com.dndcraft.atlas.agnostic.AbstractComponentBuilder;
import com.google.common.primitives.Longs;
import net.kyori.adventure.text.Component;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public final class TimeUtil {

	private TimeUtil() {}
	
	public static Instant parseEager(String input) {
		Timestamp timestamp = parseTimestamp(input);
		if(timestamp != null) return timestamp.toInstant();

		Duration duration = parseDuration(input);
		if(duration != null) return Instant.now().minus(duration);
		
		LocalDateTime t = tryParseTime(input);
		if(t == null) t = tryParseDate(input);
		if(t == null) t = tryParseDateTime(input);
		if(t == null) return null;
		return t.toInstant(ZoneOffset.UTC);
	}

	public static LocalDateTime tryParseTime(String input) {
		try {
			LocalTime t = LocalTime.parse(input);
			return t.atDate(LocalDate.now());
		}catch(DateTimeParseException e) {
			return null;
		}
	}

	public static LocalDateTime tryParseDate(String input) {
		try {
			LocalDate t = LocalDate.parse(input);
			return t.atTime(LocalTime.NOON);
		}catch(DateTimeParseException e) {
			return null;
		}
	}

	public static LocalDateTime tryParseDateTime(String input) {
		try {
			return LocalDateTime.parse(input);
		}catch(DateTimeParseException e) {
			return null;
		}
	}


	public static Duration parseDuration(String parsable) {
		Duration duration = Duration.ZERO;
		boolean anything = false;
		Pattern pat = Pattern.compile("(\\d+)([ywdhms])");
		Matcher matcher = pat.matcher(parsable);
		while(matcher.find()) {
			anything = true;
			int quantity = Integer.parseInt(matcher.group(1));
			String timescale = matcher.group(2);

			TemporalUnit unit = null;
			switch (timescale) {
			case "y":
				quantity *= 365;
				unit = ChronoUnit.DAYS;
				break;
			case "w":
				quantity *= 7;
				unit = ChronoUnit.DAYS;
				break;
			case "d":
				unit = ChronoUnit.DAYS;
				break;
			case "h":
				unit = ChronoUnit.HOURS;
				break;
			case "m":
				unit = ChronoUnit.MINUTES;
				break;
			case "s":
				unit = ChronoUnit.SECONDS;
				break;
			default:
				return null;
			}
			duration = duration.plus(quantity, unit);
		}
		if(anything) return duration;
		else return null;
	}

	public static Timestamp parseTimestamp(String input) {
		Long simple = Longs.tryParse(input);
		if(simple != null) return new Timestamp(simple);
		else return null;
	}
	
	public static Component printTicks(long ticks) {
		return printMillis(ticks * 50l);
	}
	
	public static Component printTicksRaw(long ticks) {
		return printMillisRaw(ticks * 50l);
	}
	
	public static Component printMillis(long millis) {
		return print(millis, false, AtlasColor.WHITE, AtlasColor.GRAY);
	}
	
	public static Component printMillisRaw(long millis) {
		return print(millis, false, null, null);
	}
	
	public static Component printBrief(long millis) {
		return print(millis, true, null, null);
	}
	
	public static Component print(long ms, boolean brief, AtlasColor numColor, AtlasColor unitColor) {
		var sb = Atlas.get().componentBuilder();
		
		if(ms > 10 * 365 * 24 * 3600 * 1000) {
			sb.append("unknown");
			if(numColor != null) sb.color(numColor);
			return sb.build();
		}
		
		long days = MILLISECONDS.toDays(ms);
		long hours = MILLISECONDS.toHours(ms) - days*24;
		long minutes = MILLISECONDS.toMinutes(ms) - DAYS.toMinutes(days) - hours*60;
		long seconds = MILLISECONDS.toSeconds(ms) - DAYS.toSeconds(days) - hours*3600 - minutes*60;
		
		boolean space = false;
		space = append(sb, days, brief, "days", "d", numColor, unitColor, space);
		space = append(sb, hours, brief, "hours", "h", numColor, unitColor, space);
		space = append(sb, minutes, brief, "minutes", "m", numColor, unitColor, space);
		append(sb, seconds, brief, "seconds", "s", numColor, unitColor, space);
		return sb.build();
	}

	private static boolean append(AbstractComponentBuilder<?> sb, long val, boolean brief, String big, String small, AtlasColor c1, AtlasColor c2, boolean space) {
		if(val == 0) return false;
		
		if(space) sb.append(' ');
		sb.append(val);
		if(c1 != null) sb.color(c1);
		
		if(brief) sb.append(small);
		else sb.append(' ' + big);
		if(c2!=null) sb.color(c2);
		
		return true;
	}
}
