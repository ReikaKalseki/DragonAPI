/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.Bees;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeMutation;
import forestry.api.apiculture.IBeeRoot;
import forestry.api.core.IIconProvider;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IClassification;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IIndividual;

public abstract class BeeSpecies implements IAlleleBeeSpecies, IIconProvider {

	private final IBeeRoot beeRoot;
	private final IAllele[] template;
	private final Icon[][] icons = new Icon[EnumBeeType.VALUES.length][3];
	private final HashMap specials = new HashMap();
	private final HashMap products = new HashMap();
	private final BeeBranch branch;
	private final String scientific;
	private final String creator;
	private final String uid;
	private final String name;

	protected BeeSpecies(String name, String uid, String scientific, String creator) {
		beeRoot = (IBeeRoot)AlleleManager.alleleRegistry.getSpeciesRoot("rootBees");
		template = this.getSpeciesTemplate();

		branch = new BeeBranch(this);

		this.name = name;
		this.creator = creator;
		this.scientific = scientific;
		this.uid = uid;
	}

	public void register() {
		AlleleManager.alleleRegistry.registerAllele(this);
		beeRoot.registerTemplate(template);
		AlleleManager.alleleRegistry.getClassification("family.apidae").addMemberGroup(branch);
	}

	@Override
	public final String getBinomial() {
		return scientific;
	}

	@Override
	public final String getAuthority() {
		return creator;
	}

	@Override
	public final String getUID() {
		return uid;
	}

	@Override
	public final String getName() {
		return name;
	}

	public final void addSpecialty(ItemStack item, int chance) {
		specials.put(item, chance);
	}

	public final void addProduct(ItemStack item, int chance) {
		products.put(item, chance);
	}

	@Override
	public IClassification getBranch() {
		return branch;
	}

	@Override
	public final Map getProducts() {
		Map m = new HashMap();
		m.putAll(products);
		return m;
	}

	@Override
	public final Map getSpecialty() {
		Map m = new HashMap();
		m.putAll(specials);
		return m;
	}

	private final class BeeBranch implements IClassification {

		public final BeeSpecies species;

		private BeeBranch(BeeSpecies b) {
			species = b;
		}

		@Override
		public EnumClassLevel getLevel() {
			return EnumClassLevel.GENUS;
		}

		@Override
		public String getUID() {
			return species.getUID();
		}

		@Override
		public String getName() {
			return species.getName();
		}

		@Override
		public String getScientific() {
			return species.getBinomial();
		}

		@Override
		public String getDescription() {
			return species.getDescription();
		}

		@Override
		public IClassification[] getMemberGroups() {
			return new IClassification[0];
		}

		@Override
		public void addMemberGroup(IClassification icl) {

		}

		@Override
		public IAlleleSpecies[] getMemberSpecies() {
			return new IAlleleSpecies[0];
		}

		@Override
		public void addMemberSpecies(IAlleleSpecies ias) {

		}

		@Override
		public IClassification getParent() {
			return null;
		}

		@Override
		public void setParent(IClassification icl) {

		}
	}

	public final void addBreeding(String parent1, String parent2, int chance) {
		beeRoot.registerMutation(new BeeBreeding(parent1, parent2, chance, this.isSecret()));
	}

	private final class BeeBreeding implements IBeeMutation {

		public final IAllele parent1;
		public final IAllele parent2;
		public final int chance;
		private boolean secret;

		protected BeeBreeding(String p1, String p2, int chance, boolean secret) {
			parent1 = AlleleManager.alleleRegistry.getAllele("forestry.species"+p1);
			parent2 = AlleleManager.alleleRegistry.getAllele("forestry.species"+p2);
			this.chance = chance;
			this.secret = secret;
		}

		@Override
		public IAllele getAllele0() {
			return parent1;
		}

		@Override
		public IAllele getAllele1() {
			return parent2;
		}

		@Override
		public IAllele[] getTemplate() {
			ReikaJavaLibrary.pConsole(Arrays.toString(template));
			return template;
		}

		@Override
		public float getBaseChance() {
			return chance;
		}

		@Override
		public Collection getSpecialConditions() {
			return new ArrayList<String>();
		}

		@Override
		public boolean isPartner(IAllele ia) {
			return parent1.getUID().equals(ia.getUID()) || parent2.getUID().equals(ia.getUID());
		}

