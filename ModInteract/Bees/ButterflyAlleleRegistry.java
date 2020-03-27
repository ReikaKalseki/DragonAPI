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

import java.util.EnumMap;
import java.util.Locale;

import net.minecraftforge.common.util.EnumHelper;

import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.NestedMap;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleArea;
import forestry.api.genetics.IAlleleFloat;
import forestry.api.genetics.IAlleleFlowers;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IAlleleTolerance;
import forestry.api.lepidopterology.EnumButterflyChromosome;
import forestry.api.lepidopterology.IAlleleButterflyEffect;

/** Butterflies only. */
public class ButterflyAlleleRegistry {

	private static final NestedMap<Class, String, ButterflyGene> geneMap = new NestedMap();
	private static final EnumMap<EnumButterflyChromosome, Class<? extends ButterflyGene>> classTypes = new EnumMap(EnumButterflyChromosome.class);

	static {
		classTypes.put(EnumButterflyChromosome.SPEED, Speeds.class);
		classTypes.put(EnumButterflyChromosome.SIZE, Size.class);
		classTypes.put(EnumButterflyChromosome.LIFESPAN, Life.class);
		classTypes.put(EnumButterflyChromosome.FERTILITY, Fertility.class);
		//classTypes.put(EnumButterflyChromosome.METABOLISM, Metabolisms.class);
		classTypes.put(EnumButterflyChromosome.FLOWER_PROVIDER, Flower.class);
		classTypes.put(EnumButterflyChromosome.TERRITORY, Territory.class);
		classTypes.put(EnumButterflyChromosome.TEMPERATURE_TOLERANCE, Tolerance.class);
		classTypes.put(EnumButterflyChromosome.HUMIDITY_TOLERANCE, Tolerance.class);
		classTypes.put(EnumButterflyChromosome.EFFECT, Effect.class);
	}

	public static Class<? extends ButterflyGene> getEnumType(EnumButterflyChromosome ebc) {
		return classTypes.get(ebc);
	}

	public static ButterflyGene getEnum(EnumButterflyChromosome ebc, String name) {
		return (ButterflyGene)Enum.valueOf((Class<? extends Enum>)getEnumType(ebc), name.toUpperCase(Locale.ENGLISH));
	}

	public static interface ButterflyGene {

		ButterflyGene oneBetter();

		public IAllele getAllele();

	}

	public static enum Speeds implements ButterflyGene {
		SLOWEST("Slowest"),
		SLOWER("Slower"),
		SLOW("Slow"),
		NORMAL("Norm"),
		FAST("Fast"),
		FASTER("Faster"),
		FASTEST("Fastest");

		public final String tag;

		private Speeds(String s) {
			this("forestry", s);
		}

		private Speeds(String pre, String s) {
			tag = pre+"."+"speed"+s;
			register(this, tag);
		}

		public IAlleleFloat getAllele() {
			return (IAlleleFloat)AlleleManager.alleleRegistry.getAllele(tag);
		}

		public static Speeds createNew(String id, float speed, boolean dominant) {
			id = ReikaStringParser.capFirstChar(id);
			IAlleleFloat allele = AlleleManager.alleleFactory.createFloat("dragonapi", "speed", id, speed, dominant, EnumButterflyChromosome.SPEED);
			AlleleManager.alleleRegistry.registerAllele(allele, EnumButterflyChromosome.SPEED);
			return EnumHelper.addEnum(Speeds.class, id.toUpperCase(), new Class[]{String.class, String.class}, new Object[]{"dragonapi", id});
		}

		@Override
		public ButterflyGene oneBetter() {
			return this == FASTEST ? null : values()[this.ordinal()+1];
		}
	}

	public static enum Size implements ButterflyGene {
		SMALLEST("Smallest"),
		SMALLER("Smaller"),
		SMALL("Small"),
		AVERAGE("Average"),
		LARGE("Large"),
		LARGER("Larger"),
		LARGEST("Largest");

		public final String tag;

		private Size(String s) {
			this("forestry", s);
		}

		private Size(String pre, String s) {
			tag = pre+"."+"size"+s;
			register(this, tag);
		}

		public IAlleleFloat getAllele() {
			return (IAlleleFloat)AlleleManager.alleleRegistry.getAllele(tag);
		}

