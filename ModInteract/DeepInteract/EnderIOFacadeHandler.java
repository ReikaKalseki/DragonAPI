package Reika.DragonAPI.ModInteract.DeepInteract;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;

public class EnderIOFacadeHandler {

	public static final EnderIOFacadeHandler instance = new EnderIOFacadeHandler();

	private Method paintConduit;
	private Method getPaintConduit;
	private Method getPaintConduitMeta;

	private Class conduitTileClass;

	private Field facadeBlock;
	private Field facadeMeta;

	private EnderIOFacadeHandler() {
		if (ModList.ENDERIO.isLoaded()) {
			try {
				Class c = Class.forName("crazypants.enderio.machine.painter.PainterUtil");
				paintConduit = c.getMethod("setSourceBlock", ItemStack.class, Block.class, int.class);
				paintConduit.setAccessible(true);

				getPaintConduit = c.getMethod("getSourceBlock", ItemStack.class);
				getPaintConduit.setAccessible(true);

				getPaintConduitMeta = c.getMethod("getSourceBlockMetadata", ItemStack.class);
				getPaintConduitMeta.setAccessible(true);

				conduitTileClass = Class.forName("crazypants.enderio.conduit.TileConduitBundle");

				facadeBlock = conduitTileClass.getDeclaredField("facadeId");
				facadeBlock.setAccessible(true);
				facadeMeta = conduitTileClass.getDeclaredField("facadeMeta");
				facadeMeta.setAccessible(true);
			}
			catch (Exception e) {
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.ENDERIO, e);
				DragonAPICore.logError("Could not load EiO facade handler!");
				e.printStackTrace();
			}
		}
	}

	public void paintConduitItem(ItemStack is, Block b, int meta) {
		try {
			paintConduit.invoke(null, is, b, meta);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Block getConduitItemPaint(ItemStack is) {
		try {
			return (Block)getPaintConduit.invoke(null, is);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Block getFacade(TileEntity te) {
		try {
			return te != null && conduitTileClass != null && conduitTileClass.isAssignableFrom(te.getClass()) ? (Block)facadeBlock.get(te) : null;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public int getFacadeMeta(TileEntity te) {
		try {
			return te != null && conduitTileClass != null && conduitTileClass.isAssignableFrom(te.getClass()) ? facadeMeta.getInt(te) : 0;
		}
		catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

}
