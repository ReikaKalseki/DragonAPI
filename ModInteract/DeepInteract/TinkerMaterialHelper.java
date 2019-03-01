/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.DeepInteract;

import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fluids.Fluid;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.NEI_DragonAPI_Config;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Exception.IDConflictException;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.TinkerBlockHandler.Pulses;
import Reika.DragonAPI.ModInteract.ItemHandlers.TinkerToolHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.TinkerToolHandler.ToolPartType;
import Reika.DragonAPI.ModInteract.ItemHandlers.TinkerToolHandler.ToolParts;
import Reika.DragonAPI.ModInteract.ItemHandlers.TinkerToolHandler.Tools;
import Reika.DragonAPI.ModInteract.ItemHandlers.TinkerToolHandler.WeaponParts;
import Reika.DragonAPI.ModInteract.RecipeHandlers.SmelteryRecipeHandler;
import Reika.DragonAPI.ModRegistry.InterfaceCache;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

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
	private static Method registerRepairMaterial;

	public static final TinkerMaterialHelper instance = new TinkerMaterialHelper();

	private final HashMap<String, AbstractMaterial> materialNames = new HashMap();
	private final HashMap<Integer, AbstractMaterial> materialIDs = new HashMap();

	private TinkerMaterialHelper() {
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void breakDisallowedTools(BlockEvent.BreakEvent evt) { //Breaks tools cheated in with uncraftable parts
		if (Pulses.TOOLS.isLoaded()) {
			EntityPlayer ep = evt.getPlayer();
			ItemStack tool = ep.getCurrentEquippedItem();
			try {
				if (tool != null && InterfaceCache.TINKERTOOL.instanceOf(tool.getItem())) {
					Tools t = TinkerToolHandler.getInstance().getTool(tool.getItem());
					if (t != null) {
						for (int i = 0; i < ToolPartType.types.length; i++) {
							ToolPartType type = ToolPartType.types[i];
							ToolParts p = TinkerToolHandler.getInstance().getPart(t, type);
							if (p != null) {
								int id = TinkerToolHandler.getInstance().getToolMaterial(tool, type);
								AbstractMaterial mat = materialIDs.get(id);
								if (mat instanceof CustomTinkerMaterial) {
									CustomTinkerMaterial cm = (CustomTinkerMaterial)mat;
									if (cm.enforceNoCheating && !cm.toolParts.contains(p)) { //forbidden part, had to be spawned in
										ReikaSoundHelper.playSoundFromServer(ep.worldObj, ep.posX, ep.posY, ep.posZ, "random.break", 2, 1, true);
										ep.attackEntityFrom(DamageSource.generic, 1);
										ep.setCurrentItemOrArmor(0, null);
										evt.setCanceled(true);
									}
								}
							}
						}
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
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

			instance.addNativeToolMaterial(0, "Wood", 1, 97, 350, 0, 1.0F, 0, 0.0F, EnumChatFormatting.YELLOW.toString(), 7690273);
			instance.addNativeToolMaterial(1, "Stone", 1, 131, 400, 1, 0.5F, 0, 1.0F, EnumChatFormatting.GRAY.toString(), 8355711);
			instance.addNativeToolMaterial(2, "Iron", 2, 250, 600, 2, 1.3F, 1, 0.0F, EnumChatFormatting.WHITE.toString(), 14342874);
			instance.addNativeToolMaterial(3, "Flint", 1, 171, 525, 2, 0.7F, 0, 0.0F, EnumChatFormatting.DARK_GRAY.toString(), 4737096);
			instance.addNativeToolMaterial(4, "Cactus", 1, 150, 500, 2, 1.0F, 0, -1.0F, EnumChatFormatting.DARK_GREEN.toString(), 1206539);
			instance.addNativeToolMaterial(5, "Bone", 1, 200, 400, 1, 1.0F, 0, 0.0F, EnumChatFormatting.YELLOW.toString(), 15592394);
			instance.addNativeToolMaterial(6, "Obsidian", 3, 89, 700, 2, 0.8F, 3, 0.0F, EnumChatFormatting.LIGHT_PURPLE.toString(), 11173877);
			instance.addNativeToolMaterial(7, "Netherrack", 2, 131, 400, 1, 1.2F, 0, 1.0F, EnumChatFormatting.DARK_RED.toString(), 8598072);
			instance.addNativeToolMaterial(8, "Slime", 0, 500, 150, 0, 1.5F, 0, 0.0F, EnumChatFormatting.GREEN.toString(), 7254117);
			instance.addNativeToolMaterial(9, "Paper", 0, 30, 200, 0, 0.3F, 0, 0.0F, EnumChatFormatting.WHITE.toString(), 16777215);
			instance.addNativeToolMaterial(10, "Cobalt", 4, 800, 1400, 3, 1.75F, 2, 0.0F, EnumChatFormatting.DARK_AQUA.toString(), 2324189);
			instance.addNativeToolMaterial(11, "Ardite", 4, 500, 800, 3, 2.0F, 0, 2.0F, EnumChatFormatting.DARK_RED.toString(), 10825728);
			instance.addNativeToolMaterial(12, "Manyullyn", 5, 1200, 900, 4, 2.5F, 0, 0.0F, EnumChatFormatting.DARK_PURPLE.toString(), 7551141);
			instance.addNativeToolMaterial(13, "Copper", 1, 180, 500, 2, 1.15F, 0, 0.0F, EnumChatFormatting.RED.toString(), 13394960);
			instance.addNativeToolMaterial(14, "Bronze", 2, 550, 800, 2, 1.3F, 1, 0.0F, EnumChatFormatting.GOLD.toString(), 13277526);
			instance.addNativeToolMaterial(15, "Alumite", 4, 700, 800, 3, 1.3F, 2, 0.0F, EnumChatFormatting.LIGHT_PURPLE.toString(), 16754665);
			instance.addNativeToolMaterial(16, "Steel", 4, 750, 1000, 4, 1.3F, 2, 0.0F, EnumChatFormatting.GRAY.toString(), 10526880);
			instance.addNativeToolMaterial(17, "BlueSlime", 0, 1200, 150, 0, 2.0F, 0, 0.0F, EnumChatFormatting.AQUA.toString(), 6729392);
			instance.addNativeToolMaterial(18, "PigIron", 3, 250, 600, 2, 1.3F, 1, 0.0F, EnumChatFormatting.RED.toString(), 15771812);

			if (ModList.THAUMCRAFT.isLoaded())
				instance.addNativeToolMaterial(31, "Thaumium", 3, 400, 700, 2, 1.3F, 0, 0.0F, "§5", 5325692);
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
			registerRepairMaterial = c.getDeclaredMethod("registerFullMaterial", ItemStack.class, int.class, String.class, ItemStack.class, ItemStack.class, int.class);
		}
		catch (Exception e) {
			DragonAPICore.logError("Could not load Tool Material Handler!");
			e.printStackTrace();
			ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.TINKERER, e);
		}
	}

	public CustomTinkerMaterial createMaterial(int id, DragonAPIMod mod, String name) {
		AbstractMaterial prev = materialIDs.get(id);
		if (prev != null)
			throw new IDConflictException(mod, "Tool material ID "+id+" is already occupied by '"+prev.materialName+"'!");
		CustomTinkerMaterial mat = new CustomTinkerMaterial(id, mod, name);

		if (!Pulses.TOOLS.isLoaded())
			mat.clearToolParts();
		if (!Pulses.WEAPONS.isLoaded())
			mat.clearWeaponParts();

		mat.disableWeaponPart(WeaponParts.FLETCHING).disableWeaponPart(WeaponParts.BOWSTRING); //technical

		materialIDs.put(id, mat);
		materialNames.put(name, mat);
		return mat;
	}

	private void addNativeToolMaterial(int id, String name, int harvestLevel, int dura, int speed, int attack, float handleModifier, int reinforced, float stonebound, String chat, int color) {
		NativeTinkerMaterial mat = new NativeTinkerMaterial(id, name, harvestLevel, dura, speed, attack, handleModifier, reinforced, stonebound, chat, color);

		materialIDs.put(id, mat);
		materialNames.put(name, mat);
	}

	public int getMaterialID(String name) {
		AbstractMaterial mat = materialNames.get(name);
		return mat != null ? mat.id : null;
	}

	public boolean isPartEnabled(int id, ToolParts p) {
		AbstractMaterial mat = materialIDs.get(id);
		return mat instanceof CustomTinkerMaterial ? ((CustomTinkerMaterial)mat).toolParts.contains(p) : true;
	}

	public boolean isPartEnabled(int id, WeaponParts p) {
		AbstractMaterial mat = materialIDs.get(id);
		return mat instanceof CustomTinkerMaterial ? ((CustomTinkerMaterial)mat).weaponParts.contains(p) : true;
	}

	private abstract static class AbstractMaterial {

		public final int id;
		public final String materialName;

		protected AbstractMaterial(int id, String name) {
			this.id = id;
			materialName = name;
		}
	}

	public static class NativeTinkerMaterial extends AbstractMaterial {

		public final int harvestLevel;
		public final int durability;
		public final int miningSpeed;
		public final int reinforced;
		public final float handleModifier;
		public final float stonebound;
		public final int damageBoost;
		public final int renderColor;
		public final String chatColor;

		private NativeTinkerMaterial(int id, String name, int harvestLevel, int dura, int speed, int attack, float handleModifier, int reinforced, float stonebound, String chat, int color) {
			super(id, name);
			this.harvestLevel = harvestLevel;
			miningSpeed = speed;
			durability = dura;
			this.reinforced = reinforced;
			this.handleModifier = handleModifier;
			this.stonebound = stonebound;
			damageBoost = attack;
			renderColor = color;
			chatColor = chat;
		}

	}

	public static class CustomTinkerMaterial extends AbstractMaterial {

		public final DragonAPIMod mod;

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

		private boolean enforceNoCheating = false;

		//private final EnumSet<TinkerToolHandler.Tools> tools = EnumSet.allOf(TinkerToolHandler.Tools.class);
		//private final EnumSet<TinkerToolHandler.Weapons> weapons = EnumSet.allOf(TinkerToolHandler.Weapons.class);

		private final EnumSet<TinkerToolHandler.ToolParts> toolParts = EnumSet.allOf(TinkerToolHandler.ToolParts.class);
		private final EnumSet<TinkerToolHandler.WeaponParts> weaponParts = EnumSet.allOf(TinkerToolHandler.WeaponParts.class);

		private CustomTinkerMaterial(int id, DragonAPIMod mod, String name) {
			super(id, name);
			this.mod = mod;
		}

		public CustomTinkerMaterial setUnbreakable() {
			reinforced = 10;
			return this;
		}

		public CustomTinkerMaterial setDisallowCheatedParts() {
			enforceNoCheating = true;
			return this;
		}

		public CustomTinkerMaterial addToolPart(TinkerToolHandler.ToolParts tool) {
			toolParts.add(tool);
			return this;
		}

		public CustomTinkerMaterial addWeaponPart(TinkerToolHandler.WeaponParts weapon) {
			weaponParts.add(weapon);
			return this;
		}

		public CustomTinkerMaterial disableToolPart(TinkerToolHandler.ToolParts tool) {
			toolParts.remove(tool);
			return this;
		}

		public CustomTinkerMaterial disableWeaponPart(TinkerToolHandler.WeaponParts weapon) {
			weaponParts.remove(weapon);
			return this;
		}

		public CustomTinkerMaterial clearToolParts() {
			toolParts.clear();
			return this;
		}

		public CustomTinkerMaterial clearWeaponParts() {
			weaponParts.clear();
			return this;
		}

		public CustomTinkerMaterial register(boolean shard) {
			try {
				registerMaterial.invoke(null, id, materialName, harvestLevel, durability, miningSpeed, damageBoost, handleModifier, reinforced, stonebound, chatColor, renderColor);
				registerDefaultMaterial.invoke(null, id);
				if (shard)
					registerChunkMaterial.invoke(null, id);

				if (ModList.NEI.isLoaded()) {
					for (int i = 0; i < ToolParts.partList.length; i++) {
						ToolParts p = ToolParts.partList[i];
						if (!toolParts.contains(p)) {
							ItemStack is = p.getItem(id);
							if (is != null)
								NEI_DragonAPI_Config.hideItem(is);
						}
					}
					for (int i = 0; i < WeaponParts.partList.length; i++) {
						WeaponParts p = WeaponParts.partList[i];
						if (!weaponParts.contains(p)) {
							ItemStack is = p.getItem(id);
							if (is != null)
								NEI_DragonAPI_Config.hideItem(is);
						}
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.TINKERER, e);
			}
			return this;
		}

		//Have to put textures in TiC subfolder for parts
		public CustomTinkerMaterial registerTexture(String path, boolean prependToolType) {
			try {
				registerTextures.invoke(null, id, mod.getDisplayName(), path, prependToolType);
			}
			catch (Exception e) {
				e.printStackTrace();
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.TINKERER, e);
			}
			return this;
		}

		public CustomTinkerMaterial registerRepairMaterial(ItemStack input) {
			try {
				registerRepairMaterial.invoke(patternBuilder, input, 2, materialName, new ItemStack(toolShard, 1, id), new ItemStack(toolRod, 1, id), id);
			}
			catch (Exception e) {
				e.printStackTrace();
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.TINKERER, e);
			}
			return this;
		}

		public CustomTinkerMaterial registerPatternBuilder(ItemStack input) {
			try {
				for (ToolParts p : toolParts) {
					registerPartMapping.invoke(null, TinkerToolHandler.getInstance().toolWoodPattern, p.castMeta, id, p.getItem(id));
				}
				for (WeaponParts p : weaponParts) {
					registerPartMapping.invoke(null, TinkerToolHandler.getInstance().weaponWoodPattern, p.castMeta, id, p.getItem(id));
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.TINKERER, e);
			}
			return this;
		}

		public CustomTinkerMaterial registerSmelteryCasting(ItemStack input, Fluid molten, int temp, ItemStack render) {
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

		public CustomTinkerMaterial registerWeapons(ItemStack input, int drawSpeed, float drawSpeedMult, float arrowMass, float flightSpeed, float maxSpeed, float breakChance) {
			try {
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
