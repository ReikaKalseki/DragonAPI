package Reika.DragonAPI.ModInteract.DeepInteract;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.ClassReparenter.Reparent;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.FocusUpgradeType;
import thaumcraft.api.wands.ItemFocusBasic;

@Reparent(value = {"thaumcraft.api.wands.ItemFocusBasic", "net.minecraft.item.Item"})
public abstract class ItemCustomFocus extends ItemFocusBasic {

	public ItemCustomFocus(CreativeTabs tab) {
		this.setCreativeTab(tab);
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected abstract String getIconString();

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamage(int par1) {
		return itemIcon;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public abstract AspectList getVisCost(ItemStack focusStack);

	@Override
	@SideOnly(Side.CLIENT)
	public abstract int getFocusColor(ItemStack focusstack);

	@Override
	@SideOnly(Side.CLIENT)
	public abstract IIcon getOrnament(ItemStack focusstack);

	@Override
	@SideOnly(Side.CLIENT)
	public abstract IIcon getFocusDepthLayerIcon(ItemStack focusstack);

	@Override
	public final String getSortingHelper(ItemStack focusstack) {
		String out= this.getID();
		for (short id:this.getAppliedUpgrades(focusstack)) {
			out = out + id;
		}
		return out;
	}

	protected abstract String getID();

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public abstract FocusUpgradeType[] getPossibleUpgradesByRank(ItemStack focusstack, int rank);

	public boolean onLeftClick(World world, int x, int y, int z, EntityPlayer ep, ItemStack wand, ForgeDirection side) {
		return false;
	}

}
