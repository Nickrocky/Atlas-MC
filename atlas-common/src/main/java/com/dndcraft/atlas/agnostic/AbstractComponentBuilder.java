package com.dndcraft.atlas.agnostic;

import com.dndcraft.atlas.Atlas;
import com.dndcraft.atlas.util.AtlasColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * @Author Nickrocky213
 * Kyori has a similar system to what is implemented here. Though it should be said that while its neat to add some Kyori
 * stuff, Kyori is a bit exhaustive and ultimately we were better off doing our own wrapper on Kyori to allow for quicker Component creation.
 * Especially since Kyori is used as the Component core for Paper, Minestom, and Velocity.
 * Component replaces MD5 ChatComponent, no support will be given for building legacy components.
 * */
public abstract class AbstractComponentBuilder<E extends AbstractComponentBuilder<E>> {

    protected final TextComponent.Builder handle;

    protected AbstractComponentBuilder(String initialContent){
        handle = Component.text(initialContent).toBuilder();
    }

    /**
     * Adds a character to a new component with a leading and trailing square bracket in a AtlasColor.GRAY color
     * @param ch the character in brackets
     * @return the Component builder to continue building
     * @ApiNote: to complete call AbstractComponentBuilder#build()
     * */
    public E appendBracketed(char ch){
        AbstractComponentBuilder subComponent = Atlas.get().componentBuilder().append("[", AtlasColor.GRAY).append(ch, AtlasColor.GRAY).append("]", AtlasColor.GRAY);
        handle.append(subComponent.build());
        return getThis();
    }

    /**
     * Adds a character to a new component with a leading and trailing square bracket
     * @param ch the character in brackets
     * @param bracketColor the AtlasColor of the character and brackets
     * @return the Component builder to continue building
     * @ApiNote: to complete call AbstractComponentBuilder#build()
     * */
    public E appendBracketed(char ch, AtlasColor bracketColor){
        AbstractComponentBuilder subComponent = Atlas.get().componentBuilder().append("[", bracketColor).append(ch, bracketColor).append("]", bracketColor);
        handle.append(subComponent.build());
        return getThis();
    }

    /**
     * Adds a character to a new component with a leading and trailing square bracket
     * @param ch the character in brackets
     * @param bracketColor the AtlasColor of brackets
     * @param characterColor the AtlasColor of the character
     * @return the Component builder to continue building
     * @ApiNote: to complete call AbstractComponentBuilder#build()
     * */
    public E appendBracketed(char ch, AtlasColor bracketColor, AtlasColor characterColor){
        AbstractComponentBuilder subComponent = Atlas.get().componentBuilder().append("[", bracketColor).append(ch, characterColor).append("]", bracketColor);
        handle.append(subComponent.build());
        return getThis();
    }

    /**
     * Adds a character to a new component with a leading and trailing square bracket
     * @param ch the character in brackets
     * @param bracketColor the AtlasColor of brackets
     * @param characterColor the AtlasColor of the character
     * @param decoration the decoration that should be placed on each of the brackets
     * @return the Component builder to continue building
     * @ApiNote: to complete call AbstractComponentBuilder#build()
     * */
    public E appendBracketed(char ch, AtlasColor bracketColor, AtlasColor characterColor, TextDecoration decoration){
        AbstractComponentBuilder subComponent = Atlas.get().componentBuilder().append("[", bracketColor, decoration).append(ch, characterColor).append("]", bracketColor, decoration);
        handle.append(subComponent.build());
        return getThis();
    }


    /**
     * Adds a string to a new component with a leading and trailing square bracket in a AtlasColor.GRAY color
     * @param word the string in brackets
     * @return the Component builder to continue building
     * @ApiNote: to complete call AbstractComponentBuilder#build()
     * */
    public E appendBracketed(String word){
        AbstractComponentBuilder subComponent = Atlas.get().componentBuilder().append("[", AtlasColor.GRAY).append(word, AtlasColor.GRAY).append("]", AtlasColor.GRAY);
        handle.append(subComponent.build());
        return getThis();
    }

