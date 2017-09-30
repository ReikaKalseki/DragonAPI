package Reika.DragonAPI.Instantiable;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;


public abstract class TemperatureEffect {

	public static final TemperatureEffect rockMelting = new BlockConversionEffect(1500){

		@Override
		protected void doAction(World world, int x, int y, int z, int temperature) {
			if (ReikaWorldHelper.isMeltable(world, x, y, z, temperature))
				super.doAction(world, x, y, z, temperature);
		}

		@Override
		protected Block getBlock(int temperature) {
			return Blocks.flowing_lava;
		}

	};

	public static final TemperatureEffect groundGlassing = new BlockConversionEffect(900){
		@Override
		protected Block getBlock(int temperature) {
			return Blocks.glass;
		}
	};

	public static final TemperatureEffect snowVaporization = new BlockConversionEffect(0){
		@Override
		protected Block getBlock(int temperature) {
			return Blocks.air;
		}
	};

	public static final TemperatureEffect iceMelting = new BlockConversionEffect(0){
		@Override
		protected Block getBlock(int temperature) {
			return Blocks.flowing_water;
		}
	};

	public static final TemperatureEffect woodIgnition = new IgnitionEffect(450);
	public static final TemperatureEffect woolIgnition = new IgnitionEffect(600);
	public static final TemperatureEffect tntIgnition = new IgnitionEffect(300);
	public static final TemperatureEffect plantIgnition = new IgnitionEffect(230);

	public final int minimumTemperature;

	protected TemperatureEffect(int temp) {
		minimumTemperature = temp;
	}

	public final void apply(World world, int x, int y, int z, int temperature, TemperatureCallback call) {
		this.doAction(world, x, y, z, temperature);
		if (call != null) {
			call.onApplyTemperature(world, x, y, z, temperature);
		}
	}

	private static class IgnitionEffect extends TemperatureEffect {

		protected IgnitionEffect(int temp) {
			super(temp);
		}

		@Override
		protected final void doAction(World world, int x, int y, int z, int temperature) {
			if (ReikaWorldHelper.flammable(world, x, y, z))
				ReikaWorldHelper.ignite(world, x, y, z);
		}

	}

	private static abstract class BlockConversionEffect extends TemperatureEffect {

		protected BlockConversionEffect(int temp) {
			super(temp);
		}

		@Override
		protected void doAction(World world, int x, int y, int z, int temperature) {
			world.setBlock(x, y, z, this.getBlock(temperature));
		}

		protected abstract Block getBlock(int temperature);

	}

	protected abstract void doAction(World world, int x, int y, int z, int temperature);

	public static interface TemperatureCallback {

		void onApplyTemperature(World world, int x, int y, int z, int temperature);

	}

}
