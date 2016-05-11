/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.Java;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraftforge.classloading.FMLForgePlugin;
import Reika.DragonAPI.Exception.VanillaIntegrityException;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class ReikaObfuscationHelper {

	private static final boolean deobf = testDeobf();

	private static final HashMap<String, Method> methods = new HashMap();
	private static final HashMap<String, Field> fields = new HashMap();
	private static final HashMap<String, String> labels = new HashMap();

	private static boolean testDeobf() {/*
		try {
			Method m = ItemHoe.class.getMethod("onItemUse", ItemStack.class, EntityPlayer.class, World.class, int.class, int.class, int.class, int.class, float.class, float.class, float.class);
			return true;
		}
		catch (NoSuchMethodException e) {
			return false;
		}*/
		return !FMLForgePlugin.RUNTIME_DEOBF;
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
			else {
				f = c.getDeclaredField(sg);
				f.setAccessible(true);
			}
			fields.put(deobf, f);
			labels.put(deobf, obf);
			ReikaJavaLibrary.pConsole("DRAGONAPI: Registering reflexive field access to "+c+"."+deobf+" (obfuscated as "+obf+")"); //cannot use logger
		}
		catch (NoSuchFieldException e) {
			throw new IllegalArgumentException("Tried to register nonexistent field "+deobf+"/"+obf, e);
		}
	}

	private static void addMethod(String deobf, String obf, boolean isVisible, Class c, Class... args) {
		try {
			String sg = isDeObfEnvironment() ? deobf : obf;
			Method m;
			if (isVisible)
				m = c.getMethod(sg, args);
			else {
				m = c.getDeclaredMethod(sg, args);
				m.setAccessible(true);
			}
			methods.put(deobf, m);
			labels.put(deobf, obf);
			ReikaJavaLibrary.pConsole("DRAGONAPI: Registering reflexive method access to "+c+"."+deobf+" (obfuscated as "+obf+")"); //cannot use logger
		}
		catch (NoSuchMethodException e) {
			//throw new VanillaIntegrityException("Tried to register nonexistent method "+deobf+"/"+obf+". Check signature.");
			throw new VanillaIntegrityException(deobf, c, args);
		}
	}

	static {

		//addMethod("onItemUse", "func_77648_a", true, Item.class, ItemStack.class, EntityPlayer.class, World.class, int.class, int.class, int.class, int.class, float.class, float.class, float.class);
		addMethod("dropFewItems", "func_70628_a", false, EntityLivingBase.class, boolean.class, int.class);
		addMethod("dropEquipment", "func_82160_b", false, EntityLivingBase.class, boolean.class, int.class);
		addMethod("dropRareDrop", "func_70600_l", false, EntityLivingBase.class, int.class);
		addMethod("jump", "func_70664_aZ", false, EntityLivingBase.class);
		if (isClientSide()) {
			addMethod("getInputStreamByName", "func_110591_a", false, AbstractResourcePack.class, String.class);
		}/*

		if (isClientSide()) {
			addField("field_110859_k", "field_110859_k", false, RenderBiped.class); //armor texture map
			addField("scaleAmount", "field_77092_a", false, RenderSlime.class);
			addField("mainModel", "field_77045_g", false, RendererLivingEntity.class);
			addField("modelBipedMain", "field_77109_a", false, RenderPlayer.class);
		}
		addField("isJumping", "field_70703_bu", false, EntityLivingBase.class);
		addField("timeSinceIgnited", "field_70833_d", false, EntityCreeper.class);*/
		addField("potionTypes", "field_76425_a", true, Potion.class);/*
		addField("biomeList", "field_76773_a", true, BiomeGenBase.class);
		addField("weaponDamage", "field_77827_a", false, ItemSword.class);
		addField("isAggressive", "field_104003_g", false, EntityEnderman.class);
		addField("stareTimer", "field_70826_g", false, EntityEnderman.class);
		addField("theWorldGenerator", "field_82915_S", false, BiomeGenHills.class);
		addField("blockFlammability", "blockFlammability", false, Blocks.class);
		addField("blockFireSpreadSpeed", "blockFireSpreadSpeed", false, Blocks.class);
		addField("stringToIDMapping", "field_75622_f", false, EntityList.class);*/

		if (isClientSide()) {
			//addField("soundLibrary", "soundLibrary", false, SoundSystem.class);
			//addField("streamThread", "streamThread", false, Library.class);
		}
	}

	private static boolean isClientSide() {
		return FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT;
	}
}
