package com.dndcraft.atlas.util;

import com.dndcraft.atlas.command.RanCommand;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Pages, this should allow to create pages with clickable buttons and other stuff with ease.
 * @Author Commissar_Voop
 */

@FieldDefaults(level= AccessLevel.PRIVATE)
@SuppressWarnings("unused")
public final class Pages {

    public enum ClickableReturnType{SUGGEST_COMMAND, RUN_COMMAND,NONE}
    private final Set<String> data;

    private int offset;
    private int amount;

    private ClickableReturnType clickableReturnType;

    private String label;
    private String commandData;
    private String commandListing;
    private String separator;

    private TextComponent nextButtonEnabled;
    private TextComponent nextButtonDisabled;
    private TextComponent previousButtonEnabled;
    private TextComponent previousButtonDisabled;
    private TextComponent header;

    private TextColor dataColor;
    private TextColor separatorColor;

    public Pages(Set<String> data, String label) {
        this.data = data;
        this.label = label;
        this.offset = 40;
        this.amount = 40;
        this.separator = ", ";
        this.dataColor = AtlasColor.YELLOW.toTextColor();
        this.separatorColor = AtlasColor.DANDELION.toTextColor();
        this.clickableReturnType = ClickableReturnType.NONE;
        this.header = Component.text("Showing ",AtlasColor.CYAN.toTextColor())
                .append(Component.text(label,AtlasColor.AQUA.toTextColor()))
                .append(Component.text(" List",AtlasColor.CYAN.toTextColor()));
        this.nextButtonEnabled = Component.text("[",AtlasColor.GRAY.toTextColor())
                .append(Component.text("→",AtlasColor.DANDELION.toTextColor()))
                .append(Component.text("]",AtlasColor.GRAY.toTextColor()));
        this.nextButtonDisabled = Component.text("[",AtlasColor.GRAY.toTextColor())
                .append(Component.text("→",AtlasColor.DARK_GRAY.toTextColor()))
                .append(Component.text("]",AtlasColor.GRAY.toTextColor()));
        this.previousButtonEnabled = Component.text("[",AtlasColor.GRAY.toTextColor())
                .append(Component.text("←",AtlasColor.DANDELION.toTextColor()))
                .append(Component.text("]",AtlasColor.GRAY.toTextColor()));
        this.previousButtonDisabled = Component.text("[",AtlasColor.GRAY.toTextColor())
                .append(Component.text("←",AtlasColor.DARK_GRAY.toTextColor()))
                .append(Component.text("]",AtlasColor.GRAY.toTextColor()));
    }

    /**
     * Set a header
     * @param component Kyori Component Style
     * @return Updated Header
     */
    public Pages setHeader(TextComponent component) {
        this.header = component;
        return this;
    }

    /**
     * Set's a separator character
     * @param separator Kyori Component Style
     * @return Updater Separators
     */
    public Pages setSeparator(String separator) {
        this.separator = separator;
        return this;
    }

    /**
     * Set's color of a separator
     * @param separatorColor Kyori Adventure TextColor
     * @return Updated Separator Color
     */
    public Pages setSeparatorColor(TextColor separatorColor) {
        this.separatorColor = separatorColor;
        return this;
    }

    /**
     * Set's color of a data
     * @param dataColor Kyori Adventure TextColor
     * @return Updated Data Color
     */
    public Pages setDataColor(TextColor dataColor) {
        this.dataColor = dataColor;
        return this;
    }

    /**
     * Set's next page button enabled style
     * @param component Component Style
     * @return Updated Next Button Enabled
     */
    public Pages setNextButtonEnabled(TextComponent component) {
        this.nextButtonEnabled = component;
        return this;
    }

    /**
     * Set's next page button disabled style
     * @param component Component Style
     * @return Updated Next Button Disabled
     */
    public Pages setNextButtonDisabled(TextComponent component) {
        this.nextButtonDisabled = component;
        return this;
    }

    /**
     * Set's previous page button enabled style
     * @param component Component Style
     * @return Updated Previous Button Enabled
     */
    public Pages setPreviousButtonEnabled(TextComponent component){
        this.previousButtonEnabled = component;
        return this;
    }

    /**
     * Set's previous page button disabled style
     * @param component Component Style
     * @return Updated Previous Button Disabled
     */
    public Pages setPreviousButtonDisabled(TextComponent component){
        this.previousButtonDisabled = component;
        return this;
    }

    /**
     * Sets pages data command runner
     * This is used for the data side of pages for sample it can be used to click on the page data three things can happens depends on ClickableReturnType
     * Upon click it can run the command, suggest or do nothing
     * @param command integer
     * @return Updated Previous Button Enabled
     */
    public Pages setCommandRunner(String command) {
        this.commandData = command;
        return this;
    }