		public static Size createNew(String id, float size, boolean dominant) {
			id = ReikaStringParser.capFirstChar(id);
			IAlleleFloat allele = AlleleManager.alleleFactory.createFloat("dragonapi", "size", id, size, dominant, EnumButterflyChromosome.SIZE);
			AlleleManager.alleleRegistry.registerAllele(allele, EnumButterflyChromosome.SIZE);
			return EnumHelper.addEnum(Size.class, id.toUpperCase(), new Class[]{String.class, String.class}, new Object[]{"dragonapi", id});
		}

		@Override
		public ButterflyGene oneBetter() {
			return this == LARGEST ? null : values()[this.ordinal()+1];
		}
	}

	public static enum Fertility implements ButterflyGene {
		LOW("Low"), //1
		NORMAL("Normal"),
		HIGH("High"),
		MAXIMUM("Maximum"); //4

		public final String tag;

		private Fertility(String s) {
			this("forestry", s);
		}

		private Fertility(String pre, String s) {
			tag = pre+"."+"fertility"+s;
			register(this, tag);
		}

		public IAlleleInteger getAllele() {
			return (IAlleleInteger)AlleleManager.alleleRegistry.getAllele(tag);
		}

		public static Fertility createNew(String id, int drones, boolean dominant) {
			id = ReikaStringParser.capFirstChar(id);
			IAlleleInteger allele = AlleleManager.alleleFactory.createInteger("dragonapi", "fertility", id, drones, dominant, EnumButterflyChromosome.FERTILITY);
			AlleleManager.alleleRegistry.registerAllele(allele, EnumButterflyChromosome.FERTILITY);
			return EnumHelper.addEnum(Fertility.class, id.toUpperCase(), new Class[]{String.class, String.class}, new Object[]{"dragonapi", id});
		}

		@Override
		public ButterflyGene oneBetter() {
			return this == MAXIMUM ? null : values()[this.ordinal()+1];
		}

		public static Fertility getFromAllele(IAllele gene) {
			for (Fertility f : values())
				if (f.tag.equals(gene.getUID()))
					return f;
			return null;
		}
	}

	public static enum Flower implements ButterflyGene {
		VANILLA("Vanilla"),
		NETHER("Nether"),
		CACTUS("Cacti"),
		MUSHROOM("Mushrooms"),
		ENDER("End"),
		JUNGLE("Jungle"),
		SNOW("Snow"),
		WHEAT("Wheat"),
		GOURD("Gourd");

		public final String tag;

		private Flower(String s) {
			this("forestry", s);
		}

		private Flower(String pre, String s) {
			tag = pre+"."+"flowers"+s;
			register(this, tag);
		}

		public IAlleleFlowers getAllele() {
			return (IAlleleFlowers)AlleleManager.alleleRegistry.getAllele(tag);
		}

		@Override
		public ButterflyGene oneBetter() {
			return null;
		}
	}

	public static enum Territory implements ButterflyGene {
		DEFAULT("Default"), //9-6-9
		LARGE("Large"),
		LARGER("Larger"),
		LARGEST("Largest"); //15-13-15

		public final String tag;

		private Territory(String s) {
			this("forestry", s);
		}

		private Territory(String pre, String s) {
			tag = pre+"."+"territory"+s;
			register(this, tag);
		}

		public IAlleleArea getAllele() {
			return (IAlleleArea)AlleleManager.alleleRegistry.getAllele(tag);
		}

		/** = 9+2*ordinal */
		public Coordinate getRange() {
			int[] val = this.getAllele().getValue();
			return new Coordinate(val[0], val[1], val[2]);
		}

		public static Territory createNew(String id, int rangeXZ, int rangeY, boolean dominant) {
			id = ReikaStringParser.capFirstChar(id);
			IAlleleArea allele = AlleleManager.alleleFactory.createArea("dragonapi", "territory", id, rangeXZ, rangeY, rangeXZ, dominant, EnumButterflyChromosome.TERRITORY);
			AlleleManager.alleleRegistry.registerAllele(allele, EnumButterflyChromosome.TERRITORY);
			return EnumHelper.addEnum(Territory.class, id.toUpperCase(), new Class[]{String.class, String.class}, new Object[]{"dragonapi", id});
		}

		@Override
		public ButterflyGene oneBetter() {
			return this == LARGEST ? null : values()[this.ordinal()+1];
		}
	}

	public static enum Life implements ButterflyGene {
		SHORTEST("Shortest"), //10
		SHORTER("Shorter"),
		SHORT("Short"),
		SHORTENED("Shortened"),
		NORMAL("Normal"),
		ELONGATED("Elongated"),
		LONG("Long"),
		LONGER("Longer"),
		LONGEST("Longest"); //70

