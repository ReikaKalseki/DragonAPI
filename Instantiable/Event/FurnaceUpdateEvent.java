/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.tileentity.TileEntityFurnace;
import cpw.mods.fml.common.eventhandler.Cancelable;

public abstract class FurnaceUpdateEvent extends TileEntityEvent {

	public final TileEntityFurnace furnace;

	public FurnaceUpdateEvent(TileEntityFurnace te) {
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
