package Reika.DragonAPI.Base;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Interfaces.Registry.TileEnum;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class StructureBase {

	private boolean isDisplayCall;

	@SideOnly(Side.CLIENT)
	public synchronized final FilledBlockArray getStructureForDisplay() {
		isDisplayCall = true;
		this.initDisplayData();
		FilledBlockArray ret = this.getArray(Minecraft.getMinecraft().theWorld, 0, 0, 0);
		isDisplayCall = false;
		this.finishDisplayCall();
		return ret;
	}

	protected void initDisplayData() {

	}

	protected void finishDisplayCall() {

	}

	public abstract FilledBlockArray getArray(World world, int x, int y, int z);

	protected final void setTile(FilledBlockArray f, int x, int y, int z, TileEnum te) {
		f.setBlock(x, y, z, te.getBlock(), te.getBlockMetadata());
	}

	protected final void addTile(FilledBlockArray f, int x, int y, int z, TileEnum te) {
		f.addBlock(x, y, z, te.getBlock(), te.getBlockMetadata());
	}

	protected final boolean isDisplay() {
		return isDisplayCall;
	}

}
