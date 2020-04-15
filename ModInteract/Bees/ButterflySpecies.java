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

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.mojang.authlib.GameProfile;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary.Type;

import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.Tolerance;
import Reika.DragonAPI.ModInteract.Bees.ButterflyAlleleRegistry.Fertility;
import Reika.DragonAPI.ModInteract.Bees.ButterflyAlleleRegistry.Flower;
import Reika.DragonAPI.ModInteract.Bees.ButterflyAlleleRegistry.Life;
import Reika.DragonAPI.ModInteract.Bees.ButterflyAlleleRegistry.Size;
import Reika.DragonAPI.ModInteract.Bees.ButterflyAlleleRegistry.Speeds;
import Reika.DragonAPI.ModInteract.Bees.ButterflyAlleleRegistry.Territory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.api.core.IIconProvider;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleFlowers;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IClassification;
import forestry.api.genetics.IIndividual;
import forestry.api.lepidopterology.EnumButterflyChromosome;
import forestry.api.lepidopterology.IAlleleButterflyEffect;
import forestry.api.lepidopterology.IAlleleButterflySpecies;
import forestry.api.lepidopterology.IButterfly;
import forestry.api.lepidopterology.IButterflyRoot;

public abstract class ButterflySpecies implements IAlleleButterflySpecies {

	protected final Random rand = new Random();

	private static final IButterflyRoot butterflyRoot;

	private final IClassification branch;
	private final String scientific;
	private final String creator;
	private final String uid;
	private final String name;
	private boolean isRegistered = false;
	private final IAllele[] template = new IAllele[EnumButterflyChromosome.values().length];

	private final HashMap<ItemStack, Float> drops = new HashMap();
	private final HashMap<ItemStack, Float> caterpillarDrops = new HashMap();

	static {
		butterflyRoot = ReikaBeeHelper.getButterflyRoot();
	}

	protected ButterflySpecies(String name, String uid, String latinName, String creator, IClassification g) {
		branch = g;

		this.name = name;
		this.creator = creator;
		scientific = latinName;
		this.uid = uid;
	}

	public final void register() {
		System.arraycopy(this.getSpeciesTemplate(), 0, template, 0, template.length);
		AlleleManager.alleleRegistry.registerAllele(this, EnumButterflyChromosome.SPECIES);
		butterflyRoot.registerTemplate(template);
		//AlleleManager.alleleRegistry.getClassification("family.apidae").addMemberGroup(branch);
		isRegistered = true;
		this.onRegister();
	}

	protected void onRegister() {

	}

	public final void addDrop(ItemStack item, float chance) {
		drops.put(item, chance/100F);
	}

