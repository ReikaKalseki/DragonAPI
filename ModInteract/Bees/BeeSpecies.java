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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.Fertility;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.Flowering;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.Life;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.Speeds;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.Territory;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.Tolerance;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IAlleleBeeEffect;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBeeGenome;
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
import forestry.api.genetics.IIndividual;

public abstract class BeeSpecies implements IAlleleBeeSpecies, IIconProvider {

	protected final Random rand = new Random();

	private static final IBeeRoot beeRoot;

	private final IIcon[][] icons = new IIcon[EnumBeeType.VALUES.length][3];
	private final HashMap<ItemStack, Float> specials = new HashMap();
	private final HashMap<ItemStack, Float> products = new HashMap();
	private final IClassification branch;
	private final String scientific;
	private final String genus;
	private final String creator;
	private final String uid;
	private final String name;
	private boolean isRegistered = false;
	private final IAllele[] template = new IAllele[EnumBeeChromosome.values().length];

	static {
		beeRoot = ReikaBeeHelper.getBeeRoot();
	}

	protected BeeSpecies(String name, String uid, String latinName, String creator, IClassification g) {
		branch = g;

		this.name = name;
		this.creator = creator;

		String[] s = latinName.split(" ");
		if (s.length < 2)
			throw new RuntimeException("Bee latin names must be at least two words (genus and species)!");
		genus = s[0];
		String scn = s[1];
		for (int i = 2; i < s.length; i++) {
			scn = scn+" "+s[i];
		}
		scientific = scn;
		this.uid = uid;
	}

	public void register() {
		System.arraycopy(this.getSpeciesTemplate(), 0, template, 0, template.length);
		AlleleManager.alleleRegistry.registerAllele(this, EnumBeeChromosome.SPECIES);
		beeRoot.registerTemplate(template);
		AlleleManager.alleleRegistry.getClassification("family.apidae").addMemberGroup(branch);
		isRegistered = true;
	}

