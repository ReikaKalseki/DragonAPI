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
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.Territory;
import Reika.DragonAPI.ModInteract.Bees.TreeAlleleRegistry.Heights;
import Reika.DragonAPI.ModInteract.Bees.TreeAlleleRegistry.Maturation;
import Reika.DragonAPI.ModInteract.Bees.TreeAlleleRegistry.Saplings;
import Reika.DragonAPI.ModInteract.Bees.TreeAlleleRegistry.Sappiness;
import Reika.DragonAPI.ModInteract.Bees.TreeAlleleRegistry.Yield;
import Reika.DragonAPI.ModInteract.ItemHandlers.ForestryHandler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.arboriculture.IAlleleGrowth;
import forestry.api.arboriculture.IAlleleLeafEffect;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.ITreeGenerator;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.arboriculture.ITreeMutation;
import forestry.api.arboriculture.ITreeRoot;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.IIconProvider;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IClassification;
import forestry.api.genetics.IFruitFamily;
import forestry.api.genetics.IIndividual;
import forestry.api.world.ITreeGenData;

public abstract class TreeSpecies implements IAlleleTreeSpecies, IIconProvider {

	protected final Random rand = new Random();

	private static final ITreeRoot treeRoot;

	private final IClassification branch;
	private final String scientific;
	private final String creator;
	private final String uid;
	private final String name;

	private final IAllele[] template = new IAllele[EnumTreeChromosome.values().length];
	private final ITreeGenerator generator = new ForestryTreeGenerator();
	private final HashSet<IFruitFamily> suitableFruits = new HashSet();

	private boolean isRegistered = false;
	private final IIcon[] pollenIcons = new IIcon[2];
	private IIcon saplingIcon;

	static {
		treeRoot = ReikaBeeHelper.getTreeRoot();
	}

	protected TreeSpecies(String name, String uid, String latinName, String creator, IClassification g) {
		branch = g;

		this.name = name;
		this.creator = creator;
		scientific = latinName;
		this.uid = uid;
	}

	public final void register() {
		System.arraycopy(this.getSpeciesTemplate(), 0, template, 0, template.length);
		AlleleManager.alleleRegistry.registerAllele(this, EnumTreeChromosome.SPECIES);
		treeRoot.registerTemplate(template);
		//AlleleManager.alleleRegistry.getClassification("family.apidae").addMemberGroup(branch);
		isRegistered = true;
		suitableFruits.add(this.getFruitAllele().getProvider().getFamily());
		this.onRegister();
	}

