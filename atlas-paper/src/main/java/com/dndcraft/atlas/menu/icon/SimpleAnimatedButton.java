package com.dndcraft.atlas.menu.icon;

import com.dndcraft.atlas.AtlasPaper;
import com.dndcraft.atlas.menu.MenuAction;
import com.dndcraft.atlas.menu.MenuAgent;
import com.dndcraft.atlas.util.ItemUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SimpleAnimatedButton extends SimpleButton{

    private Component titleComponent;
    private List<Component> lore;
    private Material[] materials;
    private Integer[] customModelData;
    private int visibleItemIndex;

    public SimpleAnimatedButton(Consumer<MenuAction> doThis, Component title, List<Component> lore, Material... items) {
        super(ItemUtil.make(items[0], title, lore), doThis); //Doesnt really matter
        this.titleComponent = title;
        this.lore = lore;
        this.materials = items;
    }

    public SimpleAnimatedButton(Consumer<MenuAction> doThis, Component title, List<Component> lore, Material material, Integer... customModelDatas){
        super(ItemUtil.make(material, title, lore), doThis); //Doesnt really matter
        this.titleComponent = title;
        this.lore = lore;
        this.materials = new Material[]{material};
        this.customModelData = customModelDatas;
    }

    @Override
    public ItemStack getItemStack(MenuAgent agent) {
        List<ItemStack> itemsToLoopThrough = new ArrayList<>();
        if(customModelData == null){
            for(Material m : materials){
                itemsToLoopThrough.add(ItemUtil.make(m, titleComponent, lore));
            }
        }else{
            for(int id : customModelData){
                ItemStack item = ItemUtil.make(materials[0], titleComponent, lore);
                var meta = item.getItemMeta();
                meta.setCustomModelData(id);
                item.setItemMeta(meta);
                itemsToLoopThrough.add(item);
            }
        }

        Bukkit.getScheduler().runTaskTimerAsynchronously(AtlasPaper.get(), () -> {
            Bukkit.getScheduler().runTask(AtlasPaper.get(), () -> agent.getMenu().updateIconItem(this));
            visibleItemIndex += 1;
        }, 0, 20);

        return itemsToLoopThrough.get((visibleItemIndex % itemsToLoopThrough.size()));
    }




}