	@Override
	public final String getUnlocalizedName() {
		return uid;
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

	public final void addSpecialty(ItemStack item, float chance) {
		specials.put(item, chance/100F);
	}

	public final void addProduct(ItemStack item, float chance) {
		products.put(item, chance/100F);
	}

	@Override
	public final IClassification getBranch() {
		return branch;
	}

	@Override
	public final Map<ItemStack, Float> getProductChances() {
		return Collections.unmodifiableMap(products);
	}

	@Override
	public final Map<ItemStack, Float> getSpecialtyChances() {
		return Collections.unmodifiableMap(specials);
	}

	public abstract boolean isTolerantFlyer();

	public abstract static class TraitsBee extends BeeSpecies {

		protected final BeeTraits traits;

		protected TraitsBee(String name, String uid, String latinName, String creator, IClassification g, BeeTraits traits) {
			super(name, uid, latinName, creator, g);
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

		@Override
		public final boolean isTolerantFlyer() {
			return traits.isTolerant;
		}
	}

	public static class BeeBranch implements IClassification {

		public final String displayName;
		public final String description;

		private final String uid;
		private final String latinName;

		public BeeBranch(String id, String n, String latin, String desc) {
			displayName = n;
			description = desc;
			latinName = latin;
			uid = id;
		}

		@Override
		public final EnumClassLevel getLevel() {
			return EnumClassLevel.GENUS;
		}

		@Override
		public final String getUID() {
			return uid;
		}

		@Override
		public final String getName() {
			return displayName;
		}

		@Override
		public final String getScientific() {
			return latinName;
		}

		@Override
		public final String getDescription() {
			return description;
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
		IAlleleBeeSpecies p1 = (IAlleleBeeSpecies)AlleleManager.alleleRegistry.getAllele("forestry.species"+parent1);
		IAlleleBeeSpecies p2 = (IAlleleBeeSpecies)AlleleManager.alleleRegistry.getAllele("forestry.species"+parent2);
		if (p1 == null)
			throw new MisuseException("Error breeding from "+parent1+": You cannot breed a bee from null!");
		if (p2 == null)
			throw new MisuseException("Error breeding from "+parent2+": You cannot breed a bee from null!");
		this.addBreeding(p1, p2, chance);
	}

	public final void addBreeding(String parent1, ModList mod1, String parent2, ModList mod2, int chance) {
		IAlleleBeeSpecies p1 = (IAlleleBeeSpecies)AlleleManager.alleleRegistry.getAllele(mod1.modLabel.toLowerCase(Locale.ENGLISH)+".species"+parent1);
		IAlleleBeeSpecies p2 = (IAlleleBeeSpecies)AlleleManager.alleleRegistry.getAllele(mod2.modLabel.toLowerCase(Locale.ENGLISH)+".species"+parent2);
		if (p1 == null)
			throw new MisuseException("Error breeding from "+parent1+": You cannot breed a bee from null!");
		if (p2 == null)
			throw new MisuseException("Error breeding from "+parent2+": You cannot breed a bee from null!");
		this.addBreeding(p1, p2, chance);
	}

	public final void addBreeding(String parent1, ModList mod1, BeeSpecies parent2, int chance) {
		IAlleleBeeSpecies p1 = (IAlleleBeeSpecies)AlleleManager.alleleRegistry.getAllele(mod1.modLabel.toLowerCase(Locale.ENGLISH)+".species"+parent1);
		if (p1 == null)
			throw new MisuseException("Error breeding from "+parent1+": You cannot breed a bee from null!");
		this.addBreeding(p1, parent2, chance);
	}

	public final void addBreeding(String parent1, BeeSpecies parent2, int chance) {
		IAlleleBeeSpecies p1 = (IAlleleBeeSpecies)AlleleManager.alleleRegistry.getAllele("forestry.species"+parent1);
		if (p1 == null)
			throw new MisuseException("Error breeding from "+parent1+": You cannot breed a bee from null!");
		this.addBreeding(p1, parent2, chance);
	}

	public final void addBreeding(BeeSpecies parent1, BeeSpecies parent2, int chance) {
		this.addBreeding((IAlleleBeeSpecies)parent1, (IAlleleBeeSpecies)parent2, chance);
	}

	private final void addBreeding(IAlleleBeeSpecies p1, IAlleleBeeSpecies p2, int chance) {
		if (!isRegistered)
			throw new MisuseException("You must register a bee before adding breeding pairs!");
		if (p1 == null || p2 == null)
			throw new MisuseException("You cannot breed a bee from null!");
		beeRoot.registerMutation(new BeeBreeding(p1, p2, chance, this));
	}

	private static final class BeeBreeding implements IBeeMutation {

		public final IAlleleBeeSpecies parent1;
		public final IAlleleBeeSpecies parent2;
		public final int chance;
		private final BeeSpecies bee;

		private BeeBreeding(IAlleleBeeSpecies p1, IAlleleBeeSpecies p2, int chance, BeeSpecies bee) {
			parent1 = p1;
			parent2 = p2;
			this.chance = chance;
			this.bee = bee;
		}

		@Override
		public IAlleleSpecies getAllele0() {
			return parent1;
		}

		@Override
		public IAlleleSpecies getAllele1() {
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
		public final float getChance(IBeeHousing ibh, IAlleleBeeSpecies ia1, IAlleleBeeSpecies ia2, IBeeGenome ig1, IBeeGenome ig2) {
			return this.isValidParents(ia1, ia2) ? chance : 0;
		}

		private boolean isValidParents(IAllele ia1, IAllele ia2) {
			if (ia1.getUID().equals(parent1.getUID()) && ia2.getUID().equals(parent2.getUID()))
				return true;
			if (ia1.getUID().equals(parent2.getUID()) && ia2.getUID().equals(parent1.getUID()))
				return true;
			return false;
		}
		/*
		@Override
		@Deprecated
		public float getChance(IBeeHousing ibh, IAllele ia1, IAllele ia2, IGenome ig1, IGenome ig2) {
			return this.isValidParents(ia1, ia2) ? chance : 0;
		}
		 */

	}

	public abstract IAllele getFlowerAllele();
	public abstract IAlleleBeeEffect getEffectAllele();
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
				return this.getBeeStripeColor();
			case 2:
				return 0xffffff;
			default:
				return 0xffffff;
		}
	}

	public int getBeeStripeColor() {
		return 0xffff00;
	}

	private final IAllele getGeneForBoolean(boolean b) {
		String s = b ? "forestry.boolTrue" : "forestry.boolFalse";
		return AlleleManager.alleleRegistry.getAllele(s);
	}

	private final IAllele getGeneForInt(int i) {
		return AlleleManager.alleleRegistry.getAllele(String.format("i%dd", i));
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
		alleles[EnumBeeChromosome.TEMPERATURE_TOLERANCE.ordinal()] = ReikaBeeHelper.getToleranceGene(this.getTemperatureToleranceDir(), this.getTemperatureTolerance());
		alleles[EnumBeeChromosome.HUMIDITY_TOLERANCE.ordinal()] = ReikaBeeHelper.getToleranceGene(this.getHumidityToleranceDir(), this.getHumidityTolerance());
		alleles[EnumBeeChromosome.TOLERANT_FLYER.ordinal()] = this.getGeneForBoolean(this.isTolerantFlyer());
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
		String iconType = this.getIconCategory();
		String mod = this.getIconMod();
		String body = this.simplifiedIconSystem() ? "/body" : "/body1";

		IIcon body1 = ico.registerIcon(mod + ":bees/" + iconType + body);
		IIcon larva = ico.registerIcon(mod+":bees/"+iconType+"/"+EnumBeeType.LARVAE.name().toLowerCase(Locale.ENGLISH)+".body");

		for (int i = 0; i < EnumBeeType.VALUES.length; i++) {
			if (EnumBeeType.VALUES[i] != EnumBeeType.NONE) {
				String type = EnumBeeType.VALUES[i].name().toLowerCase(Locale.ENGLISH);
				String out = EnumBeeType.VALUES[i] != EnumBeeType.LARVAE && this.simplifiedIconSystem() ? "outline" : type+".outline";
				icons[i][0] = ico.registerIcon(mod+":bees/"+iconType+"/"+out);
				icons[i][1] = EnumBeeType.VALUES[i] == EnumBeeType.LARVAE ? larva : body1;
				String clas = this.simplifiedIconSystem() ? type : type+".body2";
				icons[i][2] = ico.registerIcon(mod+":bees/"+iconType+"/"+clas);
			}
		}
	}

	protected String getIconMod() {
		return "forestry";
	}

	protected String getIconCategory() {
		return "default";
	}

	protected boolean simplifiedIconSystem() {
		return false;
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
	public ItemStack[] getResearchBounty(World paramWorld, GameProfile f, IIndividual paramIIndividual, int paramInt) {
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
