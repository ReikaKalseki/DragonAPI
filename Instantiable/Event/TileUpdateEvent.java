package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.tileentity.TileEntity;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import cpw.mods.fml.common.eventhandler.Cancelable;

@Cancelable
public class TileUpdateEvent extends TileEntityEvent {

	public TileUpdateEvent(TileEntity te) {
		super(te);

		ReikaJavaLibrary.pConsole("Updating "+te);
	}

}
