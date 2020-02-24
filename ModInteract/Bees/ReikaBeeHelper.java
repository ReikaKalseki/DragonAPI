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
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.text.WordUtils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
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
import Reika.DragonAPI.ModInteract.ItemHandlers.ForestryHandler;

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
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IAlleleTolerance;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.ISpeciesRoot;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.EnumButterflyChromosome;
import forestry.api.lepidopterology.EnumFlutterType;
import forestry.api.lepidopterology.IButterflyGenome;
import forestry.api.lepidopterology.IButterflyRoot;
import forestry.api.multiblock.IAlvearyController;


public class ReikaBeeHelper {

	private static Field beeHealth;
	private static final HashSet<String> allBees = new HashSet();

	private static Class geneTemplate;
	private static Method addSample;
	private static Class geneSample;
	private static Constructor geneSampleCtr;

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
		}

		if (ModList.GENDUSTRY.isLoaded()) {
			try {
				geneSample = Class.forName("net.bdew.gendustry.forestry.GeneSampleInfo");
				geneSampleCtr = geneSample.getDeclaredConstructor(ISpeciesRoot.class, int.class, IAllele.class);
				geneSampleCtr.setAccessible(true);

				geneTemplate = Class.forName("net.bdew.gendustry.items.GeneTemplate$");
				addSample = geneTemplate.getDeclaredMethod("addSample", ItemStack.class, geneSample);
				addSample.setAccessible(true);
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
		return li;
	}

	public static String getTemperatureDisplay(IAlleleBeeSpecies bee, IAlleleTolerance tol, boolean title, boolean primary) {
		EnumChatFormatting ec = primary ? EnumChatFormatting.GREEN : EnumChatFormatting.RED;
		String ret = ec+AlleleManager.climateHelper.toDisplay(bee.getTemperature())+"/"+tol.getName()+EnumChatFormatting.RESET;
		if (title)
			ret = EnumChatFormatting.LIGHT_PURPLE+"Temperature: "+EnumChatFormatting.RESET+ret;
		return ret;
	}

	public static String getHumidityDisplay(IAlleleBeeSpecies bee, IAlleleTolerance tol, boolean title, boolean primary) {
		EnumChatFormatting ec = primary ? EnumChatFormatting.GREEN : EnumChatFormatting.RED;
		String ret = ec+AlleleManager.climateHelper.toDisplay(bee.getHumidity())+"/"+tol.getName()+EnumChatFormatting.RESET;
		if (title)
			ret = EnumChatFormatting.LIGHT_PURPLE+"Humidity: "+EnumChatFormatting.RESET+ret;
		return ret;
	}

	public static String getGeneDisplay(ITreeGenome itg, IAllele gene, EnumTreeChromosome type, boolean title, boolean primary) {
		EnumChatFormatting ec = primary ? EnumChatFormatting.GREEN : EnumChatFormatting.RED;
		String tag = "";
		String val = gene.getName();
		switch(type) {
			case EFFECT:
				tag = "for.gui.effect";
				break;
			case MATURATION:
				tag = "for.gui.maturity";
				val += " ("+((IAlleleInteger)gene).getValue()+" growth ticks)";
				break;
			case FERTILITY:
				tag = "for.gui.fertility";
				val += " ("+((IAlleleFloat)gene).getValue()+" %/leaf)";
				break;
			case HEIGHT:
				tag = "for.gui.height";
				val += " ("+((IAlleleFloat)gene).getValue()+"x)";
				break;
			case YIELD:
				tag = "for.gui.yield";
				val += " ("+((IAlleleFloat)gene).getValue()+"x)";
				break;
			case SAPPINESS:
				tag = "for.gui.sappiness";
				val += " ("+((IAlleleFloat)gene).getValue()+"x)";
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
				val = StatCollector.translateToLocal("for."+((IAlleleFruit)gene).getProvider().getDescription());
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

	public static boolean isPristine(ItemStack is) {
		IIndividual bee = AlleleManager.alleleRegistry.getIndividual(is);
		return bee instanceof IBee && ((IBee)bee).isNatural();
	}

	public static IGenome getGenome(ItemStack is) {
		IIndividual bee = AlleleManager.alleleRegistry.getIndividual(is);
		return bee instanceof IBee ? ((IBee)bee).getGenome() : null;
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

	public static int[] getFinalTerritory(IBeeGenome ibg, IBeeHousing ibh) {
		float f = 1;
		for (IBeeModifier ibm : ibh.getBeeModifiers()) {
			f *= ibm.getTerritoryModifier(ibg, f);
		}
		int[] ret = ibg.getTerritory();
		for (int i = 0; i < ret.length; i++)
			ret[i] *= f;
		return ret;
	}

	public static void getGeneTemplate(ItemStack template, BeeSpecies bee) {
		getGeneTemplate(template, bee.getSpeciesTemplate(), getBeeRoot());
	}

	public static void getGeneTemplate(ItemStack template, IIndividual ii) {
		getGeneTemplate(template, getBeeRoot().getTemplate(ii.getIdent()), AlleleManager.alleleRegistry.getSpeciesRoot(ii.getClass()));
	}

	private static void getGeneTemplate(ItemStack template, IAllele[] genes, ISpeciesRoot root) {
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
	}
}
