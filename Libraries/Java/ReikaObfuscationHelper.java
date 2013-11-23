/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.Java;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

import net.minecraft.client.audio.SoundPool;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ReikaObfuscationHelper {

	private static final boolean deobf = testDeobf();

	private static final HashMap<String, Method> methods = new HashMap();
	private static final HashMap<String, Field> fields = new HashMap();
	private static final HashMap<String, String> labels = new HashMap();

	private static boolean testDeobf() {
		try {
			Method m = ItemHoe.class.getMethod("onItemUse", ItemStack.class, EntityPlayer.class, World.class, int.class, int.class, int.class, int.class, float.class, float.class, float.class);
			return true;
		}
		catch (NoSuchMethodException e) {
			return false;
		}
	}

	public static boolean isDeObfEnvironment() {
		return deobf;
	}

	public static Field getField(String deobf) {
		return fields.get(deobf);
	}

	public static Method getMethod(String deobf) {
		return methods.get(deobf);
	}

	public static String getLabelName(String deobf) {
		String sg = labels.get(deobf);
		if (sg == null)
			throw new IllegalArgumentException("Tried to get obfuscated name for non-mapped deobf field/method "+deobf+"!");
		if (isDeObfEnvironment())
			return deobf;
		else
			return sg;
	}

	private static void addField(String deobf, String obf, boolean isVisible, Class c) {
		try {
			String sg = isDeObfEnvironment() ? deobf : obf;
			Field f;
			if (isVisible)
				f = c.getField(sg);
			else
				f = c.getDeclaredField(sg);
			fields.put(deobf, f);
			labels.put(deobf, obf);
			ReikaJavaLibrary.pConsole("DRAGONAPI: Registering reflexive field access to "+c+"."+deobf+" (obfuscated as "+obf+")");
		}
		catch (NoSuchFieldException e) {
			throw new IllegalArgumentException("Tried to register nonexistent field "+deobf+"/"+obf);
		}
	}

	private static void addMethod(String deobf, String obf, boolean isVisible, Class c, Class... args) {
		try {
			String sg = isDeObfEnvironment() ? deobf : obf;
			Method m;
			if (isVisible)
				m = c.getMethod(sg, args);
			else
				m = c.getDeclaredMethod(sg, args);
			methods.put(deobf, m);
			labels.put(deobf, obf);
			ReikaJavaLibrary.pConsole("DRAGONAPI: Registering reflexive method access to "+c+"."+deobf+" (obfuscated as "+obf+")");
		}
		catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("Tried to register nonexistent method "+deobf+"/"+obf+". Check signature.");
		}
	}

	static {
		addMethod("onItemUse", "func_77648_a", true, Item.class, ItemStack.class, EntityPlayer.class, World.class, int.class, int.class, int.class, int.class, float.class, float.class, float.class);
		addMethod("getInputStreamByName", "func_110591_a", false, AbstractResourcePack.class, String.class);

		addField("field_110859_k", "field_110859_k", false, RenderBiped.class); //armor texture map
		addField("nameToSoundPoolEntriesMapping", "field_77461_d", false, SoundPool.class);
		addField("isJumping", "field_70703_bu", false, EntityLivingBase.class);
	}
}
