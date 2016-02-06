package Reika.DragonAPI.ModInteract.Bees;

import net.minecraftforge.common.util.EnumHelper;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.IAlleleBeeEffect;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAlleleArea;
import forestry.api.genetics.IAlleleFloat;
import forestry.api.genetics.IAlleleFlowers;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IAlleleTolerance;


public class AlleleRegistry {


	public static enum Speeds {
		SLOWEST("Slowest"), //0.3
		SLOWER("Slower"),
		SLOW("Slow"),
		NORMAL("Norm"),
		FAST("Fast"),
		FASTER("Faster"),
		FASTEST("Fastest"); //1.7; blinding from MagicBees is 2.0

		public final String tag;

		private Speeds(String s) {
			this("forestry", s);
		}

		private Speeds(String pre, String s) {
			tag = pre+"."+"speed"+s;
		}

		public IAlleleFloat getAllele() {
			return (IAlleleFloat)AlleleManager.alleleRegistry.getAllele(tag);
		}

		public static Speeds createNew(String id, float speed, boolean dominant) {
			id = ReikaStringParser.capFirstChar(id);
			IAlleleFloat allele = AlleleManager.alleleFactory.createFloat("dragonapi", "speed", id, speed, dominant, EnumBeeChromosome.SPEED);
			AlleleManager.alleleRegistry.registerAllele(allele, EnumBeeChromosome.SPEED);
			return EnumHelper.addEnum(Speeds.class, id.toUpperCase(), new Class[]{String.class, String.class}, new Object[]{"dragonapi", id});
		}
	}

	public static enum Fertility {
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
		}

		public IAlleleInteger getAllele() {
			return (IAlleleInteger)AlleleManager.alleleRegistry.getAllele(tag);
		}

		public static Fertility createNew(String id, int drones, boolean dominant) {
			id = ReikaStringParser.capFirstChar(id);
			IAlleleInteger allele = AlleleManager.alleleFactory.createInteger("dragonapi", "fertility", id, drones, dominant, EnumBeeChromosome.FERTILITY);
			AlleleManager.alleleRegistry.registerAllele(allele, EnumBeeChromosome.FERTILITY);
			return EnumHelper.addEnum(Fertility.class, id.toUpperCase(), new Class[]{String.class, String.class}, new Object[]{"dragonapi", id});
		}
	}

	public static enum Flower {
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
		}

		public IAlleleFlowers getAllele() {
			return (IAlleleFlowers)AlleleManager.alleleRegistry.getAllele(tag);
		}
	}

	public static enum Flowering {
		SLOWEST("Slowest"), //5
		SLOWER("Slower"),
		SLOW("Slow"),
		AVERAGE("Average"),
		FAST("Fast"),
		FASTER("Faster"),
		FASTEST("Fastest"),
		MAXIMUM("Maximum"); //99, "gui.maximum" in 1.6

		public final String tag;

		private Flowering(String s) {
			this("forestry", s);
		}

		private Flowering(String pre, String s) {
			tag = pre+"."+"flowering"+s;
		}

		public IAlleleInteger getAllele() {
			return (IAlleleInteger)AlleleManager.alleleRegistry.getAllele(tag);
		}

		public static Flowering createNew(String id, int value, boolean dominant) {
			id = ReikaStringParser.capFirstChar(id);
			IAlleleInteger allele = AlleleManager.alleleFactory.createInteger("dragonapi", "flowering", id, value, dominant, EnumBeeChromosome.FLOWERING);
			AlleleManager.alleleRegistry.registerAllele(allele, EnumBeeChromosome.FLOWERING);
			return EnumHelper.addEnum(Flowering.class, id.toUpperCase(), new Class[]{String.class, String.class}, new Object[]{"dragonapi", id});
		}
	}

	public static enum Territory {
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
			IAlleleArea allele = AlleleManager.alleleFactory.createArea("dragonapi", "territory", id, rangeXZ, rangeY, rangeXZ, dominant, EnumBeeChromosome.TERRITORY);
			AlleleManager.alleleRegistry.registerAllele(allele, EnumBeeChromosome.TERRITORY);
			return EnumHelper.addEnum(Territory.class, id.toUpperCase(), new Class[]{String.class, String.class}, new Object[]{"dragonapi", id});
		}
	}

	public static enum Life {
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
		}

		public IAlleleInteger getAllele() {
			return (IAlleleInteger)AlleleManager.alleleRegistry.getAllele(tag);
		}

		public static Life createNew(String id, int life, boolean dominant) {
			id = ReikaStringParser.capFirstChar(id);
			IAlleleInteger allele = AlleleManager.alleleFactory.createInteger("dragonapi", "lifespan", id, life, dominant, EnumBeeChromosome.LIFESPAN);
			AlleleManager.alleleRegistry.registerAllele(allele, EnumBeeChromosome.LIFESPAN);
			return EnumHelper.addEnum(Life.class, id.toUpperCase(), new Class[]{String.class, String.class}, new Object[]{"dragonapi", id});
		}
	}

	public static enum Tolerance {
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
		}

		public IAlleleTolerance getAllele() {
			return (IAlleleTolerance)AlleleManager.alleleRegistry.getAllele(tag);
		}
		/*
		public static Tolerance createNew(String id, ToleranceCheck t, boolean dominant) {
			id = ReikaStringParser.capFirstChar(id);
			Tolerance allele = AlleleManager.alleleFactory.createTolerance("dragonapi", "lifespan", id, life, dominant, EnumBeeChromosome.);
			AlleleManager.alleleRegistry.registerAllele(allele, EnumBeeChromosome.TEMPERATURE_TOLERANCE, EnumBeeChromosome.HUMIDITY_TOLERANCE);
			return EnumHelper;
		}*/
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
			this("forestry", s);
		}

		private Effect(String pre, String s) {
			tag = pre+"."+s;
		}

		public IAlleleBeeEffect getAllele() {
			return (IAlleleBeeEffect)AlleleManager.alleleRegistry.getAllele(tag);
		}
	}
}
