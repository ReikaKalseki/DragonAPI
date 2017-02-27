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

import java.util.EnumMap;
import java.util.Locale;

import net.minecraftforge.common.util.EnumHelper;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.NestedMap;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.arboriculture.IAlleleGrowth;
import forestry.api.arboriculture.IAlleleLeafEffect;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleArea;
import forestry.api.genetics.IAlleleFloat;
import forestry.api.genetics.IAlleleInteger;

/** Trees only. */
public class TreeAlleleRegistry {

	private static final NestedMap<Class, String, TreeGene> geneMap = new NestedMap();
	private static final EnumMap<EnumTreeChromosome, Class<? extends TreeGene>> classTypes = new EnumMap(EnumTreeChromosome.class);

	static {
		classTypes.put(EnumTreeChromosome.HEIGHT, Heights.class);
		classTypes.put(EnumTreeChromosome.FERTILITY, Fertility.class);
		classTypes.put(EnumTreeChromosome.SAPPINESS, Sappiness.class);
		classTypes.put(EnumTreeChromosome.YIELD, Yield.class);
		classTypes.put(EnumTreeChromosome.FRUITS, Fruit.class);
		classTypes.put(EnumTreeChromosome.GIRTH, Girth.class);
		classTypes.put(EnumTreeChromosome.MATURATION, Maturation.class);
		classTypes.put(EnumTreeChromosome.TERRITORY, Territory.class);
		classTypes.put(EnumTreeChromosome.GROWTH, Growth.class);
		classTypes.put(EnumTreeChromosome.EFFECT, Effect.class);
	}

	public static Class<? extends TreeGene> getEnumType(EnumTreeChromosome ebc) {
		return classTypes.get(ebc);
	}

	public static TreeGene getEnum(EnumTreeChromosome ebc, String name) {
		return (TreeGene)Enum.valueOf((Class<? extends Enum>)getEnumType(ebc), name.toUpperCase(Locale.ENGLISH));
	}

	public static interface TreeGene {

		TreeGene oneBetter();

		public IAllele getAllele();

	}

	public static enum Heights implements TreeGene {
		SMALLEST("Smallest"),
		SMALLER("Smaller"),
		SMALL("Small"),
		AVERAGE("Average"),
		LARGE("Large"),
		LARGER("Larger"),
		LARGEST("Largest");

		public final String tag;

		private Heights(String s) {
			this("forestry", s);
		}

		private Heights(String pre, String s) {
			tag = pre+"."+"height"+s;
			register(this, tag);
		}

		public IAlleleFloat getAllele() {
			return (IAlleleFloat)AlleleManager.alleleRegistry.getAllele(tag);
		}

		public static Heights createNew(String id, float speed, boolean dominant) {
			id = ReikaStringParser.capFirstChar(id);
			IAlleleFloat allele = AlleleManager.alleleFactory.createFloat("dragonapi", "height", id, speed, dominant, EnumTreeChromosome.HEIGHT);
			AlleleManager.alleleRegistry.registerAllele(allele, EnumTreeChromosome.HEIGHT);
			return EnumHelper.addEnum(Heights.class, id.toUpperCase(), new Class[]{String.class, String.class}, new Object[]{"dragonapi", id});
		}

		@Override
		public TreeGene oneBetter() {
			return this == LARGEST ? null : values()[this.ordinal()+1];
		}
	}

	public static enum Fertility implements TreeGene {
		LOWEST("Lowest"),
		LOWER("Lower"),
		LOW("Low"),
		HIGH("High"),
		HIGHER("Higher"),
		HIGHEST("Highest");

		public final String tag;

		private Fertility(String s) {
			this("forestry", s);
		}

		private Fertility(String pre, String s) {
			tag = pre+"."+"fertility"+s;
			register(this, tag);
		}

		public IAlleleFloat getAllele() {
			return (IAlleleFloat)AlleleManager.alleleRegistry.getAllele(tag);
		}