		@Override
		public IAllele getPartner(IAllele ia) {
			IAllele val = parent1;
			if(val.getUID().equals(ia.getUID()))
				val = parent2;
			return val;
		}

		@Override
		public boolean isSecret() {
			return secret;
		}

		@Override
		public IBeeRoot getRoot() {
			return beeRoot;
		}

		@Override
		public float getChance(IBeeHousing ibh, IAllele ia1, IAllele ia2, IGenome ig1, IGenome ig2) {
			return chance/100F;
		}

	}

	protected static enum Speeds {
		SLOWEST("speedSlowest"),
		SLOWER("speedSlower"),
		SLOW("speedSlow"),
		NORMAL("speedNorm"),
		FAST("speedFast"),
		FASTER("speedFaster"),
		FASTEST("speedFastest");

		public final String tag;

		private Speeds(String s) {
			tag = "forestry."+s;
		}
	}

	protected static enum Fertility {
		LOW("fertilityLow"),
		NORMAL("fertilityNormal"),
		HIGH("fertilityHigh"),
		MAXIMUM("fertilityMaximum");

		public final String tag;

		private Fertility(String s) {
			tag = "forestry."+s;
		}
	}

	protected static enum Flowering {
		SLOWEST("floweringSlowest"),
		SLOWER("floweringSlower"),
		SLOW("floweringSlow"),
		AVERAGE("floweringAverage"),
		FAST("floweringFast"),
		FASTER("floweringFaster"),
		FASTEST("floweringFastest"),
		MAXIMUM("floweringMaximum"); //"gui.maximum"

		public final String tag;

		private Flowering(String s) {
			tag = "forestry."+s;
		}
	}

	protected static enum Territory {
		DEFAULT("territoryDefault"),
		LARGE("territoryLarge"),
		LARGER("territoryLarger"),
		LARGEST("territoryLargest");

		public final String tag;

		private Territory(String s) {
			tag = "forestry."+s;
		}
	}

	protected static enum Life {
		SHORTEST("lifespanShortest"),
		SHORTER("lifespanShorter"),
		SHORT("lifespanShort"),
		SHORTENED("lifespanShortened"),
		NORMAL("lifespanNormal"),
		ELONGATED("lifespanElongated"),
		LONG("lifespanLong"),
		LONGER("lifespanLonger"),
		LONGEST("lifespanLongest");

		public final String tag;

		private Life(String s) {
			tag = "forestry."+s;
		}
	}

	protected static enum ToleranceDirection {
		UP("toleranceUp"),
		DOWN("toleranceDown"),
		BOTH("toleranceBoth"),
		NONE("toleranceNone");

		public final String tag;

		private ToleranceDirection(String s) {
			tag = "forestry."+s;
		}
	}

	protected static enum Effect {
		NONE("effectNone"),
		AGRESSION("effectAggressive"),
		HEROIC("effectHeroic"),
		REGEN("effectBeatific"),
		MIASMIC("effectMiasmic"),
		MISANTHROPE("effectMisanthrope"),
		ICY("effectGlacial"),
		RADIATION("effectRadioactive"),
		CREEPER("effectCreeper"),
		FIRE("effectIgnition"),
		EXPLORE("effectExploration"),
		EASTER("effectFestiveEaster"),
		SNOW("effectSnowing"),
		NAUSEA("effectDrunkard"),
		REANIMATE("effectReanimation"),
		RESURRECT("effectResurrection"),
		REPULSION("effectRepulsion");

		public final String tag;

		private Effect(String s) {
			tag = "forestry."+s;
		}
	}

	public abstract IAllele getFlowerAllele();
	public abstract Speeds getProductionSpeed();
	public abstract Fertility getFertility();
	public abstract Flowering getFloweringRate();
	public abstract Life getLifespan();
	public abstract Territory getTerritorySize();
	public abstract boolean isCaveDwelling();
	public abstract int getTemperatureTolerance();
	public abstract int getHumidityTolerance();
	public abstract ToleranceDirection getHumidityToleranceDir();
	public abstract ToleranceDirection getTemperatureToleranceDir();
	public abstract Effect getEffect();

