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

import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeMutation;
import forestry.api.apiculture.IBeeRoot;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.IIconProvider;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IClassification;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IIndividual;

public abstract class BeeSpecies implements IAlleleBeeSpecies, IIconProvider {

	protected final Random rand = new Random();

	private final IBeeRoot beeRoot;
	private final IIcon[][] icons = new IIcon[EnumBeeType.VALUES.length][3];
	private final HashMap specials = new HashMap();
	private final HashMap products = new HashMap();
	private final BeeBranch branch;
	private final String scientific;
	private final String genus;
	private final String creator;
	private final String uid;
	private final String name;
	private boolean isRegistered = false;
	private final IAllele[] template = new IAllele[EnumBeeChromosome.values().length];

	protected BeeSpecies(String name, String uid, String latinName, String creator) {
		beeRoot = (IBeeRoot)AlleleManager.alleleRegistry.getSpeciesRoot("rootBees");

		branch = new BeeBranch(this);

		this.name = name;
		this.creator = creator;

		String[] s = latinName.split(" ");
		genus = s[0];
		scientific = s[1];
		this.uid = uid;
	}

	public void register() {
		System.arraycopy(this.getSpeciesTemplate(), 0, template, 0, template.length);
		AlleleManager.alleleRegistry.registerAllele(this);
		beeRoot.registerTemplate(template);
		AlleleManager.alleleRegistry.getClassification("family.apidae").addMemberGroup(branch);
		isRegistered = true;
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

	public abstract static class TraitsBee extends BeeSpecies {

		protected final BeeTraits traits;

		protected TraitsBee(String name, String uid, String latinName, String creator, BeeTraits traits) {
			super(name, uid, latinName, creator);
			this.traits = traits;
		}

		@Override
		public final EnumTemperature getTemperature() {
			return traits.temperature;
		}

		@Override
		public final EnumHumidity getHumidity() {
			return traits.humidity;
		}

		@Override
		public final Speeds getProductionSpeed() {
			return traits.speed;
		}

		@Override
		public final Fertility getFertility() {
			return traits.fertility;
		}

		@Override
		public final Flowering getFloweringRate() {
			return traits.flowering;
		}

		@Override
		public final Life getLifespan() {
			return traits.lifespan;
		}

		@Override
		public final Territory getTerritorySize() {
			return traits.area;
		}

		@Override
		public final boolean isCaveDwelling() {
			return traits.isCaveDwelling;
		}

		@Override
		public final int getTemperatureTolerance() {
			return traits.tempTol;
		}

		@Override
		public final int getHumidityTolerance() {
			return traits.humidTol;
		}

		@Override
		public final Tolerance getHumidityToleranceDir() {
			return traits.humidDir;
		}

		@Override
		public final Tolerance getTemperatureToleranceDir() {
			return traits.tempDir;
		}

		@Override
		public final boolean isNocturnal() {
			return traits.isNocturnal;
		}
	}

	private static final class BeeBranch implements IClassification {

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
			return species.genus;
		}

		@Override
		public String getScientific() {
			return species.genus;
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

	public final ItemStack getBeeItem(World world, EnumBeeType type) {
		return beeRoot.getMemberStack(beeRoot.getBee(world, beeRoot.templateAsGenome(template)), type.ordinal());
	}

	public final void addBreeding(String parent1, String parent2, int chance) {
		IAllele p1 = AlleleManager.alleleRegistry.getAllele("forestry.species"+parent1);
		IAllele p2 = AlleleManager.alleleRegistry.getAllele("forestry.species"+parent2);
		if (p1 == null)
			throw new MisuseException("Error breeding from "+parent1+": You cannot breed a bee from null!");
		if (p2 == null)
			throw new MisuseException("Error breeding from "+parent2+": You cannot breed a bee from null!");
		this.addBreeding(p1, p2, chance);
	}

	public final void addBreeding(String parent1, BeeSpecies parent2, int chance) {
		IAllele p1 = AlleleManager.alleleRegistry.getAllele("forestry.species"+parent1);
		if (p1 == null)
			throw new MisuseException("Error breeding from "+parent1+": You cannot breed a bee from null!");
		this.addBreeding(p1, parent2, chance);
	}

	public final void addBreeding(BeeSpecies parent1, BeeSpecies parent2, int chance) {
		this.addBreeding((IAllele)parent1, (IAllele)parent2, chance);
	}

	private final void addBreeding(IAllele p1, IAllele p2, int chance) {
		if (!isRegistered)
			throw new MisuseException("You must register a bee before adding breeding pairs!");
		if (p1 == null || p2 == null)
			throw new MisuseException("You cannot breed a bee from null!");
		beeRoot.registerMutation(new BeeBreeding(p1, p2, chance, this));
	}

	private static final class BeeBreeding implements IBeeMutation {

		public final IAllele parent1;
		public final IAllele parent2;
		public final int chance;
		private final BeeSpecies bee;

		private BeeBreeding(IAllele p1, IAllele p2, int chance, BeeSpecies bee) {
			parent1 = p1;
			parent2 = p2;
			this.chance = chance;
			this.bee = bee;
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
			return bee.template;
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
			if (val.getUID().equals(ia.getUID()))
				val = parent2;
			return val;
		}

		@Override
		public boolean isSecret() {
			return bee.isSecret();
		}

		@Override
		public IBeeRoot getRoot() {
			return bee.getRoot();
		}

		@Override
		public final float getChance(IBeeHousing ibh, IAllele ia1, IAllele ia2, IGenome ig1, IGenome ig2) {
			return this.isValidParents(ia1, ia2) ? 1 : 0;
		}

		private boolean isValidParents(IAllele ia1, IAllele ia2) {
			if (ia1.getUID().equals(parent1.getUID()) && ia2.getUID().equals(parent2.getUID()))
				return true;
			if (ia1.getUID().equals(parent2.getUID()) && ia2.getUID().equals(parent1.getUID()))
				return true;
			return false;
		}

	}

	public static enum Speeds {
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

		public IAllele getAllele() {
			return AlleleManager.alleleRegistry.getAllele(tag);
		}
	}

	public static enum Fertility {
		LOW("fertilityLow"),
		NORMAL("fertilityNormal"),
		HIGH("fertilityHigh"),
		MAXIMUM("fertilityMaximum");

		public final String tag;

		private Fertility(String s) {
			tag = "forestry."+s;
		}

		public IAllele getAllele() {
			return AlleleManager.alleleRegistry.getAllele(tag);
		}
	}

	public static enum Flower {
		VANILLA("flowersVanilla"),
		NETHER("flowersNether"),
		CACTUS("flowersCacti"),
		MUSHROOM("flowersMushrooms"),
		ENDER("flowersEnd"),
		JUNGLE("flowersJungle"),
		SNOW("flowersSnow"),
		WHEAT("flowersWheat"),
		GOURD("flowersGourd");

		public final String tag;

		private Flower(String s) {
			tag = "forestry."+s;
		}

		public IAllele getAllele() {
			return AlleleManager.alleleRegistry.getAllele(tag);
		}
	}

	public static enum Flowering {
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

		public IAllele getAllele() {
			return AlleleManager.alleleRegistry.getAllele(tag);
		}
	}

	public static enum Territory {
		DEFAULT("territoryDefault"),
		LARGE("territoryLarge"),
		LARGER("territoryLarger"),
		LARGEST("territoryLargest");

		public final String tag;

		private Territory(String s) {
			tag = "forestry."+s;
		}

		public IAllele getAllele() {
			return AlleleManager.alleleRegistry.getAllele(tag);
		}
	}

	public static enum Life {
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

		public IAllele getAllele() {
			return AlleleManager.alleleRegistry.getAllele(tag);
		}
	}

	public static enum Tolerance {
		UP("toleranceUp"),
		DOWN("toleranceDown"),
		BOTH("toleranceBoth"),
		NONE("toleranceNone");

		public final String tag;

		private Tolerance(String s) {
			tag = "forestry."+s;
		}
	}

	public static enum Effect {
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

		public IAllele getAllele() {
			return AlleleManager.alleleRegistry.getAllele(tag);
		}
	}

	public abstract IAllele getFlowerAllele();
	public abstract IAllele getEffectAllele();
	public abstract Speeds getProductionSpeed();
	public abstract Fertility getFertility();
	public abstract Flowering getFloweringRate();
	public abstract Life getLifespan();
	public abstract Territory getTerritorySize();
	public abstract boolean isCaveDwelling();
	public abstract int getTemperatureTolerance();
	public abstract int getHumidityTolerance();
	public abstract Tolerance getHumidityToleranceDir();
	public abstract Tolerance getTemperatureToleranceDir();
	public abstract int getOutlineColor();

	@Override
	public int getIconColour(int renderpass) {
		switch(renderpass) {
		case 0:
			return this.getOutlineColor();
		case 1:
			return 0xffff00;
		case 2:
			return 0xffffff;
		default:
			return 0xffffff;
		}
	}

	private final IAllele getGeneForBoolean(boolean b) {
		String s = b ? "forestry.boolTrue" : "forestry.boolFalse";
		return AlleleManager.alleleRegistry.getAllele(s);
	}

	private final IAllele getGeneForInt(int i) {
		return AlleleManager.alleleRegistry.getAllele(String.format("i%dd", i));
	}

	private final IAllele getToleranceGene(Tolerance d, int i) {
		String s = i != 0 ? String.format("%s%d", d.tag, Math.min(Math.abs(i), 5)) : Tolerance.NONE.tag;
		return AlleleManager.alleleRegistry.getAllele(s);
	}

	protected final IAllele[] getSpeciesTemplate() {
		IAllele[] alleles = beeRoot.getDefaultTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = this;
		alleles[EnumBeeChromosome.FLOWER_PROVIDER.ordinal()] = this.getFlowerAllele();
		alleles[EnumBeeChromosome.SPEED.ordinal()] = this.getProductionSpeed().getAllele();
		alleles[EnumBeeChromosome.LIFESPAN.ordinal()] = this.getLifespan().getAllele();
		alleles[EnumBeeChromosome.TERRITORY.ordinal()] = this.getTerritorySize().getAllele();
		alleles[EnumBeeChromosome.FLOWERING.ordinal()] = this.getFloweringRate().getAllele();
		alleles[EnumBeeChromosome.FERTILITY.ordinal()] = this.getFertility().getAllele();
		alleles[EnumBeeChromosome.EFFECT.ordinal()] = this.getEffectAllele();
		alleles[EnumBeeChromosome.NOCTURNAL.ordinal()] = this.getGeneForBoolean(this.isNocturnal());
		alleles[EnumBeeChromosome.CAVE_DWELLING.ordinal()] = this.getGeneForBoolean(this.isCaveDwelling());
		alleles[EnumBeeChromosome.TEMPERATURE_TOLERANCE.ordinal()] = this.getToleranceGene(this.getTemperatureToleranceDir(), this.getTemperatureTolerance());
		alleles[EnumBeeChromosome.HUMIDITY_TOLERANCE.ordinal()] = this.getToleranceGene(this.getHumidityToleranceDir(), this.getHumidityTolerance());
		return alleles;
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
	public final void registerIcons(IIconRegister ico) {
		String iconType = "default";
		String mod = "forestry";

		IIcon body1 = ico.registerIcon(mod + ":bees/" + iconType + "/body1");
		IIcon larva = ico.registerIcon(mod+":bees/"+iconType+"/"+EnumBeeType.LARVAE.name().toLowerCase()+".body");

		for (int i = 0; i < EnumBeeType.VALUES.length; i++) {
			if (EnumBeeType.VALUES[i] != EnumBeeType.NONE) {
				icons[i][0] = ico.registerIcon(mod+":bees/"+iconType+"/"+EnumBeeType.VALUES[i].name().toLowerCase()+".outline");
				icons[i][1] = EnumBeeType.VALUES[i] == EnumBeeType.LARVAE ? larva : body1;
				icons[i][2] = ico.registerIcon(mod+":bees/"+iconType+"/"+EnumBeeType.VALUES[i].name().toLowerCase()+".body2");
			}
		}
	}

	@Override
	public final IIcon getIcon(EnumBeeType type, int renderPass) {
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
	public final IIcon getIcon(short ps) {
		return ReikaTextureHelper.getMissingIcon();
	}

	@Override
	public final String getEntityTexture() {
		return "";
	}

}