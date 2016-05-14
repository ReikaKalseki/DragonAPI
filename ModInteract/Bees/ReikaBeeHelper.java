/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.Bees;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.Bees.AlleleRegistry.BeeGene;
import Reika.DragonAPI.ModInteract.Bees.AlleleRegistry.Effect;
import Reika.DragonAPI.ModInteract.Bees.AlleleRegistry.Fertility;
import Reika.DragonAPI.ModInteract.Bees.AlleleRegistry.Flower;
import Reika.DragonAPI.ModInteract.Bees.AlleleRegistry.Flowering;
import Reika.DragonAPI.ModInteract.Bees.AlleleRegistry.Life;
import Reika.DragonAPI.ModInteract.Bees.AlleleRegistry.Speeds;
import Reika.DragonAPI.ModInteract.Bees.AlleleRegistry.Territory;
import Reika.DragonAPI.ModInteract.Bees.AlleleRegistry.Tolerance;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeRoot;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IIndividual;


public class ReikaBeeHelper {

	public static final ItemStack getBeeItem(String bee, EnumBeeType type) {
		return getBeeItem(ReikaWorldHelper.getBasicReferenceWorld(), bee, type);
	}

	public static final ItemStack getBeeItem(World world, String bee, EnumBeeType type) {
		IBeeRoot root = BeeManager.beeRoot;
		return root.getMemberStack(root.getBee(world, root.templateAsGenome(root.getTemplate(bee))), type.ordinal());
	}

	public static void analyzeBee(ItemStack is) {
		if (is != null) {
			IIndividual bee = AlleleManager.alleleRegistry.getIndividual(is);
			//ReikaJavaLibrary.pConsole(bee);
			if (bee != null) {
				bee.analyze();
				saveBee(bee, is);
			}
		}
	}

	public static void setPristine(ItemStack is, boolean flag) {
		IIndividual bee = AlleleManager.alleleRegistry.getIndividual(is);
		if (bee instanceof IBee) {
			((IBee)bee).setIsNatural(flag);
			saveBee(bee, is);
		}
	}

	private static void saveBee(IIndividual bee, ItemStack is) {
		NBTTagCompound tag = new NBTTagCompound();
		bee.writeToNBT(tag);
		is.stackTagCompound = tag;
	}

	private static void saveBee(IGenome bee, ItemStack is) {
		NBTTagCompound tag = new NBTTagCompound();
		bee.writeToNBT(tag);
		if (is.stackTagCompound == null)
			is.stackTagCompound = new NBTTagCompound();
		is.stackTagCompound.setTag("Genome", tag);
	}

	public static int getToleranceValue(EnumTolerance t) {
		return t == EnumTolerance.NONE ? 0 : ReikaMathLibrary.getWithinBoundsElse(Character.getNumericValue(t.name().charAt(t.name().length()-1)), 0, 5, 0);
	}

	public static Tolerance getToleranceType(EnumTolerance t) {
		if (t == EnumTolerance.NONE)
			return Tolerance.NONE;
		String s = t.name().substring(0, t.name().indexOf('_')+1);
		return Tolerance.valueOf(s);
	}

	public static EnumTolerance getOneBetterTolerance(EnumTolerance t) {
		Tolerance type = getToleranceType(t);
		int val = getToleranceValue(t);
		if (val > 0 && val < 5) {
			val++;
			return getTolerance(type, val);
		}
		return null;
	}

	public static EnumTolerance getTolerance(Tolerance type, int val) {
		String s = val > 0 ? type.name()+"_"+val : type.name();
		return EnumTolerance.valueOf(s);
	}

	public static void setGene(ItemStack queen, IBeeGenome ibg, EnumBeeChromosome gene, IAllele value, boolean inactive) {
		boolean ana = AlleleManager.alleleRegistry.getIndividual(queen).isAnalyzed();
		IChromosome[] ic = ibg.getChromosomes();
		IAllele[] arr = BeeManager.beeRoot.getDefaultTemplate();
		IAllele[] arr2 = BeeManager.beeRoot.getDefaultTemplate();
		for (int i = 0; i < arr.length; i++) {
			if (ic[i] != null) {
				arr[i] = ic[i].getActiveAllele();
				arr2[i] = ic[i].getInactiveAllele();
			}
		}
		arr[gene.ordinal()] = value;
		if (inactive) {
			arr2[gene.ordinal()] = value;
		}
		IBeeGenome repl = BeeManager.beeRoot.templateAsGenome(arr, arr2);
		saveBee(repl, queen);
		if (ana)
			analyzeBee(queen);
	}

	public static BeeGene getGeneEnum(EnumBeeChromosome gene, IBeeGenome ibg) {
		switch(gene) {
			case EFFECT:
				return AlleleRegistry.getEnum(ibg.getActiveAllele(gene), Effect.class);
			case FERTILITY:
				return AlleleRegistry.getEnum(ibg.getActiveAllele(gene), Fertility.class);
			case FLOWERING:
				return AlleleRegistry.getEnum(ibg.getActiveAllele(gene), Flowering.class);
			case FLOWER_PROVIDER:
				return AlleleRegistry.getEnum(ibg.getActiveAllele(gene), Flower.class);
			case HUMIDITY_TOLERANCE:
				return getToleranceType(ibg.getToleranceHumid());
			case TEMPERATURE_TOLERANCE:
				return getToleranceType(ibg.getToleranceTemp());
			case LIFESPAN:
				return AlleleRegistry.getEnum(ibg.getActiveAllele(gene), Life.class);
			case SPEED:
				return AlleleRegistry.getEnum(ibg.getActiveAllele(gene), Speeds.class);
			case TERRITORY:
				return AlleleRegistry.getEnum(ibg.getActiveAllele(gene), Territory.class);
			default:
				return null;
		}
	}

	public static IAllele getToleranceGene(EnumTolerance t) {
		return getToleranceGene(getToleranceType(t), getToleranceValue(t));
	}

	public static IAllele getToleranceGene(Tolerance d, int i) {
		String s = i > 0 ? String.format("%s%d", d.tag, Math.min(Math.abs(i), 5)) : Tolerance.NONE.tag;
		return AlleleManager.alleleRegistry.getAllele(s);
	}

	public static void runProductionCycle(IBeeHousing ibh) { //skips frame damage, other checks, and pollination
		//ibh.getBeekeepingLogic().doWork();
		if (ibh.getBeekeepingLogic().canWork()) {
			ItemStack is = ibh.getBeeInventory().getQueen();
			if (is != null) {
				IIndividual bee = AlleleManager.alleleRegistry.getIndividual(is);
				if (bee instanceof IBee) {
					ItemStack[] ret = ((IBee)bee).produceStacks(ibh);
					if (ret != null) {
						for (int i = 0; i < ret.length; i++) {
							ItemStack in = ret[i];
							ibh.getBeeInventory().addProduct(in, false);
						}
					}
				}
			}
		}
	}

	public static void setBeeMate(IBee ii, IBee repl) {
		ii.mate(repl);
	}

}
