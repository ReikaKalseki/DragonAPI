/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.DeepInteract;

import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.HashMap;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.ModInteract.ItemHandlers.TinkerToolHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.TinkerToolHandler.ToolParts;
import Reika.DragonAPI.ModInteract.ItemHandlers.TinkerToolHandler.WeaponParts;
import Reika.DragonAPI.ModInteract.RecipeHandlers.SmelteryRecipeHandler;

public class TinkerMaterialHelper {

	private static Method registerMaterial;
	private static Method registerDefaultMaterial;
	private static Method registerChunkMaterial;

	/** Part Builder */
	private static Method registerPartMapping;

	private static Method registerTextures;

	private static Method registerBowMaterial;
	private static Method registerBowstringMaterial;
	private static Method registerArrowMaterial;

	private static Item toolShard;
	private static Item toolRod;

	private static Item arrowhead;
	private static Item bowstring;

	private static Object patternBuilder;
	private static Method registerPatterns;

	public static final TinkerMaterialHelper instance = new TinkerMaterialHelper();

	private final HashMap<String, TinkerMaterial> customMaterialNames = new HashMap();
	private final HashMap<Integer, TinkerMaterial> customMaterialIDs = new HashMap();

	private TinkerMaterialHelper() {
		MinecraftForge.EVENT_BUS.register(this);
	}
	/*
	@ModDependent(ModList.TINKERER)
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void handleToolCrafting(ToolCraftEvent.NormalTool evt) {
		ItemStack out = evt.getResultStack();
		ReikaJavaLibrary.pConsole(out);
		if (out == null)
			return;

		TinkerMaterial mat = customMaterialIDs.get(out.getItemDamage());

		if (mat != null) {
			TinkerToolHandler.Tools tool = TinkerToolHandler.getInstance().getTool(out.getItem());
			if (tool != null) {
				if (!mat.tools.contains(tool)) {
					evt.setResult(Result.DENY);
				}
			}
			else {
				TinkerToolHandler.Weapons weapon = TinkerToolHandler.getInstance().getWeapon(out.getItem());
				if (weapon != null) {
					if (!mat.weapons.contains(weapon)) {
						evt.setResult(Result.DENY);
					}
				}
			}
		}
	}
	 */
	static {
		try {
			Class c = Class.forName("tconstruct.library.TConstructRegistry");

			registerMaterial = c.getDeclaredMethod("addToolMaterial", int.class, String.class, int.class, int.class, int.class, int.class, float.class, int.class, float.class, String.class, int.class);
			registerDefaultMaterial = c.getDeclaredMethod("addDefaultToolPartMaterial", int.class);
			registerChunkMaterial = c.getDeclaredMethod("addDefaultShardMaterial", int.class);

			registerPartMapping = c.getDeclaredMethod("addPartMapping", Item.class, int.class, int.class, ItemStack.class);

			registerBowstringMaterial = c.getDeclaredMethod("addBowstringMaterial", int.class, int.class, ItemStack.class, ItemStack.class, float.class, float.class, float.class, int.class);
			registerBowMaterial = c.getDeclaredMethod("addBowMaterial", int.class, int.class, float.class);
			registerArrowMaterial = c.getDeclaredMethod("addArrowMaterial", int.class, float.class, float.class);
		}
		catch (Exception e) {
			DragonAPICore.logError("Could not load Tool Material Handler!");
			e.printStackTrace();
			ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.TINKERER, e);
		}

