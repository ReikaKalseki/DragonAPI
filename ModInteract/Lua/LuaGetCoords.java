package Reika.DragonAPI.ModInteract.Lua;

import net.minecraft.tileentity.TileEntity;

public class LuaGetCoords extends LuaMethod {

	public LuaGetCoords() {
		super("getCoords", TileEntity.class);
	}

	@Override
	public Object[] invoke(TileEntity te, Object[] args) throws Exception {
		return new Object[]{te.xCoord, te.yCoord, te.zCoord};
	}

}
