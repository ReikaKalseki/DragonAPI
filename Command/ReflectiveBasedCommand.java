package Reika.DragonAPI.Command;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;
import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaReflectionHelper;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaBiomeHelper;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import cpw.mods.fml.common.FMLCommonHandler;



public abstract class ReflectiveBasedCommand extends DragonCommandBase {

	private static final HashMap<Class, HashMap<String, String>> SRGMap = new HashMap();
	private static final HashMap<String, String> packageShortcuts = new HashMap();
	private static final HashMap<String, Class> classShortcuts = new HashMap();

	private static final UUID consoleUUID = UUID.randomUUID();

	static {
		addSRGMapping(World.class, "getBlock", "func_147439_a");
		addSRGMapping(World.class, "getBlockMetadata", "func_72805_g");
		addSRGMapping(World.class, "getTileEntity", "func_147438_o");
		addSRGMapping(World.class, "getPlayerEntityByName", "func_72924_a");
		addSRGMapping(World.class, "getPlayerEntityByUUID", "func_152378_a");
		addSRGMapping(EntityLivingBase.class, "getHealth", "func_110143_aJ");

		packageShortcuts.put("mcforge", "net.minecraftforge");
		packageShortcuts.put("mcworld", "net.minecraft.world"); //TODO: not fully implemented

		classShortcuts.put("String", String.class);
		classShortcuts.put("Object", Object.class);
		classShortcuts.put("ArrayList", ArrayList.class);
		classShortcuts.put("HashMap", HashMap.class);

		classShortcuts.put("Class", Class.class);
		classShortcuts.put("Field", Field.class);
		classShortcuts.put("Method", Method.class);
		classShortcuts.put("Array", Array.class);

		classShortcuts.put("Arrays", Arrays.class);
		classShortcuts.put("Math", Math.class);

		classShortcuts.put("World", World.class);
		classShortcuts.put("Entity", Entity.class);
		classShortcuts.put("EntityLivingBase", EntityLivingBase.class);
		classShortcuts.put("EntityPlayer", EntityPlayer.class);
		classShortcuts.put("TileEntity", TileEntity.class);
		classShortcuts.put("Block", Block.class);
		classShortcuts.put("Item", Item.class);
		classShortcuts.put("Blocks", Blocks.class);
		classShortcuts.put("Items", Items.class);
		classShortcuts.put("ItemStack", ItemStack.class);
		classShortcuts.put("Biome", BiomeGenBase.class);
		classShortcuts.put("Server", MinecraftServer.class);

		classShortcuts.put("DimensionManager", DimensionManager.class);
		classShortcuts.put("FML", FMLCommonHandler.class);
		classShortcuts.put("Forge", MinecraftForge.class);

		classShortcuts.put("ASMHelper", ReikaASMHelper.class);
		classShortcuts.put("ReflectionHelper", ReikaReflectionHelper.class);
		classShortcuts.put("JavaLibrary", ReikaJavaLibrary.class);
		classShortcuts.put("StringParser", ReikaStringParser.class);
		classShortcuts.put("ArrayHelper", ReikaArrayHelper.class);
		classShortcuts.put("MathLibrary", ReikaMathLibrary.class);
		classShortcuts.put("PhysicsHelper", ReikaPhysicsHelper.class);
		classShortcuts.put("WorldHelper", ReikaWorldHelper.class);
		classShortcuts.put("BiomeHelper", ReikaBiomeHelper.class);
		classShortcuts.put("BlockHelper", ReikaBlockHelper.class);
		classShortcuts.put("ItemHelper", ReikaItemHelper.class);
		classShortcuts.put("InventoryHelper", ReikaInventoryHelper.class);
		classShortcuts.put("PlayerAPI", ReikaPlayerAPI.class);
	}

	protected static boolean addClassShortcut(Class c) {
		if (!classShortcuts.containsKey(c)) {
			classShortcuts.put(c.getSimpleName(), c);
			return true;
		}
		return false;
	}

	protected final Class findClass(String name) throws ClassNotFoundException {
		Class c = name.startsWith("#") ? classShortcuts.get(name.substring(1)) : null;
		name = name.replaceAll("/", ".");
		if (c == null)
			c = Class.forName(name);
		return c;
	}

	protected final UUID getUID(ICommandSender ics) {
		return ics instanceof EntityPlayer ? ((EntityPlayer)ics).getUniqueID() : consoleUUID;
	}

	private static void addSRGMapping(Class c, String deobf, String obf) {
		HashMap<String, String> map = SRGMap.get(c);
		if (map == null) {
			map = new HashMap();
			SRGMap.put(c, map);
		}
		map.put(deobf, obf);
	}

	protected final String toReadableString(Object o) {
		return (o instanceof Object[] ? Arrays.deepToString((Object[])o) : String.valueOf(o));
	}

	@Override
	protected final boolean isAdminOnly() {
		return true;
	}

	protected final Class[] parseTypes(String arg) throws ClassNotFoundException {
		String[] parts = arg.split(";");
		Class[] types = new Class[parts.length];
		for (int i = 0; i < types.length; i++) {
			types[i] = this.parseType(parts[i]);
		}
		return types;
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
			return Double.parseDouble(s);
		}
		catch (NumberFormatException e) {

		}
		return s;
	}

	protected void error(ICommandSender ics, String s) {
		this.sendChatToSender(ics, EnumChatFormatting.RED+"Error: "+s);
	}

	protected final String deSRG(Class c, String s) {
		HashMap<String, String> map = SRGMap.get(c);
		if (map == null)
			return s;
		String ret = map.get(s);
		return ret != null ? ret : s;
	}

}