    /**
     * Sets pages buttons commands
     * Example if a button goes to a next page it should run as /something list 2, but for previous page it should be /something list 1
     * Example in code .setCommand("something list"), Pages will include start of the command /
     * @param command integer
     * @return Updated Command Listing
     */
    public Pages setCommandListing(String command) {
        this.commandListing = command;
        return this;
    }

    /**
     * Sets pages label name
     * @param label integer
     * @return Updated Label
     */
    public Pages setLabel(String label){
        this.label = label;
        return this;
    }

    /**
     * Sets a Clickable Return Type
     * @param clickableReturnType integer
     * @return Updated ClickableReturnType
     */
    public Pages setClickableReturnType(ClickableReturnType clickableReturnType) {
        this.clickableReturnType = clickableReturnType;
        return this;
    }

    /**
     * Sets an amount information will be displayed
     * @param amount integer
     * @return Updated Amount
     */
    public Pages setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    /**
     * Sets an offset for the pages
     * @param offset integer
     * @return Updated Offset
     */
    public Pages setOffset(int offset){
        this.offset = offset;
        return this;
    }

    /**
     * Creates the pages into a Component
     * @param page Current Page!
     * @return Kyori Component
     */
    public Component build(int page) {
        int pages = (int) Math.ceil(data.size()/(double) amount);
        if (pages < page) return RanCommand.ERROR_PREFIX.append(Component.text("Invalid Page!",AtlasColor.RED.toTextColor()));
        List<TextComponent.Builder> dataListing = new ArrayList<>();
        var iter = this.data.iterator();
        int spage = page;
        spage--;
        int offset = spage*this.offset;
        int parsed = 0;
        while (iter.hasNext() && parsed < offset+this.amount) {
            var data = iter.next();
            if (parsed>=offset) {
                var c_data = Component.text().append(Component.text(data,dataColor));
                if (this.commandData!=null) {
                    if (this.clickableReturnType.equals(ClickableReturnType.SUGGEST_COMMAND)){
                        c_data.clickEvent(ClickEvent.suggestCommand("/"+this.commandData+" "+data));
                        c_data.hoverEvent(HoverEvent.showText(Component.text("Click here to paste runnable command in chat",AtlasColor.WARNING_YELLOW.toTextColor())
                                .append(Component.newline())
                                .append(Component.text("/"+this.commandData+" ",AtlasColor.GREEN.toTextColor()))
                                .append(Component.text(data,AtlasColor.WHITE.toTextColor()))));
                    }
                    if (this.clickableReturnType.equals(ClickableReturnType.RUN_COMMAND)) {
                        c_data.clickEvent(ClickEvent.runCommand("/"+this.commandData+" "+data));
                        c_data.hoverEvent(HoverEvent.showText(Component.text("Click here to execute the command",AtlasColor.WARNING_YELLOW.toTextColor())
                                .append(Component.newline())
                                .append(Component.text("/"+this.commandData+" ",AtlasColor.GREEN.toTextColor()))
                                .append(Component.text(data,AtlasColor.WHITE.toTextColor()))));
                    }
                }
                if (!dataListing.contains(c_data)) dataListing.add(c_data);
            }
            parsed++;
        }
        var result = Component.text().append(header);
        result.append(Component.newline());
        result.append(Component.join(Component.text(separator,separatorColor), dataListing));
        result.append(Component.newline());
        if (!(page-1>0)) {
            result.append(this.previousButtonDisabled);
        } else {
            int p = page-1;
            result.append(this.previousButtonEnabled
                    .hoverEvent(HoverEvent.showText(Component.text("Click here go to a previous page!",AtlasColor.BLUE.toTextColor())
                            .append(Component.newline())
                            .append(Component.text("Page: ",AtlasColor.GOLD.toTextColor()).append(Component.text(p,AtlasColor.DANDELION.toTextColor())))))
                    .clickEvent(ClickEvent.runCommand("/"+this.commandListing+" "+p)));
        }
        result.append(Component.text(" Page "+page+"/"+pages+" ",AtlasColor.WARNING_TEXT_YELLOW.toTextColor()));
        if (!(pages>=page+1)) {
            result.append(this.nextButtonDisabled);
        } else {
            int p = page+1;
            result.append(this.nextButtonEnabled
                    .hoverEvent(HoverEvent.showText(Component.text("Click here go to a next page!",AtlasColor.BLUE.toTextColor())
                            .append(Component.newline())
                            .append(Component.text("Page: ",AtlasColor.GOLD.toTextColor()).append(Component.text(p,AtlasColor.DANDELION.toTextColor())))))
                    .clickEvent(ClickEvent.runCommand("/"+this.commandListing+" "+p)));
        }
        return result.build();
    }
}