    /**
     * Adds a string to a new component with a leading and trailing square bracket
     * @param word the string in brackets
     * @param bracketColor the AtlasColor of the character and brackets
     * @return the Component builder to continue building
     * @ApiNote: to complete call AbstractComponentBuilder#build()
     * */
    public E appendBracketed(String word, AtlasColor bracketColor){
        AbstractComponentBuilder subComponent = Atlas.get().componentBuilder().append("[", bracketColor).append(word, bracketColor).append("]", bracketColor);
        handle.append(subComponent.build());
        return getThis();
    }

    /**
     * Adds a string to a new component with a leading and trailing square bracket
     * @param word the string in brackets
     * @param bracketColor the AtlasColor of brackets
     * @param wordColor the AtlasColor of the character
     * @return the Component builder to continue building
     * @ApiNote: to complete call AbstractComponentBuilder#build()
     * */
    public E appendBracketed(String word, AtlasColor bracketColor, AtlasColor wordColor){
        AbstractComponentBuilder subComponent = Atlas.get().componentBuilder().append("[", bracketColor).append(word, wordColor).append("]", bracketColor);
        handle.append(subComponent.build());
        return getThis();
    }

    /**
     * Adds a string to a new component with a leading and trailing square bracket
     * @param word the string in brackets
     * @param bracketColor the AtlasColor of brackets
     * @param wordColor the AtlasColor of the character
     * @param decoration the decoration that should be placed on each of the brackets
     * @return the Component builder to continue building
     * @ApiNote: to complete call AbstractComponentBuilder#build()
     * */
    public E appendBracketed(String word, AtlasColor bracketColor, AtlasColor wordColor, TextDecoration decoration){
        AbstractComponentBuilder subComponent = Atlas.get().componentBuilder().append("[", bracketColor, decoration).append(word, wordColor).append("]", bracketColor, decoration);
        handle.append(subComponent.build());
        return getThis();
    }


    /**
     * Adds a string to a new component with a leading and trailing square bracket in a AtlasColor.GRAY color
     * @param component the component in brackets
     * @return the Component builder to continue building
     * @ApiNote: to complete call AbstractComponentBuilder#build()
     * */
    public E appendBracketed(Component component){
        AbstractComponentBuilder subComponent = Atlas.get().componentBuilder().append("[", AtlasColor.GRAY).append(component).append("]", AtlasColor.GRAY);
        handle.append(subComponent.build());
        return getThis();
    }

    /**
     * Adds a string to a new component with a leading and trailing square bracket
     * @param component the component in brackets
     * @param bracketColor the AtlasColor of the character and brackets
     * @return the Component builder to continue building
     * @ApiNote: to complete call AbstractComponentBuilder#build()
     * */
    public E appendBracketed(Component component, AtlasColor bracketColor){
        AbstractComponentBuilder subComponent = Atlas.get().componentBuilder().append("[", bracketColor).append(component).append("]", bracketColor);
        handle.append(subComponent.build());
        return getThis();
    }

    /**
     * Adds a string to a new component with a leading and trailing square bracket
     * @param component the component in brackets
     * @param bracketColor the AtlasColor of brackets
     * @param wordColor the AtlasColor of the character
     * @param decoration the decoration that should be placed on each of the brackets
     * @return the Component builder to continue building
     * @ApiNote: to complete call AbstractComponentBuilder#build()
     * */
    public E appendBracketed(Component component, AtlasColor bracketColor, AtlasColor wordColor, TextDecoration decoration){
        AbstractComponentBuilder subComponent = Atlas.get().componentBuilder().append("[", bracketColor, decoration).append(component.color(wordColor.toTextColor())).append("]", bracketColor, decoration);
        handle.append(subComponent.build());
        return getThis();
    }

    /**
     * Adds a {@link Number} to a new component with a leading and trailing square bracket in a AtlasColor.GRAY color
     * @param number the number in brackets
     * @return the Component builder to continue building
     * @ApiNote: to complete call AbstractComponentBuilder#build()
     * */
    public E appendBracketed(Number number){
        AbstractComponentBuilder subComponent = Atlas.get().componentBuilder().append("[", AtlasColor.GRAY).append(number, AtlasColor.GRAY).append("]", AtlasColor.GRAY);
        handle.append(subComponent.build());
        return getThis();
    }