		public static Fertility createNew(String id, float speed, boolean dominant) {
			id = ReikaStringParser.capFirstChar(id);
			IAlleleFloat allele = AlleleManager.alleleFactory.createFloat("dragonapi", "fertility", id, speed, dominant, EnumTreeChromosome.FERTILITY);
			AlleleManager.alleleRegistry.registerAllele(allele, EnumTreeChromosome.FERTILITY);
			return EnumHelper.addEnum(Fertility.class, id.toUpperCase(), new Class[]{String.class, String.class}, new Object[]{"dragonapi", id});
		}

		@Override
		public TreeGene oneBetter() {
			return this == HIGHEST ? null : values()[this.ordinal()+1];
		}
	}

	public static enum Sappiness implements TreeGene {
		LOWEST("Lowest"),
		LOWER("Lower"),
		LOW("Low"),
		HIGH("High"),
		HIGHER("Higher"),
		HIGHEST("Highest");

		public final String tag;

		private Sappiness(String s) {
			this("forestry", s);
		}

		private Sappiness(String pre, String s) {
			tag = pre+"."+"sappiness"+s;
			register(this, tag);
		}

		public IAlleleFloat getAllele() {
			return (IAlleleFloat)AlleleManager.alleleRegistry.getAllele(tag);
		}

		public static Sappiness createNew(String id, int drones, boolean dominant) {
			id = ReikaStringParser.capFirstChar(id);
			IAlleleFloat allele = AlleleManager.alleleFactory.createFloat("dragonapi", "sappiness", id, drones, dominant, EnumTreeChromosome.SAPPINESS);
			AlleleManager.alleleRegistry.registerAllele(allele, EnumTreeChromosome.SAPPINESS);
			return EnumHelper.addEnum(Sappiness.class, id.toUpperCase(), new Class[]{String.class, String.class}, new Object[]{"dragonapi", id});
		}

		@Override
		public TreeGene oneBetter() {
			return this == HIGHEST ? null : values()[this.ordinal()+1];
		}
	}

	public static enum Yield implements TreeGene {
		LOWEST("Lowest"),
		LOWER("Lower"),
		LOW("Low"),
		HIGH("High"),
		HIGHER("Higher"),
		HIGHEST("Highest");

		public final String tag;

		private Yield(String s) {
			this("forestry", s);
		}

		private Yield(String pre, String s) {
			tag = pre+"."+"yield"+s;
			register(this, tag);
		}

		public IAlleleFloat getAllele() {
			return (IAlleleFloat)AlleleManager.alleleRegistry.getAllele(tag);
		}

		public static Yield createNew(String id, int drones, boolean dominant) {
			id = ReikaStringParser.capFirstChar(id);
			IAlleleFloat allele = AlleleManager.alleleFactory.createFloat("dragonapi", "yield", id, drones, dominant, EnumTreeChromosome.YIELD);
			AlleleManager.alleleRegistry.registerAllele(allele, EnumTreeChromosome.YIELD);
			return EnumHelper.addEnum(Yield.class, id.toUpperCase(), new Class[]{String.class, String.class}, new Object[]{"dragonapi", id});
		}

		@Override
		public TreeGene oneBetter() {
			return this == HIGHEST ? null : values()[this.ordinal()+1];
		}
	}

	public static enum Fruit implements TreeGene {
		;

		public final String tag;

		private Fruit(String s) {
			this("forestry", s);
		}

		private Fruit(String pre, String s) {
			tag = pre+"."+"fruits"+s;
			register(this, tag);
		}

		public IAlleleFruit getAllele() {
			return (IAlleleFruit)AlleleManager.alleleRegistry.getAllele(tag);
		}

		@Override
		public TreeGene oneBetter() {
			return null;
		}
	}

	public static enum Girth implements TreeGene {
		SLOWEST("Slowest"),
		SLOWER("Slower"),
		SLOW("Slow"),
		AVERAGE("Average"),
		FAST("Fast"),
		FASTER("Faster"),
		FASTEST("Fastest"),
		MAXIMUM("Maximum");

		public final String tag;

		private Girth(String s) {
			this("forestry", s);
		}

		private Girth(String pre, String s) {
			tag = pre+"."+"flowering"+s;
			register(this, tag);
		}

