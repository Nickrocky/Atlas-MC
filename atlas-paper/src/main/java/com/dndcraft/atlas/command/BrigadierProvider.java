package com.dndcraft.atlas.command;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.dndcraft.atlas.Atlas;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.lang.Validate;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class BrigadierProvider {
	/**
	 * This is used only for check what kind of NMS is
	 * Starting 1.17.X or higher NMS layout was changed
	 */
	enum NMSType{Nms117,Nms1}

	private static final BrigadierProvider INSTANCE = new BrigadierProvider();
	public static BrigadierProvider get() { return INSTANCE; }

	@Getter private boolean functional = true;

	private Method getServer;
	private Method getMCDispatcher;
	private Method getBrigadier;
	private Method getItemStack;

	private Constructor<ArgumentType<Object>> itemStackArgument;

	private Method getBukkitSender = null;

	/**
	 * 1.18 getCommandDispatcher = aA
	 * 1.19 getCommandDispatcher = kms
	 * */

	@SuppressWarnings("unchecked")
	private BrigadierProvider() {
		try {
			var serverClass = MinecraftReflection.getMinecraftServerClass();
			getServer = serverClass.getMethod("getServer");
			getMCDispatcher = serverClass.getDeclaredMethod("aA");
			getMCDispatcher.setAccessible(true);
			getBrigadier = reflectBrigadierGetter();
			getItemStack = reflectItemStackGetter();
			if (getNMSType()==NMSType.Nms1) {
				itemStackArgument = (Constructor<ArgumentType<Object>>) MinecraftReflection.getMinecraftClass("ArgumentItemStack").getConstructor();
			} else {
				Class<?> argItemS = null;
				try { argItemS = Class.forName("net.minecraft.commands.arguments.item.ArgumentItemStack");
				} catch (ClassNotFoundException e) {
                    Atlas.get().getLogger().severe("Failed to locate class: ArgumentItemStack "+e);}
				assert argItemS != null;
				itemStackArgument = (Constructor<ArgumentType<Object>>) argItemS.getConstructor();
			}


		} catch(Exception e) {
			Atlas.get().getLogger().severe("We were unable to set up the BrigadierProvider. Likely a reflection error!");
			functional = false;
			e.printStackTrace();
		}
	}

	private Method reflectBrigadierGetter() throws Exception {
		if (getNMSType()==NMSType.Nms1) {
			var dispatcherClass = MinecraftReflection.getMinecraftClass("CommandDispatcher");
			for(var xx : dispatcherClass.getDeclaredMethods()) {
				if(xx.getParameterCount() == 0 && CommandDispatcher.class.isAssignableFrom(xx.getReturnType()))
					return xx;
			}
		} else {
			Class<?> commandDishp = null;
			try { commandDishp = Class.forName("net.minecraft.commands.CommandDispatcher");
			} catch (ClassNotFoundException e) {}
			for(var xx : commandDishp.getDeclaredMethods()) {
				if(xx.getParameterCount() == 0 && CommandDispatcher.class.isAssignableFrom(xx.getReturnType())) return xx;
			}
		}
		throw new NoSuchMethodError("CommandDispatcher getter in Minecraft");
	}

	private Method reflectItemStackGetter() throws Exception {
		if (getNMSType()==NMSType.Nms1) {
			var dispatcherClass = MinecraftReflection.getMinecraftClass("ArgumentPredicateItemStack");
			var itemStackClass = MinecraftReflection.getItemStackClass();
			for(var xx : dispatcherClass.getDeclaredMethods()) {
				if(xx.getParameterCount() == 2
						&& itemStackClass.isAssignableFrom(xx.getReturnType())
						&& xx.getParameters()[0].getType() == int.class
						&& xx.getParameters()[1].getType() == boolean.class)
					return xx;
			}
		} else {
			Class<?> argumentPrediIS = null;
			Class<?> itemStackClass = null;
			try {
				argumentPrediIS = Class.forName("net.minecraft.commands.arguments.item.ArgumentPredicateItemStack");
				itemStackClass = Class.forName("net.minecraft.world.item.ItemStack");
			} catch (ClassNotFoundException e) {
				Atlas.get().getLogger().severe("Failed to locate class: ArgumentPredicateItemStack or ItemStack "+e);
			}
			for(var xx : argumentPrediIS.getDeclaredMethods()) {
				if(xx.getParameterCount() == 2
						&& itemStackClass.isAssignableFrom(xx.getReturnType())
						&& xx.getParameters()[0].getType() == int.class
						&& xx.getParameters()[1].getType() == boolean.class)
					return xx;
			}
		}
		throw new NoSuchMethodError("ArgumentPredicateItemStack getter for ItemStack");
	}

	@SuppressWarnings("unchecked")
	public CommandDispatcher<Object> getBrigadier() {
		Validate.isTrue(functional);
		try {
			var server = getServer.invoke(null); //Static MinecraftServer getter
			var dispatch = getMCDispatcher.invoke(server);
			return (CommandDispatcher<Object>) getBrigadier.invoke(dispatch);
		} catch(Exception e) {
			Atlas.get().getLogger().severe("Brigadier Decided to crash on us after startup time");
			functional = false;
			return null;
		}
	}

	public ArgumentType<Object> argumentItemStack(){
		try {
			return itemStackArgument.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Method getItemStackParser() {
		return getItemStack;
	}

	@SneakyThrows
	public CommandSender getBukkitSender(Object commandListenerWrapper) {
		if(getBukkitSender == null) {
			getBukkitSender = commandListenerWrapper.getClass().getMethod("getBukkitSender");
		}

		return (CommandSender) getBukkitSender.invoke(commandListenerWrapper);
	}
	private NMSType getNMSType(){
		try {
			Class.forName("net.minecraft.commands.CommandDispatcher");
			Class.forName("net.minecraft.commands.arguments.item.ArgumentItemStack");
			Class.forName("net.minecraft.commands.arguments.item.ArgumentPredicateItemStack");
			Class.forName("net.minecraft.world.item.ItemStack");
			return NMSType.Nms117;
		} catch (ClassNotFoundException e1) {return NMSType.Nms1;}
	}
}