    /**
     * Adds a {@link Number} to a new component with a leading and trailing square bracket
     * @param number the number in brackets
     * @param bracketColor the AtlasColor of the character and brackets
     * @return the Component builder to continue building
     * @ApiNote: to complete call AbstractComponentBuilder#build()
     * */
    public E appendBracketed(Number number, AtlasColor bracketColor){
        AbstractComponentBuilder subComponent = Atlas.get().componentBuilder().append("[", bracketColor).append(number, bracketColor).append("]", bracketColor);
        handle.append(subComponent.build());
        return getThis();
    }

    /**
     * Adds a {@link Number} to a new component with a leading and trailing square bracket
     * @param number the number in brackets
     * @param bracketColor the AtlasColor of brackets
     * @param numberColor the AtlasColor of the character
     * @return the Component builder to continue building
     * @ApiNote: to complete call AbstractComponentBuilder#build()
     * */
    public E appendBracketed(Number number, AtlasColor bracketColor, AtlasColor numberColor){
        AbstractComponentBuilder subComponent = Atlas.get().componentBuilder().append("[", bracketColor).append(number, numberColor).append("]", bracketColor);
        handle.append(subComponent.build());
        return getThis();
    }

    /**
     * Adds a {@link Number} to a new component with a leading and trailing square bracket
     * @param number the number in brackets
     * @param bracketColor the AtlasColor of brackets
     * @param numberColor the AtlasColor of the character
     * @param decoration the decoration that should be placed on each of the brackets
     * @return the Component builder to continue building
     * @ApiNote: to complete call AbstractComponentBuilder#build()
     * */
    public E appendBracketed(Number number, AtlasColor bracketColor, AtlasColor numberColor, TextDecoration decoration){
        AbstractComponentBuilder subComponent = Atlas.get().componentBuilder().append("[", bracketColor, decoration).append(number, numberColor).append("]", bracketColor, decoration);
        handle.append(subComponent.build());
        return getThis();
    }

    /**
     * Adds a char to a new Component then appends that component on the end of the current component.
     * @param ch Character you are looking to append
     * @return the Component builder to continue building
     * @ApiNote: to complete call AbstractComponentBuilder#build()
     * */
    public E append(char ch) {
        handle.append(Component.text(String.valueOf(ch)));
        return getThis();
    }

    /**
     * Adds a char to a new Component then appends that component on the end of the current component.
     * @param ch Character you are looking to append
     * @param color the AtlasColor you are looking to apply to the character you are appending
     * @return the Component builder to continue building
     * @ApiNote: to complete call AbstractComponentBuilder#build()
     * */
    public E append(char ch, AtlasColor color) {
        handle.append(Component.text(String.valueOf(ch), color.toTextColor()));
        return getThis();
    }

    /**
     * Adds a char to a new Component then appends that component on the end of the current component.
     * @param ch Character you are looking to append
     * @param color the AtlasColor you are looking to apply to the character you are appending
     * @param decoration The decoration you want to apply to the character you are looking to append. Ex. Italics, Bold, etc
     * @return the Component builder to continue building
     * @ApiNote: to complete call AbstractComponentBuilder#build()
     * */
    public E append(char ch, AtlasColor color, TextDecoration decoration) {
        handle.append(Component.text(String.valueOf(ch), color.toTextColor(), decoration));
        return getThis();
    }

    /**
     * Number allows for the support of making components the following types: int, long, short, double, float
     * Adds a char to a new Component then appends that component on the end of the current component.
     * @param number the number you are looking to append
     * @return the Component builder to continue building
     * @ApiNote: to complete call AbstractComponentBuilder#build()
     * */
    public E append(Number number) {
        handle.append(Component.text(String.valueOf(number)));
        return getThis();
    }

    /**
     * Number allows for the support of making components the following types: int, long, short, double, float
     * Adds a char to a new Component then appends that component on the end of the current component.
     * @param number the number you are looking to append
     * @param color the AtlasColor you are looking to apply to the number you are appending
     * @return the Component builder to continue building
     * @ApiNote: to complete call AbstractComponentBuilder#build()
     * */
    public E append(Number number, AtlasColor color) {
        handle.append(Component.text(String.valueOf(number), color.toTextColor()));
        return getThis();
    }