		try {
			Class c = Class.forName("tconstruct.library.client.TConstructClientRegistry");

			registerTextures = c.getDeclaredMethod("addMaterialRenderMapping", int.class, String.class, String.class, boolean.class);
		}
		catch (Exception e) {
			DragonAPICore.logError("Could not load Tool Material Handler!");
			e.printStackTrace();
			ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.TINKERER, e);
		}

		try {
			Class c = Class.forName("tconstruct.tools.TinkerTools");

			toolShard = (Item)c.getDeclaredField("toolShard").get(null);
			toolRod = (Item)c.getDeclaredField("toolRod").get(null);
		}
		catch (Exception e) {
			DragonAPICore.logError("Could not load Tool Material Handler!");
			e.printStackTrace();
			ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.TINKERER, e);
		}

		try {
			Class c = Class.forName("tconstruct.weaponry.TinkerWeaponry");

			arrowhead = (Item)c.getDeclaredField("arrowhead").get(null);
			bowstring = (Item)c.getDeclaredField("bowstring").get(null);
		}
		catch (Exception e) {
			DragonAPICore.logError("Could not load Tool Material Handler!");
			e.printStackTrace();
			ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.TINKERER, e);
		}

		try {
			Class c = Class.forName("tconstruct.library.crafting.PatternBuilder");

			patternBuilder = c.getDeclaredField("instance").get(null);
			registerPatterns = c.getDeclaredMethod("registerFullMaterial", ItemStack.class, int.class, String.class, ItemStack.class, ItemStack.class, int.class);
		}
		catch (Exception e) {
			DragonAPICore.logError("Could not load Tool Material Handler!");
			e.printStackTrace();
			ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.TINKERER, e);
		}
	}

	public TinkerMaterial createMaterial(int id, String mod, String name) {
		TinkerMaterial mat = new TinkerMaterial(id, mod, name);
		customMaterialIDs.put(id, mat);
		customMaterialNames.put(name, mat);
		return mat;
	}

	public static class TinkerMaterial {

		public final int id;
		public final String modName;
		public final String materialName;

		/** Pickaxe levels - 0: Wood, 1: Stone, 2: Redstone/Diamond, 3: Obsidian, 4: Cobalt/Ardite, 5: Manyullyn */
		public int harvestLevel = 2;
		public int durability = 300;
		/** Note that this one is x100 larger in code. */
		public int miningSpeed = 800;
		/** Set this to 10 to make the tool unbreakable. */
		public int reinforced = 0;
		/** Durability modifier */
		public float handleModifier = 1;
		/** Negative numbers are "spiny" */
		public float stonebound = 0;
		public int damageBoost = 2;
		public int renderColor = 0xffffff;
		public String chatColor = "";

		//private final EnumSet<TinkerToolHandler.Tools> tools = EnumSet.allOf(TinkerToolHandler.Tools.class);
		//private final EnumSet<TinkerToolHandler.Weapons> weapons = EnumSet.allOf(TinkerToolHandler.Weapons.class);

		private final EnumSet<TinkerToolHandler.ToolParts> toolParts = EnumSet.allOf(TinkerToolHandler.ToolParts.class);
		private final EnumSet<TinkerToolHandler.WeaponParts> weaponParts = EnumSet.allOf(TinkerToolHandler.WeaponParts.class);

		private TinkerMaterial(int id, String mod, String name) {
			this.id = id;
			materialName = name;
			modName = mod;
		}

		public TinkerMaterial setUnbreakable() {
			reinforced = 10;
			return this;
		}

		public TinkerMaterial addToolPart(TinkerToolHandler.ToolParts tool) {
			toolParts.add(tool);
			return this;
		}

		public TinkerMaterial addWeaponPart(TinkerToolHandler.WeaponParts weapon) {
			weaponParts.add(weapon);
			return this;
		}

		public TinkerMaterial disableToolPart(TinkerToolHandler.ToolParts tool) {
			toolParts.remove(tool);
			return this;
		}

		public TinkerMaterial disableWeaponPart(TinkerToolHandler.WeaponParts weapon) {
			weaponParts.remove(weapon);
			return this;
		}

		public TinkerMaterial clearToolParts() {
			toolParts.clear();
			return this;
		}

		public TinkerMaterial clearWeaponParts() {
			weaponParts.clear();
			return this;
		}

		public TinkerMaterial register(boolean shard) {
			try {
				registerMaterial.invoke(null, id, materialName, harvestLevel, durability, miningSpeed, damageBoost, handleModifier, reinforced, stonebound, chatColor, renderColor);
				registerDefaultMaterial.invoke(null, id);
				if (shard)
					registerChunkMaterial.invoke(null, id);
			}
			catch (Exception e) {
				e.printStackTrace();
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.TINKERER, e);
			}
			return this;
		}

		//Have to put textures in TiC subfolder for parts
		public TinkerMaterial registerTexture(String path, boolean prependToolType) {
			try {
				registerTextures.invoke(null, id, modName, path, prependToolType);
			}
			catch (Exception e) {
				e.printStackTrace();
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.TINKERER, e);
			}
			return this;
		}

		public TinkerMaterial registerPatternBuilder(ItemStack input) {
			try {
				registerPatterns.invoke(patternBuilder, input, 2, materialName, new ItemStack(toolShard, 1, id), new ItemStack(toolRod, 1, id), id);

				for (ToolParts p : toolParts) {
					registerPartMapping.invoke(null, TinkerToolHandler.getInstance().toolWoodPattern, p.castMeta, id, p.getItem(id));
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.TINKERER, e);
			}
			return this;
		}

		public TinkerMaterial registerSmelteryCasting(ItemStack input, Fluid molten, int temp, ItemStack render) {
			int base = SmelteryRecipeHandler.INGOT_AMOUNT;
			SmelteryRecipeHandler.addIngotMelting(input, render, temp, molten);
			SmelteryRecipeHandler.addIngotCasting(input, molten, 40);

			for (ToolParts p : toolParts) {
				ItemStack cast = p.getCast();
				ItemStack item = p.getItem(id);
				SmelteryRecipeHandler.addReversibleCasting(cast, item, render, temp, molten, (int)(base*p.ingotCost), 80);
			}

			for (WeaponParts p : weaponParts) {
				ItemStack cast = p.getCast();
				ItemStack item = p.getItem(id);
				SmelteryRecipeHandler.addReversibleCasting(cast, item, render, temp, molten, (int)(base*p.ingotCost), 80);
			}

			return this;
		}

		public TinkerMaterial registerWeapons(ItemStack input, int drawSpeed, float drawSpeedMult, float arrowMass, float flightSpeed, float maxSpeed, float breakChance) {
			try {
				for (WeaponParts p : weaponParts)
					registerPartMapping.invoke(null, TinkerToolHandler.getInstance().weaponWoodPattern, p.castMeta, id, p.getItem(id));

				registerBowstringMaterial.invoke(null, 1, 2, input, new ItemStack(bowstring, 1, 1), durability, drawSpeedMult, flightSpeed, renderColor);
				registerBowMaterial.invoke(null, id, drawSpeed, maxSpeed);
				registerArrowMaterial.invoke(null, id, arrowMass, reinforced == 10 ? 0 : breakChance);
			}
			catch (Exception e) {
				e.printStackTrace();
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.TINKERER, e);
			}
			return this;
		}

	}

}
