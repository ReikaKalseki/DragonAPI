/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.classloading.FMLForgePlugin;

import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;



public abstract class ReflectiveBasedCommand extends DragonCommandBase {

	private static final HashMap<Class, HashMap<String, String>> SRGMap = new HashMap();
	private static final HashMap<String, String> packageShortcuts = new HashMap();
	private static final HashMap<String, Class> classShortcuts = new HashMap();

	private static final UUID consoleUUID = UUID.randomUUID();

	static {
		//SRG begin (do not remove, used by Website Gen)
		addSRGMapping(World.class, "getBlock", "func_147439_a");
		addSRGMapping(World.class, "getBlockMetadata", "func_72805_g");
		addSRGMapping(World.class, "getTileEntity", "func_147438_o");
		addSRGMapping(World.class, "getPlayerEntityByName", "func_72924_a");
		addSRGMapping(World.class, "getPlayerEntityByUUID", "func_152378_a");
		addSRGMapping(World.class, "provider", "field_73011_w");
		addSRGMapping(EntityLivingBase.class, "getHealth", "func_110143_aJ");
		addSRGMapping(EntityPlayer.class, "getCurrentEquippedItem", "func_71045_bC");
		addSRGMapping(ItemStack.class, "getItem", "func_77973_b");
		addSRGMapping(ItemStack.class, "getItemDamage", "func_77960_j");
		addSRGMapping(ItemStack.class, "stackSize", "field_77994_a");
		addSRGMapping(TileEntity.class, "worldObj", "field_145850_b");
		addSRGMapping(TileEntity.class, "xCoord", "field_145851_c");
		addSRGMapping(TileEntity.class, "yCoord", "field_145848_d");
		addSRGMapping(TileEntity.class, "zCoord", "field_145849_e");
		addSRGMapping(Entity.class, "worldObj", "field_70170_p");
		addSRGMapping(Entity.class, "posX", "field_70165_t");
		addSRGMapping(Entity.class, "posY", "field_70163_u");
		addSRGMapping(Entity.class, "posZ", "field_70161_v");
		addSRGMapping(BiomeGenBase.class, "biomeID", "field_76756_M");
		addSRGMapping(BiomeGenBase.class, "theBiomeDecorator", "field_76760_I");
		//SRG end (do not remove, used by Website Gen)

		packageShortcuts.put("mcforge", "net.minecraftforge");
		packageShortcuts.put("mcworld", "net.minecraft.world"); //TODO: not fully implemented

		//Class Shortcut begin (do not remove, used by Website Gen)
		classShortcuts.put("String", java.lang.String.class);
		classShortcuts.put("Object", java.lang.Object.class);
		classShortcuts.put("Enum", java.lang.Enum.class);
		classShortcuts.put("ArrayList", java.util.ArrayList.class);
		classShortcuts.put("HashMap", java.util.HashMap.class);

		classShortcuts.put("Class",java.lang. Class.class);
		classShortcuts.put("Field", java.lang.reflect.Field.class);
		classShortcuts.put("Method", java.lang.reflect.Method.class);
		classShortcuts.put("Array", java.lang.reflect.Array.class);

		classShortcuts.put("Arrays", java.util.Arrays.class);
		classShortcuts.put("Math", java.lang.Math.class);

		classShortcuts.put("World", net.minecraft.world.World.class);
		classShortcuts.put("Entity", net.minecraft.entity.Entity.class);
		classShortcuts.put("EntityLivingBase", net.minecraft.entity.EntityLivingBase.class);
		classShortcuts.put("EntityPlayer", net.minecraft.entity.player.EntityPlayer.class);
		classShortcuts.put("EntityPlayerMP", net.minecraft.entity.player.EntityPlayerMP.class);
		classShortcuts.put("TileEntity", net.minecraft.tileentity.TileEntity.class);
		classShortcuts.put("Block", net.minecraft.block.Block.class);
		classShortcuts.put("Item", net.minecraft.item.Item.class);
		classShortcuts.put("Blocks", net.minecraft.init.Blocks.class);
		classShortcuts.put("Items", net.minecraft.init.Items.class);
		classShortcuts.put("ItemStack", net.minecraft.item.ItemStack.class);
		classShortcuts.put("Biome", net.minecraft.world.biome.BiomeGenBase.class);
		classShortcuts.put("BiomeGenBase", net.minecraft.world.biome.BiomeGenBase.class);
		classShortcuts.put("Server", net.minecraft.server.MinecraftServer.class);
		classShortcuts.put("MinecraftServer", net.minecraft.server.MinecraftServer.class);
		classShortcuts.put("ChunkProviderServer", net.minecraft.world.gen.ChunkProviderServer.class);
		classShortcuts.put("ChunkProviderGenerate", net.minecraft.world.gen.ChunkProviderGenerate.class);

		classShortcuts.put("DimensionManager", net.minecraftforge.common.DimensionManager.class);
		classShortcuts.put("FML", cpw.mods.fml.common.FMLCommonHandler.class);
		classShortcuts.put("Forge", net.minecraftforge.common.MinecraftForge.class);
		classShortcuts.put("ForgeDirection", net.minecraftforge.common.util.ForgeDirection.class);
		classShortcuts.put("Loader", cpw.mods.fml.common.Loader.class);
		classShortcuts.put("Fluid", net.minecraftforge.fluids.Fluid.class);
		classShortcuts.put("FluidStack", net.minecraftforge.fluids.FluidStack.class);
		classShortcuts.put("FluidRegistry", net.minecraftforge.fluids.FluidRegistry.class);
		classShortcuts.put("GameRegistry", cpw.mods.fml.common.registry.GameRegistry.class);
		classShortcuts.put("OreDictionary", net.minecraftforge.oredict.OreDictionary.class);

		classShortcuts.put("ASMHelper", Reika.DragonAPI.Libraries.Java.ReikaASMHelper.class);
		classShortcuts.put("ReflectionHelper", Reika.DragonAPI.Libraries.Java.ReikaReflectionHelper.class);
		classShortcuts.put("JavaLibrary", Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary.class);
		classShortcuts.put("StringParser", Reika.DragonAPI.Libraries.Java.ReikaStringParser.class);
		classShortcuts.put("ArrayHelper", Reika.DragonAPI.Libraries.Java.ReikaArrayHelper.class);
		classShortcuts.put("MathLibrary", Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary.class);
		classShortcuts.put("PhysicsHelper", Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper.class);
		classShortcuts.put("WorldHelper", Reika.DragonAPI.Libraries.World.ReikaWorldHelper.class);
		classShortcuts.put("BiomeHelper", Reika.DragonAPI.Libraries.World.ReikaBiomeHelper.class);
		classShortcuts.put("BlockHelper", Reika.DragonAPI.Libraries.World.ReikaBlockHelper.class);
		classShortcuts.put("ItemHelper", Reika.DragonAPI.Libraries.Registry.ReikaItemHelper.class);
		classShortcuts.put("InventoryHelper", Reika.DragonAPI.Libraries.ReikaInventoryHelper.class);
		classShortcuts.put("PlayerAPI", Reika.DragonAPI.Libraries.ReikaPlayerAPI.class);
		classShortcuts.put("PlantHelper", Reika.DragonAPI.Libraries.Registry.ReikaPlantHelper.class);
		classShortcuts.put("CropHelper", Reika.DragonAPI.Libraries.Registry.ReikaCropHelper.class);
		classShortcuts.put("OreHelper", Reika.DragonAPI.Libraries.Registry.ReikaOreHelper.class);
		classShortcuts.put("TreeHelper", Reika.DragonAPI.Libraries.Registry.ReikaTreeHelper.class);
		classShortcuts.put("ModList", Reika.DragonAPI.ModList.class);
		classShortcuts.put("ModOreList", Reika.DragonAPI.ModRegistry.ModOreList.class);
		classShortcuts.put("ModWoodList", Reika.DragonAPI.ModRegistry.ModWoodList.class);
		classShortcuts.put("ModCropList", Reika.DragonAPI.ModRegistry.ModCropList.class);
		//Class Shortcut end (do not remove, used by Website Gen)
	}

