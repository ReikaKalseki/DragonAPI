/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.entity.player.EntityPlayer;
import Reika.DragonAPI.Auxiliary.Trackers.KeyWatcher.Key;
import cpw.mods.fml.common.eventhandler.Event;

public class RawKeyPressEvent extends Event {

	public final Key key;
	public final EntityPlayer player;

	public RawKeyPressEvent(Key k, EntityPlayer ep) {
		key = k;
		player = ep;
	}

}
