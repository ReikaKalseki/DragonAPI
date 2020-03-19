package Reika.DragonAPI.Interfaces;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import Reika.DragonAPI.Auxiliary.Trackers.KeyWatcher;
import Reika.DragonAPI.Auxiliary.Trackers.KeyWatcher.Key;
import Reika.DragonAPI.Base.CoreContainer;
import Reika.DragonAPI.Interfaces.TileEntity.CraftingTile;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public abstract class CraftingContainer<V> extends CoreContainer {

	private World world;
	private InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
	private IInventory craftResult = new InventoryCraftResult();
	private boolean noUpdate;
	private CraftingTile<V> crafter;
	public final boolean isGUI;

	public CraftingContainer(EntityPlayer player, CraftingTile<V> te, World worldObj, boolean gui) {
		super(player, (TileEntity)te);
		world = worldObj;
		crafter = te;
		this.isGUI = gui;
	}

	protected final void updateCraftMatrix() {
		for (int i = 0; i < 9; i++) {
			ItemStack stack = crafter.getStackInSlot(i);
			noUpdate = true;
			craftMatrix.setInventorySlotContents(i, stack);
		}
	}

	protected final void onCraftMatrixChanged()  {
		this.onCraftMatrixChanged(craftMatrix);
	}

	@Override
	public final void onCraftMatrixChanged(IInventory par1IInventory) {
		if (!this.isGUI) {
			super.onCraftMatrixChanged(par1IInventory);
			return;
		}
		if (noUpdate) {
			noUpdate = false;
			return;
		}
		V wr = this.getRecipe(craftMatrix, world);
		if (wr == null) {
			crafter.setToCraft(null);
			return;
		}
		ItemStack is = this.getOutput(wr);
		ItemStack slot13 = crafter.getStackInSlot(crafter.getOutputSlot());
		if (slot13 != null) {
			if (is.getItem() != slot13.getItem())
				return;
			if (is.getItemDamage() != slot13.getItemDamage())
				return;
			if (slot13.stackSize >= slot13.getMaxStackSize())
				return;
		}
		crafter.setToCraft(wr);
	}

	private void craft(V wr, EntityPlayer ep) {
		if (crafter.handleCrafting(wr, ep, KeyWatcher.instance.isKeyDown(ep, Key.LSHIFT)))
			this.updateCraftMatrix();
		//tile.craftable = false;
	}

	@Override
	public final ItemStack slotClick(int slot, int par2, int action, EntityPlayer ep) {
		/*
		if (slot >= 18 && slot < tile.getSizeInventory()) {
			ItemStack held = ep.inventory.getItemStack();
			tile.setMapping(slot, ReikaItemHelper.getSizedItemStack(held, 1));
			return held;
		}
		 */

		//if (action == 4 && slot >= 18 && slot < tile.getSizeInventory())
		//	action = 0;

		ItemStack is = super.slotClick(slot, par2, action, ep);
		this.updateCraftMatrix();
		this.onCraftMatrixChanged(craftMatrix);
		InventoryPlayer ip = ep.inventory;
		//ReikaJavaLibrary.pConsole(ip.getItemStack());
		V wr = this.getRecipe(craftMatrix, world);
		if (wr != null && crafter.isReadyToCraft() && slot == 13) {
			ItemStack drop = ip.getItemStack();
			ItemStack craft = this.getOutput(wr);
			if (drop != null && (!ReikaItemHelper.matchStacks(drop, craft) || drop.stackSize+craft.stackSize > drop.getMaxStackSize()))
				return is;
			this.craft(wr, ep);
			craft.onCrafting(world, ep, craft.stackSize);
			int outslot = crafter.getOutputSlot();
			if (drop == null)
				ip.setItemStack(crafter.getStackInSlot(outslot));
			else
				drop.stackSize += crafter.getStackInSlot(outslot).stackSize;
			crafter.setInventorySlotContents(outslot, null);
		}
		return is;
	}

	protected abstract ItemStack getOutput(V wr);

	protected abstract V getRecipe(InventoryCrafting craftMatrix, World world);

}
