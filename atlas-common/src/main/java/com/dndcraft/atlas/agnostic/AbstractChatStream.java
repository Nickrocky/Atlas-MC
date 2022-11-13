package com.dndcraft.atlas.agnostic;

import com.dndcraft.atlas.Atlas;
import com.dndcraft.atlas.util.AtlasColor;
import com.dndcraft.atlas.util.Context;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import net.kyori.adventure.text.Component;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;


@RequiredArgsConstructor
public abstract class AbstractChatStream<T extends AbstractChatStream<T>> {
	protected final Sender converser;
	protected final UUID uuid;
	protected final Context context = new Context();
	protected final List<Prompt> prompts = new ArrayList<>();
	
	protected Consumer<Context> onAbandon, onActivate;
	
	public T prompt(Prompt prompt) {
		prompts.add(prompt);
		return getThis();
	}
	
	public T prompt(String contextTag, Component promptText, Consumer<Prompt> fulfillment) {
		Prompt p = new Prompt(this, contextTag, promptText, fulfillment);
		return prompt(p);
	}
	
	public T confirmPrompt() {
		var msg = Atlas.get().componentBuilder().append("Are you sure you want to continue? Type ", AtlasColor.WHITE)
				.append("YES", AtlasColor.RED).bold().append(" in all capitals to confirm your choice.", AtlasColor.WHITE).build();
		return confirmPrompt(msg, "YES");
	}
	
	public T confirmPrompt(String whatTheyShouldType) {
		var msg = Atlas.get().componentBuilder().append("Are you sure you want to continue? Type ", AtlasColor.WHITE)
				.append(whatTheyShouldType, AtlasColor.RED).bold().append(" to confirm your choice (case-sensitive).", AtlasColor.WHITE).build();
		return confirmPrompt(msg, "YES");
	}
	
	public T confirmPrompt(Component message, String whatTheyShouldType) {
		return prompt(null, message, s->s.equals(whatTheyShouldType), $->$);
	}
	
	public T choice(String contextTag, Component message, String... options) {
		AbstractComponentBuilder<? extends AbstractComponentBuilder<?>> cb = Atlas.get().componentBuilder().append(message).newline();
		for(String option : options) cb.appendButton(option, option);
		message = cb.build();
		
		Function<String, String> maps = s-> (Stream.of(options).filter(o->s.equalsIgnoreCase(s)).findAny().orElse(null));
		return prompt(contextTag, message, maps);
	}

	public T colorChoice(String contextTag, Component message, AtlasColor... colors){
		AbstractComponentBuilder<? extends AbstractComponentBuilder<?>> cb = Atlas.get().componentBuilder().append(message).newline();
		for(AtlasColor color : colors) cb.appendColorOption(color, color.name().toLowerCase());
		message = cb.build();
		List<String> colorOptions = new ArrayList<>();
		for(AtlasColor c : colors) colorOptions.add(c.name());
		Function<String, String> maps = s-> (Stream.of((String[]) colorOptions.toArray()).filter(o->s.equalsIgnoreCase(s)).findAny().orElse(null));
		return prompt(contextTag, message, maps);
	}

	public T prompt(String contextTag, String message) {
		return prompt(contextTag, Component.text(message));
	}
	
	public T prompt(String contextTag, Component message) {
		Predicate<String> somePredicate = $->true; //Compiler wants it explicit in generic type
		return prompt( contextTag, message, somePredicate );
	}
	
	public T prompt(String contextTag, String message, Predicate<String> filter) {
		return prompt(contextTag, Component.text(message), filter);
	}
	
	public T prompt(String contextTag, Component message, Predicate<String> filter) {
		return prompt(contextTag, message, filter, $->$);
	}
	
	public T prompt(String contextTag, Component message, Function<String, ?> mapper) {
		return prompt(contextTag, message, $->true, $->$);
	}
	
	public T prompt(String contextTag, String message, Predicate<String> filter, Function<String, ?> mapper) {
		return prompt(contextTag, Component.text(message), filter, mapper);
	}
	
	public abstract T prompt(String contextTag, Component message, Predicate<String> filter, Function<String, ?> mapper);
	
	
	public T intPrompt(String contextTag, String message) {
		return intPrompt(contextTag, Component.text(message));
	}
	
	public T intPrompt(String contextTag, Component message) {
		return prompt(contextTag, message, NumberUtils::isDigits, Ints::tryParse);
	}
	
	public T doublePrompt(String contextTag, String message) {
		return doublePrompt(contextTag, Component.text(message));
	}
	
	//Would optimally like to use isParsable from NumberUtils, however we aren't using lang3. If we move to lang3, change this.
	public T doublePrompt(String contextTag, Component message) {
		return prompt(contextTag, message, NumberUtils::isNumber, Doubles::tryParse);
	}
	
	public T withContext(String key, Object value) {
		context.set(key, value);
		return getThis();
	}
	
	public T withContext(Context context) {
		context.getMap().forEach( (k,v) -> this.context.set(k, v));
		return getThis();
	}
	
	public void activate(Consumer<Context> go) {
		onActivate = go;
		Validate.isTrue(prompts.size() > 0);
		prompts.get(0).open();
	}
	
	public T withAbandonment(Consumer<Context> stop) {
		onAbandon = stop;
		return getThis();
	}
	
	protected abstract void resolveFinishedStream();
	
	protected abstract void resolveAbaondonedStream();
	
	protected abstract T getThis();
	
	@RequiredArgsConstructor
	@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
	@Getter
	public static final class Prompt{
		AbstractChatStream<?> stream;
		String contextTag;
		Component text;
		
		Consumer<Prompt> fulfillment;
		
		public Sender getConverser() {
			return stream.converser;
		}
		
		void open() {
			sendPrompt();
			fulfillment.accept(this);
		}
		
		public void sendPrompt() {
			Atlas.get().componentBuilder()
			.appendButton("X", "stop", "Click to exit prompt", AtlasColor.RED, AtlasColor.RED)
			.append(" ")
			.append(text)
			.send(stream.converser);
		}
		
		public void fulfil(Object value) {
			if(StringUtils.isNotEmpty(contextTag)) stream.context.set(contextTag, value);
			next();
		}
		
		void next() {
			int i = stream.prompts.indexOf(this) + 1;
			if(i >= stream.prompts.size()) {
				stream.resolveFinishedStream();
			} else {
				stream.prompts.get(i).open();
			}
		}

		public void abandon() {
			stream.resolveAbaondonedStream();
		}
	}
	
}
