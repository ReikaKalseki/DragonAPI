/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class AppEngHandler extends ModHandlerBase {

	private static final AppEngHandler instance = new AppEngHandler();

	private ItemStack certus;
	private ItemStack dust;
	private ItemStack silicon;

	private ItemStack basicChip;
	private ItemStack advChip;
	private ItemStack basicChipPattern;
	private ItemStack advChipPattern;

	private ItemStack siliconPress;
	private ItemStack logicPress;
	private ItemStack calcPress;
	private ItemStack engPress;

	private ItemStack goldProcessor;
	private ItemStack quartzProcessor;
	private ItemStack diamondProcessor;

	public final Block skystone;
	public final Block quartzGlass;

	private Object itemList;
	private Object matList;
	private Object blockList;
	private Object partList;

	private Class itemClass;
	private Class matClass;
	private Class blockClass;
	private Class partClass;

	private Method itemGet;
	private Method itemstackGet;
	private Method blockGet;

	private AppEngHandler() {
		super();
		Block sky = null;
		Block glass = null;
		if (this.hasMod()) {
			try {
				this.initGetters();

				certus = this.getMaterial("materialCertusQuartzCrystal");
				dust = this.getMaterial("materialCertusQuartzDust");
				silicon = this.getMaterial("materialSilicon");

				basicChip = this.getMaterial("materialBasicCard");
				advChip = this.getMaterial("materialAdvCard");
				basicChipPattern = this.getMaterial("materialBasicCard");
				advChipPattern = this.getMaterial("materialAdvCard");

				calcPress = this.getMaterial("materialCalcProcessorPress");
				engPress = this.getMaterial("materialEngProcessorPress");
				logicPress = this.getMaterial("materialLogicProcessorPress");
				siliconPress = this.getMaterial("materialSiliconPress");

				goldProcessor = this.getMaterial("materialLogicProcessor");
				quartzProcessor = this.getMaterial("materialCalcProcessor");
				diamondProcessor = this.getMaterial("materialEngProcessor");

				sky = this.getBlock("blockSkyStone");
				glass = this.getBlock("blockQuartzGlass");
			}
			catch (Exception e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Cannot read AE class contents!");
				e.printStackTrace();
			}
		}
		else {
			this.noMod();
		}
		skystone = sky;
		quartzGlass = glass;
	}

	private void initGetters() throws Exception {
		Class ae = Class.forName("appeng.core.Api");
		Field instance = ae.getField("instance");
		Object inst = instance.get(null);

		Field b = ae.getDeclaredField("blocks");
		Field i = ae.getDeclaredField("items");
		Field p = ae.getDeclaredField("parts");
		Field m = ae.getDeclaredField("materials");

		b.setAccessible(true);
		i.setAccessible(true);
		p.setAccessible(true);
		m.setAccessible(true);

		partList = p.get(inst);
		itemList = i.get(inst);
		blockList = b.get(inst);
		matList = m.get(inst);

		Class def = Class.forName("appeng.api.util.AEItemDefinition");

		itemGet = def.getMethod("item");
		blockGet = def.getMethod("block");
		itemstackGet = def.getMethod("stack", int.class);

		partClass = Class.forName("appeng.api.definitions.Parts");
		itemClass = Class.forName("appeng.api.definitions.Items");
		blockClass = Class.forName("appeng.api.definitions.Blocks");
		matClass = Class.forName("appeng.api.definitions.Materials");
	}

	private Block getBlock(String field) throws Exception {
		Field f = blockClass.getField(field);
		Object def = f.get(blockList);
		return (Block)blockGet.invoke(def);
	}

	private Item getItem(String field) throws Exception {
		Field f = itemClass.getField(field);
		Object def = f.get(itemList);
		return (Item)itemGet.invoke(def);
	}

	private ItemStack getMaterial(String field) throws Exception {
		Field f = matClass.getField(field);
		Object def = f.get(matList);
		return (ItemStack)itemstackGet.invoke(def, 1);
	}

	private ItemStack getPart(String field) throws Exception {
		Field f = partClass.getField(field);
		Object def = f.get(partList);
		return (ItemStack)itemstackGet.invoke(def, 1);
	}

	public static AppEngHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return certus != null && dust != null && skystone != null;
	}

	@Override
	public ModList getMod() {
		return ModList.APPENG;
	}

	public ItemStack getCertusQuartz() {
		return certus != null ? certus.copy() : null;
	}

	public ItemStack getCertusQuartzDust() {
		return dust != null ? dust.copy() : null;
	}

	public Collection<ItemStack> getPossibleMeteorChestLoot() {
		ArrayList<ItemStack> li = new ArrayList();
		li.add(calcPress);
		li.add(engPress);
		li.add(logicPress);
		li.add(siliconPress);
		return li;
	}

	public Collection<ItemStack> getMeteorChestLoot() {
		ArrayList<ItemStack> li = new ArrayList();
		int n = 1+rand.nextInt(3);
		for (int i = 0; i < n; i++) {
			switch (rand.nextInt(4)) {
			case 0:
				li.add(calcPress);
				break;
			case 1:
				li.add(engPress);
				break;
			case 2:
				li.add(logicPress);
				break;
			case 3:
				li.add(siliconPress);
				break;
			}
		}
		return li;
	}

	public ItemStack getBasicChipPattern() {
		return basicChipPattern != null ? basicChipPattern.copy() : null;
	}

	public ItemStack getAdvancedChipPattern() {
		return advChipPattern != null ? advChipPattern.copy() : null;
	}

	public ItemStack getSilicon() {
		return silicon != null ? silicon.copy() : null;
	}

	public ItemStack getGoldProcessor() {
		return goldProcessor != null ? goldProcessor.copy() : null;
	}

	public ItemStack getQuartzProcessor() {
		return quartzProcessor != null ? quartzProcessor.copy() : null;
	}

	public ItemStack getDiamondProcessor() {
		return diamondProcessor != null ? diamondProcessor.copy() : null;
	}

}
