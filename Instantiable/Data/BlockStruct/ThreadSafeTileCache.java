package Reika.DragonAPI.Instantiable.Data.BlockStruct;

import java.util.Iterator;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import Reika.DragonAPI.Instantiable.Data.Collections.ThreadSafeSet;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;


public class ThreadSafeTileCache extends ThreadSafeSet<WorldLocation> {

	/** The class tiles at the locations have to extend to be valid. May be null to indicate even no tile is valid. */
	public Class tileClass = TileEntity.class;

	public ThreadSafeTileCache setTileClass(Class<? extends TileEntity> c) {
		tileClass = c;
		return this;
	}

	public boolean lookForMatch(World world, boolean skipOtherDimension, TileEntityMatchCheck check) {
		return this.lookForMatch(world, skipOtherDimension, check, null);
	}

	public boolean lookForMatch(World world, boolean skipOtherDimension, TileEntityMatchCheck check, ValidityFailHandler errorHandle) {
		synchronized (data) {
			Iterator<WorldLocation> it = data.iterator(); // Must be in the synchronized block
			while (it.hasNext()) {
				WorldLocation val = it.next();
				boolean other = val.dimensionID != world.provider.dimensionId;
				if (skipOtherDimension && other)
					continue;
				TileEntity te = other ? val.getTileEntity() : val.getTileEntity(world);
				if (te == null && world.isRemote)
					continue;
				if (tileClass != null) {
					if (!tileClass.isAssignableFrom(te.getClass())) {
						it.remove();
						if (errorHandle != null) {
							errorHandle.handle(val, te);
						}
						continue;
					}
				}
				if (check.handle(val, te))
					return true;
			}
		}
		return false;
	}

	public void applyToMatches(World world, boolean skipOtherDimension, TileEntityMatchEffect check) {
		this.applyToMatches(world, skipOtherDimension, check, null);
	}

	public void applyToMatches(World world, boolean skipOtherDimension, TileEntityMatchEffect check, ValidityFailHandler errorHandle) {
		synchronized (data) {
			Iterator<WorldLocation> it = data.iterator(); // Must be in the synchronized block
			while (it.hasNext()) {
				WorldLocation val = it.next();
				boolean other = val.dimensionID != world.provider.dimensionId;
				if (skipOtherDimension && other)
					continue;
				TileEntity te = other ? val.getTileEntity() : val.getTileEntity(world);
				if (te == null && world.isRemote)
					continue;
				if (tileClass != null) {
					if (!tileClass.isAssignableFrom(te.getClass())) {
						it.remove();
						if (errorHandle != null) {
							errorHandle.handle(val, te);
						}
						continue;
					}
				}
				check.handle(val, te);
			}
		}
	}

	public TileEntity returnMatch(World world, boolean skipOtherDimension, TileEntityMatchCheck check) {
		return this.returnMatch(world, skipOtherDimension, check, null);
	}

	public TileEntity returnMatch(World world, boolean skipOtherDimension, TileEntityMatchCheck check, ValidityFailHandler errorHandle) {
		synchronized (data) {
			Iterator<WorldLocation> it = data.iterator(); // Must be in the synchronized block
			while (it.hasNext()) {
				WorldLocation val = it.next();
				boolean other = val.dimensionID != world.provider.dimensionId;
				if (skipOtherDimension && other)
					continue;
				TileEntity te = other ? val.getTileEntity() : val.getTileEntity(world);
				if (te == null && world.isRemote)
					continue;
				if (tileClass != null) {
					if (!tileClass.isAssignableFrom(te.getClass())) {
						it.remove();
						if (errorHandle != null) {
							errorHandle.handle(val, te);
						}
						continue;
					}
				}
				if (check.handle(val, te))
					return te;
			}
		}
		return null;
	}

	@FunctionalInterface
	public static interface TileEntityMatchCheck {

		public boolean handle(WorldLocation loc, TileEntity te);

	}

	@FunctionalInterface
	public static interface TileEntityMatchEffect {

		public void handle(WorldLocation loc, TileEntity te);

	}

	@FunctionalInterface
	public static interface ValidityFailHandler {

		public void handle(WorldLocation loc, TileEntity te);

	}

}
