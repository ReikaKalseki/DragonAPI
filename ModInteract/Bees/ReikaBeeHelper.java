/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.Bees;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.text.WordUtils;

import com.mojang.authlib.GameProfile;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Instantiable.AI.AITaskSeekLocation;
import Reika.DragonAPI.Instantiable.Data.SphericalVector;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Interfaces.EntityPathfinder;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.BeeGene;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.Effect;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.Fertility;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.Flower;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.Flowering;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.Life;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.Speeds;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.Territory;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.Tolerance;
import Reika.DragonAPI.ModInteract.Bees.BeeEvent.BeeSetHealthEvent;
import Reika.DragonAPI.ModInteract.Bees.ButterflyAlleleRegistry.Size;
import Reika.DragonAPI.ModInteract.Bees.TreeAlleleRegistry.Sappiness;
import Reika.DragonAPI.ModInteract.Bees.TreeAlleleRegistry.Yield;
import Reika.DragonAPI.ModInteract.Bees.TreeSpecies.NoLocaleDescriptionFruit;
import Reika.DragonAPI.ModInteract.ItemHandlers.ForestryHandler;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.IBeeRoot;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.arboriculture.IAlleleGrowth;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.arboriculture.ITreeRoot;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleArea;
import forestry.api.genetics.IAlleleBoolean;
import forestry.api.genetics.IAlleleFloat;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IAllelePlantType;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IAlleleTolerance;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.ISpeciesRoot;
import forestry.api.genetics.ISpeciesType;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.EnumButterflyChromosome;
import forestry.api.lepidopterology.EnumFlutterType;
import forestry.api.lepidopterology.IAlleleButterflySpecies;
import forestry.api.lepidopterology.IButterfly;
import forestry.api.lepidopterology.IButterflyGenome;
import forestry.api.lepidopterology.IButterflyRoot;
import forestry.api.lepidopterology.IEntityButterfly;
import forestry.api.multiblock.IAlvearyController;


public class ReikaBeeHelper {

	private static Field beeHealth;
	private static final HashSet<String> allBees = new HashSet();

	private static final HashMap<ChunkCoordinates, CachedTerritory> territoryCache = new HashMap();

	private static Class geneTemplate;
	private static Method addSample;
	private static Class geneSample;
	private static Constructor geneSampleCtr;
	private static Class geneSampleItem;
	private static Method geneSampleGetItem;

	private static Method setTreeLeaf;
	private static Method setTreeLeafOwner;
	private static Method getTreeLeaf;
	private static Method treeHasFruit;
	private static Method updatePacket;
	private static Field deco;
	private static Field treeRipeness;
	private static Field treeRipeTime;

	private static Class butterflyStateEnum;
	private static Class butterfly;
	private static Field butterflyState;
	private static Field butterflyTarget;

	static {
		if (ModList.FORESTRY.isLoaded()) {
			try {
				beeHealth = Class.forName("forestry.core.genetics.IndividualLiving").getDeclaredField("health");
				beeHealth.setAccessible(true);
			}
			catch (Exception e) {
				DragonAPICore.logError("Could not find forestry bee life parameter!");
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.FORESTRY, e);
				e.printStackTrace();
			}

			try {
				butterflyStateEnum = Class.forName("forestry.lepidopterology.entities.EnumButterflyState");
				butterfly = Class.forName("forestry.lepidopterology.entities.EntityButterfly");
				butterflyState = butterfly.getDeclaredField("state");
				butterflyState.setAccessible(true);
				butterflyTarget = butterfly.getDeclaredField("flightTarget");
				butterflyTarget.setAccessible(true);
			}
			catch (Exception e) {
				DragonAPICore.logError("Could not find forestry butterfly parameters!");
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.FORESTRY, e);
				e.printStackTrace();
			}