	protected static boolean addClassShortcut(Class c) {
		if (!classShortcuts.containsKey(c)) {
			classShortcuts.put(c.getSimpleName(), c);
			return true;
		}
		return false;
	}

	public static Map<String, Class> getClassShortcuts() {
		return Collections.unmodifiableMap(classShortcuts);
	}

	public static final Class findClass(String name) throws ClassNotFoundException {
		Class c = name.startsWith("#") ? classShortcuts.get(name.substring(1)) : null;
		name = name.replaceAll("/", ".");
		if (c == null)
			c = Class.forName(name);
		return c;
	}

	protected final UUID getUID(ICommandSender ics) {
		return ics instanceof EntityPlayer ? ((EntityPlayer)ics).getUniqueID() : consoleUUID;
	}

	protected static void addSRGMapping(Class c, String deobf, String obf) {
		HashMap<String, String> map = SRGMap.get(c);
		if (map == null) {
			map = new HashMap();
			SRGMap.put(c, map);
		}
		map.put(deobf, obf);
	}

	protected final String toReadableString(Object o) {
		try {
			if (o != null && o.getClass().isArray()) {
				if (o instanceof int[]) {
					return Arrays.toString((int[])o);
				}
				else if (o instanceof boolean[]) {
					return Arrays.toString((boolean[])o);
				}
				else if (o instanceof float[]) {
					return Arrays.toString((float[])o);
				}
				else if (o instanceof double[]) {
					return Arrays.toString((double[])o);
				}
				else if (o instanceof short[]) {
					return Arrays.toString((short[])o);
				}
				else if (o instanceof long[]) {
					return Arrays.toString((long[])o);
				}
				else if (o instanceof byte[]) {
					return Arrays.toString((byte[])o);
				}
				else if (o instanceof char[]) {
					return Arrays.toString((char[])o);
				}
				else {
					return Arrays.deepToString((Object[])o);
				}
			}
			return String.valueOf(o);
		}
		catch (Exception e) {
			return "Object "+o.getClass().getName()+" threw exception on toString(): "+e.toString();
		}
	}

