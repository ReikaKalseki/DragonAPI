package Reika.DragonAPI.Interfaces;

import net.minecraft.entity.player.EntityPlayer;
import Reika.DragonAPI.Auxiliary.Trackers.PlayerSpecificRenderer.PlayerRotationData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface PlayerRenderObj {

	/** Render starts centered on eye position */
	public void render(EntityPlayer ep, float ptick, PlayerRotationData dat);

}