	public final void addCaterpillarDrop(ItemStack item, float chance) {
		caterpillarDrops.put(item, chance/100F);
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

	@Override
	public final IClassification getBranch() {
		return branch;
	}

	@Override
	public final Map<ItemStack, Float> getButterflyLoot() {
		return Collections.unmodifiableMap(drops);
	}

	@Override
	public final Map<ItemStack, Float> getCaterpillarLoot() {
		return Collections.unmodifiableMap(caterpillarDrops);
	}

	public static class ButterflyBranch implements IClassification {

		public final String displayName;
		public final String description;

		private final String uid;
		private final String latinName;

		public ButterflyBranch(String id, String n, String latin, String desc) {
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
	/*
	public final ItemStack getButterflyItem(World world, EnumFlutterType type) {
		return butterflyRoot.getMemberStack(butterflyRoot.getButterfly(world, butterflyRoot.templateAsGenome(template)), type.ordinal());
	}
	 */
	public final IButterfly constructIndividual() {
		return butterflyRoot.templateAsIndividual(template);
	}

	public final IAlleleButterflyEffect getNoEffect() {
		return (IAlleleButterflyEffect)AlleleManager.alleleRegistry.getAllele("forestry.bfNone");
	}

	public final IAlleleFlowers getBasicFlowers() {
		return Flower.VANILLA.getAllele();
	}

	public abstract boolean isNocturnal();
	public abstract boolean isFireproof();
	public abstract boolean isTolerantFlyer();
	public abstract int getMetabolism();
	public abstract int getTemperatureTolerance();
	public abstract int getHumidityTolerance();
	public abstract Tolerance getHumidityToleranceDir();
	public abstract Tolerance getTemperatureToleranceDir();
	public abstract Speeds getSpeed();
	public abstract Size getSize();
	public abstract Fertility getFertility();
	public abstract Territory getTerritorySize();
	public abstract Life getLifespan();
	public abstract IAlleleButterflyEffect getEffect();
	public abstract IAlleleFlowers getFlowerAllele();
	//public abstract Metabolisms getMetabolism();


	protected final IAllele[] getSpeciesTemplate() {
		IAllele[] alleles = butterflyRoot.getDefaultTemplate();
		alleles[EnumButterflyChromosome.SPECIES.ordinal()] = this;
		alleles[EnumButterflyChromosome.SIZE.ordinal()] = this.getSize().getAllele();
		alleles[EnumButterflyChromosome.SPEED.ordinal()] = this.getSpeed().getAllele();
		alleles[EnumButterflyChromosome.LIFESPAN.ordinal()] = this.getLifespan().getAllele();
		alleles[EnumButterflyChromosome.METABOLISM.ordinal()] = ReikaBeeHelper.getIntegerAllele(this.getMetabolism());
		alleles[EnumButterflyChromosome.FERTILITY.ordinal()] = this.getFertility().getAllele();
		alleles[EnumButterflyChromosome.TEMPERATURE_TOLERANCE.ordinal()] = ReikaBeeHelper.getToleranceGene(this.getTemperatureToleranceDir(), this.getTemperatureTolerance());
		alleles[EnumButterflyChromosome.HUMIDITY_TOLERANCE.ordinal()] = ReikaBeeHelper.getToleranceGene(this.getHumidityToleranceDir(), this.getHumidityTolerance());
		alleles[EnumButterflyChromosome.NOCTURNAL.ordinal()] = ReikaBeeHelper.getBooleanAllele(this.isNocturnal());
		alleles[EnumButterflyChromosome.TOLERANT_FLYER.ordinal()] = ReikaBeeHelper.getBooleanAllele(this.isTolerantFlyer());
		alleles[EnumButterflyChromosome.FIRE_RESIST.ordinal()] = ReikaBeeHelper.getBooleanAllele(this.isFireproof());
		alleles[EnumButterflyChromosome.FLOWER_PROVIDER.ordinal()] = this.getFlowerAllele();
		alleles[EnumButterflyChromosome.EFFECT.ordinal()] = this.getEffect();
		alleles[EnumButterflyChromosome.TERRITORY.ordinal()] = this.getTerritorySize().getAllele();
		return alleles;
	}

	@Override
	public final IButterflyRoot getRoot() {
		return butterflyRoot;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final IIconProvider getIconProvider() {
		return null;
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

	public static abstract class BasicButterflySpecies extends ButterflySpecies {

		protected BasicButterflySpecies(String name, String uid, String latinName, String creator, IClassification g) {
			super(name, uid, latinName, creator, g);
		}

		@Override
		public boolean isNocturnal() {
			return false;
		}

		@Override
		public boolean hasEffect() {
			return false;
		}

		@Override
		public boolean isSecret() {
			return false;
		}

		@Override
		public boolean isCounted() {
			return true;
		}

		@Override
		public EnumSet<Type> getSpawnBiomes() {
			return EnumSet.noneOf(Type.class);
		}

		@Override
		public boolean strictSpawnMatch() {
			return false;
		}

		@Override
		public int getIconColour(int renderPass) {
			return 0xffffff;
		}

		@Override
		public boolean isFireproof() {
			return false;
		}

		@Override
		public boolean isTolerantFlyer() {
			return false;
		}

		@Override
		public IAlleleButterflyEffect getEffect() {
			return this.getNoEffect();
		}

		@Override
		public IAlleleFlowers getFlowerAllele() {
			return this.getBasicFlowers();
		}

	}

}