	protected void onRegister() {

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

	public static class TreeBranch implements IClassification {

		public final String displayName;
		public final String description;

		private final String uid;
		private final String latinName;

		public TreeBranch(String id, String n, String latin, String desc) {
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

	public final ItemStack getTreeItem(World world, EnumGermlingType type) {
		return treeRoot.getMemberStack(treeRoot.getTree(world, treeRoot.templateAsGenome(template)), type.ordinal());
	}

	public final IAlleleFruit getNoFruit() {
		return (IAlleleFruit)AlleleManager.alleleRegistry.getAllele("forestry.fruitNone");
	}

	public final IAlleleLeafEffect getNoEffect() {
		return (IAlleleLeafEffect)AlleleManager.alleleRegistry.getAllele("forestry.leavesNone");
	}

	public final IAlleleGrowth getLightGrowth() {
		return (IAlleleGrowth)AlleleManager.alleleRegistry.getAllele("forestry.growthLightlevel");
	}

	public final void addBreeding(TreeSpecies parent1, TreeSpecies parent2, int chance) {
		this.addBreeding((IAlleleTreeSpecies)parent1, (IAlleleTreeSpecies)parent2, chance);
	}

	public final void addBreeding(IAlleleTreeSpecies p1, IAlleleTreeSpecies p2, int chance) {
		if (!isRegistered)
			throw new MisuseException("You must register a tree before adding breeding pairs!");
		if (p1 == null || p2 == null)
			throw new MisuseException("You cannot breed a tree from null!");
		treeRoot.registerMutation(new TreeBreeding(p1, p2, chance, this));
	}

	public final void addSuitableFruit(IFruitFamily fam) {
		suitableFruits.add(fam);
	}

	public final ITree constructIndividual() {
		return treeRoot.templateAsIndividual(template);
	}

	public final void setLeaves(ITreeGenome genome, World world, GameProfile owner, int x, int y, int z, boolean decorative) {
		generator.setLeaves(genome, world, owner, x, y, z, decorative);
	}

	private static final class TreeBreeding implements ITreeMutation {

		public final IAlleleTreeSpecies parent1;
		public final IAlleleTreeSpecies parent2;
		public final int chance;
		private final TreeSpecies tree;

		private TreeBreeding(IAlleleTreeSpecies p1, IAlleleTreeSpecies p2, int chance, TreeSpecies tree) {
			parent1 = p1;
			parent2 = p2;
			this.chance = chance;
			this.tree = tree;
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
			return tree.template;
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
			return tree.isSecret();
		}

		@Override
		public ITreeRoot getRoot() {
			return tree.getRoot();
		}

		@Override
		public final float getChance(World world, int x, int y, int z, IAlleleTreeSpecies ia1, IAlleleTreeSpecies ia2, ITreeGenome ig1, ITreeGenome ig2) {
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

	public final EnumTemperature getTemperature() {
		switch(this.getPlantType()) {
			case Cave:
				return EnumTemperature.COLD;
			case Desert:
				return EnumTemperature.HOT;
			case Nether:
				return EnumTemperature.HELLISH;
			case Beach:
			case Plains:
			case Water:
			case Crop:
				return EnumTemperature.NORMAL;
			default:
				return EnumTemperature.NONE;
		}
	}

	public final EnumHumidity getHumidity() {
		switch(this.getPlantType()) {
			case Nether:
			case Desert:
				return EnumHumidity.ARID;
			case Beach:
			case Water:
				return EnumHumidity.DAMP;
			case Cave:
			case Plains:
			case Crop:
			default:
				return EnumHumidity.NORMAL;
		}
	}

	@Override
	public final Collection<IFruitFamily> getSuitableFruit() {
		return Collections.unmodifiableCollection(suitableFruits);
	}

	@Override
	public final ITreeGenerator getGenerator() {
		return generator;
	}

	public abstract IAlleleFruit getFruitAllele();
	public abstract IAlleleLeafEffect getEffectAllele();
	public abstract IAlleleGrowth getGrowthAllele();
	public abstract Yield getYield();
	public abstract Heights getHeight();
	public abstract int getGirth();
	public abstract Sappiness getSappiness();
	public abstract Maturation getMaturation();
	public abstract Saplings getSaplingRate();
	public abstract Territory getTerritorySize();
	public abstract boolean isFireproof();

	protected final IAllele[] getSpeciesTemplate() {
		IAllele[] alleles = treeRoot.getDefaultTemplate();
		alleles[EnumTreeChromosome.SPECIES.ordinal()] = this;
		alleles[EnumTreeChromosome.FRUITS.ordinal()] = this.getFruitAllele();
		alleles[EnumTreeChromosome.YIELD.ordinal()] = this.getYield().getAllele();
		alleles[EnumTreeChromosome.MATURATION.ordinal()] = this.getMaturation().getAllele();
		alleles[EnumTreeChromosome.TERRITORY.ordinal()] = this.getTerritorySize().getAllele();
		alleles[EnumTreeChromosome.SAPPINESS.ordinal()] = this.getSappiness().getAllele();
		alleles[EnumTreeChromosome.HEIGHT.ordinal()] = this.getHeight().getAllele();
		alleles[EnumTreeChromosome.FERTILITY.ordinal()] = this.getSaplingRate().getAllele();
		alleles[EnumTreeChromosome.EFFECT.ordinal()] = this.getEffectAllele();
		alleles[EnumTreeChromosome.GIRTH.ordinal()] = ReikaBeeHelper.getIntegerAllele(this.getGirth());
		alleles[EnumTreeChromosome.PLANT.ordinal()] = ReikaBeeHelper.getAlleleForPlantType(this.getPlantType());
		alleles[EnumTreeChromosome.GROWTH.ordinal()] = this.getGrowthAllele();
		alleles[EnumTreeChromosome.FIREPROOF.ordinal()] = ReikaBeeHelper.getBooleanAllele(this.isFireproof());
		return alleles;
	}

	@Override
	public final ITreeRoot getRoot() {
		return treeRoot;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final IIconProvider getIconProvider() {
		return this;
	}

	@Override
	public final void registerIcons(IIconRegister ico) {
		saplingIcon = ico.registerIcon(this.getIconMod(false)+":"+this.getIconFolderRoot(false)+"/"+this.getSaplingIconName());
		for (int i = 0; i < pollenIcons.length; i++)
			pollenIcons[i] = ico.registerIcon(this.getIconMod(true)+":"+this.getIconFolderRoot(true)+"/pollen."+i);
	}

	protected String getIconMod(boolean pollen) {
		return "forestry";
	}

	protected String getIconFolderRoot(boolean pollen) {
		return "germlings";
	}

	protected String getSaplingIconName() {
		return "sapling."+uid.toLowerCase(Locale.ENGLISH);
	}

	@Override
	public final IIcon getGermlingIcon(EnumGermlingType type, int renderPass) {
		switch(type) {
			case SAPLING:
				return saplingIcon;
			case POLLEN:
				return pollenIcons[renderPass];
			default:
				return ReikaTextureHelper.getMissingIcon();
		}
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

	private class ForestryTreeGenerator extends WorldGenerator implements ITreeGenerator {

		private ITreeGenData data;

		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			data = tree;
			return this;
		}

		@Override
		public void setLogBlock(ITreeGenome genome, World world, int x, int y, int z, ForgeDirection facing) {
			BlockKey bk = TreeSpecies.this.getLogBlock(genome, world, x, y, z, rand, data);
			if (bk != null) {
				bk.place(world, x, y, z);
			}
		}

		@Override
		public void setLeaves(ITreeGenome genome, World world, GameProfile owner, int x, int y, int z, boolean decorative) {
			if (world.setBlock(x, y, z, ForestryHandler.BlockEntry.LEAF.getBlock())) {
				TileEntity te = world.getTileEntity(x, y, z);
				ReikaBeeHelper.setTree(te, TreeManager.treeRoot.getTree(world, genome));
				ReikaBeeHelper.setTreeOwner(te, owner);
				ReikaBeeHelper.setTreeLeafDecorative(te, decorative);
			}
		}

		@Override
		public void setLogBlock(World world, int x, int y, int z, ForgeDirection facing) {
			this.setLogBlock(TreeManager.treeRoot.templateAsGenome(template), world, x, y, z, facing);
		}

		@Override
		public void setLeaves(World world, GameProfile owner, int x, int y, int z, boolean decorative) {
			this.setLeaves(TreeManager.treeRoot.templateAsGenome(template), world, owner, x, y, z, decorative);
		}

		@Override
		public boolean generate(World world, Random rand, int x, int y, int z) {
			return TreeSpecies.this.generate(world, x, y, z, rand, data);
		}

	}

	protected abstract BlockKey getLogBlock(ITreeGenome genes, World world, int x, int y, int z, Random rand, ITreeGenData data);

	protected abstract boolean generate(World world, int x, int y, int z, Random rand, ITreeGenData data);

	public static abstract class BasicTreeSpecies extends TreeSpecies {

		protected BasicTreeSpecies(String name, String uid, String latinName, String creator, IClassification g) {
			super(name, uid, latinName, creator, g);
		}

		@Override
		public EnumPlantType getPlantType() {
			return EnumPlantType.Plains;
		}

		@Override
		public IAlleleFruit getFruitAllele() {
			return this.getNoFruit();
		}

		@Override
		public IAlleleLeafEffect getEffectAllele() {
			return this.getNoEffect();
		}

		@Override
		public IAlleleGrowth getGrowthAllele() {
			return this.getLightGrowth();
		}

		@Override
		public boolean isFireproof() {
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

	}

	public abstract static class TraitsTree extends TreeSpecies {

		protected final TreeTraits traits;

		protected TraitsTree(String name, String uid, String latinName, String creator, IClassification g, TreeTraits traits) {
			super(name, uid, latinName, creator, g);
			this.traits = traits;
		}

		public final EnumPlantType getPlantType() {
			return traits.plant;
		}

		@Override
		public final Yield getYield() {
			return traits.yield;
		}

		@Override
		public final Heights getHeight() {
			return traits.height;
		}

		@Override
		public final int getGirth() {
			return traits.girth;
		}

		@Override
		public final Sappiness getSappiness() {
			return traits.sappiness;
		}

		@Override
		public final Maturation getMaturation() {
			return traits.maturation;
		}

		@Override
		public final Saplings getSaplingRate() {
			return traits.fertility;
		}

		@Override
		public final Territory getTerritorySize() {
			return traits.area;
		}

		@Override
		public final boolean isFireproof() {
			return traits.isFireproof;
		}

	}

}