	private final IAllele getGeneForBoolean(boolean b) {
		String s = b ? "forestry.boolTrue" : "forestry.boolFalse";
		return AlleleManager.alleleRegistry.getAllele(s);
	}

	private final IAllele getGeneForInt(int i) {
		return AlleleManager.alleleRegistry.getAllele(String.format("i%dd", i));
	}

	private final IAllele getToleranceGene(ToleranceDirection d, int i) {
		return AlleleManager.alleleRegistry.getAllele(String.format("%s%d", d.tag, Math.abs(Math.min(i, 5))));
	}

	protected final IAllele[] getSpeciesTemplate() {
		IAllele[] alleles = beeRoot.getDefaultTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = this;
		alleles[EnumBeeChromosome.FLOWER_PROVIDER.ordinal()] = this.getFlowerAllele();
		alleles[EnumBeeChromosome.SPEED.ordinal()] = AlleleManager.alleleRegistry.getAllele(this.getProductionSpeed().tag);
		alleles[EnumBeeChromosome.LIFESPAN.ordinal()] = AlleleManager.alleleRegistry.getAllele(this.getLifespan().tag);
		alleles[EnumBeeChromosome.TERRITORY.ordinal()] = AlleleManager.alleleRegistry.getAllele(this.getTerritorySize().tag);
		alleles[EnumBeeChromosome.FLOWERING.ordinal()] = AlleleManager.alleleRegistry.getAllele(this.getFloweringRate().tag);
		alleles[EnumBeeChromosome.FERTILITY.ordinal()] = AlleleManager.alleleRegistry.getAllele(this.getFertility().tag);
		alleles[EnumBeeChromosome.EFFECT.ordinal()] = AlleleManager.alleleRegistry.getAllele(this.getEffect().tag);
		alleles[EnumBeeChromosome.NOCTURNAL.ordinal()] = this.getGeneForBoolean(this.isNocturnal());
		alleles[EnumBeeChromosome.CAVE_DWELLING.ordinal()] = this.getGeneForBoolean(this.isCaveDwelling());
		alleles[EnumBeeChromosome.TEMPERATURE_TOLERANCE.ordinal()] = this.getToleranceGene(this.getTemperatureToleranceDir(), this.getTemperatureTolerance());
		alleles[EnumBeeChromosome.HUMIDITY_TOLERANCE.ordinal()] = this.getToleranceGene(this.getHumidityToleranceDir(), this.getHumidityTolerance());
		return alleles;
	}

	public final IAllele getVanillaFlowerAllele() {
		return AlleleManager.alleleRegistry.getAllele("flowersVanilla");
	}

	@Override
	public final IBeeRoot getRoot() {
		return beeRoot;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final IIconProvider getIconProvider() {
		return this;
	}

	@Override
	public final void registerIcons(IconRegister ico) {
		String iconType = "default";
		String mod = "forestry";

		Icon body1 = ico.registerIcon(mod + ":bees/" + iconType + "/body1");
		Icon larva = ico.registerIcon(mod+":bees/"+iconType+"/"+EnumBeeType.LARVAE.name().toLowerCase()+".body");

		for (int i = 0; i < EnumBeeType.VALUES.length; i++) {
			if (EnumBeeType.VALUES[i] != EnumBeeType.NONE) {
				icons[i][0] = ico.registerIcon(mod+":bees/"+iconType+"/"+EnumBeeType.VALUES[i].name().toLowerCase()+".outline");
				icons[i][1] = EnumBeeType.VALUES[i] == EnumBeeType.LARVAE ? larva : body1;
				icons[i][2] = ico.registerIcon(mod+":bees/"+iconType+"/"+EnumBeeType.VALUES[i].name().toLowerCase()+".body2");
			}
		}
	}

	@Override
	public final Icon getIcon(EnumBeeType type, int renderPass) {
		return icons[type.ordinal()][renderPass];
	}

	@Override
	public int getComplexity() {
		return 0;
	}

	@Override
	public float getResearchSuitability(ItemStack is) {
		return 0;
	}

	@Override
	public ItemStack[] getResearchBounty(World paramWorld, String paramString, IIndividual paramIIndividual, int paramInt) {
		return new ItemStack[0];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final Icon getIcon(short ps) {
		return ReikaTextureHelper.getMissingIcon();
	}

}
