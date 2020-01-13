/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.ItemHandlers;

import java.lang.reflect.Field;

import net.minecraft.block.Block;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;

public class BoPBlockHandler extends ModHandlerBase {

	private static final BoPBlockHandler instance = new BoPBlockHandler();

	public final Block coral1;
	public final Block coral2;

	public final Block flower1;
	public final Block flower2;

	public final Block newGrass;
	public final Block newDirt;

	public final Block foliage;

	private final Block[] basicLeaves = new Block[4];
	private final Block[] colorLeaves = new Block[2];

	public static enum Flower1Types {
		clover,
		swampflower,
		deadbloom,
		glowflower,
		hydrangea,
		cosmos,
		daffodil,
		wildflower,
		violet,
		anemone,
		lilyflower,
		enderlotus,
		bromeliad,
		eyebulbbottom,
		eyebulbtop,
		dandelion
	}

	public static enum Flower2Types {
		hibiscus,
		lilyofthevalley,
		burningblossom,
		lavender,
		goldenrod,
		bluebells,
		minersdelight,
		icyiris,
		rose
	}

	public static enum FoliageTypes {
		duckweed,
		shortgrass,
		mediumgrass,
		flaxbottom,
		bush,
		sprout,
		flaxtop,
		poisonivy,
		berrybush,
		shrub,
		wheatgrass,
		dampgrass,
		koru,
		cloverpatch,
		leafpile,
		deadleafpile
	}

	public static enum LeafTypes {
		yellowautumn(false, 1, 0),
		bamboo(false, 1, 1),
		magic(false, 1, 2),
		dark(false, 1, 3),
		dead(false, 2, 0),
		fir(false, 2, 1),
		ethereal(false, 2, 2),
		orangeautumn(false, 2, 3),
		origin(false, 3, 0),
		pinkcherry(false, 3, 1),
		maple(false, 3, 2),
		whitecherry(false, 3, 3),
		hellbark(false, 4, 0),
		jacaranda(false, 4, 1),

		//colorized
		sacredoak(true, 1, 0),
		mangrove(true, 1, 1),
		palm(true, 1, 2),
		redwood(true, 1, 3),
		willow(true, 2, 0),
		pine(true, 2, 1),
		mahogany(true, 2, 2),
		flowering(true, 2, 3)
		;

		private final boolean isColorized;
		private final int blockIndex;
		private final int blockMeta;

		private LeafTypes(boolean color, int idx, int meta) {
			isColorized = color;
			blockIndex = idx-1;
			blockMeta = meta;
		}

		public BlockKey getBlock() {
			Block b = isColorized ? instance.colorLeaves[blockIndex] : instance.basicLeaves[blockIndex];
			return new BlockKey(b, blockMeta);
		}
	}

	private BoPBlockHandler() {
		super();
		Block idcoral1 = null;
		Block idcoral2 = null;

		Block idflower1 = null;
		Block idflower2 = null;

		Block idgrass = null;
		Block iddirt = null;

		Block idfoliage = null;

		if (this.hasMod()) {
			try {
				Class blocks = this.getMod().getBlockClass();
				Field c1 = blocks.getField("coral1");
				idcoral1 = ((Block)c1.get(null));

				Field c2 = blocks.getField("coral2");
				idcoral2 = ((Block)c2.get(null));

				Field f1 = blocks.getField("flowers");
				idflower1 = ((Block)f1.get(null));

				Field f2 = blocks.getField("flowers2");
				idflower2 = ((Block)f2.get(null));

				Field gr = blocks.getField("newBopGrass");
				Field dt = blocks.getField("newBopDirt");

				idgrass = (Block)gr.get(null);
				iddirt = (Block)dt.get(null);

				Field fol = blocks.getField("foliage");
				idfoliage = ((Block)fol.get(null));

				for (int i = 0; i < basicLeaves.length; i++) {
					Field leaf = blocks.getField("leaves"+(i+1));
					basicLeaves[i] = ((Block)leaf.get(null));
				}

				for (int i = 0; i < basicLeaves.length; i++) {
					Field leaf = blocks.getField("colorizedLeaves"+(i+1));
					colorLeaves[i] = ((Block)leaf.get(null));
				}
			}
			catch (NoSuchFieldException e) {
				DragonAPICore.logError(this.getMod()+" field not found! "+e.getMessage());
				e.printStackTrace();
				this.logFailure(e);
			}
			catch (SecurityException e) {
				DragonAPICore.logError("Cannot read "+this.getMod()+" (Security Exception)! "+e.getMessage());
				e.printStackTrace();
				this.logFailure(e);
			}
			catch (IllegalArgumentException e) {
				DragonAPICore.logError("Illegal argument for reading "+this.getMod()+"!");
				e.printStackTrace();
				this.logFailure(e);
			}
			catch (IllegalAccessException e) {
				DragonAPICore.logError("Illegal access exception for reading "+this.getMod()+"!");
				e.printStackTrace();
				this.logFailure(e);
			}
			catch (NullPointerException e) {
				DragonAPICore.logError("Null pointer exception for reading "+this.getMod()+"! Was the class loaded?");
				e.printStackTrace();
				this.logFailure(e);
			}
		}
		else {
			this.noMod();
		}

		coral1 = idcoral1;
		coral2 = idcoral2;

		flower1 = idflower1;
		flower2 = idflower2;

		newDirt = iddirt;
		newGrass = idgrass;

		foliage = idfoliage;
	}

	public static BoPBlockHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return coral1 != null && coral2 != null && flower1 != null && flower2 != null && foliage != null;
	}

	@Override
	public ModList getMod() {
		return ModList.BOP;
	}

	public boolean isCoral(Block id) {
		return id == coral1 || id == coral2;
	}

	public boolean isFlower(Block id) {
		return id == flower1 || id == flower2;
	}

}