    /**
     * Number allows for the support of making components the following types: int, long, short, double, float
     * Adds a number to a new Component then appends that component on the end of the current component.
     * @param number the number you are looking to append
     * @param color the AtlasColor you are looking to apply to the number you are appending
     * @param decoration The decoration you want to apply to the number you are looking to append. Ex. Italics, Bold, etc
     * @return the Component builder to continue building
     * @ApiNote: to complete call AbstractComponentBuilder#build()
     * */
    public E append(Number number, AtlasColor color, TextDecoration decoration) {
        handle.append(Component.text(String.valueOf(number), color.toTextColor(), decoration));
        return getThis();
    }

    /**
     * Adds a number to a new Component then appends that component on the end of the current component.
     * @param string the string you are looking to append
     * @return the Component builder to continue building
     * @ApiNote: to complete call AbstractComponentBuilder#build()
     * */
    public E append(String string) {
        handle.append(Component.text(String.valueOf(string)));
        return getThis();
    }

    /**
     * Adds a number to a new Component then appends that component on the end of the current component.
     * @param string the string you are looking to append
     * @param color the AtlasColor you are looking to apply to the string you are appending
     * @return the Component builder to continue building
     * @ApiNote: to complete call AbstractComponentBuilder#build()
     * */
    public E append(String string, AtlasColor color) {
        handle.append(Component.text(String.valueOf(string), color.toTextColor()));
        return getThis();
    }

    /**
     * Adds a number to a new Component then appends that component on the end of the current component.
     * @param string the string you are looking to append
     * @param color the AtlasColor you are looking to apply to the string you are appending
     * @param decoration The decoration you want to apply to the string you are looking to append. Ex. Italics, Bold, etc
     * @return the Component builder to continue building
     * @ApiNote: to complete call AbstractComponentBuilder#build()
     * */
    public E append(String string, AtlasColor color, TextDecoration decoration) {
        handle.append(Component.text(String.valueOf(string), color.toTextColor(), decoration));
        return getThis();
    }

    /**
     * Appends a complete component to the current component builder
     * @param component the component you wish to add to the current builder
     * @return the Component builder to continue building
     * @ApiNote: to complete call AbstractComponentBuilder#build()
     * */
    public E append(Component component) {
        handle.append(component);
        return getThis();
    }

    /**
     * Appends a complete component to the current component builder
     * @param components the list of components you wish to add to the current builder
     * @return the Component builder to continue building
     * @ApiNote: to complete call AbstractComponentBuilder#build()
     * */
    public E append(List<Component> components) {
        handle.append(components);
        return getThis();
    }

    /**
     * Terminates the line and moves you to a new line below the former one
     * @return the Component builder to continue building
     * @ApiNote: to complete call AbstractComponentBuilder#build()
     * */
    public E newline(){
        return getThis().append('\n');
    }

    /**
     * =====================================
     * ----------Text Decorations-----------
     * =====================================
     * These are do-able with Component#decorate() but this is a neat QoL
     * */

    /**
     * Applies TextDecoration#BOLD to the component before it
     * @return the Component builder to continue building
     * @ApiNote: to complete call AbstractComponentBuilder#build()
     * */
    public E bold(){
        handle.decorate(TextDecoration.BOLD);
        return getThis();
    }

    /**
     * Applies TextDecoration#ITALIC to the component before it
     * @return the Component builder to continue building
     * @ApiNote: to complete call AbstractComponentBuilder#build()
     * */
    public E italic(){
        handle.decorate(TextDecoration.ITALIC);
        return getThis();
    }

    /**
     * Applies TextDecoration#STRIKETHROUGH to the component before it
     * @return the Component builder to continue building
     * @ApiNote: to complete call AbstractComponentBuilder#build()
     * */
    public E strike(){
        handle.decorate(TextDecoration.STRIKETHROUGH);
        return getThis();
    }

    /**
     * Applies TextDecoration#OBFUSCATED (Magic) to the component before it
     * @return the Component builder to continue building
     * @ApiNote: to complete call AbstractComponentBuilder#build()
     * */
    public E magic(){
        handle.decorate(TextDecoration.OBFUSCATED);
        return getThis();
    }

    /**
     * Applies TextDecoration#UNDERLINED to the component before it
     * @return the Component builder to continue building
     * @ApiNote: to complete call AbstractComponentBuilder#build()
     * */
    public E underline(){
        handle.decorate(TextDecoration.UNDERLINED);
        return getThis();
    }

