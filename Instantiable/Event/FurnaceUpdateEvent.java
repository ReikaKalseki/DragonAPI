package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.tileentity.TileEntityFurnace;
import Reika.DragonAPI.Instantiable.Event.TileEntityEvent;
import cpw.mods.fml.common.eventhandler.Cancelable;

public abstract class FurnaceUpdateEvent extends TileEntityEvent {

	public final TileEntityFurnace furnace;

	private FurnaceUpdateEvent(TileEntityFurnace te) {
		super(te);
		furnace = te;
	}

	public static class Post extends FurnaceUpdateEvent {

		public Post(TileEntityFurnace te) {
			super(te);
		}

	}

	@Cancelable
	public static class Pre extends FurnaceUpdateEvent {

		public Pre(TileEntityFurnace te) {
			super(te);
		}

	}

}
