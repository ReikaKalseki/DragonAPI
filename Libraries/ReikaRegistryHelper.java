/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraftforge.common.util.EnumHelper;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.DragonOptions;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.Trackers.IDCollisionTracker;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Base.ISBRH;
import Reika.DragonAPI.Exception.InstallationException;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Instantiable.ItemBlockCustomLocalization;
import Reika.DragonAPI.Interfaces.Block.Submergeable;
import Reika.DragonAPI.Interfaces.Registry.BlockEnum;
import Reika.DragonAPI.Interfaces.Registry.EnchantmentEnum;
import Reika.DragonAPI.Interfaces.Registry.EntityEnum;
import Reika.DragonAPI.Interfaces.Registry.ISBRHEnum;
import Reika.DragonAPI.Interfaces.Registry.ItemEnum;
import Reika.DragonAPI.Interfaces.Registry.RegistryEntry;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.Java.ReikaReflectionHelper;
import Reika.DragonAPI.ModInteract.LegacyWailaHelper;
import Reika.DragonAPI.ModRegistry.InterfaceCache;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class ReikaRegistryHelper extends DragonAPICore {

	private static final HashMap<BlockEnum, ArrayList<Integer>> blockVariants = new HashMap();
	private static final HashMap<ItemEnum, ArrayList<Integer>> itemVariants = new HashMap();
	private static final IdentityHashMap<Object, RegistryEntry> registries = new IdentityHashMap();
	private static final IdentityHashMap<Object, EntityCollection> modEntityRegistries = new IdentityHashMap();
	private static final HashMap<BlockEnum, String> blockRegNames = new HashMap();
	private static final HashMap<ItemEnum, String> itemRegNames = new HashMap();

	/** Instantiates all blocks and registers them to the game. Uses an Enum[] that implements RegistrationList.
	 * Args: Mod, Enum.values(), Target Block[] array to save instances. */
	public static void instantiateAndRegisterBlocks(DragonAPIMod mod, BlockEnum[] enumr, Block[] target) {
		if (enumr.length != target.length)
			throw new RegistrationException(mod, "Invalid storage array!");
		boolean canLoad = !Loader.instance().hasReachedState(LoaderState.INITIALIZATION);
		if (!canLoad)
			throw new RegistrationException(mod, "This mod is loading blocks too late in the setup!");
		for (int i = 0; i < enumr.length; i++) {
			BlockEnum r = enumr[i];
			target[i] = registerBlock(mod, r, i);
		}
	}

	private static Block registerBlock(DragonAPIMod mod, BlockEnum r, int idx) {
		Block b = null;
		if (!r.isDummiedOut()) {
			b = ReikaReflectionHelper.createBlockInstance(mod, r);
			String regname = (mod.getTechnicalName()+"_block_"+r.name()).toLowerCase(Locale.ENGLISH);
			if (r.hasItemBlock())
				GameRegistry.registerBlock(b, r.getItemBlock(), regname);
			else
				GameRegistry.registerBlock(b, ItemBlockCustomLocalization.class, regname);
			registries.put(b, r);
			registries.put(Item.getItemFromBlock(b), r);
			blockRegNames.put(r, regname);
			int num = r.getNumberMetadatas();
			for (int k = 0; k < num; k++)
				registerBlockVariant(r, k);
			if (r.hasItemBlock())
				mod.getModLogger().log("Instantiating Block "+r.getBasicName()+" with ID "+b+" to Block Variable "+b.getClass().getSimpleName()+" (enum index "+idx+") with ItemBlock "+r.getItemBlock().getSimpleName());
			else
				mod.getModLogger().log("Instantiating Block "+r.getBasicName()+" with ID "+b+" to Block Variable "+b.getClass().getSimpleName()+" (enum index "+idx+")");
			if (ModList.WAILA.isLoaded() && InterfaceCache.WAILA.instanceOf(r.getObjectClass())) {
				LegacyWailaHelper.registerLegacyWAILACompat(r);
			}
			validateBlock(mod, r, b);
		}
		else {
			mod.getModLogger().log("Not instantiating Item "+r.getBasicName()+", as it is dummied out.");
		}
		return b;
	}

	private static void validateBlock(DragonAPIMod mod, BlockEnum r, Block b) {
		if (ReikaObfuscationHelper.isDeObfEnvironment())
			if (b instanceof Submergeable && ((Submergeable)b).renderLiquid(0)) {
				Submergeable s = (Submergeable)b;
				if (s.getRenderBlockPass() == 0)
					throw new RegistrationException(mod, "Block "+r+" is submergeable and fillable with liquid but does not render in pass 1!");
			}
	}

	/** Instantiates all items and registers them to the game. Uses an Enum[] that implements RegistrationList.
	 * Args: Mod, Enum.values(), Target Item[] array to save instances. */
	public static void instantiateAndRegisterItems(DragonAPIMod mod, ItemEnum[] enumr, Item[] target) {
		if (enumr.length != target.length)
			throw new RegistrationException(mod, "Invalid storage array!");
		boolean canLoad = !Loader.instance().hasReachedState(LoaderState.INITIALIZATION);
		if (!canLoad)
			throw new RegistrationException(mod, "This mod is loading items too late in the setup!");
		for (int i = 0; i < enumr.length; i++) {
			ItemEnum r = enumr[i];
			target[i] = registerItem(mod, r, i);
		}
	}

	private static Item registerItem(DragonAPIMod mod, ItemEnum r, int idx) {
		Item it = null;
		if (!r.isDummiedOut()) {
			it = ReikaReflectionHelper.createItemInstance(mod, r);
			String regname = (mod.getTechnicalName()+"_item_"+r.name()).toLowerCase(Locale.ENGLISH);
			int num = r.getNumberMetadatas();
			for (int j = 0; j < num; j++) {
				registerItemVariant(r, j);
			}
			GameRegistry.registerItem(it, regname);
			registries.put(it, r);
			itemRegNames.put(r, regname);
			mod.getModLogger().log("Instantiating Item "+r.getBasicName()+" with ID "+it+" to Item Variable "+it.getClass().getSimpleName()+" (enum index "+idx+"). Has "+r.getNumberMetadatas()+" metadatas.");
		}
		else {
			mod.getModLogger().log("Not instantiating Item "+r.getBasicName()+", as it is dummied out.");
		}
		return it;
	}

	/** Instantiates all Enchantments and registers them to the game. Uses an Enum[] that implements RegistrationList.
	 * Args: Mod, Enum.values(), Target Enchantment[] array to save instances. */
	public static void instantiateAndRegisterEnchantments(DragonAPIMod mod, EnchantmentEnum[] enumr, Enchantment[] target) {
		if (enumr.length != target.length)
			throw new RegistrationException(mod, "Invalid storage array!");
		boolean canLoad = !Loader.instance().hasReachedState(LoaderState.INITIALIZATION);
		if (!canLoad)
			throw new RegistrationException(mod, "This mod is loading Enchantments too late in the setup!");
		for (int i = 0; i < enumr.length; i++) {
			EnchantmentEnum r = enumr[i];
			target[i] = registerEnchantment(mod, r, i);
		}
	}

	private static Enchantment registerEnchantment(DragonAPIMod mod, EnchantmentEnum r, int idx) {
		Enchantment it = null;
		if (!r.isDummiedOut()) {
			IDCollisionTracker.instance.addEnchantmentID(mod, r.getEnchantmentID(), r.getObjectClass());
			it = ReikaReflectionHelper.createEnchantmentInstance(mod, r.getObjectClass(), r.getEnchantmentID(), r.getUnlocalizedName(), false);
			String regname = (mod.getTechnicalName()+"_enchantment_"+r.name()).toLowerCase(Locale.ENGLISH);
			registries.put(it, r);
			mod.getModLogger().log("Instantiating Enchantment "+r.getBasicName()+" with ID "+it+" to Enchantment Variable "+it.getClass().getSimpleName()+" (enum index "+idx+").");
		}
		else {
			mod.getModLogger().log("Not instantiating Enchantment "+r.getBasicName()+", as it is dummied out.");
		}
		return it;
	}

	private static void registerBlockVariant(BlockEnum e, int meta) {
		ArrayList<Integer> li = blockVariants.get(e);
		if (li == null) {
			li = new ArrayList();
			blockVariants.put(e, li);
		}
		li.add(meta);
	}

	private static void registerItemVariant(ItemEnum e, int meta) {
		ArrayList<Integer> li = itemVariants.get(e);
		if (li == null) {
			li = new ArrayList();
			itemVariants.put(e, li);
		}
		li.add(meta);
	}

	public static int getNumberVariantsFor(BlockEnum b) {
		return blockVariants.containsKey(b) ? blockVariants.get(b).size() : 1;
	}

	public static int getNumberVariantsFor(ItemEnum e) {
		return itemVariants.containsKey(e) ? itemVariants.get(e).size() : 1;
	}

	public static ArrayList<Integer> getVariantsFor(BlockEnum b) {
		ArrayList<Integer> ret = new ArrayList();
		if (blockVariants.containsKey(b)) {
			ArrayList li = blockVariants.get(b);
			ret.addAll(li);
		}
		return ret;
	}

	public static ArrayList<Integer> getVariantsFor(ItemEnum e) {
		ArrayList<Integer> ret = new ArrayList();
		if (itemVariants.containsKey(e)) {
			ArrayList li = itemVariants.get(e);
			ret.addAll(li);
		}
		return ret;
	}

	public static void loadNames() {/*
		for (BlockEnum b : blockVariants.keySet()) {
			ArrayList<Integer> metas = blockVariants.get(b);
			if (metas == null || metas.isEmpty()) {
				LanguageRegistry.addName(b.getBlockInstance(), b.getBasicName());
			}
			else {
				for (int i = 0; i < metas.size(); i++) {
					int meta = metas.get(i);
					ItemStack is = new ItemStack(b.getBlockInstance(), 1, meta);
					LanguageRegistry.addName(is, b.getMultiValuedName(meta));
				}
			}
		}

		for (ItemEnum e : itemVariants.keySet()) {
			ArrayList<Integer> metas = itemVariants.get(e);
			if (metas == null || metas.isEmpty()) {
				LanguageRegistry.addName(e.getItemInstance(), e.getBasicName());
			}
			else {
				for (int i = 0; i < metas.size(); i++) {
					int meta = metas.get(i);
					ItemStack is = new ItemStack(e.getItemInstance(), 1, meta);
					LanguageRegistry.addName(is, e.getMultiValuedName(meta));
				}
			}
		}*/

		for (BlockEnum b : blockVariants.keySet()) {
			Item item = Item.getItemFromBlock(b.getBlockInstance());
			if (item instanceof ItemBlockCustomLocalization) {
				/*if (b == null) {
					b = (BlockEnum)getRegistryForObject(((ItemBlock)item).field_150939_a);
				}*/
				((ItemBlockCustomLocalization)item).setEnumObject(b);

			}
			else {

			}
		}
	}

	public static LoaderState getForgeLoadState() {
		LoaderState[] list = LoaderState.values();
		for (int i = list.length-1; i >= 0; i--) {
			if (Loader.instance().hasReachedState(list[i]))
				return list[i];
		}
		return list[0];
	}

	public static void setupModData(DragonAPIMod mod, FMLPreInitializationEvent evt) {
		ModMetadata dat = evt.getModMetadata();
		dat.authorList.clear();
		dat.authorList.add(mod.getModAuthorName());
		dat.version = mod.getModVersion().toString();
		dat.credits = mod.getModAuthorName();
		dat.name = mod.getDisplayName();
		dat.url = mod.getDocumentationSite().toString();
		dat.updateUrl = mod.getUpdateCheckURL();
		if (dat.updateUrl == null)
			dat.updateUrl = "";
		dat.autogenerated = false; //no idea why this needs to be set, but OK
		//dat.description = "Hello";
		String path = mod.getModAuthorName()+"/"+mod.getDisplayName()+"/logo.png";
		dat.logoFile = path;
	}

	/** Overrides one block in the Block database with another. The new block must
	 * have an ID, material constructor! Returns true if successful. */
	public static boolean overrideBlock(DragonAPIMod mod, String blockField, Class<?extends Block> toOverride) {
		mod.getModLogger().log("Overriding Blocks."+blockField+" with "+toOverride);
		try {
			Field f = Blocks.class.getField(blockField);
			Block target = (Block)f.get(null);
			Constructor c = toOverride.getConstructor(Material.class);
			Block block = (Block)c.newInstance(target.getMaterial());
			block.setTickRandomly(target.getTickRandomly());
			block.setBlockName(target.getUnlocalizedName().substring(5));
			block.setLightOpacity(target.getLightOpacity());
			//block.opaqueCubeLookup[target.blockID] = target.isOpaqueCube();
			block.setLightLevel(block.getLightValue());
			block.slipperiness = target.slipperiness;
			block.blockHardness = target.blockHardness;
			block.blockResistance = target.blockResistance;
			int id = Block.getIdFromBlock(target);
			String name = Block.blockRegistry.getNameForObject(target);
			Block.blockRegistry.addObject(id, name, block);
			ReikaReflectionHelper.setFinalField(f, null, block);
			return true;
		}
		catch (Exception e) {
			mod.getModLogger().logError("Could not override Blocks."+blockField+" with "+toOverride);
			e.printStackTrace();
			return false;
		}
	}

	@SideOnly(Side.CLIENT)
	public static SoundCategory addSoundCategory(String name) {
		SoundCategory cat = null;
		String label = name.toLowerCase(Locale.ENGLISH);
		try {
			cat = EnumHelper.addEnum(SoundCategory.class, name.toUpperCase(), new Class[]{String.class, int.class}, new Object[]{label, SoundCategory.values().length});
			SoundCategory.field_147168_j.put(cat.getCategoryName(), cat);
			SoundCategory.field_147169_k.put(Integer.valueOf(cat.getCategoryId()), cat);

			Map<SoundCategory, Float> map = Minecraft.getMinecraft().gameSettings.mapSoundLevels;
			Minecraft.getMinecraft().gameSettings.mapSoundLevels = useSoundHashMap() ? new HashMap() : new EnumMap(SoundCategory.class);
			for (SoundCategory c : map.keySet()) {
				Minecraft.getMinecraft().gameSettings.mapSoundLevels.put(c, map.get(c));
			}
			Minecraft.getMinecraft().gameSettings.mapSoundLevels.put(cat, 1F);
		}
		catch (Exception e) {
			if (e instanceof ArrayIndexOutOfBoundsException) {
				throw new InstallationException(DragonAPIInit.instance, "Could not add sound category "+name+"! Use the Sound HashMap config!", e);
			}
			else {
				throw new RuntimeException("Could not add sound category "+name+"!", e);
			}
		}
		return cat;
	}

	@SideOnly(Side.CLIENT)
	private static boolean useSoundHashMap() {
		return DragonOptions.SOUNDHASHMAP.getState();//*ModList.ENDEREXPANSION.isLoaded() && ModList.FORESTRY.isLoaded() && */ReikaJVMParser.getJavaVersion(0) == 8 && KeyBinding.getKeybinds().size() >= 63;
	}

	public static RegistryEntry getRegistryForObject(Object o) {
		return registries.get(o);
	}

	public static String getGameRegistryName(DragonAPIMod mod, BlockEnum reg) {
		return mod.getModContainer().getModId()+":"+blockRegNames.get(reg);
	}

	public static String getGameRegistryName(DragonAPIMod mod, ItemEnum reg) {
		return mod.getModContainer().getModId()+":"+itemRegNames.get(reg);
	}

	public static void registerModEntity(Object mod, Class<? extends Entity> e, String name, boolean sendVelocity, int trackingDist) {
		boolean upd = sendVelocity;
		int dist = trackingDist;
		EntityCollection ec = getNextCollectionFor(mod);
		int id = ec.maxID;
		EntityRegistry.registerModEntity(e, name, id, mod, dist, upd ? 20 : 1, upd);
		ec.maxID++;
	}

	public static void registerModEntities(Object mod, EntityEnum[] list) {
		for (int i = 0; i < list.length; i++) {
			EntityEnum e = list[i];
			if (!e.isDummiedOut()) {
				boolean upd = e.sendsVelocityUpdates();
				int dist = e.getTrackingDistance();
				EntityCollection ec = getNextCollectionFor(mod);
				int id = ec.maxID;
				if (e.hasGlobalID()) {
					id = EntityRegistry.findGlobalUniqueEntityId();
					if (e.hasSpawnEgg()) {
						EntityRegistry.registerGlobalEntityID(e.getObjectClass(), e.getUnlocalizedName(), id, e.eggColor2(), e.eggColor1());
					}
					else {
						EntityRegistry.registerGlobalEntityID(e.getObjectClass(), e.getUnlocalizedName(), id);
					}
				}
				EntityRegistry.registerModEntity(e.getObjectClass(), e.getUnlocalizedName(), id, mod, dist, upd ? 1 : 20, upd);
				ec.addEntry(e);
			}
		}
	}

	public static List<EntityEnum> getEntityEnumForMod(Object mod) {
		EntityCollection ec = modEntityRegistries.get(mod);
		return ec != null ? Collections.unmodifiableList(ec.entities) : null;
	}

	private static EntityCollection getNextCollectionFor(Object mod) {
		EntityCollection ec = modEntityRegistries.get(mod);
		if (ec == null) {
			ec = new EntityCollection();
			modEntityRegistries.put(mod, ec);
		}
		return ec;
	}

	private static class EntityCollection {

		private int maxID = 1;
		private ArrayList<EntityEnum> entities = new ArrayList();

		private EntityCollection() {

		}

		private EntityCollection addEntry(EntityEnum e) {
			entities.add(e);
			maxID++;
			return this;
		}

	}

	public static ArrayList<String> getMods() {
		ArrayList<String> ret = new ArrayList();
		List<ModContainer> li = Loader.instance().getActiveModList();
		for (ModContainer mc : li) {
			Object mod = mc.getMod();
			String name = mc.getModId();
			if (mod instanceof DragonAPIMod)
				name = ((DragonAPIMod)mod).getDisplayName();
			else {
				ModList en = ModList.getModFromID(mc.getModId());
				if (en != null) {
					name = en.getDisplayName();
				}
			}
			String ver = mc.getDisplayVersion();
			if (mod instanceof DragonAPIMod) {
				ver = ((DragonAPIMod)mod).getModVersion().toString();
			}
			ret.add(name+": "+ver);
		}
		return ret;
	}

	public static String getActiveLoadingMod() {
		ModContainer mc = Loader.instance().activeModContainer();
		return mc != null ? mc.getName() : "Null";
	}

	public static void instantiateAndRegisterISBRHs(DragonAPIMod mod, ISBRHEnum[] enumr) {
		boolean canLoad = Loader.instance().hasReachedState(LoaderState.INITIALIZATION);// && !Loader.instance().hasReachedState(LoaderState.POSTINITIALIZATION);
		if (!canLoad)
			throw new RegistrationException(mod, "This mod is loading ISBRHs at an invalid time in the loading cycle!");
		for (int i = 0; i < enumr.length; i++) {
			ISBRHEnum r = enumr[i];
			loadISBRH(mod, r);
		}
	}

	private static void loadISBRH(DragonAPIMod mod, ISBRHEnum r) {
		r.setRenderID(RenderingRegistry.getNextAvailableRenderId());
		ISBRH render = constructRenderer(mod, r);
		r.setRenderer(render);
		RenderingRegistry.registerBlockHandler(r.getRenderID(), render);
		if (render.shouldRender3DInInventory(r.getRenderID())) {
			try {
				Method m = r.getRenderClass().getDeclaredMethod("renderInventoryBlock", Block.class, int.class, int.class, RenderBlocks.class);
			}
			catch (NoSuchMethodException e) {
				throw new RegistrationException(mod, "ISBRH "+r+" is invalid - no item render method!", e);
			}
			catch (SecurityException e) {
				throw new RegistrationException(mod, "Error validating ISBRH "+r+"!", e);
			}
		}
	}

	private static ISBRH constructRenderer(DragonAPIMod mod, ISBRHEnum r) {
		Constructor<ISBRH> c;
		try {
			c = (Constructor<ISBRH>)r.getRenderClass().getConstructor(int.class);
			return c.newInstance(r.getRenderID());
		}
		catch (NoSuchMethodException e) {
			throw new RegistrationException(mod, "Could not find constructor for ISBRH "+r+"!", e);
		}
		catch (SecurityException e) {
			throw new RegistrationException(mod, "Disallowed access constructor for ISBRH "+r+"!", e);
		}
		catch (InstantiationException e) {
			throw new RegistrationException(mod, "Could use constructor for ISBRH "+r+"!", e);
		}
		catch (IllegalAccessException e) {
			throw new RegistrationException(mod, "Could not access constructor for ISBRH "+r+"!", e);
		}
		catch (IllegalArgumentException e) {
			throw new RegistrationException(mod, "Illegal args for constructor for ISBRH "+r+"!", e);
		}
		catch (InvocationTargetException e) {
			throw new RegistrationException(mod, "Errored calling constructor for ISBRH "+r+"!", e);
		}
	}
}