		public IAlleleInteger getAllele() {
			return (IAlleleInteger)AlleleManager.alleleRegistry.getAllele(tag);
		}

		public static Girth createNew(String id, int value, boolean dominant) {
			id = ReikaStringParser.capFirstChar(id);
			IAlleleInteger allele = AlleleManager.alleleFactory.createInteger("dragonapi", "girth", id, value, dominant, EnumTreeChromosome.GIRTH);
			AlleleManager.alleleRegistry.registerAllele(allele, EnumTreeChromosome.GIRTH);
			return EnumHelper.addEnum(Girth.class, id.toUpperCase(), new Class[]{String.class, String.class}, new Object[]{"dragonapi", id});
		}

		@Override
		public TreeGene oneBetter() {
			return this == MAXIMUM ? null : values()[this.ordinal()+1];
		}
	}

	public static enum Maturation implements TreeGene {
		SLOWEST("Slowest"),
		SLOWER("Slower"),
		SLOW("Slow"),
		AVERAGE("Average"),
		FAST("Fast"),
		FASTER("Faster"),
		FASTEST("Fastest"),
		MAXIMUM("Maximum");

		public final String tag;

		private Maturation(String s) {
			this("forestry", s);
		}

		private Maturation(String pre, String s) {
			tag = pre+"."+"flowering"+s;
			register(this, tag);
		}

		public IAlleleInteger getAllele() {
			return (IAlleleInteger)AlleleManager.alleleRegistry.getAllele(tag);
		}

		public static Maturation createNew(String id, int value, boolean dominant) {
			id = ReikaStringParser.capFirstChar(id);
			IAlleleInteger allele = AlleleManager.alleleFactory.createInteger("dragonapi", "maturation", id, value, dominant, EnumTreeChromosome.MATURATION);
			AlleleManager.alleleRegistry.registerAllele(allele, EnumTreeChromosome.MATURATION);
			return EnumHelper.addEnum(Maturation.class, id.toUpperCase(), new Class[]{String.class, String.class}, new Object[]{"dragonapi", id});
		}

		@Override
		public TreeGene oneBetter() {
			return this == MAXIMUM ? null : values()[this.ordinal()+1];
		}
	}

	public static enum Territory implements TreeGene {
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
			IAlleleArea allele = AlleleManager.alleleFactory.createArea("dragonapi", "territory", id, rangeXZ, rangeY, rangeXZ, dominant, EnumTreeChromosome.TERRITORY);
			AlleleManager.alleleRegistry.registerAllele(allele, EnumTreeChromosome.TERRITORY);
			return EnumHelper.addEnum(Territory.class, id.toUpperCase(), new Class[]{String.class, String.class}, new Object[]{"dragonapi", id});
		}

		@Override
		public TreeGene oneBetter() {
			return this == LARGEST ? null : values()[this.ordinal()+1];
		}
	}

	public static enum Growth implements TreeGene {
		LIGHTLEVEL("lightlevel"),
		ACACIA("acacia"),
		TROPICAL("tropical");

		public final String tag;

		private Growth(String s) {
			this("forestry", s);
		}

		private Growth(String pre, String s) {
			tag = pre+"."+s;
			register(this, tag);
		}

		public IAlleleGrowth getAllele() {
			return (IAlleleGrowth)AlleleManager.alleleRegistry.getAllele(tag);
		}

		@Override
		public TreeGene oneBetter() {
			return null;
		}
	}

	public static enum Effect implements TreeGene {
		NONE("effectNone");

		public final String tag;

		private Effect(String s) {
			this("forestry", s);
		}

		private Effect(String pre, String s) {
			tag = pre+"."+s;
			register(this, tag);
		}

		public IAlleleLeafEffect getAllele() {
			return (IAlleleLeafEffect)AlleleManager.alleleRegistry.getAllele(tag);
		}

		@Override
		public TreeGene oneBetter() {
			return null;
		}
	}

	private static void register(TreeGene g, String n) {
		geneMap.put(g.getClass(), n, g);
	}

	public static TreeGene getEnum(IAllele allele, Class<? extends TreeGene> type) {
		return geneMap.get(type, allele.getUID());
	}
}