    /**
     * Applies an AtlasColor to the component before it
     * @return the Component builder to continue building
     * @ApiNote: to complete call AbstractComponentBuilder#build()
     * */
    public E color(AtlasColor color){
        handle.color(color.toTextColor());
        return getThis();
    }

    /**
     * Applies a hover event to the previous component with the provided Component as the hover text
     * @ApiNote: Applying hoverText to hoverText will not do anything. No you cannot hover over a hover text to get more hover text
     * @return the Component builder to continue building
     * @ApiNote: to complete call AbstractComponentBuilder#build()
     * */
    public E hoverText(Component component){
        return hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, component));
    }

    /**
     * Applies a hover event to the previous component with provided string as the hover text
     * @ApiNote: You can use Bukkit ChatColor's concat to the string to apply colors to the string in the component
     * @Example: ChatColor.BLUE + "Example" - This will result in a blue colored "Example" that is seen when hovering over the previous component
     * @return the Component builder to continue building
     * @ApiNote: to complete call AbstractComponentBuilder#build()
     * */
    public E hoverText(String content){
        return hoverText(Component.text(content));
    }

    /**
     * Applies a click event to the component before, when clicked the provided content will be copied to the users clipboard
     * @param content the content to be copied to the clipboard of the client
     * @return the Component builder to continue building
     * @ApiNote: to complete call AbstractComponentBuilder#build()
     * */
    public E onClickCopyToClipboard(String content){
        return clickEvent(ClickEvent.copyToClipboard(content));
    }

    /**
     * Applies a click event to the component before, when clicked the player will run the command provided in command parameter
     * @param command the command to be ran after clicking the component
     * @return the Component builder to continue building
     * @ApiNote: to complete call AbstractComponentBuilder#build()
     * */
    public E onClickRunCommand(String command){
        return clickEvent(ClickEvent.runCommand(command));
    }

    /**
     * Applies a click event to the component before, when clicked will switch the page of the book to the one provided
     * @param pageToGoTo the page number that the book should go to
     * @Warning if you specify a page that is outside of the length of the book, this may throw an error silently.
     * @return the Component builder to continue building
     * @ApiNote to complete call AbstractComponentBuilder#build()
     * */
    public E onClickChangePage(int pageToGoTo){
        return clickEvent(ClickEvent.changePage(pageToGoTo));
    }

    /**
     * Applies a click event to the component before, when clicked will place the suggested command in the chat box
     * @param suggestion the command suggested to the player
     * @return the Component builder to continue building
     * @ApiNote to complete call AbstractComponentBuilder#build()
     * */
    public E onClickSuggestCommand(String suggestion){
        return clickEvent(ClickEvent.suggestCommand(suggestion));
    }

    /**
     * Applies a click event to the component before, when clicked will open a dialog box to take a player to a website URL
     * @param url the URL you want to take the player to
     * @return the Component builder to continue building
     * @ApiNote to complete call AbstractComponentBuilder#build()
     * */
    public E onClickOpenUrl(String url){
        return clickEvent(ClickEvent.openUrl(url));
    }

    /**
     * Applies a click event to the component before, when clicked will open a dialog box to take a player to a website URL
     * @param file the File you would like to open on the clients device
     * @return the Component builder to continue building
     * @ApiNote to complete call AbstractComponentBuilder#build()
     * */
    public E onClickOpenFileOnClient(String file){
        return clickEvent(ClickEvent.openFile(file));
    }

    /**
     * Appends a button with a given command to execute when clicked
     * @param content Button text
     * @param command Command to execute
     * */
    public E appendButton(String content, String command){
        handle.append(buttonComponent(content, command, null,  AtlasColor.GRAY, AtlasColor.BLUE, null, null));
        return getThis();
    }

    /**
     * Appends a button with a given command to execute when clicked, additionally hover text is shown
     * @param content Button text
     * @param command Command to execute
     * @param hoverText text to show when the button is hovered over.
     * */
    public E appendButton(String content, String command, String hoverText){
        handle.append(buttonComponent(content, command, hoverText, AtlasColor.GRAY, AtlasColor.BLUE, AtlasColor.GRAY, TextDecoration.ITALIC));
        return getThis();
    }

    /**
     * Appends a button with a given command to execute when clicked
     * @param content Button text
     * @param command Command to execute
     * @param bracketColor color of the brackets that surround a given option
     * */
    public E appendButton(String content, String command, AtlasColor textColor, AtlasColor bracketColor){
        handle.append(buttonComponent(content, command, null, textColor, bracketColor, null, null));
        return getThis();
    }

    /**
     * Appends a button with a given command to execute when clicked, additionally hover text is shown
     * @param content Button text
     * @param command Command to execute
     * @param bracketColor color of the brackets that surround a given option
     * @param hoverText text to show when the button is hovered over.
     * @param textColor color of the actual text content of the button
     * */
    public E appendButton(String content, String command, String hoverText, AtlasColor textColor, AtlasColor bracketColor){
        handle.append(buttonComponent(content, command, hoverText, textColor, bracketColor, AtlasColor.GRAY, TextDecoration.ITALIC));
        return getThis();
    }

    /**
     * Appends a button with a given command to execute when clicked, additionally hover text is shown
     * @param content Button text
     * @param command Command to execute
     * @param bracketColor color of the brackets that surround a given option
     * @param hoverText text to show when the button is hovered over.
     * @param textColor color of the actual text content of the button
     * @param hoverTextColor color of the hoverText
     * @param hoverTextDecoration the text decoration of the hover text, Ex. Italic, Bold, Strikethrough
     * */
    public E appendButton(String content, String command, String hoverText, AtlasColor textColor, AtlasColor bracketColor, AtlasColor hoverTextColor, TextDecoration hoverTextDecoration){
        handle.append(buttonComponent(content, command, hoverText, textColor, bracketColor, hoverTextColor, hoverTextDecoration));
        return getThis();
    }

    /**
     * Appends a button showcasing a particular AtlasColor
     * @param color Color you want to showcase and have selectable
     * @param command command to execute on click.
     */
    public E appendColorOption(AtlasColor color, String command){
        handle.append(buttonComponent(StringUtils.capitalize(color.name().toLowerCase()), command, "The quick brown fox jumps over the lazy dog", color, AtlasColor.DARK_GRAY, color, TextDecoration.ITALIC));
        return getThis();
    }

    /**
     * DO NOT EVER MAKE THIS PUBLIC UNDER ANY CIRCUMSTANCE
     * THIS IS INTENTIONALLY LEFT PRIVATE KEEP IT THAT WAY
     * */
    private static Component buttonComponent(String text, String command, String hoverText, AtlasColor textColor, AtlasColor bracketColor, AtlasColor hoverTextColor, TextDecoration hoverTextDecoration){
        AbstractComponentBuilder buttonTextComponent = Atlas.get().componentBuilder().appendBracketed(text, bracketColor, textColor);
        buttonTextComponent.onClickRunCommand(command);
        if(hoverText != null && !hoverText.isEmpty()){
            AbstractComponentBuilder hoverComponent = Atlas.get().componentBuilder();
            hoverComponent.append(hoverText, hoverTextColor, hoverTextDecoration);
            buttonTextComponent.hoverText(hoverComponent.build());
        }
        return buttonTextComponent.build();
    }


    //Really shouldnt grab this outside of this class. Like you can? but its just eh, id rather people use the public methods
    protected E clickEvent(ClickEvent clickEvent){
        handle.clickEvent(clickEvent);
        return getThis();
    }

    //Really shouldnt grab this outside of this class. Like you can? but its just eh, id rather people use the public methods
    protected E hoverEvent(HoverEvent event){
        handle.hoverEvent(event);
        return getThis();
    }

    /**
     * Builds the component and sends it to a provided Sender
     * @param sender Entity to be sent to
     * @return the Component builder to continue building (if desired for whatever fucking reason)
     * @ApiNote to complete call AbstractComponentBuilder#build()
     * */
    public E send(Sender sender) {
        sender.sendMessage(handle.build());
        return getThis();
    }

    /**
     * Builds the component from the component builder
     * @return the final component object built in the builder
     * */
    public Component build(){
        return handle.build();
    }

    /**
     * Gets the current builder for this component
     * @return the current Builder this component
     * */
    protected abstract E getThis();

}