	@Override
	protected boolean isAdminOnly() {
		return true;
	}

	protected final Class[] parseTypes(String arg) throws ClassNotFoundException {
		if (arg.charAt(0) == '(')
			return this.parseASMTypes(arg);
		String[] parts = arg.split(";");
		Class[] types = new Class[parts.length];
		for (int i = 0; i < types.length; i++) {
			types[i] = this.parseType(parts[i]);
		}
		return types;
	}

	private Class[] parseASMTypes(String arg) throws ClassNotFoundException {
		ArrayList<String> li = ReikaASMHelper.parseMethodSignature(arg);
		li.remove(li.size()-1);
		Class[] types = new Class[li.size()];
		for (int i = 0; i < types.length; i++) {
			types[i] = this.parseASMType(li.get(i));
		}
		return types;
	}

	private Class parseASMType(String s) throws ClassNotFoundException {
		if (s.startsWith("L#")) {
			Class ret = this.findClass(s.substring(1, s.length()-1));
			if (ret != null)
				return ret;
		}
		return ReikaASMHelper.parseClass(s);
	}

	protected final Class parseType(String s) throws ClassNotFoundException {
		switch(s) {
			case "int":
				return int.class;
			case "long":
				return long.class;
			case "short":
				return short.class;
			case "byte":
				return byte.class;
			case "double":
				return double.class;
			case "float":
				return float.class;
			case "boolean":
				return boolean.class;
			case "void":
				return void.class;
			case "array":
			case "Object[]":
				return Object[].class;
			case "int[]":
				return int[].class;
			case "double[]":
				return double[].class;
			case "float[]":
				return float[].class;
			case "short[]":
				return short[].class;
			case "byte[]":
				return byte[].class;
			case "long[]":
				return long[].class;
			default:
				return this.findClass(s);
		}
	}

	protected final Class parseType(Object o) {
		Class ret = o.getClass();
		if (ret == Integer.class)
			ret = int.class;
		else if (ret == Long.class)
			ret = long.class;
		else if (ret == Float.class)
			ret = float.class;
		else if (ret == Double.class)
			ret = double.class;
		else if (ret == Byte.class)
			ret = byte.class;
		else if (ret == Short.class)
			ret = short.class;
		return ret;
	}

	protected final Object parseObject(String s) {
		if (s.equalsIgnoreCase("null") || s.equalsIgnoreCase("nil"))
			return null;
		if (s.equalsIgnoreCase("true"))
			return true;
		if (s.equalsIgnoreCase("false"))
			return false;
		try {
			return Integer.parseInt(s);
		}
		catch (NumberFormatException e) {

		}
		try {
			return Float.parseFloat(s);
		}
		catch (NumberFormatException e) {

		}
		try {
			return Double.parseDouble(s);
		}
		catch (NumberFormatException e) {

		}
		return s.replaceAll("__sp__", " ");
	}

	protected void error(ICommandSender ics, String s) {
		this.sendChatToSender(ics, EnumChatFormatting.RED+"Error: "+s);
	}

	protected final String deSRG(Class c, String s) {
		if (!FMLForgePlugin.RUNTIME_DEOBF)
			return s;
		HashMap<String, String> map = SRGMap.get(c);
		if (map == null)
			return s;
		String ret = map.get(s);
		return ret != null ? ret : s;
	}

}
