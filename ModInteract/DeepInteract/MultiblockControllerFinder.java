package Reika.DragonAPI.ModInteract.DeepInteract;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import net.minecraft.tileentity.TileEntity;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;

public class MultiblockControllerFinder {

	public static final MultiblockControllerFinder instance = new MultiblockControllerFinder();

	private static final MultiblockHandler NONE = new MultiblockHandler() {
		@Override
		public TileEntity getController(TileEntity te) { return te; }
	};

	private final HashMap<Class, MultiblockHandler> data = new HashMap();

	private MultiblockControllerFinder() {
		if (ModList.IMMERSIVEENG.isLoaded()) {
			String s = "blusunrize.immersiveengineering.common.blocks.metal.TileEntityMultiblockPart";
			this.register(s, new IEMultiblockHandler(s));
		}
		if (ModList.RAILCRAFT.isLoaded()) {
			String s = "mods.railcraft.common.blocks.machine.TileMultiBlock";
			this.register(s, new RailcraftMultiblockHandler(s));
		}
	}

	private void register(String s, MultiblockHandler m) {
		try {
			Class c = Class.forName(s);
			data.put(c, m);
		}
		catch (Exception e) {
			e.printStackTrace();
			ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.IMMERSIVEENG, e);
			DragonAPICore.logError("Could not find IE Multiblock class!");
		}
	}

	public boolean isMultiblockTile(TileEntity te) {
		return this.getHandler(te.getClass()) != NONE;
	}

	private MultiblockHandler getHandler(Class c) {
		MultiblockHandler ret = data.get(c);
		if (ret == null) {
			Class seek = null;
			for (Class k : data.keySet()) {
				if (k.isAssignableFrom(c)) {
					seek = k;
					break;
				}
			}
			if (seek != null) {
				ret = data.get(seek);
			}
			else {
				ret = NONE;
			}
			data.put(c, ret);
		}
		return ret;
	}

	public Set<Class> getClasses() {
		return Collections.unmodifiableSet(data.keySet());
	}

	public TileEntity getController(TileEntity root) {
		try {
			return this.getHandler(root.getClass()).getController(root);
		}
		catch (Exception e) {
			DragonAPICore.logError("Could not handle multiblock!");
			e.printStackTrace();
			return root;
		}
	}

	private static class RailcraftMultiblockHandler extends MethodCallMultiblockHandler {

		private RailcraftMultiblockHandler(String s) {
			super(s, "getMasterBlock", ModList.RAILCRAFT);
		}

	}

	private static class IEMultiblockHandler extends MethodCallMultiblockHandler {

		private IEMultiblockHandler(String s) {
			super(s, "master", ModList.IMMERSIVEENG);
		}

	}

	private static abstract class MethodCallMultiblockHandler implements MultiblockHandler {

		private Method getMaster;

		private MethodCallMultiblockHandler(String s, String method, ModList mod) {
			try {
				Class c = Class.forName(s);
				getMaster = c.getDeclaredMethod(method);
				getMaster.setAccessible(true);
			}
			catch (Exception e) {
				e.printStackTrace();
				ReflectiveFailureTracker.instance.logModReflectiveFailure(mod, e);
				DragonAPICore.logError("Could not find "+mod.getDisplayName()+" Multiblock internal members!");
			}
		}

		@Override
		public final TileEntity getController(TileEntity te) throws Exception {
			TileEntity relay = (TileEntity)getMaster.invoke(te);
			return relay != null ? relay : te;
		}
	}

	private static interface MultiblockHandler {

		public TileEntity getController(TileEntity te) throws Exception;

	}

}