			try {
				Class c = Class.forName("forestry.arboriculture.tiles.TileTreeContainer");
				setTreeLeaf = c.getDeclaredMethod("setTree", ITree.class);
				setTreeLeaf.setAccessible(true);
				setTreeLeafOwner = c.getDeclaredMethod("setOwner", GameProfile.class);
				setTreeLeafOwner.setAccessible(true);
				getTreeLeaf = c.getDeclaredMethod("getTree");
				getTreeLeaf.setAccessible(true);
				c = Class.forName("forestry.arboriculture.tiles.TileLeaves");
				deco = c.getDeclaredField("isDecorative");
				deco.setAccessible(true);
				treeRipeness = c.getDeclaredField("ripeningTime");
				treeRipeness.setAccessible(true);
				treeRipeTime = c.getDeclaredField("ripeningPeriod");
				treeRipeTime.setAccessible(true);
				treeHasFruit = c.getDeclaredMethod("hasFruit");
				treeHasFruit.setAccessible(true);
				updatePacket = c.getDeclaredMethod("sendNetworkUpdateRipening");
				updatePacket.setAccessible(true);
			}
			catch (Exception e) {
				DragonAPICore.logError("Could not find forestry leaf parameters!");
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.FORESTRY, e);
				e.printStackTrace();
			}
		}

		if (ModList.GENDUSTRY.isLoaded()) {
			try {
				geneSample = Class.forName("net.bdew.gendustry.forestry.GeneSampleInfo");
				geneSampleCtr = geneSample.getDeclaredConstructor(ISpeciesRoot.class, int.class, IAllele.class);
				geneSampleCtr.setAccessible(true);

				geneTemplate = Class.forName("net.bdew.gendustry.items.GeneTemplate$");
				addSample = geneTemplate.getDeclaredMethod("addSample", ItemStack.class, geneSample);
				addSample.setAccessible(true);

				geneSampleItem = Class.forName("net.bdew.gendustry.items.GeneSample$");
				geneSampleGetItem = geneSampleItem.getDeclaredMethod("newStack", geneSample);
				geneSampleGetItem.setAccessible(true);
			}
			catch (Exception e) {
				DragonAPICore.logError("Could not find GenDustry sample data!");
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.GENDUSTRY, e);
				e.printStackTrace();
			}
		}
	}

	public static void buildSpeciesList() {
		allBees.clear();
		for (IAllele ia : AlleleManager.alleleRegistry.getRegisteredAlleles(EnumBeeChromosome.SPECIES)) {
			if (ia != null && ia.getUID() != null) //because someone is being stupid
				allBees.add(ia.getUID());
		}
	}

	public static Set<String> getAllBeeSpecies() {
		if (allBees.isEmpty())
			buildSpeciesList();
		return Collections.unmodifiableSet(allBees);
	}

	public static String getRandomBeeSpecies() {
		return ReikaJavaLibrary.getRandomCollectionEntry(DragonAPICore.rand, getAllBeeSpecies());
	}

	public static final ItemStack getBeeItem(String bee, EnumBeeType type) {
		return getBeeItem(ReikaWorldHelper.getBasicReferenceWorld(), bee, type);
	}

	public static final ItemStack getBeeItem(World world, String bee, EnumBeeType type) {
		IBeeRoot root = BeeManager.beeRoot;
		return root.getMemberStack(root.getBee(world, root.templateAsGenome(root.getTemplate(bee))), type.ordinal());
	}

	public static final ItemStack getTreeItem(String Tree, EnumGermlingType type) {
		return getTreeItem(ReikaWorldHelper.getBasicReferenceWorld(), Tree, type);
	}

	public static final ItemStack getTreeItem(World world, String Tree, EnumGermlingType type) {
		ITreeRoot root = TreeManager.treeRoot;
		return root.getMemberStack(root.getTree(world, root.templateAsGenome(root.getTemplate(Tree))), type.ordinal());
	}

	public static final ItemStack getButterflyItem(String Butterfly, EnumFlutterType type) {
		IButterflyRoot root = ButterflyManager.butterflyRoot;
		return root.getMemberStack(root.templateAsIndividual(root.getTemplate(Butterfly)), type.ordinal());
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

	public static void ageBee(World world, ItemStack is, float modifier) {
		if (is != null) {
			IIndividual bee = AlleleManager.alleleRegistry.getIndividual(is);
			if (bee instanceof IBee) {
				((IBee)bee).age(world, modifier);
				saveBee(bee, is);
			}
		}
	}

	public static void rejuvenateBee(IAlvearyController iac, ItemStack is) {
		if (is != null) {
			IIndividual bee = AlleleManager.alleleRegistry.getIndividual(is);
			if (bee instanceof IBee) {
				int max = ((IBee)bee).getMaxHealth();
				setBeeHealth(iac, (IBee)bee, max);
				saveBee(bee, is);
			}
		}
	}

	private static void setBeeHealth(IAlvearyController iac, IBee bee, int health) {
		try {
			beeHealth.set(bee, health);
			MinecraftForge.EVENT_BUS.post(new BeeSetHealthEvent(iac, iac.getBeekeepingLogic(), bee));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void rejuvenateBee(IBeeHousing te, IBeekeepingLogic lgc, ItemStack is) {
		if (is != null) {
			IIndividual bee = AlleleManager.alleleRegistry.getIndividual(is);
			if (bee instanceof IBee) {
				int max = ((IBee)bee).getMaxHealth();
				setBeeHealth(te, lgc, (IBee)bee, max);
				saveBee(bee, is);
			}
		}
	}

	private static void setBeeHealth(IBeeHousing te, IBeekeepingLogic lgc, IBee bee, int health) {
		try {
			beeHealth.set(bee, health);
			MinecraftForge.EVENT_BUS.post(new BeeSetHealthEvent(te, lgc, bee));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean isGenedItem(ItemStack is) {
		return BeeManager.beeRoot.getType(is) != EnumBeeType.NONE || TreeManager.treeRoot.getType(is) != EnumGermlingType.NONE || ButterflyManager.butterflyRoot.getType(is) != EnumFlutterType.NONE;
	}

	public static boolean isBee(ItemStack is) {
		return is.getItem() == ForestryHandler.ItemEntry.DRONE.getItem() || is.getItem() == ForestryHandler.ItemEntry.PRINCESS.getItem() || is.getItem() == ForestryHandler.ItemEntry.QUEEN.getItem();
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
		String s = t.name().substring(0, t.name().indexOf('_'));
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

	public static void setGene(ItemStack queen, ITreeGenome ibg, EnumTreeChromosome gene, IAllele value, boolean inactive) {
		boolean ana = AlleleManager.alleleRegistry.getIndividual(queen).isAnalyzed();
		IChromosome[] ic = ibg.getChromosomes();
		IAllele[] arr = TreeManager.treeRoot.getDefaultTemplate();
		IAllele[] arr2 = TreeManager.treeRoot.getDefaultTemplate();
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
		ITreeGenome repl = TreeManager.treeRoot.templateAsGenome(arr, arr2);
		saveBee(repl, queen);
		if (ana)
			analyzeBee(queen);
	}

	public static void setGene(ItemStack queen, IButterflyGenome ibg, EnumButterflyChromosome gene, IAllele value, boolean inactive) {
		boolean ana = AlleleManager.alleleRegistry.getIndividual(queen).isAnalyzed();
		IChromosome[] ic = ibg.getChromosomes();
		IAllele[] arr = ButterflyManager.butterflyRoot.getDefaultTemplate();
		IAllele[] arr2 = ButterflyManager.butterflyRoot.getDefaultTemplate();
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
		IButterflyGenome repl = ButterflyManager.butterflyRoot.templateAsGenome(arr, arr2);
		saveBee(repl, queen);
		if (ana)
			analyzeBee(queen);
	}

	public static BeeGene getGeneEnum(EnumBeeChromosome gene, IBeeGenome ibg) {
		switch(gene) {
			case EFFECT:
				return BeeAlleleRegistry.getEnum(ibg.getActiveAllele(gene), Effect.class);
			case FERTILITY:
				return BeeAlleleRegistry.getEnum(ibg.getActiveAllele(gene), Fertility.class);
			case FLOWERING:
				return BeeAlleleRegistry.getEnum(ibg.getActiveAllele(gene), Flowering.class);
			case FLOWER_PROVIDER:
				return BeeAlleleRegistry.getEnum(ibg.getActiveAllele(gene), Flower.class);
			case HUMIDITY_TOLERANCE:
				return getToleranceType(ibg.getToleranceHumid());
			case TEMPERATURE_TOLERANCE:
				return getToleranceType(ibg.getToleranceTemp());
			case LIFESPAN:
				return BeeAlleleRegistry.getEnum(ibg.getActiveAllele(gene), Life.class);
			case SPEED:
				return BeeAlleleRegistry.getEnum(ibg.getActiveAllele(gene), Speeds.class);
			case TERRITORY:
				return BeeAlleleRegistry.getEnum(ibg.getActiveAllele(gene), Territory.class);
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

	public static IAlleleBoolean getBooleanAllele(boolean value) { //exact same as forestry code
		return (IAlleleBoolean)AlleleManager.alleleRegistry.getAllele("forestry.bool"+WordUtils.capitalize(Boolean.toString(value)));
	}

	public static IAlleleInteger getIntegerAllele(int value) { //exact same as forestry code
		return (IAlleleInteger)AlleleManager.alleleRegistry.getAllele("forestry.i"+value+"d");
	}

	public static void setBeeMate(IBee ii, IBee repl) {
		ii.mate(repl);
	}

	public static ArrayList<String> getGenesAsStringList(ItemStack is) {
		IIndividual ii = AlleleManager.alleleRegistry.getIndividual(is);
		return ii != null ? getGenesAsStringList(ii.getGenome()) : new ArrayList();
	}

	public static ArrayList<String> getGenesAsStringList(IGenome ig) {
		ArrayList<String> li = new ArrayList();
		if (ig instanceof IBeeGenome) {
			IAlleleBeeSpecies sp1 = (IAlleleBeeSpecies)ig.getPrimary();
			IAlleleBeeSpecies sp2 = (IAlleleBeeSpecies)ig.getSecondary();
			EnumBeeChromosome[] order = {
					EnumBeeChromosome.SPECIES,
					EnumBeeChromosome.LIFESPAN,
					EnumBeeChromosome.SPEED,
					EnumBeeChromosome.FLOWERING,
					EnumBeeChromosome.FLOWER_PROVIDER,
					EnumBeeChromosome.FERTILITY,
					EnumBeeChromosome.TERRITORY,
					EnumBeeChromosome.EFFECT,
					//EnumBeeChromosome.TEMPERATURE_TOLERANCE,
					//EnumBeeChromosome.HUMIDITY_TOLERANCE,
					EnumBeeChromosome.NOCTURNAL,
					EnumBeeChromosome.TOLERANT_FLYER,
					EnumBeeChromosome.CAVE_DWELLING
			};

			for (int i = 0; i < order.length; i++) {
				IAllele ia1 = ig.getActiveAllele(order[i]);
				IAllele ia2 = ig.getInactiveAllele(order[i]);
				li.add(getGeneDisplay(ia1, order[i], true, true)+" / "+getGeneDisplay(ia2, order[i], false, false));
				if (order[i] == EnumBeeChromosome.EFFECT) {
					String t1 = getTemperatureDisplay(sp1, (IAlleleTolerance)ig.getActiveAllele(EnumBeeChromosome.TEMPERATURE_TOLERANCE), true, true);
					String t2 = getTemperatureDisplay(sp2, (IAlleleTolerance)ig.getInactiveAllele(EnumBeeChromosome.TEMPERATURE_TOLERANCE), false, false);
					li.add(t1+" / "+t2);
					String h1 = getHumidityDisplay(sp1, (IAlleleTolerance)ig.getActiveAllele(EnumBeeChromosome.HUMIDITY_TOLERANCE), true, true);
					String h2 = getHumidityDisplay(sp2, (IAlleleTolerance)ig.getInactiveAllele(EnumBeeChromosome.HUMIDITY_TOLERANCE), false, false);
					li.add(h1+" / "+h2);
				}
			}
		}
		else if (ig instanceof ITreeGenome) {
			IAlleleTreeSpecies sp1 = (IAlleleTreeSpecies)ig.getPrimary();
			IAlleleTreeSpecies sp2 = (IAlleleTreeSpecies)ig.getSecondary();
			EnumTreeChromosome[] order = {
					EnumTreeChromosome.SPECIES,
					EnumTreeChromosome.FERTILITY,
					EnumTreeChromosome.MATURATION,
					EnumTreeChromosome.HEIGHT,
					EnumTreeChromosome.GIRTH,
					EnumTreeChromosome.YIELD,
					EnumTreeChromosome.SAPPINESS,
					EnumTreeChromosome.FIREPROOF,
					EnumTreeChromosome.EFFECT,
					EnumTreeChromosome.GROWTH,
					EnumTreeChromosome.PLANT,
					EnumTreeChromosome.FRUITS,
			};

			for (int i = 0; i < order.length; i++) {
				IAllele ia1 = ig.getActiveAllele(order[i]);
				IAllele ia2 = ig.getInactiveAllele(order[i]);
				li.add(getGeneDisplay((ITreeGenome)ig, ia1, order[i], true, true)+" / "+getGeneDisplay((ITreeGenome)ig, ia2, order[i], false, false));
			}
		}
		else if (ig instanceof IButterflyGenome) {
			IAlleleButterflySpecies sp1 = (IAlleleButterflySpecies)ig.getPrimary();
			IAlleleButterflySpecies sp2 = (IAlleleButterflySpecies)ig.getSecondary();
			EnumButterflyChromosome[] order = {
					EnumButterflyChromosome.SPECIES,
					EnumButterflyChromosome.SIZE,
					EnumButterflyChromosome.SPEED,
					EnumButterflyChromosome.LIFESPAN,
					EnumButterflyChromosome.METABOLISM,
					EnumButterflyChromosome.FERTILITY,
					//EnumButterflyChromosome.TEMPERATURE_TOLERANCE,
					//EnumButterflyChromosome.HUMIDITY_TOLERANCE,
					//EnumButterflyChromosome.TERRITORY,
					EnumButterflyChromosome.EFFECT,
					EnumButterflyChromosome.NOCTURNAL,
					EnumButterflyChromosome.TOLERANT_FLYER,
					EnumButterflyChromosome.FIRE_RESIST,
			};

			for (int i = 0; i < order.length; i++) {
				IAllele ia1 = ig.getActiveAllele(order[i]);
				IAllele ia2 = ig.getInactiveAllele(order[i]);
				li.add(getGeneDisplay((IButterflyGenome)ig, ia1, order[i], true, true)+" / "+getGeneDisplay((IButterflyGenome)ig, ia2, order[i], false, false));
				if (order[i] == EnumButterflyChromosome.EFFECT) {
					String t1 = getTemperatureDisplay(sp1, (IAlleleTolerance)ig.getActiveAllele(EnumButterflyChromosome.TEMPERATURE_TOLERANCE), true, true);
					String t2 = getTemperatureDisplay(sp2, (IAlleleTolerance)ig.getInactiveAllele(EnumButterflyChromosome.TEMPERATURE_TOLERANCE), false, false);
					li.add(t1+" / "+t2);
					String h1 = getHumidityDisplay(sp1, (IAlleleTolerance)ig.getActiveAllele(EnumButterflyChromosome.HUMIDITY_TOLERANCE), true, true);
					String h2 = getHumidityDisplay(sp2, (IAlleleTolerance)ig.getInactiveAllele(EnumButterflyChromosome.HUMIDITY_TOLERANCE), false, false);
					li.add(h1+" / "+h2);
				}
			}
		}
		return li;
	}

	public static String getTemperatureDisplay(IAlleleSpecies bee, IAlleleTolerance tol, boolean title, boolean primary) {
		EnumChatFormatting ec = primary ? EnumChatFormatting.GREEN : EnumChatFormatting.RED;
		String ret = ec+AlleleManager.climateHelper.toDisplay(bee.getTemperature())+"/"+tol.getName()+EnumChatFormatting.RESET;
		if (title)
			ret = EnumChatFormatting.LIGHT_PURPLE+"Temperature: "+EnumChatFormatting.RESET+ret;
		return ret;
	}

	public static String getHumidityDisplay(IAlleleSpecies bee, IAlleleTolerance tol, boolean title, boolean primary) {
		EnumChatFormatting ec = primary ? EnumChatFormatting.GREEN : EnumChatFormatting.RED;
		String ret = ec+AlleleManager.climateHelper.toDisplay(bee.getHumidity())+"/"+tol.getName()+EnumChatFormatting.RESET;
		if (title)
			ret = EnumChatFormatting.LIGHT_PURPLE+"Humidity: "+EnumChatFormatting.RESET+ret;
		return ret;
	}

	public static String getGeneDisplay(IButterflyGenome itg, IAllele gene, EnumButterflyChromosome type, boolean title, boolean primary) {
		EnumChatFormatting ec = primary ? EnumChatFormatting.GREEN : EnumChatFormatting.RED;
		String tag = "";
		String val = gene.getName();
		switch(type) {
			case EFFECT:
				tag = "for.gui.effect";
				//val += ((IAlleleLeafEffect)gene).getName();
				break;
			case FERTILITY:
				tag = "for.gui.fertility";
				//val = ReikaStringParser.capFirstChar(ButterflyAlleleRegistry.Fertility.getFromAllele(gene).name());
				val = ((IAlleleInteger)gene).getValue()+"x";
				break;
			case SPECIES:
				tag = "for.gui.species";
				break;
			case TERRITORY:
				tag = "for.gui.area";
				IAlleleArea ia = (IAlleleArea)gene;
				val += " ("+ia.getValue()[0]+"x"+ia.getValue()[1]+"x"+ia.getValue()[2]+")";
				break;
			case FLOWER_PROVIDER:
				tag = "for.gui.flowers";
				break;
			case LIFESPAN:
				tag = "for.gui.life";
				val += getLifeDisplay(itg);
				break;
			case METABOLISM:
				tag = "for.gui.metabolism";
				//val = ReikaStringParser.capFirstChar(Metabolisms.getFromAllele(gene).name());
				val = ((IAlleleInteger)gene).getValue()+"x";
				break;
			case SIZE:
				tag = "for.gui.size";
				val += " ("+((IAlleleFloat)gene).getValue()/Size.AVERAGE.getAllele().getValue()+"x)";
				break;
			case SPEED:
				tag = "for.gui.maxspeed";
				val += " ("+((IAlleleFloat)gene).getValue()*10+" m/s)";
				break;
			case NOCTURNAL:
				tag = "for.gui.nocturnal";
				val = StatCollector.translateToLocal(((IAlleleBoolean)gene).getValue() ? "for.yes" : "for.no");
				break;
			case FIRE_RESIST:
				tag = "for.gui.fireresist";
				val = StatCollector.translateToLocal(((IAlleleBoolean)gene).getValue() ? "for.yes" : "for.no");
				break;
			case TOLERANT_FLYER:
				tag = "for.gui.flyer";
				val = StatCollector.translateToLocal(((IAlleleBoolean)gene).getValue() ? "for.yes" : "for.no");
				break;
			case HUMIDITY_TOLERANCE:
				tag = "for.gui.tolerance";
				break;
			case TEMPERATURE_TOLERANCE:
				tag = "for.gui.tolerance";
				break;
		}
		return title ? EnumChatFormatting.LIGHT_PURPLE+StatCollector.translateToLocal(tag)+": "+EnumChatFormatting.RESET+ec+val+EnumChatFormatting.RESET : ec+val+EnumChatFormatting.RESET;
	}

	private static String getLifeDisplay(IButterflyGenome gene) {
		return " ("+gene.getLifespan()+" ticks) [UNIMPLEMENTED]";
	}

	public static String getGeneDisplay(ITreeGenome itg, IAllele gene, EnumTreeChromosome type, boolean title, boolean primary) {
		EnumChatFormatting ec = primary ? EnumChatFormatting.GREEN : EnumChatFormatting.RED;
		String tag = "";
		String val = gene.getName();
		switch(type) {
			case EFFECT:
				tag = "for.gui.effect";
				//val += ((IAlleleLeafEffect)gene).getName();
				break;
			case MATURATION:
				tag = "for.gui.maturity";
				val += " ("+((IAlleleInteger)gene).getValue()+" growth ticks)";
				break;
			case FERTILITY:
				tag = "for.gui.fertility";
				val += " ("+((IAlleleFloat)gene).getValue()*100+" %/leaf)";
				break;
			case HEIGHT:
				tag = "for.gui.height";
				val += " ("+((IAlleleFloat)gene).getValue()+"x)";
				break;
			case YIELD:
				tag = "for.gui.yield";
				val += " ("+((IAlleleFloat)gene).getValue()/Yield.LOW.getAllele().getValue()+"x)";
				break;
			case SAPPINESS:
				tag = "for.gui.sappiness";
				val += " ("+((IAlleleFloat)gene).getValue()/Sappiness.LOW.getAllele().getValue()+"x)";
				break;
			case GIRTH:
				tag = "for.gui.girth";
				val = ((IAlleleInteger)gene).getValue()+"x"+((IAlleleInteger)gene).getValue();
				break;
			case SPECIES:
				tag = "for.gui.species";
				break;
			case FRUITS:
				tag = "for.gui.fruits";
				IFruitProvider iaf = ((IAlleleFruit)gene).getProvider();
				val = StatCollector.translateToLocal("for."+iaf.getDescription());
				if (iaf instanceof NoLocaleDescriptionFruit)
					val = ((NoLocaleDescriptionFruit)iaf).getDirectDescription();
				break;
			case PLANT:
				tag = "for.gui.native";
				val = StatCollector.translateToLocal((primary ? itg.getPrimary().getPlantType().toString() : itg.getSecondary().getPlantType().toString()));
				break;
			case GROWTH:
				tag = "for.gui.growth";
				val = StatCollector.translateToLocal(((IAlleleGrowth)gene).getProvider().getDescription());
				break;
			case TERRITORY:
				tag = "for.gui.area";
				IAlleleArea ia = (IAlleleArea)gene;
				val += " ("+ia.getValue()[0]+"x"+ia.getValue()[1]+"x"+ia.getValue()[2]+")";
				break;
			case FIREPROOF:
				tag = "for.gui.fireproof";
				val = StatCollector.translateToLocal(((IAlleleBoolean)gene).getValue() ? "for.yes" : "for.no");
				break;
			default:
				break;
		}
		return title ? EnumChatFormatting.LIGHT_PURPLE+StatCollector.translateToLocal(tag)+": "+EnumChatFormatting.RESET+ec+val+EnumChatFormatting.RESET : ec+val+EnumChatFormatting.RESET;
	}

	public static String getGeneDisplay(IAllele gene, EnumBeeChromosome type, boolean title, boolean primary) {
		EnumChatFormatting ec = primary ? EnumChatFormatting.GREEN : EnumChatFormatting.RED;
		String tag = "";
		String val = gene.getName();
		switch(type) {
			case EFFECT:
				tag = "for.gui.effect";
				break;
			case FERTILITY:
				tag = "for.gui.fertility";
				val += " ("+((IAlleleInteger)gene).getValue()+")";
				break;
			case FLOWERING:
				tag = "for.gui.pollination";
				val += " ("+((IAlleleInteger)gene).getValue()*20*60/550+"/min)";
				break;
			case FLOWER_PROVIDER:
				tag = "for.gui.flowers";
				break;
			case HUMIDITY_TOLERANCE:
				tag = "for.gui.tolerance";
				break;
			case TEMPERATURE_TOLERANCE:
				tag = "for.gui.tolerance";
				break;
			case LIFESPAN:
				tag = "for.gui.life";
				val += " ("+((IAlleleInteger)gene).getValue()+")";
				break;
			case SPECIES:
				tag = "for.gui.species";
				break;
			case SPEED:
				tag = "for.gui.worker";
				val += " ("+((IAlleleFloat)gene).getValue()+"x)";
				break;
			case TERRITORY:
				tag = "for.gui.area";
				IAlleleArea ia = (IAlleleArea)gene;
				val += " ("+ia.getValue()[0]+"x"+ia.getValue()[1]+"x"+ia.getValue()[2]+")";
				break;
			case NOCTURNAL:
				tag = "for.gui.nocturnal";
				val = StatCollector.translateToLocal(((IAlleleBoolean)gene).getValue() ? "for.yes" : "for.no");
				break;
			case CAVE_DWELLING:
				tag = "for.gui.cave";
				val = StatCollector.translateToLocal(((IAlleleBoolean)gene).getValue() ? "for.yes" : "for.no");
				break;
			case TOLERANT_FLYER:
				tag = "for.gui.flyer";
				val = StatCollector.translateToLocal(((IAlleleBoolean)gene).getValue() ? "for.yes" : "for.no");
				break;
			default:
				break;
		}
		return title ? EnumChatFormatting.LIGHT_PURPLE+StatCollector.translateToLocal(tag)+": "+EnumChatFormatting.RESET+ec+val+EnumChatFormatting.RESET : ec+val+EnumChatFormatting.RESET;
	}

	public static IBeeRoot getBeeRoot() {
		return (IBeeRoot)AlleleManager.alleleRegistry.getSpeciesRoot("rootBees");
	}

	public static ITreeRoot getTreeRoot() {
		return (ITreeRoot)AlleleManager.alleleRegistry.getSpeciesRoot("rootTrees");
	}

	public static IButterflyRoot getButterflyRoot() {
		return ButterflyManager.butterflyRoot;
	}

	public static boolean isPristine(ItemStack is) {
		IIndividual bee = AlleleManager.alleleRegistry.getIndividual(is);
		return bee instanceof IBee && ((IBee)bee).isNatural();
	}

	public static IGenome getGenome(ItemStack is) {
		IIndividual bee = AlleleManager.alleleRegistry.getIndividual(is);
		return bee != null ? bee.getGenome() : null;
	}

	public static IBee getBee(ItemStack is) {
		IIndividual bee = AlleleManager.alleleRegistry.getIndividual(is);
		return bee instanceof IBee ? (IBee)bee : null;
	}

	public static IAlleleSpecies getSpecies(ItemStack is) {
		IGenome ig = getGenome(is);
		return ig != null ? ig.getPrimary() : null;
	}

	public static boolean isDefaultJubilance(IBeeGenome ibg, IBeeHousing ibh) {
		return isDefaultJubilance(ibg.getPrimary(), ibg, ibh);
	}

	public static boolean isDefaultJubilance(IAlleleBeeSpecies ias, IBeeGenome ibg, IBeeHousing ibh) {
		return BeeManager.jubilanceFactory.getDefault().isJubilant(ias, ibg, ibh);
	}

	public static float getTemperatureRangeCenter(EnumTemperature t) {
		switch(t) {
			case ICY:
				return -0.1F;
			case COLD:
				return 0.175F;
			case NORMAL:
				return 0.6F;
			case WARM:
				return 0.925F;
			case HOT:
				return 1.1F;
			case HELLISH:
				return 2F;
			case NONE:
			default:
				return Float.NaN;
		}
	}

	public static float getHumidityRangeCenter(EnumHumidity h) {
		switch(h) {
			case ARID:
				return 0.15F;
			case NORMAL:
				return 0.5F;
			case DAMP:
				return 1F;
			default:
				return Float.NaN;

		}
	}

	public static boolean isTree(ItemStack is) {
		return is.getItem() == ForestryHandler.ItemEntry.SAPLING.getItem();
	}

	public static boolean isPollen(ItemStack is) {
		return is.getItem() == ForestryHandler.ItemEntry.TREEPOLLEN.getItem();
	}

	public static boolean isButterfly(ItemStack is) {
		return is.getItem() == ForestryHandler.ItemEntry.BUTTERFLY.getItem();
	}

	public static boolean isCaterpillar(ItemStack is) {
		return is.getItem() == ForestryHandler.ItemEntry.CATERPILLAR.getItem();
	}

	public static boolean isButterfly(Entity e) {
		return e instanceof IEntityButterfly;
	}

	private static int[] getFinalTerritory(IBeeGenome ibg, IBeeHousing ibh) {
		float f = 1;
		for (IBeeModifier ibm : ibh.getBeeModifiers()) {
			f *= ibm.getTerritoryModifier(ibg, f);
		}
		int[] ret = ibg.getTerritory();
		for (int i = 0; i < ret.length; i++)
			ret[i] *= f;
		return ret;
	}

	public static ItemStack getGeneTemplate(BeeSpecies bee) {
		return getGeneTemplate(bee.getSpeciesTemplate(), getBeeRoot());
	}

	public static ItemStack getGeneTemplate(IIndividual ii) {
		return getGeneTemplate(getBeeRoot().getTemplate(ii.getIdent()), AlleleManager.alleleRegistry.getSpeciesRoot(ii.getClass()));
	}

	public static ItemStack getGeneTemplate(IAllele[] genes, ISpeciesRoot root) {
		ItemStack template = ReikaItemHelper.lookupItem("gendustry:GeneTemplate");
		if (template.getItem().getClass() != geneTemplate)
			throw new MisuseException("You can only put genes on a template!");
		for (int i = 0; i < genes.length; i++) {
			IAllele gene = genes[i];
			if (gene != null) { //skip the empty slots, eg for bee climate
				try {
					Object sample = geneSampleCtr.newInstance(root, i, gene);
					addSample.invoke(template.getItem(), template, sample);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return template;
	}

	public static ItemStack getGeneSample(IChromosomeType ic, IAllele ie) {
		ItemStack item = ReikaItemHelper.lookupItem("gendustry:GeneSample");
		try {
			Object sample = geneSampleCtr.newInstance(ic.getSpeciesRoot(), ic.ordinal(), ie);
			ItemStack ret = (ItemStack)geneSampleGetItem.invoke(item.getItem(), sample);
			return ret;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Item getGendustrySampleItem() {
		return ModList.GENDUSTRY.isLoaded() ? GameRegistry.findItem(ModList.GENDUSTRY.modLabel, "GeneSample") : null;
	}

	public static Item getBinnieSampleItem() {
		return Loader.isModLoaded("Genetics") ? GameRegistry.findItem("Genetics", "serum") : null;
	}

	public static void setTree(TileEntity leaf, ITree tree) {
		try {
			setTreeLeaf.invoke(leaf, tree);
			updatePacket.invoke(leaf);
			leaf.worldObj.markBlockRangeForRenderUpdate(leaf.xCoord, leaf.yCoord, leaf.zCoord, leaf.xCoord, leaf.yCoord, leaf.zCoord);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setTreeOwner(TileEntity leaf, GameProfile owner) {
		try {
			setTreeLeafOwner.invoke(leaf, owner);
			updatePacket.invoke(leaf);
			leaf.worldObj.markBlockRangeForRenderUpdate(leaf.xCoord, leaf.yCoord, leaf.zCoord, leaf.xCoord, leaf.yCoord, leaf.zCoord);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setTreeLeafDecorative(TileEntity leaf, boolean decor) {
		try {
			deco.setBoolean(leaf, decor);
			updatePacket.invoke(leaf);
			leaf.worldObj.markBlockRangeForRenderUpdate(leaf.xCoord, leaf.yCoord, leaf.zCoord, leaf.xCoord, leaf.yCoord, leaf.zCoord);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ITree getTree(TileEntity leaf) {
		try {
			return (ITree)getTreeLeaf.invoke(leaf);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean hasFruit(TileEntity leaf) {
		try {
			return (boolean)treeHasFruit.invoke(leaf) && treeRipeness.getInt(leaf) >= treeRipeTime.getShort(leaf);
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void setTreeRipeness(TileEntity leaf, int val) {
		try {
			if (val == -1)
				val = treeRipeTime.getShort(leaf);
			treeRipeness.setInt(leaf, val);
			updatePacket.invoke(leaf);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int getTreeRipeness(TileEntity leaf) {
		try {
			return treeRipeness.getInt(leaf);
		}
		catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static IAllelePlantType getAlleleForPlantType(EnumPlantType plantType) {
		return (IAllelePlantType)AlleleManager.alleleRegistry.getAllele("forestry.plantType"+ReikaStringParser.capFirstChar(plantType.name()));
	}

	public static void attractButterflies(World world, double x, double y, double z, double r, EntityPathfinder path) {
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x-r, y-r, z-r, x+r, y+r, z+r);
		for (EntityCreature e : ((List<EntityCreature>)world.getEntitiesWithinAABB(IEntityButterfly.class, box))) {
			setButterflyState(e, "FLYING");
			boolean flag = false;
			if (path != null) {
				if (path.isInRange(e)) {
					DecimalPosition c = path.getNextWaypoint(e);
					if (c != null) {
						flag = true;
						SphericalVector vec = SphericalVector.fromCartesian(x-e.posX, y-e.posY, z-e.posZ);
						e.setLocationAndAngles(c.xCoord, c.yCoord, c.zCoord, (float)vec.rotation, (float)vec.inclination);
					}
					else {
						//ReikaJavaLibrary.pConsole(e);
						/*
						int[] xp = ReikaJavaLibrary.splitDoubleToInts(e.posX);
						int[] yp = ReikaJavaLibrary.splitDoubleToInts(e.posY);
						int[] zp = ReikaJavaLibrary.splitDoubleToInts(e.posZ);
						ReikaPacketHelper.sendDataPacket(DragonAPIInit.packetChannel, PacketIDs.PARTICLEWITHPOS.ordinal(), new PacketTarget.RadiusTarget(e, 32), ReikaParticleHelper.SMOKE.ordinal(), 4, xp[0], xp[1], yp[0], yp[1], zp[0], zp[1]);
						 */
					}
				}
				else {
					flag = true;
				}
			}
			if (!flag) {
				if (e.getDistanceSq(x, y, z) <= 32 || true) {
					double dx = x-e.posX;
					double dy = y-e.posY;
					double dz = z-e.posZ;
					double dd = ReikaMathLibrary.py3d(dx, dy, dz);
					SphericalVector vec = SphericalVector.fromCartesian(dx, dy, dz);
					e.rotationPitch = (float)vec.inclination;
					e.rotationYaw = e.rotationYawHead = (float)vec.rotation;
					double v = 0.125;
					double vx = v*dx/dd;
					double vy = v*dy/dd;
					double vz = v*dz/dd;
					int idx = MathHelper.floor_double(e.posX+vx*5);
					int idy = MathHelper.floor_double(e.posY+vy*5);
					int idz = MathHelper.floor_double(e.posZ+vz*5);
					Block b = world.getBlock(idx, idy, idz);
					if (b.getMaterial().blocksMovement() && b.getCollisionBoundingBoxFromPool(world, idx, idy, idz) != null) {
						vx *= -1;
						vz *= -1;
						vy += 0.25;
					}
					e.motionX = vx;
					e.motionY = vy;
					e.motionZ = vz;
					e.velocityChanged = true;
				}
				else {
					if (e.tasks.taskEntries.size() != 1 || !(((EntityAITaskEntry)e.tasks.taskEntries.get(0)).action instanceof AITaskSeekLocation)) {
						e.tasks.taskEntries.clear();
						e.tasks.addTask(0, new AITaskSeekLocation(e, 0.5, new Coordinate(x, y, z)));
					}
				}
			}
		}
	}

	private static void setButterflyState(EntityCreature e, String val) {
		try {
			Object entry = Enum.valueOf(butterflyStateEnum, val);
			butterflyState.set(e, entry);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void collectButterflies(World world, AxisAlignedBB box, EntityPlayer ep) {
		for (IEntityButterfly e : ((List<IEntityButterfly>)world.getEntitiesWithinAABB(IEntityButterfly.class, box))) {
			if (e.getEntity().isDead || e.getEntity().getHealth() <= 0)
				continue;
			IButterfly fly = e.getButterfly();
			if (fly == null)
				continue;
			ItemStack is = ButterflyManager.butterflyRoot.getMemberStack(fly, EnumFlutterType.BUTTERFLY.ordinal());
			if (is != null) {
				e.getEntity().setDead();
				ReikaSoundHelper.playSoundAtEntity(world, e.getEntity(), "random.pop", 0.5F, 2F);
				is = is.copy();
				if (ep != null) {
					if (MinecraftForge.EVENT_BUS.post(new EntityItemPickupEvent(ep, new EntityItem(ep.worldObj, ep.posX, ep.posY, ep.posZ, is)))) {
						continue;
					}
					ReikaPlayerAPI.addOrDropItem(is, ep);
				}
				else {
					ReikaItemHelper.dropItem(e.getEntity(), is);
				}
			}
		}
	}

	public static Class getButterflyClass() {
		return butterfly;
	}

	public static ItemStack convertToBasicSpeciesTemplate(ItemStack is) {
		ISpeciesRoot isr = AlleleManager.alleleRegistry.getSpeciesRoot(is);
		if (isr != null) {
			IIndividual ii = isr.getMember(is);
			if (ii != null) {
				IAlleleSpecies iae = ii.getGenome().getPrimary();
				if (iae != null) {
					IAllele[] genes = isr.getTemplate(iae.getUID());
					int type = ((Enum)isr.getType(is)).ordinal();
					ii = isr.templateAsIndividual(genes);
					is = isr.getMemberStack(ii, type);
				}
			}
		}
		return is;
	}

	public static ISpeciesType getSpeciesType(ItemStack is) {
		ISpeciesRoot isr = AlleleManager.alleleRegistry.getSpeciesRoot(is);
		return isr != null ? isr.getType(is) : null;
	}

	public static int[] getEffectiveTerritory(IBeeHousing ibh, ChunkCoordinates c, IBeeGenome ibg, long time) {
		CachedTerritory t = territoryCache.get(c);
		if (t == null || t.territory == null || t.age >= 20) {
			if (t == null) {
				t = new CachedTerritory();
				territoryCache.put(c, t);
			}
			t.recalculate(ibg, ibh);
		}
		else if (time > t.lastTick) {
			t.lastTick = time;
			t.age++;
		}
		return t.territory;
	}

	private static class CachedTerritory {

		private int[] territory;
		private int age;
		private long lastTick;

		private void recalculate(IBeeGenome ibg, IBeeHousing ibh) {
			age = 0;
			territory = getFinalTerritory(ibg, ibh);
		}

	}

	public static void setButterflyTarget(IEntityButterfly butterfly, Vec3 vec) {
		try {
			butterflyTarget.set(butterfly, vec);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
