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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
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
import forestry.api.genetics.IGenome;

public abstract class BeeSpecies implements IAlleleBeeSpecies, IIconProvider {

	private final IBeeRoot beeRoot;
	private IAllele[] template;
	private final Icon[][] icons = new Icon[EnumBeeType.VALUES.length][3];
	private final HashMap specials = new HashMap();
	private final HashMap products = new HashMap();

	protected BeeSpecies() {
		beeRoot = (IBeeRoot)AlleleManager.alleleRegistry.getSpeciesRoot("rootBees");
		template = this.getSpeciesTemplate();
		AlleleManager.alleleRegistry.registerAllele(this);

		beeRoot.registerTemplate(template);
	}

	public final void addSpecialty(ItemStack item, int chance) {
		specials.put(item, chance);
	}

	public final void addProduct(ItemStack item, int chance) {
		products.put(item, chance);
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

	public final void addBreeding(String parent1, String parent2, int chance) {
		beeRoot.registerMutation(new BeeBreeding(parent1, parent2, chance));
	}

	private final class BeeBreeding implements IBeeMutation {

		public final IAllele parent1;
		public final IAllele parent2;
		public final int chance;

		protected BeeBreeding(String p1, String p2, int chance) {
			parent1 = AlleleManager.alleleRegistry.getAllele("forestry.species"+p1);
			parent2 = AlleleManager.alleleRegistry.getAllele("forestry.species"+p2);
			this.chance = chance;
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
			return false;
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
		SLOWER("fertilitySlower"),
		SLOW("fertilitySlow"),
		AVERAGE("fertilityAverage"),
		FAST("fertilityFast"),
		FASTER("fertilityFaster"),
		FASTEST("fertilityFastest"),
		MAXIMUM("fertilityMaximum"); //"gui.maximum"

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

	public abstract IAllele getFlowerAllele();
	public abstract Speeds getProductionSpeed();
	public abstract Fertility getFertility();
	public abstract Flowering getFloweringRate();
	public abstract Life getLifespan();
	public abstract Territory getTerritorySize();
	public abstract boolean isCaveDwelling();
	public abstract int getTemperatureTolerance();
	public abstract int getHumidityTolerance();

	private final IAllele getGeneForBoolean(boolean b) {
		String s = b ? "forestry.boolTrue" : "forestry.boolFalse";
		return AlleleManager.alleleRegistry.getAllele(s);
	}

	private final IAllele getGeneForInt(int i) {
		return AlleleManager.alleleRegistry.getAllele(String.format("int%d", i));
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
		alleles[EnumBeeChromosome.NOCTURNAL.ordinal()] = this.getGeneForBoolean(this.isNocturnal());
		alleles[EnumBeeChromosome.CAVE_DWELLING.ordinal()] = this.getGeneForBoolean(this.isCaveDwelling());
		alleles[EnumBeeChromosome.TEMPERATURE_TOLERANCE.ordinal()] = this.getGeneForInt(this.getTemperatureTolerance());
		alleles[EnumBeeChromosome.HUMIDITY_TOLERANCE.ordinal()] = this.getGeneForInt(this.getHumidityTolerance());
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
	public void registerIcons(IconRegister ico) {
		String iconType = "default";
		String mod = "forestry";

		Icon body1 = ico.registerIcon(mod + ":bees/" + iconType + "/body1");

		for(int i = 0; i < EnumBeeType.values().length; i++)
			if(EnumBeeType.values()[i] != EnumBeeType.NONE) {
				icons[i][0] = ico.registerIcon(mod + ":bees/" + iconType + "/" + EnumBeeType.values()[i].toString().toLowerCase() + ".outline");
				icons[i][1] = (EnumBeeType.values()[i] != EnumBeeType.LARVAE ? body1 : ico.registerIcon(mod + ":bees/" + iconType + "/" + EnumBeeType.values()[i].toString().toLowerCase() + ".body"));
				icons[i][2] = ico.registerIcon(mod + ":bees/" + iconType + "/" + EnumBeeType.values()[i].toString().toLowerCase() + ".body2");
			}
	}

	@Override
	public Icon getIcon(EnumBeeType type, int renderPass) {
		return icons[type.ordinal()][renderPass];
	}

}