		public final String tag;

		private Life(String s) {
			this("forestry", s);
		}

		private Life(String pre, String s) {
			tag = pre+"."+"lifespan"+s;
			register(this, tag);
		}

		public IAlleleInteger getAllele() {
			return (IAlleleInteger)AlleleManager.alleleRegistry.getAllele(tag);
		}

		public static Life createNew(String id, int life, boolean dominant) {
			id = ReikaStringParser.capFirstChar(id);
			IAlleleInteger allele = AlleleManager.alleleFactory.createInteger("dragonapi", "lifespan", id, life, dominant, EnumButterflyChromosome.LIFESPAN);
			AlleleManager.alleleRegistry.registerAllele(allele, EnumButterflyChromosome.LIFESPAN);
			return EnumHelper.addEnum(Life.class, id.toUpperCase(), new Class[]{String.class, String.class}, new Object[]{"dragonapi", id});
		}

		@Override
		public ButterflyGene oneBetter() {
			return null;
		}
	}
	/*
	public static enum Metabolisms implements ButterflyGene {
		SLOWEST("Slowest"),
		SLOWER("Slower"),
		SLOW("Slow"),
		NORMAL("Norm"),
		FAST("Fast"),
		FASTER("Faster"),
		FASTEST("Fastest");

		public final String tag;

		private Metabolisms(String s) {
			this("forestry", s);
		}

		private Metabolisms(String pre, String s) {
			tag = pre+"."+"metabolism"+s;
			register(this, tag);
		}

		public IAlleleInteger getAllele() {
			return (IAlleleInteger)AlleleManager.alleleRegistry.getAllele(tag);
		}

		public static Metabolisms createNew(String id, int Metabolisms, boolean dominant) {
			id = ReikaStringParser.capFirstChar(id);
			IAlleleInteger allele = AlleleManager.alleleFactory.createInteger("dragonapi", "metabolism", id, Metabolisms, dominant, EnumButterflyChromosome.METABOLISM);
			AlleleManager.alleleRegistry.registerAllele(allele, EnumButterflyChromosome.METABOLISM);
			return EnumHelper.addEnum(Metabolisms.class, id.toUpperCase(), new Class[]{String.class, String.class}, new Object[]{"dragonapi", id});
		}

		@Override
		public ButterflyGene oneBetter() {
			return null;
		}

		public static Metabolisms getFromAllele(IAllele gene) {
			for (Metabolisms f : values())
				if (f.tag.equals(gene.getUID()))
					return f;
			return null;
		}
	}
	 */
	public static enum Tolerance implements ButterflyGene {
		UP("Up"),
		DOWN("Down"),
		BOTH("Both"),
		NONE("None");

		public final String tag;

		private Tolerance(String s) {
			this("forestry", s);
		}

		private Tolerance(String pre, String s) {
			tag = pre+"."+"tolerance"+s;
			register(this, tag);
		}

		public IAlleleTolerance getAllele() {
			return (IAlleleTolerance)AlleleManager.alleleRegistry.getAllele(tag);
		}
		/*
		public static Tolerance createNew(String id, ToleranceCheck t, boolean dominant) {
			id = ReikaStringParser.capFirstChar(id);
			Tolerance allele = AlleleManager.alleleFactory.createTolerance("dragonapi", "lifespan", id, life, dominant, EnumButterflyChromosome.);
			AlleleManager.alleleRegistry.registerAllele(allele, EnumButterflyChromosome.TEMPERATURE_TOLERANCE, EnumButterflyChromosome.HUMIDITY_TOLERANCE);
			return EnumHelper;
		}*/

		@Override
		public ButterflyGene oneBetter() {
			return null;
		}
	}

	public static enum Effect implements ButterflyGene {
		NONE("effectNone");

		public final String tag;

		private Effect(String s) {
			this("forestry", s);
		}

		private Effect(String pre, String s) {
			tag = pre+"."+s;
			register(this, tag);
		}

		public IAlleleButterflyEffect getAllele() {
			return (IAlleleButterflyEffect)AlleleManager.alleleRegistry.getAllele(tag);
		}

		@Override
		public ButterflyGene oneBetter() {
			return null;
		}
	}

	private static void register(ButterflyGene g, String n) {
		geneMap.put(g.getClass(), n, g);
	}

	public static ButterflyGene getEnum(IAllele allele, Class<? extends ButterflyGene> type) {
		return geneMap.get(type, allele.getUID());
	}
}
