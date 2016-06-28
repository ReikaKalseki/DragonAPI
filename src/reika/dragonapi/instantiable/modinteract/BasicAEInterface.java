/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable.modinteract;

import java.util.EnumSet;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import reika.dragonapi.asm.APIStripper.Strippable;
import reika.dragonapi.exception.MisuseException;
import appeng.api.networking.GridFlags;
import appeng.api.networking.GridNotification;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridBlock;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.events.MENetworkPowerIdleChange;
import appeng.api.util.AEColor;
import appeng.api.util.DimensionalCoord;

@Strippable(value={"appeng.api.networking.IGridBlock"})
public class BasicAEInterface implements IGridBlock {

	private final TileEntity tile;
	private final ItemStack item;

	private double powerCost = 1;

	public BasicAEInterface(TileEntity te, ItemStack is) {
		this(te, is, 1);
	}

	public BasicAEInterface(TileEntity te, ItemStack is, double basePower) {
		if (!(te instanceof IGridHost))
			throw new MisuseException("You cannot use a non-AE-gridHost block!");
		tile = te;
		item = is;
		powerCost = basePower;
	}

	public void setPowerCost(double val) {
		if (val != powerCost) {
			IGridNode ig = ((IGridHost)tile).getGridNode(ForgeDirection.UNKNOWN);
			if (ig != null) {
				IGrid g = ig.getGrid();
				if (g != null) {
					powerCost = val;
					g.postEvent(new MENetworkPowerIdleChange(ig));
				}
			}
		}
	}

	@Override
	public double getIdlePowerUsage() {
		return powerCost;
	}

	@Override
	public EnumSet<GridFlags> getFlags() {
		return EnumSet.of(GridFlags.REQUIRE_CHANNEL);
	}

	@Override
	public boolean isWorldAccessible() {
		return true;
	}

	@Override
	public DimensionalCoord getLocation() {
		return new DimensionalCoord(tile);
	}

	@Override
	public AEColor getGridColor() {
		return AEColor.Transparent;
	}

	@Override
	public void onGridNotification(GridNotification notification) {

	}

	@Override
	public void setNetworkStatus(IGrid grid, int channelsInUse) {

	}

	@Override
	public EnumSet<ForgeDirection> getConnectableSides() {
		return EnumSet.allOf(ForgeDirection.class);
	}

	@Override
	public IGridHost getMachine() {
		return (IGridHost)tile;
	}

	@Override
	public void gridChanged() {

	}

	@Override
	public ItemStack getMachineRepresentation() {
		return item;
	}

}
